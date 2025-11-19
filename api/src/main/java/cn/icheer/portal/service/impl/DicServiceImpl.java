package cn.icheer.portal.service.impl;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.dto.DicGetDTO;
import cn.icheer.portal.dto.DicIdDTO;
import cn.icheer.portal.entity.Dic;
import cn.icheer.portal.mapper.DicMapper;
import cn.icheer.portal.service.DicService;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/28 15:31
 */
@Slf4j
@Service
public class DicServiceImpl implements DicService {
    @Resource
    private DicMapper dicMapper;

    /**
     * 查询所有字典
     * @return List<Dic> 返回字典列表
     */
    @Override
    public List<Dic> getAllDic() {
        return dicMapper.getAllDic();
    }

    /**
     * 查询字典
     * @param dicGetDTO 查询条件（包括dicName和dicKey）
     * @return Dic
     */
    @Override
    public Dic getDic(DicGetDTO dicGetDTO) {
        Dic dic = dicMapper.getDic(dicGetDTO);
        if (dic == null) {
            log.debug("Get dic failed, dic is null. ");
        } else {
            log.debug("Get dic success. ");
        }
        return dic;
    }

    /**
     * 添加字典
     * @param dic 字典对象
     * @return int
     */
    @Override
    public Integer addDic(Dic dic) {
        DicGetDTO dicGetDTO = new DicGetDTO(dic.getDicName(), dic.getDicKey());
        if (dicMapper.getDic(dicGetDTO) != null) {
            log.debug("Add dic failed, dic already exists. ");
            return Constants.OPERATION_FAILED;
        } else {
            dicMapper.addDic(dic);
            log.debug("Add dic success. ");
            return Constants.OPERATION_SUCCESS;
        }
    }

    /**
     * 修改字典
     * @param dic 字典对象
     * @return int
     */
    @Override
    public int modifyDic(Dic dic) {
        DicGetDTO dicGetDTO = new DicGetDTO(dic.getDicName(), dic.getDicKey());
        if (dicMapper.getDic(dicGetDTO) != null) {
            log.debug("Modify dic failed, dic already exists. ");
            return Constants.DIC_EXIST;
        } else {
            int flag = dicMapper.modifyDic(dic);
            if (flag == 1) {
                log.debug("Modify dic success.");
                return Constants.OPERATION_SUCCESS;
            } else {
                log.debug("Modify dic failed.");
                return Constants.OPERATION_FAILED;
            }
        }

    }

    /**
     * 删除字典
     */
    @Override
    public int deleteDic(DicIdDTO dicIdDTO) {
        if (dicMapper.deleteDic(dicIdDTO) == 1) {
            log.debug("Delete dic success.");
            return Constants.OPERATION_SUCCESS;
        } else {
            log.debug("Delete dic failed.");
            return Constants.OPERATION_FAILED;
        }
    }
}
