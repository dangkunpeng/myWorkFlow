package dang.kp.manager.sys.admin.dao;

import dang.kp.manager.sys.admin.pojo.BaseRoleSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseRoleSourceDao extends JpaRepository<BaseRoleSource, String> {
    List<BaseRoleSource> findByRoleId(String roleId);
    List<BaseRoleSource> findByRoleIdIn(List<String> roleIds);

    List<BaseRoleSource> findBySourceId(String sourceId);
}