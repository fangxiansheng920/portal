package cn.icheer.portal.service;

import cn.icheer.portal.dto.DicGetDTO;
import cn.icheer.portal.dto.DicIdDTO;
import cn.icheer.portal.entity.Dic;

import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/28 15:31
 */
public interface DicService {
    /**
     * 查询所有字典
     * @return List<Dic> 返回字典列表
     */
    List<Dic> getAllDic();

    /**
     * 查询字典
     * @param dicGetDTO 查询条件（包括dicName和dicKey）
     * @return 返回字典数据
     */
    Dic getDic(DicGetDTO dicGetDTO);

    /**
     * 添加字典
     * @param dic 字典对象
     */
    Integer addDic(Dic dic);

    /**
     * 修改字典
     * 1: 修改成功
     * -1: 修改失败，字典重复
     * 0: 修改失败，字典不存在
     * @param dic 字典对象
     * @return
     */
    int modifyDic(Dic dic);

    /**
     * 删除字典
     */
    int deleteDic(DicIdDTO dicIdDTO);


}
