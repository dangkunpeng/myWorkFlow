package dang.kp.manager.sys.init.detail.impl;

import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.biz.dict.pojo.DictType;
import dang.kp.manager.common.myenum.MyDictType;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.utils.BatchUtils;

public class DictUtils {

    public static DictType getType(MyDictType myDictType) {
        return DictType.builder()
                .typeId(BatchUtils.getKey(MyKey.DictType))
                .typeName(myDictType.getValue())
                .build();
    }

    public static DictItem getItem(DictType dictType, int index, String value, String text) {
        return DictItem.builder()
                .typeId(dictType.getTypeId())
                .lineIndex(index)
                .itemId(BatchUtils.getKey(MyKey.DictItem.getValue()))
                .value(value)
                .text(text)
                .build();
    }
}
