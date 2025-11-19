package cn.icheer.portal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fangweihao
 * @since 2025/7/3 9:12
 */
@Data
public class Portal {
    /**
     * 站点id
     */
    private Long id;

    /**
     * 创建者的用户id
     */
    private Long userId;

    /**
     * 站点标题（名称）
     */
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
     * 站点二维码图
     */
    private String portalQRcode;

    /**
     * 站点状态 0：正常
     */
    private Short status;

    /**
     * 站点信息描述
     */
    private String description;

    /**
     * 访问方式 PC端或者企微
     */
    private String accessType;

    /**
     * 备注
     */
    private String remark;

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
     * 删除标志位
     */
    private Short deleted;

    /**
     * 站点logo
     */
    private String portalLogo;

    /**
     * 站点标签id
     */
    private Long tagId;
}
