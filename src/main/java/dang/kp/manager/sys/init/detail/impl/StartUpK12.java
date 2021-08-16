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
public class StartUpK12 implements StartUpService {

    @Resource
    private DictTypeDao dictTypeDao;

    @Resource
    private DictItemDao dictItemDao;

    @Override
    public ResultData run() {

        Boolean exists = this.dictTypeDao.countByTypeName(MyDictType.k12ClassType.getValue()) > 0;
        if (exists) {
            return ResultUtils.success("exists");
        }
        log.info("创建k12Class");
        DictType type01 = DictUtils.getType(MyDictType.k12ClassType);
        DictItem item0101 = DictUtils.getItem(type01, 1, "eduMusic", "音乐课");
        DictItem item0102 = DictUtils.getItem(type01, 2, "eduSport", "运动课");
        DictItem item0103 = DictUtils.getItem(type01, 3, "eduPaint", "绘画课");

        log.info("创建k12Teacher");
        DictType type02 = DictUtils.getType(MyDictType.k12Teacher);
        DictItem item0201 = DictUtils.getItem(type02, 1, "zhangsan", "张三");
        DictItem item0202 = DictUtils.getItem(type02, 2, "lisi", "李四");
        DictItem item0203 = DictUtils.getItem(type02, 3, "wanger", "王二麻子");

        log.info("创建k12Product");
        DictType type03 = DictUtils.getType(MyDictType.k12Product);
        DictItem item0301 = DictUtils.getItem(type03, 1, "1", "1年2万");
        DictItem item0302 = DictUtils.getItem(type03, 2, "2", "2年3万");
        DictItem item0303 = DictUtils.getItem(type03, 3, "3", "3年4万");
        DictItem item0304 = DictUtils.getItem(type03, 4, "4", "4年5万");
        DictItem item0305 = DictUtils.getItem(type03, 5, "5", "5年6万");
        DictItem item0306 = DictUtils.getItem(type03, 6, "6", "6年7万");

        log.info("创建week");
        DictType type04 = DictUtils.getType(MyDictType.week);
        DictItem item0401 = DictUtils.getItem(type04, 1, "1", "周一");
        DictItem item0402 = DictUtils.getItem(type04, 2, "2", "周二");
        DictItem item0403 = DictUtils.getItem(type04, 3, "3", "周三");
        DictItem item0404 = DictUtils.getItem(type04, 4, "4", "周四");
        DictItem item0405 = DictUtils.getItem(type04, 5, "5", "周五");
        DictItem item0406 = DictUtils.getItem(type04, 6, "6", "周六");
        DictItem item0407 = DictUtils.getItem(type04, 7, "7", "周七");

        this.dictTypeDao.saveAll(Lists.newArrayList(type01, type02, type03, type04));
        this.dictItemDao.saveAll(Lists.newArrayList(
                item0101, item0102, item0103,
                item0201, item0202, item0203,
                item0301, item0302, item0303, item0304, item0305, item0306,
                item0401, item0402, item0403, item0404, item0405, item0406, item0407
        ));

        return ResultUtils.success("create");
    }
}
