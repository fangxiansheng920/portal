package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/28 10:19
 */
@Data
public class TagDeleteByIdDTO {
    /**
     * 标签id
     */
    @NotNull(message = "标签id不能为空")
    private Long tagId;
}
