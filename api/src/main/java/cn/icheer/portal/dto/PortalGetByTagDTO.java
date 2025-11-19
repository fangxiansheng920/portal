package cn.icheer.portal.dto;

import cn.icheer.portal.constant.CollectConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/30 18:02
 */
@Data
public class PortalGetByTagDTO {
    /**
     * 标签id
     */
    @NotNull(message = "标签id不能为空")
    private Long tagId;

    /**
     * 当前页号
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    private Integer pageSize = 9;

    /**
     * 关键字搜索
     */
    private String keyword;

    /**
     * 筛选全部、已收藏、未收藏
     */
    private Short isCollect = CollectConstants.ALL;
}
