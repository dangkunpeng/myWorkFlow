package dang.kp.manager.biz.dict.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class DictItem {
    @Id
    private String itemId;
    @Column
    private String value;
    @Column
    private String text;
    @Column
    private Integer lineIndex;
    @Column
    private String typeId;
    @Column
    private String note;
}
