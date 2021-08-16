package dang.kp.manager.biz.k12.dao;

import dang.kp.manager.biz.k12.pojo.K12Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface K12ClassDao extends JpaRepository<K12Class, String> {
}
