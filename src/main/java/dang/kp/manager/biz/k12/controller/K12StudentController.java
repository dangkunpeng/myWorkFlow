package dang.kp.manager.biz.k12.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import dang.kp.manager.biz.dict.api.DictApi;
import dang.kp.manager.biz.dict.dao.DictItemDao;
import dang.kp.manager.biz.dict.pojo.DictItem;
import dang.kp.manager.biz.k12.dao.K12LogDao;
import dang.kp.manager.biz.k12.dao.K12StudentDao;
import dang.kp.manager.biz.k12.pojo.K12Student;
import dang.kp.manager.common.myenum.MyDictType;
import dang.kp.manager.common.myenum.MyKey;
import dang.kp.manager.common.response.PageData;
import dang.kp.manager.common.response.PageUtils;
import dang.kp.manager.common.result.ResultData;
import dang.kp.manager.common.result.ResultUtils;
import dang.kp.manager.common.utils.BatchUtils;
import dang.kp.manager.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/k12Student")
public class K12StudentController {
    @Resource
    private K12StudentDao K12StudentDao;
    @Resource
    private K12LogDao k12LogDao;
    @Resource
    private DictItemDao dictItemDao;
    @Resource
    private DictApi dictApi;
    /**
     * 跳转到页面
     *
     * @return
     */
    @RequestMapping("/init")
    public String init(ModelMap model) {
        log.info("初始化页面");
        model.put("timestamp", DateTimeUtils.getHMSDay());
        DictItem defaultItem = DictItem.builder().value(null).text("请选择").build();
        List<DictItem> products = Lists.newArrayList(defaultItem);
        products.addAll(this.dictApi.getDict(MyDictType.k12Product));
        model.put("products", products);
        return "/biz/k12/K12Student";
    }
    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@RequestBody K12Student param) {
        log.info("save. param = {}", JSONObject.toJSONString(param));
        if (StringUtils.isBlank(param.getStudentId())) {
            param.setStudentId(BatchUtils.getKey(MyKey.K12Student));
            param.setDateBegin(DateTimeUtils.now());
            if (StringUtils.isNotBlank(param.getProductId())) {
                List<DictItem> items = this.dictApi.getDict(MyDictType.k12Product);
                DictItem item = items.stream().filter(row -> StringUtils.equals(row.getValue(), param.getProductId())).findFirst().get();
                param.setDateEnd(DateTimeUtils.getSomeYears(Integer.parseInt(item.getValue())));
            }
        }

        this.K12StudentDao.save(param);
        return ResultUtils.success(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(@RequestBody K12Student param) {
        log.info("delete. param = {}", JSONObject.toJSONString(param));
        if (this.k12LogDao.existsByClassId(param.getStudentId())) {
            return ResultUtils.fail("using");
        }
        this.K12StudentDao.deleteById(param.getStudentId());
        return ResultUtils.success("success");
    }
    /**
     * 功能描述: 获取列表
     *
     * @param:
     * @return:
     * @auther: youqing
     * @date: 2018/11/21 14:29
     */
    @PostMapping(value = "/pageList")
    @ResponseBody
    public PageData pageList(@RequestParam("pageNum") Integer pageNum,
                             @RequestParam("pageSize") Integer pageSize, K12Student param) {
        log.info("获取列表param{}", JSONObject.toJSONString(param));
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        // 匹配模式
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("studentName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("parentPhone", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreNullValues();
        if (StringUtils.isBlank(param.getStudentName())) {
            param.setStudentName(null);
        }
        if (StringUtils.isBlank(param.getParentPhone())) {
            param.setParentPhone(null);
        }
        // 查询模板
        Example<K12Student> example = Example.of(param, matcher);
        // 获取服务类目列表
        Sort sort = Sort.by(Sort.Order.asc("parentPhone"),Sort.Order.asc("dateEnd"));
        // 获取角色列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<K12Student> pageResult = this.K12StudentDao.findAll(example, pageable);
        return PageUtils.getPage(pageResult);
    }
}
