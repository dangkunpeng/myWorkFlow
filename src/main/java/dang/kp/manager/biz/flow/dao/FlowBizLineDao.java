package dang.kp.manager.biz.flow.dao;

import dang.kp.manager.biz.flow.pojo.FlowBizLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowBizLineDao extends JpaRepository<FlowBizLine, String> {

    FlowBizLine findByBizIdAndStatus(String bizId, String status);

    Integer countByLineId(String lineId);
}
