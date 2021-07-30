package dang.kp.manager.biz.dict.dao;

import dang.kp.manager.biz.dict.pojo.DictType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictTypeDao extends JpaRepository<DictType, String> {
    DictType findByTypeName(String typeName);
}
