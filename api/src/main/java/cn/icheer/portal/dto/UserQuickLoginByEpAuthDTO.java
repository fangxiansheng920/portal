package cn.icheer.portal.dto;

import lombok.Data;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/4 12:16
 */
@Data
public class UserQuickLoginByEpAuthDTO {
    /**
     * 前端通过扫码二维码传来的code
     */
    @NotBlank
    private String code;
}
