package cn.icheer.portal.mapper;

import cn.icheer.portal.dto.DicGetDTO;
import cn.icheer.portal.dto.DicIdDTO;
import cn.icheer.portal.entity.Dic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/28 15:32
 */
@Mapper
public interface DicMapper {

    /**
     * 查询所有字典
     * @return List<Dic> 返回字典列表
     */
    List<Dic> getAllDic();

    /**
     * 查询字典
     * @param dicGetDTO 查询条件（包括dicName和dicKey）
     * @return Dic 返回字典数据
     */
    Dic getDic(DicGetDTO dicGetDTO);

    /**
     * 添加字典
     * @param dic 字典对象
     */
    void addDic(Dic dic);

    /**
     * 修改字典
     * @param dic 字典对象
     * @return
     */
    int modifyDic(Dic dic);

    /**
     * 删除字典
     * @param dicIdDTO 字典id
     */
    int deleteDic(DicIdDTO dicIdDTO);


}
