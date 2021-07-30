package dang.kp.manager.common.response;

import org.springframework.data.domain.Page;

import java.util.Objects;

public class PageUtils {
    public static void defaultPage(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = 1;
        }
        pageNum -= 1;
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }
    }

    public static PageData getPage(Page<?> pageResult) {
        return PageData.builder()
                .list(pageResult.getContent())
                .totals(pageResult.getTotalElements())
                .code(200)
                .build();
    }
}
