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
@Table(name = "k12_student")
public class K12Student {
    @Id
    private String studentId;
    @Column
    private String studentName;
    @Column
    private String parentName;
    @Column
    private String parentPhone;
    @Column
    private Integer price;
    /**
     * 购买的产品
     */
    @Column
    private String productId;
    /**
     * 开始日期
     */
    @Column
    private String dateBegin;
    /**
     * 结束日期
     */
    @Column
    private String dateEnd;
    /**
     * 剩余课时
     */
    @Column
    private Integer classLast;
}
