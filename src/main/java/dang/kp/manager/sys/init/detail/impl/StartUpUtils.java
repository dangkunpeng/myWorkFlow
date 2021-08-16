package dang.kp.manager.sys.init.detail.impl;

import dang.kp.manager.biz.flow.pojo.FlowStep;
import dang.kp.manager.biz.flow.pojo.FlowStepUser;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import dang.kp.manager.common.utils.DigestUtils;
import dang.kp.manager.sys.admin.pojo.*;

public class StartUpUtils {
    public static BaseSource getSource(Integer lineIndex, String pid, String name, String url) {
        return BaseSource.builder()
                .sourceId(BatchUtils.getKey(MyKey.BaseAdminSource))
                .sourceName(name)
                .pid(pid)
                .lineIndex(lineIndex)
                .url(url)
                .createTime(DateTimeUtils.now())
                .build();
    }

    public static BaseRole getRole(String roleName) {
        return BaseRole.builder()
                .roleId(BatchUtils.getKey(MyKey.BaseAdminRole))
                .roleName(roleName)
                .build();
    }

    public static BaseUser getUser(String userName, String password, String phone) {
        return BaseUser.builder()
                .userId(BatchUtils.getKey(MyKey.BaseAdminUser))
                .userName(userName)
                .password(DigestUtils.Md5(userName, password))
                .phone(phone)
                .createTime(DateTimeUtils.now())
                .build();
    }

    public static BaseRoleSource getRoleSource(String roleId, String sourceId) {
        return BaseRoleSource.builder()
                .roleId(roleId)
                .sourceId(sourceId)
                .roleSourceId(BatchUtils.getKey(MyKey.BaseAdminRoleSource))
                .createTime(DateTimeUtils.now())
                .build();
    }

    public static BaseUserRole getUserRole(String userId, String roleId) {
        return BaseUserRole.builder()
                .userId(userId)
                .roleId(roleId)
                .userRoleId(BatchUtils.getKey(MyKey.BaseAdminUserRole))
                .createTime(DateTimeUtils.now())
                .build();
    }

    public static FlowStep getFlowStep(String lineId, Integer lineIndex, String parentStepId, String stepName, Integer stepType) {
        return FlowStep.builder().
                lineId(lineId)
                .stepId(BatchUtils.getKey(MyKey.FlowStep))
                .lineIndex(lineIndex)
                .stepName(stepName)
                .build();
    }

    public static FlowStepUser getFlowStepUser(String stepId, String userId) {
        return FlowStepUser.builder()
                .stepUserId(BatchUtils.getKey(MyKey.FlowStepUser))
                .stepId(stepId)
                .userId(userId)
                .build();
    }
}
