package cn.icheer.portal.dto;

import lombok.Data;

/**
 * @author fangweihao
 * @since 2025/7/15 17:04
 */
@Data
public class PortalPageCurrentDTO {
    /**
     * 每页显示条数
     */
    private Integer pageSize = 9;
    /**
     * 当前页号
     */
    private Integer pageCurrent = 1;
}
