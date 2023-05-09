package dang.kp.manager.sys.admin.dto;

import lombok.Data;

/**
 * @Title: UserDTO
 * @Description:
 * @author: dangkp
 * @version: 1.0
 * @date: 2018/11/21 11:19
 */
@Data
public class LoginDTO {
    private String username;
    private String password;
    private String rememberMe;
}
