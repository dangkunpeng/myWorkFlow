package dang.kp.manager.biz.task.impl;

import dang.kp.manager.biz.dict.api.DictApi;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.biz.task.BaseTask;
import dang.kp.manager.common.myenum.MyDictType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class DeleteOldFiles implements BaseTask {
    @Resource
    private DictApi dictApi;

    @Override
    public void run() {
        List<DictItem> list = this.dictApi.getDict(MyDictType.dynaTask);
        if (CollectionUtils.isEmpty(list)) {
            log.info("no task");
            return;
        }
        list.forEach(item -> {
            log.info("service = {}, trigger = {}", item.getText(), item.getValue());
        });
    }
}
