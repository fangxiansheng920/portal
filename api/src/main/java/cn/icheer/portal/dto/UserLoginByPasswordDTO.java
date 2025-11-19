package cn.icheer.portal.dto;

import lombok.Data;
import lombok.NonNull;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;

/**
 * @author fangweihao
 * @since 2025/7/4 10:40
 */
@Data
public class UserLoginByPasswordDTO {
    /**
     * 用户手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
