package dang.kp.manager.biz.k12.dao;

import dang.kp.manager.biz.k12.pojo.K12Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface K12StudentDao extends JpaRepository<K12Student, String> {

    List<K12Student> getByClassLastIsGreaterThan(Integer classLast);
}
