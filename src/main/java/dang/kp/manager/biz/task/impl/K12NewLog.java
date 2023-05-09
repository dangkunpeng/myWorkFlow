package dang.kp.manager.biz.task.impl;

import dang.kp.manager.biz.k12.api.K12Api;
import dang.kp.manager.biz.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class K12NewLog implements BaseTask {
    @Resource
    private K12Api k12Api;

    @Override
    public void run() {
        log.info("定时生成任务");
        this.k12Api.newLog();
    }
}
