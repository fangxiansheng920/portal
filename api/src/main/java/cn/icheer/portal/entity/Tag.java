package cn.icheer.portal.entity;

import lombok.Data;

/**
 * @author fangweihao
 * @since 2025/7/3 9:12
 */
@Data
public class Tag {
    /**
     * 标签id
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签图标路径
     */
    private String icon;

    /**
     * 标签颜色
     */
    private String color;

    public Tag() {
    }

    public Tag(Long id, String name, String icon, String color) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.color = color;
    }
}
