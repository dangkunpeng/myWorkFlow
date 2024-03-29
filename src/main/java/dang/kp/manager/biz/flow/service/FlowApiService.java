package dang.kp.manager.biz.flow.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.biz.flow.dao.*;
import dang.kp.manager.biz.flow.dto.FlowApiDto;
import dang.kp.manager.biz.flow.pojo.*;
import dang.kp.manager.common.myenum.*;
import dang.kp.manager.common.result.IStatusMessage;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import dang.kp.manager.common.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class FlowApiService {
    @Resource
    private FlowStepDao flowStepDao;
    @Resource
    private FlowStepUserDao flowStepUserDao;
    @Resource
    private FlowBizLogDao flowBizLogDao;
    @Resource
    private FlowBizLineDao flowBizLineDao;
    @Resource
    private FlowLineDao flowLineDao;


    /**
     * 开始新流程
     *
     * @param param
     * @return
     */
    public ResultData startFlow(FlowApiDto param) {
        log.info("flow start. param = {}", JSONObject.toJSONString(param));
        String bizId = param.getBizId();
        String lineId = param.getLineId();
        if (StringUtils.isAnyBlank(bizId, lineId)) {
            log.error("缺少参数{}", JSONObject.toJSONString(param));
            return ResultUtils.fail("缺少参数");
        }
        // 检查工作流
        ResultData resultData = this.checkFlow(param.getLineId());
        if (StringUtils.equals(resultData.getCode(), IStatusMessage.SystemStatus.ERROR.getCode())) {
            return resultData;
        }
        // 不能有已存在的工作流
        FlowBizLine entity = this.flowBizLineDao.findByBizIdAndStatus(bizId, MyFlowStatus.START.getValue());
        if (Objects.nonNull(entity)) {
            if (StringUtils.equals(lineId, entity.getLineId())) {
                log.info("不可重复进入相同流程");
                return ResultUtils.fail("不可重复进入相同流程!");
            }
            // 处理已有流程
            entity.setStatus(MyFlowStatus.DONE.getValue());
            entity.setTimeEnd(DateTimeUtils.now());
            this.flowBizLineDao.save(entity);
            // 处理已有日志
            List<FlowBizLog> logList = this.flowBizLogDao.findByBizIdAndLineIdAndStatus(bizId, lineId, MyFlowStatus.START.getValue());
            for (FlowBizLog flowBizLog : logList) {
                flowBizLog.setStatus(MyFlowStatus.DONE.getValue());
                flowBizLog.setResult(MyFlowResult.Deny.getValue());
                flowBizLog.setNote(MyFlowNote.terminate_by_other_flow.getValue());
                flowBizLog.setTimeEnd(DateTimeUtils.now());
                flowBizLog.setTimeCost(DateTimeUtils.timeCost(flowBizLog.getTimeStart()));
            }
            this.flowBizLogDao.saveAll(logList);
        }
        // 查询配置的工作流
        FlowStep step = this.flowStepDao.getFirstByLineIdAndLineIndexGreaterThanOrderByLineIndex(lineId, 0);
        // 记录节点信息
        FlowBizLine flowBizLine = FlowBizLine.builder()
                .bizLineId(BatchUtils.getKey(MyKey.FlowBizLine))
                .bizId(bizId)
                .lineId(lineId)
                .stepId(step.getStepId())
                .status(MyFlowStatus.START.getValue())
                .timeStart(DateTimeUtils.now())
                .build();
        this.flowBizLineDao.save(flowBizLine);

        this.generateLogs(flowBizLine);
        // 记录到数据库
        return ResultUtils.success(flowBizLine);
    }

    /**
     * 用户审批
     *
     * @param param
     * @return
     */
    public ResultData vote(FlowApiDto param) {
        String bizId = param.getBizId();
        String result = param.getResult();
        if (StringUtils.isAnyBlank(bizId, result)) {
            log.error("缺少参数{}", JSONObject.toJSONString(param));
            return ResultUtils.fail("缺少参数");
        }
        // 查询当前所处的工作流
        FlowBizLine bizLine = this.flowBizLineDao.findByBizIdAndStatus(bizId, MyFlowStatus.START.getValue());
        if (Objects.isNull(bizLine)) {
            log.error("参数错误,无对应工作流{}", bizId);
            return ResultUtils.fail("参数错误,无对应工作流");
        }
        // 查询当前所处的步骤
        String stepId = bizLine.getStepId();
        FlowBizLog myLog = this.flowBizLogDao.findFirstByBizIdAndStepIdAndUserIdToAndStatus(bizId, stepId,
                UserUtils.getUserId(), MyFlowStatus.START.getValue());
        if (Objects.isNull(myLog)) {
            log.error("当前人员不在审批人员之列{}", bizId);
            return ResultUtils.fail("当前人员不在审批人员之列");
        }
        // 更新记录信息
        myLog.setNote(MyFlowNote.audit_by_user.getValue());
        if (StringUtils.isNotBlank(param.getNote())) {
            myLog.setNote(param.getNote());
        }
        myLog.setStatus(MyFlowStatus.DONE.getValue());
        myLog.setTimeEnd(DateTimeUtils.now());
        myLog.setTimeCost(DateTimeUtils.timeCost(myLog.getTimeStart()));
        myLog.setResult(param.getResult());
        // 更新日志记录(立即刷新,不要再被更新了)
        this.flowBizLogDao.saveAndFlush(myLog);

        if (StringUtils.equals(param.getResult(), MyFlowResult.Deny.getValue())) {
            ResultData data = this.nextStepNo(bizLine, param.getNote());
            // 当前审批结束后开启新的工作流
            if (StringUtils.isNotBlank(param.getLineIdNew())) {
                startFlow(FlowApiDto.builder().bizId(bizId).lineId(param.getLineIdNew()).build());
            }
            return data;
        } else {
            return this.nextStepYes(bizLine);
        }
    }

    /**
     * 强制进入下一节点
     *
     * @param param
     * @return
     */
    public ResultData forceNext(FlowApiDto param) {
        String bizId = param.getBizId();
        FlowBizLine bizLine = this.flowBizLineDao.findByBizIdAndStatus(bizId, MyFlowStatus.START.getValue());
        if (Objects.isNull(bizLine)) {
            log.error("参数错误,请先开始流程{}", bizId);
            return ResultUtils.fail("参数错误,请先开始流程");
        }
        String lineId = bizLine.getLineId();
        List<FlowBizLog> myLogs = this.flowBizLogDao.findByBizIdAndLineIdAndStatus(bizId, lineId, MyFlowStatus.START.getValue());
        if (CollectionUtils.isEmpty(myLogs)) {
            log.error("参数错误,当前无可用流程{}", bizId);
            return ResultUtils.fail("参数错误,当前无可用流程");
        }
        for (FlowBizLog myLog : myLogs) {
            // 更新记录信息
            myLog.setNote(MyFlowNote.force_next.getValue());
            myLog.setStatus(MyFlowStatus.DONE.getValue());
            myLog.setTimeEnd(DateTimeUtils.now());
            myLog.setTimeCost(DateTimeUtils.timeCost(myLog.getTimeStart()));
            myLog.setResult(MyFlowResult.Approve.getValue());
        }
        this.flowBizLogDao.saveAll(myLogs);
        return this.next(bizLine);
    }

    /**
     * 强制终止
     *
     * @param param
     * @return
     */
    public ResultData forceTerminate(FlowApiDto param) {
        log.info("flow forceTerminate. param = {}", JSONObject.toJSONString(param));
        String bizId = param.getBizId();
        FlowBizLine bizLine = this.flowBizLineDao.findByBizIdAndStatus(bizId, MyFlowStatus.START.getValue());
        if (Objects.isNull(bizLine)) {
            log.error("参数错误,请先开始流程{}", bizId);
            return ResultUtils.success("finished");
        }
        bizLine.setStatus(MyFlowStatus.DONE.getValue());
        bizLine.setTimeEnd(DateTimeUtils.now());
        // 记录到数据库
        this.flowBizLineDao.save(bizLine);

        String lineId = bizLine.getLineId();
        List<FlowBizLog> myLogs = this.flowBizLogDao.findByBizIdAndLineIdAndStatus(bizId, lineId, MyFlowStatus.START.getValue());
        if (CollectionUtils.isEmpty(myLogs)) {
            log.error("参数错误,当前无可用流程{}", bizId);
            return ResultUtils.success("finished");
        }
        for (FlowBizLog myLog : myLogs) {
            // 更新记录信息
            myLog.setNote(MyFlowNote.force_terminate.getValue());
            myLog.setStatus(MyFlowStatus.DONE.getValue());
            myLog.setTimeEnd(DateTimeUtils.now());
            myLog.setTimeCost(DateTimeUtils.timeCost(myLog.getTimeStart()));
            myLog.setResult(MyFlowResult.Deny.getValue());
        }
        this.flowBizLogDao.saveAll(myLogs);

        return ResultUtils.success("finished");
    }

    /**
     * 进入下一个节点
     *
     * @param bizLine
     * @return
     */
    private ResultData nextStepYes(FlowBizLine bizLine) {
        // 查询当前节点是否已全部同意
        Boolean waiting = this.flowBizLogDao.countByBizIdAndStepIdAndStatus(bizLine.getBizId(), bizLine.getStepId(),
                MyFlowStatus.START.getValue()) > 0;
        if (waiting) {
            log.info("需要等待其他人通过{}", bizLine.getBizId());
            return ResultUtils.success(bizLine);
        }
        return this.next(bizLine);
    }

    /**
     * 工作流下一步
     *
     * @param bizLine
     * @return
     */
    private ResultData next(FlowBizLine bizLine) {
        // 获取当前步骤
        FlowStep nowStep = this.flowStepDao.findById(bizLine.getStepId()).get();
        // 获取下一步
        FlowStep nextStep = this.flowStepDao.getFirstByLineIdAndLineIndexGreaterThanOrderByLineIndex(nowStep.getLineId(), nowStep.getLineIndex());

        if (Objects.isNull(nextStep)) {
            // 结束当前审批节点
            bizLine.setStatus(MyFlowStatus.DONE.getValue());
            bizLine.setTimeEnd(DateTimeUtils.now());
            // 记录到数据库
            this.flowBizLineDao.save(bizLine);
            // 记录到数据库
            log.info("流程结束{}", bizLine.getBizId());
            return ResultUtils.success(bizLine);
        }
        String nextStepId = nextStep.getStepId();
        bizLine.setStepId(nextStepId);
        // 记录到数据库
        this.flowBizLineDao.save(bizLine);
        this.generateLogs(bizLine);
        return ResultUtils.success(bizLine);
    }

    /**
     * 终止其他日志
     *
     * @param bizLine
     * @return
     */
    private ResultData nextStepNo(FlowBizLine bizLine, String note) {
        // 结束其他日志
        List<FlowBizLog> logs = this.flowBizLogDao.findByBizIdAndStepIdAndStatus(bizLine.getBizId(), bizLine.getStepId(),
                MyFlowStatus.START.getValue());
        for (FlowBizLog flowBizLog : logs) {
            // 标记为完事
            flowBizLog.setStatus(MyFlowStatus.DONE.getValue());
            flowBizLog.setResult(MyFlowResult.Deny.getValue());
            flowBizLog.setNote(MyFlowNote.terminate_by_others.getValue());
            flowBizLog.setTimeEnd(DateTimeUtils.now());
            flowBizLog.setTimeCost(DateTimeUtils.timeCost(flowBizLog.getTimeStart()));
        }
        this.flowBizLogDao.saveAll(logs);

        // 结束当前审批节点
        bizLine.setStatus(MyFlowStatus.DONE.getValue());
        bizLine.setTimeEnd(DateTimeUtils.now());
        this.flowBizLineDao.save(bizLine);
        // 流程终止
        return ResultUtils.success(bizLine);
    }

    /**
     * 给节点人员生成审批日志
     *
     * @param bizLine
     */
    private void generateLogs(FlowBizLine bizLine) {
        // 查找待处理的人元
        FlowStep step = this.flowStepDao.findById(bizLine.getStepId()).get();
        // 根据node查找待处理的人员
        List<FlowStepUser> userList = this.flowStepUserDao.findByStepId(step.getStepId());
        // 日志列表
        List<FlowBizLog> logList = Lists.newArrayList();
        for (FlowStepUser flowStepUser : userList) {
            FlowBizLog flowBizLog = FlowBizLog.builder()
                    .bizLogId(BatchUtils.getKey(MyKey.FlowBizLog))
                    .bizId(bizLine.getBizId())
                    .lineId(bizLine.getLineId())
                    .stepId(flowStepUser.getStepId())
                    .userIdFrom(UserUtils.getUserId())
                    .timeStart(DateTimeUtils.now())
                    .status(MyFlowStatus.START.getValue())
                    .userIdTo(flowStepUser.getUserId())
                    .build();
            logList.add(flowBizLog);
        }
        log.info("给节点人员生成审批日志");
        // 记录到数据库
        this.flowBizLogDao.saveAll(logList);
    }

    /**
     * 检查工作流是否合规
     *
     * @param lineId
     * @return
     */
    public ResultData checkFlow(String lineId) {
        if (StringUtils.isBlank(lineId)) {
            log.info("主键不能为空{}", lineId);
            return ResultUtils.fail("主键不能为空");
        }
        ResultData resultData = this.checkFlowStep(lineId);
        FlowLine entity = this.flowLineDao.findById(lineId).get();

        if (StringUtils.equals(resultData.getCode(), IStatusMessage.SystemStatus.ERROR.getCode())) {
            entity.setStatus(MyStatus.negative.getValue());
        } else {
            entity.setStatus(MyStatus.positive.getValue());
        }
        this.flowLineDao.save(entity);

        return resultData;
    }

    /**
     * 检查工作流是否合规
     *
     * @param lineId
     * @return
     */
    private ResultData checkFlowStep(String lineId) {
        log.info("check. param = {}", lineId);
        if (StringUtils.isBlank(lineId)) {
            log.info("主键不能为空{}", lineId);
            return ResultUtils.fail("主键不能为空");
        }

        List<FlowStep> stepList = this.flowStepDao.getByLineIdOrderByLineIndex(lineId);
        if (CollectionUtils.isEmpty(stepList)) {
            log.info("无可用步骤{}", lineId);
            return ResultUtils.fail("无可用步骤");
        }
        Boolean hasNoLineIndex = stepList.stream().filter(step -> Objects.isNull(step.getLineIndex())).count() > 0;
        if (hasNoLineIndex) {
            log.info("00所有节点都需要有序号,当前没有{}", lineId);
            return ResultUtils.fail("所有节点都需要有序号,当前没有");
        }
        log.debug("01必须有一个序号为1的步骤");
        Boolean hasNoStepToStart = stepList.stream().filter(step -> step.getLineIndex() == 1).count() != 1;
        if (hasNoStepToStart) {
            log.info("01必须有一个序号为1的步骤,当前没有{}", lineId);
            return ResultUtils.fail("必须有一个序号为1的步骤,当前没有");
        }
        for (int i = 0; i < stepList.size() - 1; i++) {
            // 序号要求加1递增
            if (stepList.get(i).getLineIndex() + 1 != stepList.get(i + 1).getLineIndex()) {
                log.info("02序号必须依次加1,当前没有{}", lineId);
                return ResultUtils.fail("序号必须依次加1");
            }
        }
        // 审批人间检查
        List<String> ids = stepList.stream().map(FlowStep::getStepId).collect(Collectors.toList());
        List<FlowStepUser> userList = this.flowStepUserDao.findByStepIdIn(ids);
        if (CollectionUtils.isEmpty(userList)) {
            log.info("当前流程需要有审批人员,当前没有{}", lineId);
            return ResultUtils.fail("当前流程需要有审批人员");
        }
        Map<String, List<FlowStepUser>> groupMap = userList.stream().collect(Collectors.groupingBy(FlowStepUser::getStepId));
        for (FlowStep flowStep : stepList) {
            if (CollectionUtils.isEmpty(groupMap.get(flowStep.getStepId()))) {
                log.info("各个节点都需要有审批人员,当前没有{}", lineId);
                return ResultUtils.fail("各个节点都需要有审批人员");
            }
        }
        return ResultUtils.success(lineId);
    }

    /**
     * 禁用工作流
     *
     * @param stepId
     * @return
     */
    public ResultData disableFlowByStepId(String stepId) {
        log.info("disableFlow {}", stepId);
        if (StringUtils.isBlank(stepId)) {
            return ResultUtils.success("success");
        }
        FlowStep step = this.flowStepDao.findById(stepId).get();
        if (Objects.isNull(step)) {
            return ResultUtils.success("success");
        }
        FlowLine line = this.flowLineDao.findById(step.getLineId()).get();
        if (Objects.isNull(line)) {
            return ResultUtils.success("success");
        }
        // 禁用
        line.setStatus(MyStatus.negative.getValue());
        log.info("disableFlow {}", JSONObject.toJSONString(line));
        this.flowLineDao.save(line);
        return ResultUtils.success("success");
    }

    /**
     * 步骤是否已使用
     *
     * @param stepId
     * @return
     */
    public Boolean stepInUse(String stepId) {
        // 查找待处理的步骤
        FlowStep step = this.flowStepDao.findById(stepId).get();
        if (Objects.isNull(step)) {
            return false;
        }
        return this.flowBizLineDao.countByLineId(step.getLineId()) > 0;
    }

    /**
     * 按lineId删除记录
     *
     * @param lineId
     * @return
     */
    public ResultData deleteByLineId(String lineId) {
        if (StringUtils.isBlank(lineId)) {
            log.info("主键不能为空");
            return ResultUtils.fail("主键不能为空");
        }
        Boolean used = this.flowBizLineDao.countByLineId(lineId) > 0;
        if (used) {
            log.info("已使用,不可删除");
            return ResultUtils.fail("已使用,不可删除");
        }
        this.flowLineDao.deleteById(lineId);

        List<FlowStep> stepList = this.flowStepDao.getByLineIdOrderByLineIndex(lineId);
        if (CollectionUtils.isEmpty(stepList)) {
            return ResultUtils.success(lineId);
        }
        log.info("删除步骤{}", JSONObject.toJSONString(stepList));
        this.flowStepDao.deleteAll(stepList);
        List<String> stepIds = stepList.stream().map(FlowStep::getStepId).collect(Collectors.toList());
        List<FlowStepUser> stepUserList = this.flowStepUserDao.findByStepIdIn(stepIds);
        if (CollectionUtils.isEmpty(stepUserList)) {
            return ResultUtils.success(lineId);
        }
        log.info("删除步骤和人员{}", JSONObject.toJSONString(stepUserList));
        this.flowStepUserDao.deleteAll(stepUserList);
        return ResultUtils.success(lineId);
    }

    /**
     * 按stepId删除记录
     *
     * @param stepId
     * @return
     */
    public ResultData deleteByStepId(String stepId) {
        if (stepInUse(stepId)) {
            log.info("已使用,不可删除");
            return ResultUtils.fail("已使用,不可删除");
        }
        if (StringUtils.isBlank(stepId)) {
            log.info("主键不能为空");
            return ResultUtils.fail("主键不能为空");
        }
        log.info("禁用所在工作流{}", stepId);
        this.disableFlowByStepId(stepId);
        log.info("删除当前记录{}", stepId);
        this.flowStepDao.deleteById(stepId);
        List<FlowStepUser> stepUserList = this.flowStepUserDao.findByStepId(stepId);
        if (CollectionUtils.isEmpty(stepUserList)) {
            return ResultUtils.success(stepId);
        }
        log.info("删除步骤和人员{}", JSONObject.toJSONString(stepUserList));
        this.flowStepUserDao.deleteAll(stepUserList);
        return ResultUtils.success(stepId);
    }

    /**
     * 按stepUserId删除记录
     *
     * @param stepUserId
     * @return
     */
    public ResultData deleteByStepUserId(String stepUserId) {
        if (StringUtils.isBlank(stepUserId)) {
            log.info("主键不能为空");
            return ResultUtils.fail("主键不能为空");
        }
        Optional<FlowStepUser> flowStepUser = this.flowStepUserDao.findById(stepUserId);
        if (flowStepUser.isPresent()) {
            String stepId = flowStepUser.get().getStepId();
            if (this.stepInUse(stepId)) {
                log.info("已使用,不可删除");
                return ResultUtils.fail("已使用,不可删除");
            }
            log.info("禁用所在工作流{}", stepUserId);
            this.disableFlowByStepId(stepId);
            log.info("删除当前记录{}", stepUserId);
            this.flowStepUserDao.deleteById(stepUserId);
        }


        return ResultUtils.success(stepUserId);
    }

}
