package dang.kp.manager.biz.flow.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.biz.flow.dao.FlowLineDao;
import dang.kp.manager.biz.flow.dao.FlowStepDao;
import dang.kp.manager.biz.flow.pojo.FlowLine;
import dang.kp.manager.biz.flow.pojo.FlowStep;
import dang.kp.manager.biz.flow.service.FlowApiService;
import dang.kp.manager.common.myenum.MyKey;
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
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/flowStep")
public class FlowStepController {

    @Resource
    private FlowStepDao flowStepDao;
    @Resource
    private FlowLineDao flowLineDao;
    @Resource
    private FlowApiService flowApiService;

    /**
     * 跳转到管理页面
     *
     * @return
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("初始化页面");
        model.put("timestamp", DateTimeUtils.getHMSDay());
        List<FlowStep> stepList = Lists.newArrayList(FlowStep.builder().stepId(null).stepName("请选择").build());
        stepList.addAll(this.flowStepDao.findAll());
        model.put("stepList", stepList);

        List<FlowLine> lineList = Lists.newArrayList(FlowLine.builder().lineId(null).lineName("请选择").build());
        lineList.addAll(this.flowLineDao.findAll());
        model.put("lineList", lineList);

        return "/biz/flow/flowStep";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@RequestBody FlowStep param) {
        log.info("save. param = {}", JSONObject.toJSONString(param));

        if (StringUtils.isBlank(param.getStepId())) {
            param.setStepId(BatchUtils.getKey(MyKey.FlowStep));
        }
        this.flowStepDao.save(param);
        this.flowApiService.disableFlowByStepId(param.getStepId());
        return ResultUtils.success(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(@RequestBody FlowStep param) {
        log.info("delete. param = {}", JSONObject.toJSONString(param));
        return this.flowApiService.deleteByStepId(param.getStepId());
    }

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
                             @RequestParam("pageSize") Integer pageSize, FlowStep param) {
        log.info("获取列表{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("lineId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("stepName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getStepName())) {
            param.setStepName(null);
        }
        if (StringUtils.isBlank(param.getLineId())) {
            param.setLineId(null);
        }
        // 查询模板
        Example<FlowStep> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("lineId"), Sort.Order.asc("lineIndex"), Sort.Order.asc("stepName"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<FlowStep> pageResult = this.flowStepDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }
}
