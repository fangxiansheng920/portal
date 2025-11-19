package cn.icheer.portal.controller;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.dto.DicGetDTO;
import cn.icheer.portal.dto.DicIdDTO;
import cn.icheer.portal.entity.Dic;
import cn.icheer.portal.result.ResponseResult;
import cn.icheer.portal.service.DicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author fangweihao
 * @since 2025/7/28 15:13
 */
@Slf4j
@RestController
public class DicController {
    @Resource
    private DicService dicService;

    /**
     * 查询所有字典
     * @return List<Dic>
     */
    @RequestMapping("/portal/getAllDic")
    public ResponseResult getAllDic() {
        return ResponseResult.success("获取成功", dicService.getAllDic());
    }

    /**
     * 查询字典
     *
     * @param dicGetDTO 字典查询条件（包括dicName和dicKey）
     * @return Dic
     */
    @RequestMapping("/portal/getDic")
    public ResponseResult getDic(@RequestBody @Valid DicGetDTO dicGetDTO) {
        Dic dic = dicService.getDic(dicGetDTO);
        if (dic == null) {
            return ResponseResult.fail("字典不存在");
        }
        return ResponseResult.success("获取成功", dic);
    }

    /**
     * 添加字典
     *
     * @param dic 字典对象
     * @return
     */
    @RequestMapping("/portal/addDic")
    public ResponseResult addDic(@RequestBody @Valid Dic dic) {
        if (dicService.addDic(dic) == Constants.OPERATION_SUCCESS) {
            return ResponseResult.success("添加成功");
        } else {
            return ResponseResult.fail("添加失败，字典已存在");
        }
    }

    /**
     * 修改字典
     * @param dic 字典对象
     * @return
     */
    @RequestMapping("/portal/modifyDic")
    public ResponseResult modifyDic(@RequestBody @Valid Dic dic) {
        int flag = dicService.modifyDic(dic);
        if (flag == Constants.OPERATION_SUCCESS) {
            return ResponseResult.success("修改成功");
        } else if (flag == Constants.DIC_EXIST) {
            return ResponseResult.fail("修改失败，字典重复");
        } else {
            return ResponseResult.fail("修改失败，字典不存在");
        }
    }

    /**
     * 删除字典
     * @param dicIdDTO 字典id
     * @return
     */
    @RequestMapping("/portal/deleteDic")
    public ResponseResult deleteDic(@RequestBody @Valid DicIdDTO dicIdDTO) {
        if(dicService.deleteDic(dicIdDTO) == Constants.OPERATION_SUCCESS) {
            return ResponseResult.success("删除成功");
        } else {
            return ResponseResult.fail("删除失败");
        }
    }
}
