package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author fangweihao
 * @since 2025/7/9 16:11
 */
@Data
public class PortalUpdateDTO {
    /**
     * 站点id(不显示)
     */
    @NotNull(message = "站点id不能为空")
    private Long portalId;

    /**
     * 站点标题（名称）
     */
    @NotNull(message = "站点标题不能为空")
    private String title;

    /**
     * 站点链接
     */
    private String portalUrl;

    /**
     * 是否公共共享，0：私有 1：公开
     */
    private Short publicFlag;

    /**
     * 站点二维码
     */
    private String portalQRcode;

    /**
     * 站点状态，0：正常
     */
    private Short status;

    /**
     * 站点信息描述
     */
    @Size(max = 254, message = "站点信息描述不能超过254个字")
    private String description;

    /**
     * 访问方式
     */
    private String accessType;

    /**
     * 创建者（不可修改）
     */
    private String createBy;

    /**
     * 创建时间（不可修改）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（不可修改）
     */
    private LocalDateTime updateTime;

    /**
     * 站点logo
     */
    private String portalLogo;

    /**
     * 标签id集合
     */
    @NotEmpty(message = "标签id集合不能为空")
    private Set<Long> tagIdSet;

}
