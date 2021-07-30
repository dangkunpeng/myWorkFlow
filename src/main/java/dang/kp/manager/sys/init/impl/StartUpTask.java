package dang.kp.manager.sys.init.impl;

import com.google.common.collect.Lists;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.sys.init.StartUpService;
import dang.kp.manager.sys.task.dao.MyDynaTaskDao;
import dang.kp.manager.sys.task.pojo.MyDynaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StartUpTask implements StartUpService {
    @Autowired
    private MyDynaTaskDao myDynaTaskDao;

    @Override
    public ResultData run() {

        Boolean hasRecord = this.myDynaTaskDao.count() > 0;
        if (hasRecord) {
            return ResultUtils.success("exists");
        }
        log.info("初始化任务");
        List<MyDynaTask> list = Lists.newArrayList();
        list.add(MyDynaTask.builder().taskClass("deleteOldFiles").taskTrigger("0 0 0/2 * * ?").build());
        list.add(MyDynaTask.builder().taskClass("exchangeData").taskTrigger("0 0 0/3 * * ?").build());
        this.myDynaTaskDao.saveAll(list);
        return ResultUtils.success("done");
    }
}
