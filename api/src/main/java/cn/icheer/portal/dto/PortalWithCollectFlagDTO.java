package cn.icheer.portal.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fangweihao
 * @since 2025/7/22 17:06
 */
@Data
public class PortalWithCollectFlagDTO {
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
     * 标签id集合
     */
    private Set<Long> tagIdSet;

    /**
     * 是否被收藏，是：1 否：0
     */
    private Short collectFlag;

    /**
     * 是否置顶，是：1 否：0
     */
    private Short isTop;

    /**
     * 收藏备注
     */
    private String collectRemark;

    /**
     * 将查询到的结果（id字符串）进行处理，存入set中返回
     * @param tagIds 标签id字符串
     */
    public void setTagIds(String tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            this.tagIdSet = new HashSet<>();
            return;
        }
        this.tagIdSet = Arrays.stream(tagIds.split(",")).map(Long::parseLong).collect(Collectors.toSet());
    }
}
