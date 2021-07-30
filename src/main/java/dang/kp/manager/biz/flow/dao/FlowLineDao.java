package dang.kp.manager.biz.flow.dao;

import dang.kp.manager.biz.flow.pojo.FlowLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowLineDao extends JpaRepository<FlowLine, String> {

    List<FlowLine> getByLineIdIn(List<String> lineIds);
}
