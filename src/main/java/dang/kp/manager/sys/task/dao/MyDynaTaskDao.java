package dang.kp.manager.sys.task.dao;

import dang.kp.manager.sys.task.pojo.MyDynaTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyDynaTaskDao extends JpaRepository<MyDynaTask, String> {
}
