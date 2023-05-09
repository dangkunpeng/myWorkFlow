package dang.kp.manager.sys.admin.dao;

import dang.kp.manager.sys.admin.pojo.BaseSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseSourceDao extends JpaRepository<BaseSource, String> {
    List<BaseSource> findByPidOrderByLineIndex(String pid);

    List<BaseSource> findBySourceIdIn(List<String> sourceIds);

    List<BaseSource> findByPidInOrderByLineIndex(List<String> pids);
}