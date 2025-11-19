package cn.icheer.portal.utils.obs;

import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.entity.User;
import com.obs.services.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Component
public class ObsUploadUtils {

    @Autowired
    private OBSService obsService;

    @Autowired
    private OBSProperties obsProperties;


    public String uploadFile(MultipartFile file, JWTUserDetails user) throws IOException {
        // 1. 建临时文件
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        File tmp = File.createTempFile("upload_", suffix);
        //  增加userId 到文件名前，避免重复名称被覆盖        /rootFolder/userId/fileName
        String savePath = obsProperties.getRootFolder() + user.getId() + "/" + fileName.replace(suffix, "");
        // 2. 零拷贝写入
        file.transferTo(tmp);          // MultipartFile → File
        try {
            // 3. 业务上传
            PutObjectResult result = obsService.uploadObject(tmp, true, obsProperties.getBucket(), savePath);
            if (result.getStatusCode() == 200) {
                String url = obsProperties.getRequestUrl() + result.getObjectKey();
                return url;
            } else {
                return "上传失败";
            }

        } finally {
            // 4. 用完删除（可选）
            tmp.delete();
        }
    }

}
