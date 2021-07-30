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
public class BaseSource {
    @Id
    private String sourceId;
    /**
     * 菜单名称
     */
    @Column
    private String sourceName;
    /**
     * 父菜单id
     */
    @Column
    private String pid;
    /**
     * 菜单url
     */
    @Column
    private String url;
    /**
     * 顺序
     */
    @Column
    private Integer lineIndex;
    /**
     * 添加时间
     */
    @Column
    private String createTime;
}