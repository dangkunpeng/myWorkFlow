package dang.kp.manager.sys.admin.controller;

import dang.kp.manager.sys.admin.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title: LoginController
 * @Description:
 * @author: dangkp
 * @version: 1.0
 * @date: 2018/11/20 11:39
 */
@Slf4j
@Controller
public class IndexController {

    @Value("${spring.profiles.active}")
    private String profiles;

    @RequestMapping("login")
    public String tologin(ModelMap model) {
        log.info("定向登陆页");
        LoginDTO loginDTO = new LoginDTO();
        if (StringUtils.contains(profiles,"dev")) {
            loginDTO.setPassword("root");
            loginDTO.setUsername("root");
            model.put("dev", loginDTO);
        }
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
