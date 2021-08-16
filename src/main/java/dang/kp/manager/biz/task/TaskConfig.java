package dang.kp.manager.biz.task;

import dang.kp.manager.biz.dict.api.DictApi;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.common.myenum.MyDictType;
import dang.kp.manager.common.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
public class TaskConfig implements SchedulingConfigurer {

    @Resource
    private DictApi dictApi;
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        log.info("configureTasks");
        List<DictItem> list = this.dictApi.getDict(MyDictType.dynaTask);
        if (CollectionUtils.isEmpty(list)) {
            log.info("no task");
            return;
        }
        //获取单例applicationContext容器
        ApplicationContext application = SpringUtil.getApplicationContext();
        for (DictItem item : list) {
            taskRegistrar.addTriggerTask(task(application, item), getTrigger(item.getValue()));
        }
    }

    /**
     * 任务信息
     *
     * @param application
     * @param item
     * @return
     */
    private Runnable task(ApplicationContext application, DictItem item) {
        return () -> {
            // 业务逻辑
            try {
                log.info("Schedule class = {}", item.getText());
                BaseTask baseTask = (BaseTask) application.getBean(item.getText());
                baseTask.run();
            } catch (BeansException e) {
                log.info("{}.run has Exception {}", e);
            }
        };
    }

    /**
     * 触发条件
     *
     * @param cron
     * @return
     */
    private Trigger getTrigger(String cron) {
        return triggerContext -> {
            // 触发器
            //2.1 从数据库获取执行周期
            log.info("cron = {} is Scheduled", cron);
            //2.2 合法性校验.
            if (StringUtils.isEmpty(cron)) {
                log.info("TaskTrigger is blank", cron);
                return null;
            }
            //2.3 返回执行周期(Date)
            return new CronTrigger(cron).nextExecutionTime(triggerContext);
        };
    }
}
