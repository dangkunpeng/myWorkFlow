package dang.kp.manager.biz.flow.controller;

import com.alibaba.fastjson.JSONObject;
import dang.kp.manager.biz.flow.dao.FlowLineDao;
import dang.kp.manager.biz.flow.pojo.FlowLine;
import dang.kp.manager.biz.flow.service.FlowApiService;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.myenum.MyStatus;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("/flowLine")
public class FlowLineController {

    @Resource
    private FlowLineDao flowLineDao;

    @Resource
    private FlowApiService flowApiService;

    /**
     * 功能描述: 获取列表
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/21 14:29
     */
    @PostMapping(value = "/pageList")
    @ResponseBody
    public PageData pageList(@RequestParam("pageNum") Integer pageNum,
                             @RequestParam("pageSize") Integer pageSize, FlowLine param) {
        log.info("获取列表param{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("lineName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getLineName())) {
            param.setLineName(null);
        }
        // 查询模板
        Example<FlowLine> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("lineName"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<FlowLine> pageResult = this.flowLineDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    /**
     * 跳转到页面
     *
     * @return
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("初始化页面");
        model.put("timestamp", DateTimeUtils.getHMSDay());
        return "/biz/flow/flowLine";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@RequestBody FlowLine param) {
        log.info("save. param = {}", JSONObject.toJSONString(param));
        if (StringUtils.isBlank(param.getLineId())) {
            param.setLineId(BatchUtils.getKey(MyKey.FlowLine));
        }
        param.setStatus(MyStatus.negative.getValue());
        this.flowLineDao.save(param);
        return ResultUtils.success(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(@RequestBody FlowLine param) {
        log.info("delete. param = {}", JSONObject.toJSONString(param));

        return this.flowApiService.deleteByLineId(param.getLineId());
    }

    @PostMapping("/check")
    @ResponseBody
    public ResultData check(@RequestBody FlowLine param) {
        log.info("check. param = {}", JSONObject.toJSONString(param));

        return this.flowApiService.checkFlow(param.getLineId());
    }
}
