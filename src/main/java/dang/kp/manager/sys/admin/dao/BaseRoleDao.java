package dang.kp.manager.sys.admin.dao;

import dang.kp.manager.sys.admin.pojo.BaseRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseRoleDao extends JpaRepository<BaseRole, String> {
}