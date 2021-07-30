package dang.kp.manager.biz.flow.controller;

import com.alibaba.fastjson.JSONObject;
import dang.kp.manager.biz.flow.dao.FlowLineDao;
import dang.kp.manager.biz.flow.pojo.FlowLine;
import dang.kp.manager.biz.flow.service.FlowApiService;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.myenum.MyStatus;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.IStatusMessage;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/flowLine")
public class FlowLineController {

    @Autowired
    private FlowLineDao flowLineDao;

    @Autowired
    private FlowApiService flowApiService;

    /**
     * 功能描述: 获取角色列表
     *
     * @param:
     * @return:
     * @auther: youqing
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
    public ResultData save(FlowLine param) {
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
    public ResultData delete(FlowLine param) {
        log.info("delete. param = {}", JSONObject.toJSONString(param));
        if (StringUtils.isBlank(param.getLineId())) {
            log.info("主键不能为空");
            return ResultUtils.fail("主键不能为空");
        }
        this.flowLineDao.delete(param);
        return ResultUtils.success(param);
    }

    @PostMapping("/check")
    @ResponseBody
    public ResultData check(FlowLine param) {
        log.info("check. param = {}", JSONObject.toJSONString(param));

        return this.flowApiService.checkFlow(param.getLineId());
    }
}
