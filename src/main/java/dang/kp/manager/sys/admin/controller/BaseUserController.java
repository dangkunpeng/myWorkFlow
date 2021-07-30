package dang.kp.manager.sys.admin.controller;

import com.alibaba.fastjson.JSONObject;
import dang.kp.manager.biz.flow.pojo.FlowStepUser;
import dang.kp.manager.biz.flow.service.FlowApiService;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import dang.kp.manager.common.utils.DigestUtils;
import dang.kp.manager.sys.admin.dao.BaseRoleDao;
import dang.kp.manager.sys.admin.dao.BaseUserDao;
import dang.kp.manager.sys.admin.dao.BaseUserRoleDao;
import dang.kp.manager.sys.admin.dto.LoginDTO;
import dang.kp.manager.sys.admin.dto.ResetPswdDto;
import dang.kp.manager.sys.admin.pojo.BaseRole;
import dang.kp.manager.sys.admin.pojo.BaseUser;
import dang.kp.manager.sys.admin.pojo.BaseUserRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @Title: UserController
 * @Description: 系统用户管理
 * @author: youqing
 * @version: 1.0
 * @date: 2018/11/20 15:17
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class BaseUserController {
    @Autowired
    private BaseUserDao baseUserDao;
    @Autowired
    private BaseRoleDao baseRoleDao;

    @Autowired
    private BaseUserRoleDao baseUserRoleDao;
    @Autowired
    private FlowApiService flowApiService;
    /**
     * 功能描述: 登入系统
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/22 15:47
     */
    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, LoginDTO loginDTO, HttpSession session) {
        log.info("进行登陆{}", JSONObject.toJSONString(loginDTO));
        Map<String, Object> data = new HashMap();
        // 使用 shiro 进行登录
        Subject subject = SecurityUtils.getSubject();
        String userName = loginDTO.getUsername().trim();
        String password = loginDTO.getPassword().trim();
        String rememberMe = loginDTO.getRememberMe();
        String host = request.getRemoteAddr();
        //获取token
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password, host);
        // 设置 remenmberMe 的功能
        if (rememberMe != null && rememberMe.equals("on")) {
            token.setRememberMe(true);
        }
        try {
            subject.login(token);
            // 登录成功
            BaseUser user = (BaseUser) subject.getPrincipal();
            session.setAttribute("user", user.getUserName());
            data.put("code", 200);
            data.put("url", "/home");
            //data.put("message","登陆成功");
            log.info(user.getUserName() + "登陆成功");
        } catch (UnknownAccountException e) {
            data.put("code", 0);
            data.put("message", userName + "账号不存在");
            log.error(userName + "账号不存在");
            return data;
        } catch (DisabledAccountException e) {
            data.put("code", 0);
            data.put("message", userName + "账号异常");
            log.error(userName + "账号异常");
            return data;
        } catch (AuthenticationException e) {
            data.put("code", 0);
            data.put("message", userName + "密码错误");
            log.error(userName + "密码错误");
            return data;
        }
        return data;
    }

    /**
     * 功能描述: 修改密码
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/22 17:26
     */
    @RequestMapping("/setPswd")
    @ResponseBody
    public ResultData setPswd(ResetPswdDto param) {
        log.info("进行密码重置");
        if (!StringUtils.equals(param.getPswd(), param.getPwsdAgain())) {
            log.error("两次输入的密码不一致!");
            return ResultUtils.fail("两次输入的密码不一致!");
        }
        //获取当前登陆的用户信息
        BaseUser user = (BaseUser) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        String password = DigestUtils.Md5(userName, param.getPswd());
        user = this.baseUserDao.getUserByUserName(userName);
        user.setPassword(password);
        this.baseUserDao.save(user);
        return ResultUtils.fail("修改密码成功!");
    }

    /**
     * 功能描述: 跳到系统用户列表
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/21 13:50
     */
    @RequestMapping("/init")
    public String userManage(ModelMap model) {
        log.info("跳到系统用户列表");
        List<BaseRole> roleList = this.baseRoleDao.findAll();
        model.put("roleList", roleList);
        return "/sys/userManage";
    }

    /**
     * 功能描述: 分页查询用户列表
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/21 11:10
     */
    @RequestMapping(value = "/getUserList", method = RequestMethod.POST)
    @ResponseBody
    public PageData page(@RequestParam("pageNum") Integer pageNum,
                         @RequestParam("pageSize") Integer pageSize, BaseUser param) {
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        Sort sort = Sort.by(Sort.Order.asc("userName"), Sort.Order.asc("phone"));
        // 获取用户列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("userName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("phone", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        // 查询模板
        Example<BaseUser> example = Example.of(param, matcher);
        Page<BaseUser> pageResult = this.baseUserDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    /**
     * 功能描述: 新增和更新系统用户
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/22 10:14
     */
    @RequestMapping(value = "/setUser", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(BaseUser user) {
        log.info("设置用户[新增或更新]！user:" + user);
        if (StringUtils.isBlank(user.getUserId())) {
            if (StringUtils.length(user.getPhone()) != 11) {
                log.error("置用户[新增或更新]，结果=手机号位数不对！");
                return ResultUtils.fail("手机号位数不！");
            }
            String userName = user.getUserName();
            BaseUser old = this.baseUserDao.getUserByUserName(userName);
            if (Objects.nonNull(old)) {
                log.error("用户[新增]，结果=用户名已存在！");
                return ResultUtils.fail("用户名已存在！");
            }
            if (StringUtils.isBlank(user.getPassword())) {
                user.setPassword("123456");
                String password = DigestUtils.Md5(userName, user.getPassword());
                user.setPassword(password);
            }
            user.setCreateTime(DateTimeUtils.now());
            user.setUserId(BatchUtils.getKey(MyKey.BaseAdminUser));
            this.baseUserDao.save(user);
            log.info("用户[新增]，结果=新增成功！");
            return ResultUtils.success("新增成功！");
        } else {
            String userName = user.getUserName();
            BaseUser old = this.baseUserDao.getOne(user.getUserId());
            if (StringUtils.isBlank(user.getPassword())) {
                user.setPassword("123456");
                String password = DigestUtils.Md5(userName, user.getPassword());
                user.setPassword(password);
            }
            BeanUtils.copyProperties(user, old, "createTime");
            this.baseUserDao.save(old);
            log.info("用户[更新]，结果=更新成功！");
            return ResultUtils.success("更新成功！");
        }
    }

    /**
     * 功能描述: 删除/恢复 用户
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/22 11:59
     */
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResultData del(BaseUser param) {
        log.info("删除/恢复用户！param:{}", JSONObject.toJSONString(param));
        this.flowApiService.disableFlow(FlowStepUser.builder().stepId(param.getUserId()).build());
        this.baseUserDao.deleteById(param.getUserId());
        // 删除关联数据
        List<BaseUserRole> list = this.baseUserRoleDao.findByUserId(param.getUserId());
        if (!CollectionUtils.isEmpty(list)) {
            this.baseUserRoleDao.deleteInBatch(list);
        }
        return ResultUtils.success("操作成功！");
    }
}
