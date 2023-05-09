package dang.kp.manager.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dang.kp.manager.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @Title: ShiroFilterUtils
 * @Description: shiro工具类
 * @author: dangkp
 * @version: 1.0
 * @date: 2018/11/23 9:54
 */
@Slf4j
public class ShiroFilterUtils {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 功能描述: 判断请求是否是ajax
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/23 9:57
     */
    public static boolean isAjax(ServletRequest request) {
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(header)) {
            log.info("shiro工具类【ShiroFilterUtils.isAjax】当前请求,为Ajax请求");
            return Boolean.TRUE;
        }
        log.debug("shiro工具类【ShiroFilterUtils.isAjax】当前请求,非Ajax请求");
        return Boolean.FALSE;
    }

    /**
     * 功能描述: response输出json
     *
     * @param:
     * @return:
     * @auther: dangkp
     * @date: 2018/11/23 9:58
     */
    public static void out(HttpServletResponse response, ResponseResult result) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");//设置编码
            response.setContentType("application/json");//设置返回类型
            out = response.getWriter();
            out.println(objectMapper.writeValueAsString(result));//输出
            log.info("用户在线数量限制【ShiroFilterUtils.out】响应json信息成功");
        } catch (Exception e) {
            log.error("用户在线数量限制【ShiroFilterUtils.out】响应json信息出错", e);
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
    }
}
