package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/8 10:37
 */
@Data
public class CollectPortalDTO {
    /**
     * 站点id
     */
    @NotNull(message = "站点id不能为空")
    private Long portalId;

    /**
     * 是否收藏 1：收藏 0：取消收藏
     */
    @NotNull(message = "是否收藏不能为空")
    private Short doCollect;

    public CollectPortalDTO() {
    }

    public CollectPortalDTO(Long portalId, Short doCollect) {
        this.portalId = portalId;
        this.doCollect = doCollect;
    }
}
