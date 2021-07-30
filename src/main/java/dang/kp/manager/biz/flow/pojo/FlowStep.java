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
public class FlowStep implements Serializable {
    @Id
    private String stepId;
    @Column
    private String stepName;
    @Column
    private String lineId;
    @Column
    private Integer lineIndex;
}
