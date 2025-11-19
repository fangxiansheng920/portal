package cn.icheer.portal.utils.obs;

import com.obs.services.model.PartEtag;
import lombok.Data;

import java.util.List;

@Data
public class MergeUploadVo {
    String uploadId;
    String fileName;
    List<PartEtag> listPartEtag;
}

