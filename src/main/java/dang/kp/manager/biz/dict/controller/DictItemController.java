package dang.kp.manager.biz.dict.controller;

import com.alibaba.fastjson.JSONObject;
import dang.kp.manager.biz.dict.dao.DictItemDao;
import dang.kp.manager.biz.dict.dao.DictTypeDao;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.biz.dict.pojo.DictType;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/dictItem")
public class DictItemController {
    @Resource
    private DictItemDao dictItemDao;
    @Resource
    private DictTypeDao dictTypeDao;

    /**
     * 跳转到数据字典列表页面
     *
     * @param model
     * @return
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("跳转到数据字典项列表页面");
        List<DictType> dictTypeList = dictTypeDao.findAll();
        model.put("dictTypeList", dictTypeList);
        return "/biz/dict/dictItemManage";
    }

    /**
     * 功能描述: 获取字典项列表
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/30 10:30
     */
    @PostMapping("/list")
    @ResponseBody
    public PageData page(@RequestParam("pageNum") Integer pageNum,
                         @RequestParam("pageSize") Integer pageSize,
                         DictItem dictItem) {
        log.info("获取字典项列表");
        Sort sort = Sort.by(Sort.Order.asc("typeId"), Sort.Order.asc("lineIndex"));
        if (Objects.isNull(pageNum)) {
            pageNum = 1;
        }
        pageNum -= 1;
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }
        if (StringUtils.isBlank(dictItem.getTypeId())) {
            dictItem.setTypeId(null);
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("typeId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnoreNullValues()
                .withMatcher("text", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        // 查询模板
        Example<DictItem> example = Example.of(dictItem, matcher);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Page<DictItem> pageResult = this.dictItemDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@RequestBody DictItem dictItem) {
        log.info("设置字典项[新增或更新]！DictItem:{}" + JSONObject.toJSONString(dictItem));
        if (StringUtils.isBlank(dictItem.getItemId())) {
            //新增权限
            dictItem.setItemId(BatchUtils.getKey(MyKey.DictItem));
        }
        this.dictItemDao.save(dictItem);
        return ResultUtils.success(dictItem);
    }

    @PostMapping("/del")
    @ResponseBody
    public ResultData del(@RequestBody DictItem dictItem) {
        log.info("设置字典项[删除]！DictItem:{}" + JSONObject.toJSONString(dictItem));
        if (StringUtils.isBlank(dictItem.getItemId())) {
            return ResultUtils.fail("参数异常");
        }
        this.dictItemDao.deleteById(dictItem.getItemId());
        return ResultUtils.success(dictItem);
    }
}
