package dang.kp.manager.sys.admin.dao;

import dang.kp.manager.sys.admin.pojo.BaseUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseUserRoleDao extends JpaRepository<BaseUserRole, String> {
    List<BaseUserRole> findByUserId(String userId);

    List<BaseUserRole> findByRoleId(String roleId);

    void deleteByUserId(String userId);
}
