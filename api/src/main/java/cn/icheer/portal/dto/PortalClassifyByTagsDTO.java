package cn.icheer.portal.dto;

import cn.icheer.portal.entity.Tag;
import lombok.Data;

import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/23 10:32
 */
@Data
public class PortalClassifyByTagsDTO {
    /**
     * 标签
     */
    private Tag tag;

    /**
     * 站点总数
     */
    private Integer portalTotal;

    /**
     * 站点列表
     */
    private List<PortalWithCollectFlagDTO> portals;
}
