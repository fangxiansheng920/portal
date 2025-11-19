package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/28 16:48
 */
@Data
public class DicIdDTO {
    /**
     * 字典id
     */
    @NotNull(message = "字典id不能为空")
    private Integer dicId;
}
