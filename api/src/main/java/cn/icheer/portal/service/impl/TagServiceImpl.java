package cn.icheer.portal.service.impl;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.dto.TagCreateDTO;
import cn.icheer.portal.dto.TagDeleteByIdDTO;
import cn.icheer.portal.dto.TagModifyDTO;
import cn.icheer.portal.entity.Tag;
import cn.icheer.portal.mapper.TagMapper;
import cn.icheer.portal.service.PortalTagService;
import cn.icheer.portal.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/11 13:12
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {
    @Resource
    private TagMapper tagMapper;

    @Resource
    private PortalTagService portalTagService;

    /**
     * 创建(添加)标签
     * @param tagCreateDTO TagCreateDTO
     */
    @Override
    public Integer createTag(TagCreateDTO tagCreateDTO) {
        Integer flag = tagMapper.createTag(tagCreateDTO);
        if (flag == 1) {
            log.debug("Create Tag success : {} .", tagCreateDTO.getTagName());
            return Constants.OPERATION_SUCCESS;
        } else {
            log.debug("Create Tag failed : {} .", tagCreateDTO.getTagName());
            return Constants.OPERATION_FAILED;
        }
    }

    /**
     * 获取所有标签
     * @return List<Tag>
     */
    @Override
    public List<Tag> getAllTags() {
        List<Tag> tags = tagMapper.getAllTags();
        log.debug("Get all tags success.");
        return tags;
    }

    /**
     * 修改标签
     * @param tagModifyDTO TagModifyDTO
     */
    @Override
    public int modifyTag(TagModifyDTO tagModifyDTO) {
        // 获取原标签
        Tag tag = tagMapper.getTagById(tagModifyDTO.getId());
        // 如果修改标签名字，需判断一下修改后的名字存不存在，如果存在则不允许修改
        if (!tag.getName().equals(tagModifyDTO.getTagName())) {
            if (tagMapper.getTagByName(tagModifyDTO.getTagName()) != null) {
                log.error("Modify Tag failed, Tag already exist : {} .", tagModifyDTO.getTagName());
                return Constants.TAG_EXIST;
            }
        }
        // 修改标签信息
        if (tagMapper.modifyTag(tagModifyDTO) == 1) {
            log.debug("Modify Tag success : {} .", tagModifyDTO.getTagName());
            return Constants.OPERATION_SUCCESS;
        } else {
            log.debug("Modify Tag failed : {} .", tagModifyDTO.getTagName());
            return Constants.OPERATION_FAILED;
        }
    }

    /**
     * 删除标签
     * @param tagDeleteByIdDTO TagDeleteByIdDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTag(TagDeleteByIdDTO tagDeleteByIdDTO) {
        if (tagMapper.deleteTag(tagDeleteByIdDTO) == 1) {
            log.debug("Delete Tag success, tagId : {} .", tagDeleteByIdDTO.getTagId());
            portalTagService.deleteTagPortal(tagDeleteByIdDTO.getTagId());
            return Constants.OPERATION_SUCCESS;
        } else {
            log.debug("Delete Tag failed, tagId : {} .", tagDeleteByIdDTO.getTagId());
            return Constants.OPERATION_FAILED;
        }
    }

    /**
     * 判断标签是否存在
     * @param tagId Long
     * @return Integer
     */
    @Override
    public Integer getIdIsExist(Long tagId) {
        if (tagMapper.getIdIsExist(tagId) == 1) {
            log.debug("Tag Id exist.");
            return Constants.TAG_EXIST;
        } else {
            log.debug("Tag Id not exist.");
            return Constants.TAG_NOT_EXIST;
        }
    }
}
