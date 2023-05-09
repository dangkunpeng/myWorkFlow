package dang.kp.manager.sys.admin.controller;

import com.alibaba.fastjson.JSONObject;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.MyConstants;
import dang.kp.manager.sys.admin.dao.BaseRoleDao;
import dang.kp.manager.sys.admin.dao.BaseRoleSourceDao;
import dang.kp.manager.sys.admin.dao.BaseUserRoleDao;
import dang.kp.manager.sys.admin.pojo.BaseRole;
import dang.kp.manager.sys.admin.pojo.BaseRoleSource;
import dang.kp.manager.sys.admin.pojo.BaseUserRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: RoleController
 * @Description: 角色管理
 * @author: dangkp
 * @version: 1.0
 * @date: 2018/11/21 13:43
 */
@Slf4j
@Controller
@RequestMapping("/role")
public class BaseRoleController {
    @Resource
    private BaseRoleDao baseRoleDao;
    @Resource
    private BaseUserRoleDao baseUserRoleDao;
    @Resource
    private BaseRoleSourceDao baseRoleSourceDao;

    /**
     * 跳转到管理页面
     *
     * @return
     */
    @RequestMapping("/init")
    public String init() {
        log.info("进入角色管理");
        return "/sys/roleManage";
    }

    /**
     * 功能描述: 获取列表
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/21 14:29
     */
    @PostMapping(value = "/getRoleList")
    @ResponseBody
    public PageData page(@RequestParam("pageNum") Integer pageNum,
                         @RequestParam("pageSize") Integer pageSize, BaseRole param) {
        log.info("获取角色列表");
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("roleName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getRoleName())) {
            param.setRoleName(null);
        }
        // 查询模板
        Example<BaseRole> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("roleName"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<BaseRole> pageResult = this.baseRoleDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    /**
     * 述: 设置角色[新增或更新]
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/12/3 10:54
     */
    @PostMapping("/setRole")
    @ResponseBody
    public ResultData save(@RequestBody BaseRole role) {
        log.info("设置角色[新增或更新]！role:" + role);
        if (StringUtils.isBlank(role.getRoleId())) {
            //新增角色
            role.setRoleId(BatchUtils.getKey(MyKey.BaseAdminRole));
            this.baseRoleDao.save(role);
            return ResultUtils.success("新增角色成功！");
        } else {
            //修改角色
            BaseRole old = this.baseRoleDao.findById(role.getRoleId()).get();
            BeanUtils.copyProperties(role, old, MyConstants.SYSTEM_FIELDS);
            this.baseRoleDao.save(old);
            log.info("角色[更新]，结果=更新成功！");
            return ResultUtils.success("更新成功！");
        }
    }

    /**
     * 功能描述: 删除/恢复角色
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/21 16:00
     */
    @PostMapping("/del")
    @ResponseBody
    public ResultData del(@RequestBody BaseRole param) {
        log.info("删除/恢复角色！param:{}", JSONObject.toJSONString(param));
        this.baseRoleDao.deleteById(param.getRoleId());
        // 删除关联数据
        List<BaseUserRole> list = this.baseUserRoleDao.findByRoleId(param.getRoleId());
        if (!CollectionUtils.isEmpty(list)) {
            this.baseUserRoleDao.deleteInBatch(list);
        }
        List<BaseRoleSource> list1 = this.baseRoleSourceDao.findByRoleId(param.getRoleId());
        if (!CollectionUtils.isEmpty(list1)) {
            this.baseRoleSourceDao.deleteInBatch(list1);
        }
        return ResultUtils.success("操作成功！");
    }
}
