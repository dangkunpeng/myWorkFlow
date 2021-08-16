package dang.kp.manager.biz.dict.api;

import com.google.common.collect.Lists;
import dang.kp.manager.biz.dict.dao.DictItemDao;
import dang.kp.manager.biz.dict.dao.DictTypeDao;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.common.myenum.MyDictType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class DictApi {
    @Resource
    private DictItemDao dictItemDao;
    @Resource
    private DictTypeDao dictTypeDao;

    public List<DictItem> getDict(String typeName) {
        try {
            String typeId = this.dictTypeDao.findByTypeName(typeName).getTypeId();
            return this.dictItemDao.findByTypeId(typeId);
        } catch (Exception e) {
            log.error("{}未获取到字典项", typeName);
            return Lists.newArrayList();
        }
    }

    public List<DictItem> getDict(MyDictType type) {
        return getDict(type.getValue());
    }
}
