package dang.kp.manager.sys.admin.pojo;

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
public class BaseUser {
    /**
     * ID
     */
    @Id
    private String userId;
    /**
     * 系统用户名称
     */
    @Column
    private String userName;
    /**
     * 系统用户密码
     */
    @Column
    private String password;
    /**
     * 手机号
     */
    @Column
    private String phone;
    /**
     * 登记时间
     */
    @Column
    private String createTime;
}