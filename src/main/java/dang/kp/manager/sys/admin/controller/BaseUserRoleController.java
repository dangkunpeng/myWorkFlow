package dang.kp.manager.sys.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.sys.admin.dao.BaseRoleDao;
import dang.kp.manager.sys.admin.dao.BaseUserDao;
import dang.kp.manager.sys.admin.dao.BaseUserRoleDao;
import dang.kp.manager.sys.admin.pojo.BaseRole;
import dang.kp.manager.sys.admin.pojo.BaseUser;
import dang.kp.manager.sys.admin.pojo.BaseUserRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: SourceController
 * @Description: 资源管理
 * @author: youqing
 * @version: 1.0
 * @date: 2018/11/29 18:16
 */
@Slf4j
@Controller
@RequestMapping("/userRole")
public class BaseUserRoleController {
    @Resource
    private BaseRoleDao baseRoleDao;
    @Resource
    private BaseUserDao baseUserDao;
    @Resource
    private BaseUserRoleDao baseUserRoleDao;

    /**
     * 功能描述: 跳转
     *
     * @param:
     * @return:
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("进入权限管理");
        List<BaseRole> roleList = Lists.newArrayList(BaseRole.builder().roleId(null).roleName("请选择").build());
        Sort sort01 = Sort.by(Sort.Order.asc("roleId"));
        roleList.addAll(this.baseRoleDao.findAll(sort01));
        model.addAttribute("roleList", roleList);

        List<BaseUser> userList = Lists.newArrayList(BaseUser.builder().userId(null).userName("请选择").build());
        Sort sort02 = Sort.by(Sort.Order.asc("userId"));
        userList.addAll(this.baseUserDao.findAll(sort02));
        model.addAttribute("userList", userList);
        return "/sys/userRoleManage";
    }

    /**
     * 功能描述: 列表
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/30 10:30
     */
    @PostMapping("/userRoleList")
    @ResponseBody
    public PageData page(@RequestParam("pageNum") Integer pageNum,
                         @RequestParam("pageSize") Integer pageSize, BaseUserRole param) {
        log.info("获取列表param:{}", JSONObject.toJSONString(param));
        PageData pdr = new PageData();
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("roleId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnoreNullValues()
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getRoleId())) {
            param.setRoleId(null);
        }
        if (StringUtils.isBlank(param.getUserId())) {
            param.setUserId(null);
        }
        // 查询模板
        Example<BaseUserRole> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("userId"), Sort.Order.asc("roleId"));
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<BaseUserRole> pageResult = this.baseUserRoleDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    /**
     * 功能描述:设置权限[新增或更新]
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/30 9:42
     */
    @PostMapping("/setUserRole")
    @ResponseBody
    public ResultData save(@RequestBody BaseUserRole source) {
        log.info("设置权限[新增或更新]！param:{}", JSONObject.toJSONString(source));
        if (StringUtils.isBlank(source.getUserRoleId())) {
            //新增权限
            source.setUserRoleId(BatchUtils.getKey(MyKey.BaseAdminUserRole));
            this.baseUserRoleDao.save(source);
            log.info("结果=新增成功！");
            return ResultUtils.success("新增成功！");
        } else {
            //修改权限
            this.baseUserRoleDao.save(source);
            log.info("结果=更新成功！");
            return ResultUtils.success("更新成功！");
        }
    }

    /**
     * 功能描述: 删除
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/30 12:02
     */
    @PostMapping("/del")
    @ResponseBody
    public ResultData del(@RequestBody BaseUserRole source) {
        log.info("删除！param:{}", JSONObject.toJSONString(source));
        // 删除权限菜单
        this.baseUserRoleDao.deleteById(source.getUserRoleId());
        log.info("删除成功");
        return ResultUtils.success("删除成功！");
    }
}
