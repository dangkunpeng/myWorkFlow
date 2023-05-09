package dang.kp.manager.sys.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import dang.kp.manager.common.utils.MyConstants;
import dang.kp.manager.sys.admin.dao.BaseRoleSourceDao;
import dang.kp.manager.sys.admin.dao.BaseSourceDao;
import dang.kp.manager.sys.admin.pojo.BaseRoleSource;
import dang.kp.manager.sys.admin.pojo.BaseSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: SourceController
 * @Description: 资源管理
 * @author: dangkp
 * @version: 1.0
 * @date: 2018/11/29 18:16
 */
@Slf4j
@Controller
@RequestMapping("/source")
public class BaseSourceController {
    @Resource
    private BaseSourceDao baseSourceDao;
    @Resource
    private BaseRoleSourceDao baseRoleSourceDao;

    /**
     * 功能描述: 跳到管理
     *
     * @param:
     * @return:
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("进入管理");
        List<BaseSource> parentList = Lists.newArrayList(BaseSource.builder().sourceId(null).sourceName("请选择").build(),
                BaseSource.builder().sourceId("0").sourceName("根目录").build());
        parentList.addAll(this.baseSourceDao.findByPidOrderByLineIndex("0"));
        model.addAttribute("sourceList", parentList);
        return "/sys/sourceManage";
    }

    /**
     * 功能描述: 获取菜单列表
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/30 10:30
     */
    @PostMapping("/sourceList")
    @ResponseBody
    public PageData page(@RequestParam("pageNum") Integer pageNum,
                         @RequestParam("pageSize") Integer pageSize, BaseSource param) {
        log.info("获取菜单列表");
        PageData pdr = new PageData();
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("pid", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnoreNullValues()
                .withMatcher("sourceName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getPid())) {
            param.setPid(null);
        }
        if (StringUtils.isBlank(param.getSourceName())) {
            param.setSourceName(null);
        }
        // 查询模板
        Example<BaseSource> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("pid"), Sort.Order.asc("lineIndex"));
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<BaseSource> pageResult = this.baseSourceDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    /**
     * 功能描述:设置[新增或更新]
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/30 9:42
     */
    @PostMapping("/setSource")
    @ResponseBody
    public ResultData save(@RequestBody BaseSource source) {
        log.info("设置[新增或更新]！source:" + source);
        if (StringUtils.isBlank(source.getSourceId())) {
            //新增
            source.setCreateTime(DateTimeUtils.now());
            source.setSourceId(BatchUtils.getKey(MyKey.BaseAdminSource));
            this.baseSourceDao.save(source);
            log.info("[新增]，结果=新增成功！");
            return ResultUtils.success("新增成功！");
        } else {
            //修改
            BaseSource old = this.baseSourceDao.findById(source.getSourceId()).get();
            BeanUtils.copyProperties(source, old, MyConstants.SYSTEM_FIELDS);
            this.baseSourceDao.save(old);
            log.info("[更新]，结果=更新成功！");
            return ResultUtils.success("更新成功！");
        }
    }

    /**
     * 功能描述: 删除菜单
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/30 12:02
     */
    @PostMapping("/del")
    @ResponseBody
    public ResultData del(@RequestBody BaseSource param) {
        log.info("删除菜单！param:{}", JSONObject.toJSONString(param));
        //删除服务类目类型
        // 删除菜单
        this.baseSourceDao.deleteById(param.getSourceId());
        // 删除关联数据
        List<BaseRoleSource> list = this.baseRoleSourceDao.findBySourceId(param.getSourceId());
        if (!CollectionUtils.isEmpty(list)) {
            this.baseRoleSourceDao.deleteInBatch(list);
        }
        List<BaseSource> children = this.baseSourceDao.findByPidOrderByLineIndex(param.getSourceId());
        if (!CollectionUtils.isEmpty(children)) {
            this.baseSourceDao.deleteInBatch(children);
        }
        log.info("删除成功");

        return ResultUtils.success("删除成功！");
    }


}
