package cn.icheer.portal.entity;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fangweihao
 * @since 2025/7/3 9:17
 */
@Data
public class UserPortal {
    /**
     * 用户和站点关系id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 站点id
     */
    private Long portalId;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastUseAt;

    /**
     * 表示用户和站点的关系
     */
    private String permission;
}
