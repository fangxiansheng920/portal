package cn.icheer.portal.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/28 10:13
 */
@Data
public class TagModifyDTO {
    /**
     * 标签ID
     */
    @NotNull(message = "标签id不能为空")
    private Long id;

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
