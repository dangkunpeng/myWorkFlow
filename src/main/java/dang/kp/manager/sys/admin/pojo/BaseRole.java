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
public class BaseRole {
    /**
     * 权限角色ID
     */
    @Id
    private String roleId;
    /**
     * 角色名称
     */
    @Column
    private String roleName;
}