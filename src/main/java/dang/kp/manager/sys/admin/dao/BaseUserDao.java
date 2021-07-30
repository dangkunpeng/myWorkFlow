package dang.kp.manager.sys.admin.dao;

import dang.kp.manager.sys.admin.pojo.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseUserDao extends JpaRepository<BaseUser, String> {
    BaseUser getUserByUserName(String userName);
}

