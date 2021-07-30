package dang.kp.manager.common.utils;

import dang.kp.manager.sys.admin.pojo.BaseUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;

@Slf4j
public class UserUtils {

    public static BaseUser getUser() {
        try {
            return (BaseUser) SecurityUtils.getSubject().getPrincipal();
        } catch (Exception e) {
            log.info("无用户信息");
            return new BaseUser();
        }
    }

    public static String getUserId() {
        return getUser().getUserId();
    }
}
