package dang.kp.manager.biz.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowApiDto implements Serializable {
    private String lineId;
    private String bizId;
    private String note;
    private String result;

    private String lineIdNew;
}
