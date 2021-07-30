package dang.kp.manager.biz.flow.dao;

import dang.kp.manager.biz.flow.pojo.FlowBizLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowBizLogDao extends JpaRepository<FlowBizLog, String> {

    FlowBizLog findFirstByBizIdAndStepIdAndUserIdToAndStatus(String bizId, String stepId, String userTo, String status);

    Integer countByBizIdAndStepIdAndStatus(String bizId, String stepId, String status);

    List<FlowBizLog> findByBizIdAndStepIdAndStatus(String bizId, String stepId, String status);

    List<FlowBizLog> findByBizIdAndLineIdAndStatus(String bizId, String lineId, String status);

    List<FlowBizLog> findByStepId(String stepId);

    Boolean existsByStepId(String stepId);
}
