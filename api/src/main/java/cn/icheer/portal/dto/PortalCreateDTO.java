package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author fangweihao
 * @since 2025/7/8 16:57
 */
@Data
public class PortalCreateDTO {
    /**
     * 用于接收返回的站点id（sql语句中）
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 站点标题（名称）
     */
    @NotNull(message = "站点标题不能为空")
    private String title;

    /**
     * 站点链接
     */
    private String portalUrl = "";

    /**
     * 是否公共共享，0：私有 1：公开
     */
    private Short publicFlag;

    /**
     * 站点二维码
     */
    private String portalQRcode = "";

    /**
     * 站点状态，0：正常
     */
    private Short status;

    /**
     * 站点信息描述
     */
    @Size(max = 254, message = "站点信息描述不能超过254个字符")
    private String description;

    /**
     * 访问方式 PC端或者企微
     */
    private String accessType;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 站点logo
     */
    private String portalLogo;

    /**
     * 站点标签id集合
     */
    @NotEmpty(message = "站点标签id集合不能为空")
    private Set<Long> tagIdSet;

    /**
     * 是否被收藏，0：未收藏 1：已收藏
     */
    private Short collectFlag;
}
