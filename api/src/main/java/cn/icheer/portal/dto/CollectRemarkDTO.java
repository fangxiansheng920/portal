package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author fangweihao
 * @since 2025/8/4 16:37
 */
@Data
public class CollectRemarkDTO {

    /**
     * 站点ID
     */
    @NotNull(message = "站点id不能为空")
    private Long portalId;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;
}
