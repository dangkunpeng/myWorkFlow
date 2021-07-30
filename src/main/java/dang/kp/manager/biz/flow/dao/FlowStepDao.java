package dang.kp.manager.biz.flow.dao;

import dang.kp.manager.biz.flow.pojo.FlowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowStepDao extends JpaRepository<FlowStep, String> {

    List<FlowStep> getByStepIdIn(List<String> stepIds);

    List<FlowStep> getByLineIdOrderByLineIndex(String lineId);

    FlowStep getFirstByLineIdAndLineIndexGreaterThanOrderByLineIndex(String lineId, Integer lineIndex);
}
