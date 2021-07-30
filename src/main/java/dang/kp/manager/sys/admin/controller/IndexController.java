package dang.kp.manager.sys.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title: LoginController
 * @Description:
 * @author: youqing
 * @version: 1.0
 * @date: 2018/11/20 11:39
 */
@Slf4j
@Controller
public class IndexController {
    @RequestMapping("login")
    public String tologin() {
        log.info("定向登陆页");
        return "login";
    }

    @RequestMapping("home")
    public String home() {
        log.info("定向主页");
        return "home";
    }

    @RequestMapping("logout")
    public String logout() {
        log.info("退出系统");
        Subject subject = SecurityUtils.getSubject();
        subject.logout(); // shiro底层删除session的会话信息
        return "redirect:login";
    }
}
