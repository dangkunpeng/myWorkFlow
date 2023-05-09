package dang.kp.manager.biz.k12.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.biz.dict.api.DictApi;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.biz.k12.dao.K12ClassDao;
import dang.kp.manager.biz.k12.dao.K12LogDao;
import dang.kp.manager.biz.k12.pojo.K12Class;
import dang.kp.manager.common.myenum.MyDictType;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/k12Class")
public class K12ClassController {
    @Resource
    private K12ClassDao k12ClassDao;
    @Resource
    private K12LogDao k12LogDao;
    @Resource
    private DictApi dictApi;

    /**
     * 跳转到页面
     *
     * @return
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("初始化页面");
        model.put("timestamp", DateTimeUtils.getHMSDay());
        DictItem defaultItem = DictItem.builder().value(null).text("请选择").build();
        List<DictItem> classTypes = Lists.newArrayList(defaultItem);
        classTypes.addAll(this.dictApi.getDict(MyDictType.k12ClassType));
        model.put("classTypes", classTypes);

        List<DictItem> teachers = Lists.newArrayList(defaultItem);
        teachers.addAll(this.dictApi.getDict(MyDictType.k12Teacher));
        model.put("teachers", teachers);

        List<DictItem> weeks = Lists.newArrayList(defaultItem);
        weeks.addAll(this.dictApi.getDict(MyDictType.week));
        model.put("weeks", weeks);
        return "/biz/k12/k12Class";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@RequestBody K12Class param) {
        log.info("save. param = {}", JSONObject.toJSONString(param));
        if (StringUtils.isBlank(param.getClassId())) {
            param.setClassId(BatchUtils.getKey(MyKey.K12Class));
        }
        this.k12ClassDao.save(param);
        return ResultUtils.success(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(@RequestBody K12Class param) {
        log.info("delete. param = {}", JSONObject.toJSONString(param));
        if (this.k12LogDao.existsByClassId(param.getClassId())) {
            return ResultUtils.fail("using");
        }
        this.k12ClassDao.deleteById(param.getClassId());
        return ResultUtils.success("success");
    }

    /**
     * 功能描述: 获取列表
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/21 14:29
     */
    @PostMapping(value = "/pageList")
    @ResponseBody
    public PageData pageList(@RequestParam("pageNum") Integer pageNum,
                             @RequestParam("pageSize") Integer pageSize, K12Class param) {
        log.info("获取列表param{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("teacherId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("classWeek", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("classTypeId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("className", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getTeacherId())) {
            param.setTeacherId(null);
        }
        if (StringUtils.isBlank(param.getClassTypeId())) {
            param.setClassTypeId(null);
        }
        if (StringUtils.isBlank(param.getClassWeek())) {
            param.setClassWeek(null);
        }
        if (StringUtils.isBlank(param.getClassName())) {
            param.setClassName(null);
        }
        // 查询模板
        Example<K12Class> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("classWeek"), Sort.Order.asc("classTime"), Sort.Order.asc("className"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<K12Class> pageResult = this.k12ClassDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }
}
