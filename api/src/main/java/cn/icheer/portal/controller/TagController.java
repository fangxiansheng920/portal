package cn.icheer.portal.controller;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.dto.TagCreateDTO;
import cn.icheer.portal.dto.TagDeleteByIdDTO;
import cn.icheer.portal.dto.TagModifyDTO;
import cn.icheer.portal.entity.Tag;
import cn.icheer.portal.enums.HttpStatusEnum;
import cn.icheer.portal.result.ResponseResult;
import cn.icheer.portal.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/28 9:36
 */
@Slf4j
@RestController
public class TagController {
    @Resource
    private TagService tagService;

    /**
     * 获取所有标签
     *
     * @return List<Tag>
     */
    @RequestMapping("/portal/getAllTag")
    public ResponseResult getAllTags() {
        List<Tag> allTags = tagService.getAllTags();
        log.debug("Get all tags success.");
        return ResponseResult.success(allTags);
    }

    /**
     * 创建(添加)标签
     *
     * @param tagCreateDTO TagCreateDTO
     * @return
     */
    @RequestMapping("/portal/createTag")
    public ResponseResult createTag(@RequestBody @Valid TagCreateDTO tagCreateDTO) {
        int flag = tagService.createTag(tagCreateDTO);
        if (flag == Constants.OPERATION_SUCCESS) {
            log.debug("Create Tag success : {} .", tagCreateDTO.getTagName());
            return ResponseResult.success();
        } else {
            log.debug("Create Tag failed : {} .", tagCreateDTO.getTagName());
            return ResponseResult.fail(HttpStatusEnum.CONFLICT.getCode(), "标签已存在.");
        }
    }

    /**
     * 修改标签
     *
     * @param tagModifyDTO TagModifyDTO
     * @return
     */
    @RequestMapping("/portal/modifyTag")
    public ResponseResult modifyTag(@RequestBody @Valid TagModifyDTO tagModifyDTO) {
        int flag = tagService.modifyTag(tagModifyDTO);
        if (flag == Constants.OPERATION_SUCCESS) {
            return ResponseResult.success("修改成功");
        } else if (flag == Constants.TAG_EXIST) {
            return ResponseResult.fail(HttpStatusEnum.CONFLICT.getCode(), "标签已存在");
        } else {
            return ResponseResult.fail("修改失败");
        }
    }

    /**
     * 删除标签
     *
     * @param tagDeleteByIdDTO TagDeleteByIdDTO
     */
    @RequestMapping("/portal/deleteTag")
    public ResponseResult deleteTag(@RequestBody @Valid TagDeleteByIdDTO tagDeleteByIdDTO) {
        if (tagService.deleteTag(tagDeleteByIdDTO) == Constants.OPERATION_SUCCESS) {
            log.debug("Delete Tag success, tagId : {} .", tagDeleteByIdDTO.getTagId());
            return ResponseResult.success("删除成功");
        } else {
            log.debug("Delete Tag failed, tagId : {} .", tagDeleteByIdDTO.getTagId());
            return ResponseResult.fail(HttpStatusEnum.CONFLICT.getCode(), "删除失败.");
        }
    }

}
