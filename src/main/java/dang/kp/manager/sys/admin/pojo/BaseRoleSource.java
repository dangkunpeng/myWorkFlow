package dang.kp.manager.sys.admin.pojo;

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
public class BaseRoleSource implements Serializable {
    @Id
    private String roleSourceId;
    @Column
    private String sourceId;
    @Column
    private String roleId;
    @Column
    private String createTime;
}
