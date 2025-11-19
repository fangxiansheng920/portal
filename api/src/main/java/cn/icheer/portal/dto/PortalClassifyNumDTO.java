package cn.icheer.portal.dto;

import cn.icheer.portal.constant.CollectConstants;
import lombok.Data;

/**
 * @author fangweihao
 * @since 2025/7/30 15:12
 */
@Data
public class PortalClassifyNumDTO {
    /**
     * 分类展示需要的站点数量(默认20个)
     */
    private Integer num = 20;

    /**
     * 关键字搜索
     */
    private String keyword;

    /**
     * 筛选全部、已收藏、未收藏
     */
    private Short isCollect = CollectConstants.ALL;
}
