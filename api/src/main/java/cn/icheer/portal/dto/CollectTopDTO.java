package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/29 17:44
 */
@Data
public class CollectTopDTO {
    /**
     * 站点id
     */
    @NotNull(message = "站点id不能为空")
    private Long portalId;

    /**
     * 是否置顶 1：是  0：否
     */
    @NotNull(message = "是否置顶不能为空")
    private Short isTop;

    public CollectTopDTO() {
    }

    public CollectTopDTO(Long portalId, Short isTop) {
        this.portalId = portalId;
        this.isTop = isTop;
    }
}
