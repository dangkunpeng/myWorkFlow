package dang.kp.manager.biz.flow.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.biz.flow.dao.FlowBizLineDao;
import dang.kp.manager.biz.flow.dao.FlowBizLogDao;
import dang.kp.manager.biz.flow.dao.FlowLineDao;
import dang.kp.manager.biz.flow.dao.FlowStepDao;
import dang.kp.manager.biz.flow.dto.FlowApiDto;
import dang.kp.manager.biz.flow.pojo.FlowBizLine;
import dang.kp.manager.biz.flow.pojo.FlowBizLog;
import dang.kp.manager.biz.flow.pojo.FlowLine;
import dang.kp.manager.biz.flow.pojo.FlowStep;
import dang.kp.manager.biz.flow.service.FlowApiService;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.utils.DateTimeUtils;
import dang.kp.manager.sys.admin.dao.BaseUserDao;
import dang.kp.manager.sys.admin.pojo.BaseUser;
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
@RequestMapping("/flowApi")
public class FlowApiController {
    @Resource
    private BaseUserDao baseUserDao;
    @Resource
    private FlowBizLineDao flowBizLineDao;
    @Resource
    private FlowLineDao flowLineDao;
    @Resource
    private FlowStepDao flowStepDao;
    @Resource
    private FlowBizLogDao flowBizLogDao;
    @Resource
    private FlowApiService flowApiService;

    /**
     * 跳转到页面
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

        List<BaseUser> userList = Lists.newArrayList(BaseUser.builder().userId(null).userName("请选择").build());
        Sort sort02 = Sort.by(Sort.Order.asc("userId"));
        userList.addAll(this.baseUserDao.findAll(sort02));
        model.addAttribute("userList", userList);

        return "/biz/flow/flowApi";
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
                             @RequestParam("pageSize") Integer pageSize, FlowBizLine param) {
        log.info("获取列表param{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("lineId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("bizId", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getLineId())) {
            param.setLineId(null);
        }
        if (StringUtils.isBlank(param.getBizId())) {
            param.setBizId(null);
        }
        // 查询模板
        Example<FlowBizLine> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("bizId"), Sort.Order.asc("lineId"), Sort.Order.asc("timeStart"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<FlowBizLine> pageResult = this.flowBizLineDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    /**
     * 功能描述: 获取列表
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/21 14:29
     */
    @PostMapping(value = "/logList")
    @ResponseBody
    public PageData logList(@RequestParam("pageNum") Integer pageNum,
                            @RequestParam("pageSize") Integer pageSize, FlowBizLog param) {
        log.info("获取列表param{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("lineId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("bizId", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getLineId())) {
            param.setLineId(null);
        }
        if (StringUtils.isBlank(param.getBizId())) {
            param.setBizId(null);
        }
        // 查询模板
        Example<FlowBizLog> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("bizId"), Sort.Order.asc("lineId"), Sort.Order.desc("timeStart"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<FlowBizLog> pageResult = this.flowBizLogDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    @PostMapping("/start")
    @ResponseBody
    public ResultData start(@RequestBody FlowApiDto param) {
        log.info("flow start. param = {}", JSONObject.toJSONString(param));
        return this.flowApiService.startFlow(param);
    }


    @PostMapping("/vote")
    @ResponseBody
    public ResultData vote(@RequestBody FlowApiDto param) {
        log.info("flow vote. param = {}", JSONObject.toJSONString(param));
        return this.flowApiService.vote(param);
    }

    @PostMapping("/forceNext")
    @ResponseBody
    public ResultData forceNext(@RequestBody FlowApiDto param) {
        log.info("flow forceNext. param = {}", JSONObject.toJSONString(param));

        return this.flowApiService.forceNext(param);
    }

    @PostMapping("/forceTerminate")
    @ResponseBody
    public ResultData forceTerminate(@RequestBody FlowApiDto param) {
        log.info("flow forceTerminate. param = {}", JSONObject.toJSONString(param));
        return this.flowApiService.forceTerminate(param);
    }
}
