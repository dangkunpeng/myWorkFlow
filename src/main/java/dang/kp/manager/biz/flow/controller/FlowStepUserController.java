package dang.kp.manager.biz.flow.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.biz.flow.dao.FlowStepDao;
import dang.kp.manager.biz.flow.dao.FlowStepUserDao;
import dang.kp.manager.biz.flow.pojo.FlowStep;
import dang.kp.manager.biz.flow.pojo.FlowStepUser;
import dang.kp.manager.biz.flow.service.FlowApiService;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import dang.kp.manager.sys.admin.dao.BaseUserDao;
import dang.kp.manager.sys.admin.pojo.BaseUser;
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

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/flowStepUser")
public class FlowStepUserController {

    @Autowired
    private FlowStepUserDao flowStepUserDao;

    @Autowired
    private FlowStepDao flowStepDao;

    @Autowired
    private BaseUserDao baseUserDao;
    @Autowired
    private FlowApiService flowApiService;

    /**
     * 跳转到角色管理
     *
     * @return
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("初始化页面");
        model.put("timestamp", DateTimeUtils.getHMSDay());
        List<BaseUser> userList = Lists.newArrayList(BaseUser.builder().userId(null).userName("请选择").build());
        userList.addAll(this.baseUserDao.findAll());
        List<FlowStep> stepList = Lists.newArrayList(FlowStep.builder().stepId(null).stepName("请选择").build());
        stepList.addAll(this.flowStepDao.findAll());
        model.put("userList", userList);
        model.put("stepList", stepList);
        return "/biz/flow/flowStepUser";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResultData save(FlowStepUser param) {
        log.info("save. param = {}", JSONObject.toJSONString(param));
        if (StringUtils.isBlank(param.getStepUserId())) {
            param.setStepUserId(BatchUtils.getKey(MyKey.FlowStepUser));
        }
        this.flowStepUserDao.save(param);
        return ResultUtils.success(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(FlowStepUser param) {
        log.info("delete. param = {}", JSONObject.toJSONString(param));
        if (StringUtils.isBlank(param.getStepUserId())) {
            log.info("主键不能为空");
            return ResultUtils.fail("主键不能为空");
        }
        FlowStepUser flowStepUser = this.flowStepUserDao.getOne(param.getStepUserId());
        this.flowApiService.disableFlow(FlowStepUser.builder().stepId(flowStepUser.getStepId()).build());
        this.flowStepUserDao.delete(param);
        return ResultUtils.success(param);
    }

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
                             @RequestParam("pageSize") Integer pageSize, FlowStepUser param) {
        log.info("获取列表param{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("stepId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getStepId())) {
            param.setStepId(null);
        }
        if (StringUtils.isBlank(param.getUserId())) {
            param.setUserId(null);
        }
        // 查询模板
        Example<FlowStepUser> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("stepId"), Sort.Order.asc("userId"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<FlowStepUser> pageResult = this.flowStepUserDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }
}
