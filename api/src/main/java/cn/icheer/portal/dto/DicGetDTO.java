package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/28 15:37
 */
@Data
public class DicGetDTO {
    /**
     * 字典名称
     */
    @NotNull(message = "字典名称不能为空")
    private String dicName;

    /**
     * 字典键
     */
    @NotNull(message = "字典键不能为空")
    private String dicKey;

    public DicGetDTO() {
    }

    public DicGetDTO(String dicName, String dicKey) {
        this.dicName = dicName;
        this.dicKey = dicKey;
    }
}
