package cn.icheer.portal.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fangweihao
 * @since 2025/7/28 15:14
 */
@Data
public class Dic {
    /**
     * 字典id
     */
    @NotNull(message = "字典id不能为空")
    private Integer dicId;

    /**
     * 字典名称
     */
    @NotNull(message = "字典名称不能为空")
    private String dicName;

    /**
     * 字典键，如：1
     */
    @NotNull(message = "字典键不能为空")
    private String dicKey;

    /**
     * 字典含义，如：未激活
     */
    @NotNull(message = "字典含义不能为空")
    private String dicValue;

    /**
     * 变量名称
     */
    @NotNull(message = "变量名称不能为空")
    private String dicCode;

    /**
     * 颜色
     */
    private String color;

    /**
     * 排序
     */
    private Integer orderNumber;

    /**
     * 备注
     */
    private String remark;
}
