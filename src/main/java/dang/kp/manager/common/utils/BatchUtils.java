package dang.kp.manager.common.utils;

import com.google.common.collect.Maps;
import dang.kp.manager.common.myenum.MyKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class BatchUtils {
    public static final Integer FORMAT_LENGTH = 4;
    public static final Long kbSize = 1024L;
    public static final Long mbSize = 1024L * kbSize;
    public static final Long gbSize = 1024L * mbSize;
    // 计数器
    public static final Map<String, Integer> COUNT_MAP = Maps.newConcurrentMap();
    // 补位字符
    private static final String PAD_CHAR = "0";
    // 计数器补位长度
    private static final Integer COUNT_LENGTH = 4;

    public static String getKey(MyKey myKey) {
        return getKey(myKey.getValue());
    }

    /**
     * 生成主键
     *
     * @return
     */
    public static synchronized String getKey(String feature) {
        StringBuilder result = new StringBuilder();
        result.append(feature);
        // 时间戳
        result.append(DateTimeUtils.getHMSDay());
        // 获取时间戳的使用次数
        Integer counter = COUNT_MAP.get(result.toString());
        if (Objects.isNull(counter)) {
            // 重新开始计数
            counter = 1;
            // 重置MAP,避免
            if (COUNT_MAP.size() > 0 && COUNT_MAP.keySet().size() > 0) {
                for (String key : COUNT_MAP.keySet()) {
                    if (StringUtils.startsWith(key, feature)) {
                        COUNT_MAP.remove(key);
                    }
                }
            }
        } else {
            counter++;
        }
        // 使用次数添加到map
        COUNT_MAP.put(result.toString(), counter);
        // 拼接上序号
        result.append(StringUtils.leftPad(String.valueOf(counter), COUNT_LENGTH, PAD_CHAR));
        return result.toString();
    }

    public static void humanRead(Long fileSize) {
        String humanSize = humanReadStr(fileSize);
        if (StringUtils.isNotBlank(humanSize)) {
            log.info("{} B = {}", fileSize, humanSize);
        }
    }

    public static String humanReadStr(Long fileSize) {
        Integer length = 13;
        if (fileSize > gbSize) {
            length = 14;
        }
        StringBuffer humanSize = new StringBuffer();
        formatterMsg(humanSize, fileSize, mbSize, "MB,");
        fileSize = fileSize % mbSize;
        formatterMsg(humanSize, fileSize, kbSize, "KB");
        return StringUtils.leftPad(humanSize.toString(), length);
    }

    public static void formatterMsg(StringBuffer humanSize, Long fileSize, Long baseSize, String sizeType) {
        if (fileSize > baseSize) {
            humanSize.append(formatterSize(fileSize, baseSize));
            humanSize.append(sizeType);
        }
    }

    public static String formatterSize(Long size, Long base) {
        if (base == gbSize) {
            return StringUtils.leftPad(String.valueOf(size / base), 2);
        }
        return StringUtils.leftPad(String.valueOf(size / base), FORMAT_LENGTH);
    }

    public static void progress(Integer index, Integer total, String params) {
        Integer percent = total;
        if (percent > 100) {
            percent = percent / 5;
        }
        if (index > 0 && percent > 0 && index % percent == 0) {
            log.info("[{}] working on {} / {}", params, index, total);
        }
        if (index > total - 1) {
            log.info("[{}] finished", params);
        }
    }
}
