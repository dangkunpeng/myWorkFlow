package dang.kp.manager.sys.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Title: SourceDTO
 * @Description:
 * @author: youqing
 * @version: 1.0
 * @date: 2018/11/30 11:22
 */
@Data
public class SourceFirstDTO {
    List<SourceSecondDTO> childrens;
    private Integer sourceId;
    private String sourceName;
    private Integer pid;
    private String url;
    private String createTime;
    private Integer lineIndex;
}
