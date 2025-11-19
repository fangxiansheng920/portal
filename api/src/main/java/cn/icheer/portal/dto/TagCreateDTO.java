package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author fangweihao
 * @since 2025/7/14 8:55
 */
@Data
public class TagCreateDTO {
    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String tagName;

    /**
     * 标签图标路径
     */
    private String icon;

    /**
     * 标签颜色
     */
    private String color;

}
