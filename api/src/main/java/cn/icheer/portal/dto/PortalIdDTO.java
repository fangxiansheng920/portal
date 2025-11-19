package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/15 17:42
 */
@Data
public class PortalIdDTO {
    /**
     * 站点id
     */
    @NotNull(message = "站点id不能为空")
    private Long portalId;
}
