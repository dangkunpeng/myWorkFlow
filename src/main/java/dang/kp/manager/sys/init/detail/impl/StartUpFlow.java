package dang.kp.manager.sys.init.detail.impl;

import com.google.common.collect.Lists;
import dang.kp.manager.biz.flow.dao.FlowLineDao;
import dang.kp.manager.biz.flow.dao.FlowStepDao;
import dang.kp.manager.biz.flow.dao.FlowStepUserDao;
import dang.kp.manager.biz.flow.pojo.FlowLine;
import dang.kp.manager.biz.flow.pojo.FlowStep;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.myenum.MyStatus;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.sys.admin.dao.BaseUserDao;
import dang.kp.manager.sys.admin.pojo.BaseUser;
import dang.kp.manager.sys.init.detail.StartUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class StartUpFlow implements StartUpService {
    @Resource
    private FlowLineDao flowLineDao;
    @Resource
    private FlowStepDao flowStepDao;
    @Resource
    private FlowStepUserDao flowStepUserDao;
    @Resource
    private BaseUserDao baseUserDao;

    @Override
    public ResultData run() {
        Boolean exists = this.flowLineDao.count() > 0;
        if (exists) {
            return ResultUtils.success("工作流基础数据已存在,不需要从0创建");
        }
        log.info("00工作流基础数据不存在,从0创建");
        log.info("01创建工作流");
        FlowLine flowLine = FlowLine.builder().lineId(BatchUtils.getKey(MyKey.FlowLine)).lineName("出差补助审批").build();
        this.flowLineDao.save(flowLine);
        log.info("02创建工作流步骤");
        FlowStep flowStepPm = StartUpUtils.getFlowStep(flowLine.getLineId(), 1, "0", "经理审批", MyStatus.positive.getValue());
        FlowStep flowStepAcc = StartUpUtils.getFlowStep(flowLine.getLineId(), 2, flowStepPm.getStepId(), "财务审批", MyStatus.positive.getValue());
        FlowStep flowStepBoss = StartUpUtils.getFlowStep(flowLine.getLineId(), 3, flowStepAcc.getStepId(), "老板审批", MyStatus.positive.getValue());
        this.flowStepDao.saveAll(Lists.newArrayList(flowStepPm, flowStepAcc, flowStepBoss));
        log.info("03创建用户");
        BaseUser flowPm = StartUpUtils.getUser("flowPm", "123456", "18888888888");
        BaseUser flowAcc01 = StartUpUtils.getUser("flowAcc01", "123456", "18888888888");
        BaseUser flowAcc02 = StartUpUtils.getUser("flowAcc02", "123456", "18888888888");
        BaseUser flowBoss = StartUpUtils.getUser("flowBoss", "123456", "18888888888");
        this.baseUserDao.saveAll(Lists.newArrayList(flowPm, flowAcc01, flowAcc02, flowBoss));
        log.info("04关联用户和步骤");
        this.flowStepUserDao.saveAll(
                Lists.newArrayList(
                        StartUpUtils.getFlowStepUser(flowStepPm.getStepId(), flowPm.getUserId()),
                        StartUpUtils.getFlowStepUser(flowStepAcc.getStepId(), flowAcc01.getUserId()),
                        StartUpUtils.getFlowStepUser(flowStepAcc.getStepId(), flowAcc02.getUserId()),
                        StartUpUtils.getFlowStepUser(flowStepBoss.getStepId(), flowBoss.getUserId())
                )
        );
        return ResultUtils.success("工作流基础数据不存在,从0创建");
    }
}
