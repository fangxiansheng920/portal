package cn.icheer.portal.service;

import cn.icheer.portal.dto.TagCreateDTO;
import cn.icheer.portal.dto.TagDeleteByIdDTO;
import cn.icheer.portal.dto.TagModifyDTO;
import cn.icheer.portal.entity.Tag;

import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/11 13:11
 */
public interface TagService {
    /**
     * 创建(添加)标签
     * @param tagCreateDTO TagCreateDTO
     */
    Integer createTag(TagCreateDTO tagCreateDTO);

    /**
     * 获取所有标签
     * @return List<Tag>
     */
    List<Tag> getAllTags();

    /**
     * 修改标签
     * @param tagModifyDTO TagModifyDTO
     */
    int modifyTag(TagModifyDTO tagModifyDTO);

    /**
     * 删除标签
     * @param tagDeleteByIdDTO TagDeleteByIdDTO
     */
    int deleteTag(TagDeleteByIdDTO tagDeleteByIdDTO);

    /**
     * 判断标签是否存在
     * @param tagId Long
     * @return
     */
    Integer getIdIsExist(Long tagId);
}
