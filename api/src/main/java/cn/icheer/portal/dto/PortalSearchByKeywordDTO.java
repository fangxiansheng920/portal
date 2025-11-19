package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/11 14:04
 */
@Data
public class PortalSearchByKeywordDTO {
    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 每页大小
     */
    private Integer pageSize = 9;

    /**
     * 当前页码
     */
    private Integer pageCurrent = 1;

    /**
     * 1：所有站点
     * 2：我的收藏
     * 3：我的创建
     * 4：站点管理
     * 5：大搜索
     */
    @NotNull(message = "pageIndex不能为空")
    private Integer pageIndex;

    public PortalSearchByKeywordDTO(String keyword, Integer pageSize, Integer pageCurrent, Integer pageIndex) {
        this.keyword = keyword;
        this.pageSize = pageSize;
        this.pageCurrent = pageCurrent;
        this.pageIndex = pageIndex;
    }
}
