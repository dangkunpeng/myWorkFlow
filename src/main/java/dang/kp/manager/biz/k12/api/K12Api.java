package dang.kp.manager.biz.k12.api;

import com.alibaba.fastjson.JSONObject;
import dang.kp.manager.biz.k12.dao.K12ClassDao;
import dang.kp.manager.biz.k12.dao.K12LogDao;
import dang.kp.manager.biz.k12.dao.K12StudentDao;
import dang.kp.manager.biz.k12.pojo.K12Log;
import dang.kp.manager.biz.k12.pojo.K12Student;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class K12Api {
    @Resource
    private K12LogDao k12LogDao;
    @Resource
    private K12ClassDao k12ClassDao;
    @Resource
    private K12StudentDao k12StudentDao;

    public ResultData newLog(K12Log param) {
        log.info("save. param = {}", JSONObject.toJSONString(param));
        K12Student student = this.k12StudentDao.findById(param.getStudentId()).get();
        if (student.getClassLast() == 0) {
            log.info("课时已用完{}", JSONObject.toJSONString(student));
            return ResultUtils.fail("创建失败,课时已用完");
        } else if (StringUtils.compare(student.getDateEnd(), DateTimeUtils.now()) < 0) {
            log.info("账户已过期{}", JSONObject.toJSONString(student));
            return ResultUtils.fail("创建失败,账户已过期");
        }

        if (StringUtils.isBlank(param.getLogId())) {
            param.setLogId(BatchUtils.getKey(MyKey.K12Log));
        }
        param.setAttended(false);
        if (StringUtils.isBlank(param.getDateCreate())) {
            param.setDateCreate(DateTimeUtils.nowSimple());
        }
        this.k12LogDao.save(param);
        return ResultUtils.success(param);
    }

    /**
     * 创建新任务
     */
    public void newLog() {
        // 查询剩余课程大于0的学生
        List<K12Student> students = this.k12StudentDao.getByClassLastIsGreaterThan(0);
        if (CollectionUtils.isEmpty(students)) {
            return;
        }
        List<String> studentIds = students.stream().map(K12Student::getStudentId).collect(Collectors.toList());
        // 获取过去7天前的上课记录
        String lastSomeDay = DateTimeUtils.getLastSomeDay(7);
        List<K12Log> logs = this.k12LogDao.getByAttendedAndDateUpdateLikeAndStudentIdIn(true, lastSomeDay + "%", studentIds);
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        // 按学生和课程分组
        Map<String, List<K12Log>> logMap = logs.stream().collect(Collectors.groupingBy(item -> item.getStudentId() + item.getClassId()));
        for (List<K12Log> value : logMap.values()) {
            K12Log k12Log = value.get(0);
            try {
                // 每个学生和课程都创建一个记录
                newLog(K12Log.builder().classId(k12Log.getClassId()).studentId(k12Log.getStudentId()).build());
            } catch (Exception e) {
                log.error("异常{}", e);
            }
        }
    }
}
