package dang.kp.manager.biz.k12.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.biz.dict.api.DictApi;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.biz.k12.api.K12Api;
import dang.kp.manager.biz.k12.dao.K12ClassDao;
import dang.kp.manager.biz.k12.dao.K12LogDao;
import dang.kp.manager.biz.k12.dao.K12StudentDao;
import dang.kp.manager.biz.k12.pojo.K12Class;
import dang.kp.manager.biz.k12.pojo.K12Log;
import dang.kp.manager.biz.k12.pojo.K12Student;
import dang.kp.manager.common.myenum.MyDictType;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/k12Log")
public class K12LogController {
    @Resource
    private DictApi dictApi;
    @Resource
    private K12Api k12Api;
    @Resource
    private K12LogDao k12LogDao;
    @Resource
    private K12ClassDao k12ClassDao;
    @Resource
    private K12StudentDao k12StudentDao;

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
        List<DictItem> students = Lists.newArrayList(defaultItem);

        List<K12Student> studentList = this.k12StudentDao.findAll();
        for (K12Student k12Student : studentList) {
            StringBuilder text = new StringBuilder();
            text.append(k12Student.getStudentName());
            text.append(" / ");
            text.append(k12Student.getParentPhone());
            text.append(" / ");
            text.append(k12Student.getParentName());

            students.add(DictItem.builder().value(k12Student.getStudentId()).text(text.toString()).build());
        }
        model.put("students", students);
        // 取出周期
        List<DictItem> weeks = this.dictApi.getDict(MyDictType.week);
        Map<String, List<DictItem>> weekMap = weeks.stream().collect(Collectors.groupingBy(DictItem::getValue));

        List<DictItem> teachers = this.dictApi.getDict(MyDictType.k12Teacher);
        Map<String, List<DictItem>> teacherMap = teachers.stream().collect(Collectors.groupingBy(DictItem::getValue));

        List<DictItem> classTypes = this.dictApi.getDict(MyDictType.k12ClassType);
        Map<String, List<DictItem>> classTypeMap = classTypes.stream().collect(Collectors.groupingBy(DictItem::getValue));

        List<DictItem> classes = Lists.newArrayList(defaultItem);
        List<K12Class> classList = this.k12ClassDao.findAll();
        for (K12Class k12Class : classList) {
            String weekName = weekMap.get(k12Class.getClassWeek()).get(0).getText();
            String teacherName = teacherMap.get(k12Class.getTeacherId()).get(0).getText();
            String classType = classTypeMap.get(k12Class.getClassTypeId()).get(0).getText();
            StringBuilder text = new StringBuilder();
            text.append(classType);
            text.append(" / ");
            text.append(teacherName);
            text.append(" / ");
            text.append(weekName);
            text.append(" / ");
            text.append(k12Class.getClassTime());
            text.append(" / ");
            text.append(k12Class.getClassName());

            classes.add(DictItem.builder().value(k12Class.getClassId()).text(text.toString()).build());
        }
        model.put("classes", classes);

        return "/biz/k12/k12log";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@RequestBody K12Log param) {
        log.info("save. param = {}", JSONObject.toJSONString(param));
        return this.k12Api.newLog(param);
    }

    @PostMapping("/mark")
    @ResponseBody
    public ResultData mark(@RequestBody K12Log param) {
        log.info("mark. param = {}", JSONObject.toJSONString(param));
        K12Log entity = this.k12LogDao.findById(param.getLogId()).get();
        if (entity.getAttended()) {
            return ResultUtils.success("success");
        }
        K12Student student = this.k12StudentDao.findById(entity.getStudentId()).get();
        if (student.getClassLast() == 0) {
            return ResultUtils.fail("签到失败,课时已用完");
        } else if (StringUtils.compare(student.getDateEnd(), DateTimeUtils.now()) < 0) {
            return ResultUtils.fail("签到失败,账户已过期");
        }
        entity.setAttended(true);
        entity.setDateUpdate(DateTimeUtils.now());
        this.k12LogDao.save(entity);
        log.info("更新剩余课程为{}", student.getClassLast() - 1);
        student.setClassLast(student.getClassLast() - 1);
        this.k12StudentDao.save(student);
        return ResultUtils.success("success");
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResultData delete(@PathVariable String id) {
        log.info("mark. param = {}", JSONObject.toJSONString(id));
        K12Log entity = this.k12LogDao.findById(id).get();
        if (entity.getAttended()) {
            return ResultUtils.success("fail");
        }
        this.k12LogDao.delete(entity);
        return ResultUtils.success("success");
    }

    /**
     * 功能描述: 获取列表
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/21 14:29
     */
    @PostMapping(value = "/pageList")
    @ResponseBody
    public PageData pageList(@RequestParam("pageNum") Integer pageNum,
                             @RequestParam("pageSize") Integer pageSize, K12Log param) {
        log.info("获取列表param{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("studentId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("classId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getStudentId())) {
            param.setStudentId(null);
        }
        if (StringUtils.isBlank(param.getClassId())) {
            param.setClassId(null);
        }
        // 查询模板
        Example<K12Log> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("attended"), Sort.Order.desc("dateUpdate"), Sort.Order.desc("dateCreate"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<K12Log> pageResult = this.k12LogDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }
}
