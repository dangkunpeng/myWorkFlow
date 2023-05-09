package dang.kp.manager.sys.init.detail.impl;

import com.google.common.collect.Lists;
import dang.kp.manager.biz.dict.dao.DictItemDao;
import dang.kp.manager.biz.dict.dao.DictTypeDao;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.biz.dict.pojo.DictType;
import dang.kp.manager.common.myenum.MyDictType;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.sys.init.detail.StartUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class StartUpTask implements StartUpService {
    @Resource
    private DictTypeDao dictTypeDao;

    @Resource
    private DictItemDao dictItemDao;

    @Override
    public ResultData run() {

        Boolean hasRecord = this.dictTypeDao.countByTypeName(MyDictType.dynaTask.getValue()) > 0;
        if (hasRecord) {
            return ResultUtils.success("exists");
        }
        log.info("初始化动态任务");
        DictType type01 = DictUtils.getType(MyDictType.dynaTask);
        DictItem item0101 = DictUtils.getItem(type01, 1, "0 0 0/2 * * ?", "deleteOldFiles");
        DictItem item0102 = DictUtils.getItem(type01, 2, "0 0 0/3 * * ?", "exchangeData");
        DictItem item0103 = DictUtils.getItem(type01, 3, "0 0 1   * * ?", "k12NewLog");
        this.dictTypeDao.saveAll(Lists.newArrayList(type01));
        this.dictItemDao.saveAll(Lists.newArrayList(item0101, item0102, item0103));
        return ResultUtils.success("done");
    }
}
