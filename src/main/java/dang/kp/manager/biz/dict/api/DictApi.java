package dang.kp.manager.biz.dict.api;

import com.google.common.collect.Lists;
import dang.kp.manager.biz.dict.dao.DictItemDao;
import dang.kp.manager.biz.dict.dao.DictTypeDao;
import dang.kp.manager.biz.dict.pojo.DictItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DictApi {
    @Autowired
    private DictItemDao dictItemDao;
    @Autowired
    private DictTypeDao dictTypeDao;

    public List<DictItem> getDict(String typeName) {

        try {
            String typeId = this.dictTypeDao.findByTypeName(typeName).getTypeId();
            return this.dictItemDao.findByTypeId(typeId);
        } catch (Exception e) {
            log.error("[{}]获取字典报错{}", typeName, e);
            return Lists.newArrayList();
        }
    }
}
