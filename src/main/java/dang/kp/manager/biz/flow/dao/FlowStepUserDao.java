package dang.kp.manager.biz.flow.dao;

import dang.kp.manager.biz.flow.pojo.FlowStepUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowStepUserDao extends JpaRepository<FlowStepUser, String> {

    List<FlowStepUser> findByStepId(String stepId);

    List<FlowStepUser> findByUserId(String userId);

    List<FlowStepUser> findByStepIdIn(List<String> stepIds);

}
