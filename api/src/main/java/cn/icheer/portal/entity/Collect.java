package cn.icheer.portal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fangweihao
 * @since 2025/7/3 9:13
 */
@Data
public class Collect {
    /**
     * 收藏id
     */
    private Long id;

    /**
     * 站点id
     */
    private Long portalId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否收藏 1：收藏 0：取消收藏
     */
    private Short doCollect;

    /**
     * 是否置顶 1：是 0：否
     */
    private Short isTop;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
