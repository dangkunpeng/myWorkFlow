package dang.kp.manager.biz.dict.dao;

import dang.kp.manager.biz.dict.pojo.DictItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictItemDao extends JpaRepository<DictItem, String> {
    List<DictItem> findByTypeId(String typeId);
}
