package dang.kp.manager.biz.flow.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class FlowBizLog implements Serializable {
    @Id
    private String bizLogId;
    @Column
    private String bizId;
    @Column
    private String lineId;
    @Column
    private String stepId;
    @Column
    private String timeStart;
    @Column
    private String timeEnd;
    @Column
    private Long timeCost;
    @Column
    private String userIdFrom;
    @Column
    private String userIdTo;
    @Column
    private String status;
    @Column
    private String result;
    @Column
    private String note;
}
