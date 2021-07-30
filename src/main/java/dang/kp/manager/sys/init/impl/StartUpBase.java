package dang.kp.manager.sys.init.impl;

import com.google.common.collect.Lists;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.MyConstants;
import dang.kp.manager.sys.admin.dao.*;
import dang.kp.manager.sys.admin.pojo.BaseRole;
import dang.kp.manager.sys.admin.pojo.BaseRoleSource;
import dang.kp.manager.sys.admin.pojo.BaseSource;
import dang.kp.manager.sys.admin.pojo.BaseUser;
import dang.kp.manager.sys.init.StartUpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StartUpBase implements StartUpService {
    @Autowired
    private BaseSourceDao baseSourceDao;
    @Autowired
    private BaseRoleDao baseRoleDao;
    @Autowired
    private BaseUserDao baseUserDao;
    @Autowired
    private BaseRoleSourceDao baseRoleSourceDao;
    @Autowired
    private BaseUserRoleDao baseUserRoleDao;

    /**
     * 初始用户信息
     */
    @Override
    public ResultData run() {
        Long count = this.baseSourceDao.count();
        if (count > 0) {
            // 已经有数据了不需要初始化
            return ResultUtils.success("用户权限基础数据已存在,不需要从0创建");
        }
        log.info("00用户权限基础数据不存在,从0创建");
        List<BaseSource> sourceList = Lists.newArrayList();
        log.info("01创建资源");
        BaseSource srcSys = StartUpUtils.getSource(1, MyConstants.ROOT_SOURCE_ID, "系统管理", "");
        BaseSource srcBase = StartUpUtils.getSource(2, MyConstants.ROOT_SOURCE_ID, "基本设置", "");
        BaseSource srcFlowInit = StartUpUtils.getSource(3, MyConstants.ROOT_SOURCE_ID, "工作流管理", "");
        BaseSource srcFlowQuery = StartUpUtils.getSource(4, MyConstants.ROOT_SOURCE_ID, "工作流使用", "");
        sourceList.add(srcSys);
        sourceList.add(srcBase);
        sourceList.add(srcFlowInit);
        sourceList.add(srcFlowQuery);
        sourceList.add(StartUpUtils.getSource(1, srcSys.getSourceId(), "资源管理", "/source/init"));
        sourceList.add(StartUpUtils.getSource(2, srcSys.getSourceId(), "角色管理", "/role/init"));
        sourceList.add(StartUpUtils.getSource(3, srcSys.getSourceId(), "用户管理", "/user/init"));
        sourceList.add(StartUpUtils.getSource(4, srcSys.getSourceId(), "角色资源", "/roleSource/init"));
        sourceList.add(StartUpUtils.getSource(5, srcSys.getSourceId(), "用户角色", "/userRole/init"));
        sourceList.add(StartUpUtils.getSource(1, srcBase.getSourceId(), "字典大项", "/dictType/init"));
        sourceList.add(StartUpUtils.getSource(2, srcBase.getSourceId(), "字典小项", "/dictItem/init"));
        sourceList.add(StartUpUtils.getSource(1, srcFlowInit.getSourceId(), "工作流编辑", "/flowLine/init"));
        sourceList.add(StartUpUtils.getSource(2, srcFlowInit.getSourceId(), "工作流步骤", "/flowStep/init"));
        sourceList.add(StartUpUtils.getSource(3, srcFlowInit.getSourceId(), "步骤和用户", "/flowStepUser/init"));
        sourceList.add(StartUpUtils.getSource(1, srcFlowQuery.getSourceId(), "工作流配置", "/flowApi/init"));
        this.baseSourceDao.saveAll(sourceList);
        log.info("02创建角色");
        BaseRole adminRole = StartUpUtils.getRole("超级管理员");
        this.baseRoleDao.save(adminRole);
        log.info("03创建用户");
        BaseUser adminUser = StartUpUtils.getUser("root", "root", "18698683698");
        this.baseUserDao.save(adminUser);
        log.info("04关联角色和资源");
        List<BaseRoleSource> roleSourceList = Lists.newArrayList();
        for (BaseSource baseSource : sourceList) {
            if (StringUtils.equals(baseSource.getPid(), MyConstants.ROOT_SOURCE_ID)) {
                roleSourceList.add(StartUpUtils.getRoleSource(adminRole.getRoleId(), baseSource.getSourceId()));
            }
        }
        this.baseRoleSourceDao.saveAll(roleSourceList);
        log.info("05关联用户和角色");
        this.baseUserRoleDao.save(StartUpUtils.getUserRole(adminUser.getUserId(), adminRole.getRoleId()));
        return ResultUtils.success("用户权限基础数据不存在,从0创建");
    }

}
