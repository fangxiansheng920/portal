package cn.icheer.portal.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author fangweihao
 * @since 2025/8/11 15:13
 */
@Data
public class PortalTag {
    /**
     * 主键id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 站点id
     */
    @NotNull(message = "站点id不能为空")
    private Long portalId;

    /**
     * 标签id
     */
    @NotNull(message = "标签id不能为空")
    private Long tagId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
