package dang.kp.manager.biz.k12.pojo;

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
@Table(name = "k12_log")
public class K12Log {
    @Id
    private String logId;
    @Column
    private String studentId;
    @Column
    private String classId;
    @Column
    private String dateCreate;
    @Column
    private String dateUpdate;
    /**
     * 是否参加
     */
    @Column
    private Boolean attended;
}
