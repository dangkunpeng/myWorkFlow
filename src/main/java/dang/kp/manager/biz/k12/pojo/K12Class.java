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
@Table(name = "k12_class")
public class K12Class {
    @Id
    private String classId;
    @Column
    private String className;
    @Column
    private String classTypeId;
    @Column
    private String teacherId;
    @Column
    private String classWeek;
    @Column
    private String classTime;
}
