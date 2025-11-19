package cn.icheer.portal.utils.obs.impl;



import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import cn.icheer.portal.utils.obs.OBSProperties;
import cn.icheer.portal.utils.obs.OBSService;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import com.obs.services.model.fs.NewFolderRequest;
import com.obs.services.model.fs.ObsFSFolder;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static shade.kotlin.random.RandomKt.Random;


@Slf4j
@Service
@Component
@Data
public class OBSServiceImpl implements OBSService {

    @Autowired
    private OBSProperties obsProperties;

    private ObsClient obsClient;

    public ObsClient createObsClient() {
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(obsProperties.getSocketTimeout());
        config.setConnectionTimeout(obsProperties.getConnectionTimeout());
        config.setEndPoint(obsProperties.getEndpoint());
        return new ObsClient(obsProperties.getAccessKey(), obsProperties.getSecretKey(), config);
    }


    @Override
    public PutObjectResult uploadObject(File file, Boolean isCovered) throws ObsException {
        return uploadObject(file, isCovered, obsProperties.getBucket());
    }

    public PutObjectResult uploadObject(File file, Boolean isCovered, String bucketName) throws ObsException {
        // 如果没有文件名，默认生成唯一文件名
        String fileType = file.getName().substring(file.getName().lastIndexOf("."));

        String saveName =  LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) + "." + fileType;
        return uploadObject(file, isCovered, bucketName, saveName);
    }

    public PutObjectResult uploadObject(File file, Boolean isCovered, String bucketName, String saveName) throws ObsException {
//        saveName = PathUtils.toLinuxPath(saveName);
        String[] matrix = saveName.split("/");
        String[] directories = null;
        if (matrix.length > 1) {
            // saveName 中存在路径，将路径前面的目录解析出来
            saveName = matrix[matrix.length - 1];
            directories = ArrayUtil.sub(matrix, 0, matrix.length - 1);
        }
        return uploadObject(file, isCovered, bucketName, saveName, directories);
    }

    public PutObjectResult uploadObject(File file, Boolean isCovered, String bucketName, String saveName, String[] directories) throws ObsException {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null!");
        }
        if (isCovered == null) {
            throw new IllegalArgumentException("isCovered cannot be null!");
        }
        String fileName = FileUtil.getName(file);

        obsClient = createObsClient();
        PutObjectRequest request = new PutObjectRequest();

        request.setFile(file);

        if (StringUtils.isBlank(bucketName)) {
            bucketName = obsProperties.getBucket();
        }
        existOrCreateBucket(obsClient, bucketName);
        request.setBucketName(bucketName);
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
        String directory = ArrayUtil.isNotEmpty(directories) ? String.join("/", directories) + "/" : "/";
        if (StringUtils.isBlank(saveName)) {
            saveName = fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            saveName = saveName.endsWith("/") ? saveName.substring(0, saveName.length() - 1) : saveName;
        }
        if (!saveName.endsWith(fileType)) {
            // 如果保存名没有带文件类型那么补上文件类型 by zhuxiaodong
            saveName += "." + fileType;
        }
        if (directory.endsWith("/") && saveName.startsWith("/")) {
            saveName = saveName.substring(1);
        }
        if ("/".equals(directory)) {
            directory = "";
        }

        if (!isCovered && obsClient.doesObjectExist(bucketName, directory + saveName)) {
            throw new IllegalArgumentException("已存在相同objectKey！请修改输入参数！");
        }
        request.setObjectKey(directory + saveName);

        return obsClient.putObject(request);
    }

    @Override
    public Boolean copyObject(String sourcebucketname, String sourceobjectname, String destbucketname, String destobjectname) {
        try {
            obsClient = createObsClient();
            CopyObjectResult result = obsClient.copyObject(sourcebucketname, sourceobjectname, destbucketname, destobjectname);
            return true;
        } catch (ObsException e) {
            // 复制失败
            log.error("HTTP CodeHTTP Code: " + e.getResponseCode());
            log.error("Error Code:" + e.getErrorCode());
            log.error("Error Message: " + e.getErrorMessage());
        }
        return false;
    }

    @Override
    public Boolean copyFolder(String bucketname, String sourctFolderName, String destFolderName) {
        log.debug("##########################copyFolder begin######################################");
        log.debug("src=" + sourctFolderName + "and dest=" + destFolderName + "and bucket=" + bucketname);
        obsClient = createObsClient();
        ListObjectsRequest request = new ListObjectsRequest(bucketname);
        request.setPrefix(sourctFolderName);
        ObjectListing result = obsClient.listObjects(request);
        for (ObsObject obsObject : result.getObjects()) {
            String objectName = obsObject.getObjectKey();
            if (objectName.equals(sourctFolderName)) {
                continue;
            }
            String destObjectName = objectName.replace(sourctFolderName, destFolderName);
            log.debug("srcFile=" + objectName + "and destFile=" + destObjectName + "and bucket=" + bucketname);
            if (!copyObject(bucketname, objectName, bucketname, destObjectName)) {
                return false;
            }
        }
        log.debug("##########################copyFolder end######################################");
        return true;
    }

    @Override
    public Boolean newFolder(String bucketname, String folderName) {
        try {
            obsClient = createObsClient();
            NewFolderRequest newFolderRequest = new NewFolderRequest();
            newFolderRequest.setBucketName(bucketname);
            newFolderRequest.setObjectKey(folderName);
            ObsFSFolder obsFSFolder = obsClient.newFolder(newFolderRequest);
            return true;
        } catch (ObsException e) {
            // 复制失败
            log.error("HTTP Code: " + e.getResponseCode());
            log.error("Error Code:" + e.getErrorCode());
            log.error("Error Message: " + e.getErrorMessage());
        }
        return false;
    }


    @Override
    public PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered) throws ObsException {
        return uploadObjectByPath(filePath, isCovered, null);
    }

    @Override
    public PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered, String bucketName) throws ObsException {
        return uploadObjectByPath(filePath, isCovered, bucketName, null);
    }

    @Override
    public PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered, String bucketName, String saveName) throws ObsException {
        return uploadObjectByPath(filePath, isCovered, bucketName, saveName, null);
    }

    @Override
    public PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered, String bucketName, String saveName, String[] directories) throws ObsException {
        if (StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException("filePath cannot be null!");
        }
        File file = new File(filePath);
        return uploadObject(file, isCovered, bucketName, saveName, directories);
    }

    @Override
    public UploadProgressStatus batchUpload(List<String> listFilePath) {
        return batchUpload(listFilePath, null);
    }

    @Override
    public UploadProgressStatus batchUpload(List<String> listFilePath, String bucketName) {
        return batchUpload(listFilePath, bucketName, null);
    }

    @Override
    public UploadProgressStatus batchUpload(List<String> listFilePath, String bucketName, String destFolder) {
        if (listFilePath.size() < 1) {
            throw new IllegalArgumentException("listFilePath cannot be empty!");
        }
        if (StringUtils.isBlank(bucketName)) {
            bucketName = obsProperties.getBucket();
        }
        PutObjectsRequest request = new PutObjectsRequest(bucketName, listFilePath);

        // 设置request
        batchUploadRequestSetting(destFolder, request);

        // 计时
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dNow = new Date();
        log.error("Upload start time: " + ft.format(dNow));

        obsClient = createObsClient();
        UploadProgressStatus res = obsClient.putObjects(request);

        log.error("··········putObjects End···········");

        return res;
    }

    @Override
    public UploadProgressStatus batchUploadByFolderPath(String folderPath) {
        return batchUploadByFolderPath(folderPath, null);
    }

    @Override
    public UploadProgressStatus batchUploadByFolderPath(String folderPath, String bucketName) {
        return batchUploadByFolderPath(folderPath, bucketName, null);
    }

    @Override
    public UploadProgressStatus batchUploadByFolderPath(String folderPath, String bucketName, String destFolder) {
        if (StringUtils.isBlank(folderPath)) {
            throw new IllegalArgumentException("folderPath cannot be null!");
        }
        if (StringUtils.isBlank(bucketName)) {
            bucketName = obsProperties.getBucket();
        }
        PutObjectsRequest request = new PutObjectsRequest(bucketName, folderPath);

        // 设置request
        batchUploadRequestSetting(destFolder, request);

        // 计时
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dNow = new Date();
        log.error("Upload start time: " + ft.format(dNow));
        obsClient = createObsClient();
        UploadProgressStatus res = obsClient.putObjects(request);
        log.error("··········putObjects End···········");
        return res;
    }

    private void batchUploadRequestSetting(String destFolder, PutObjectsRequest request) {
        // 设置同时上传的最大线程数
        request.setTaskNum(5);

        if (StringUtils.isBlank(destFolder)) {
            destFolder = "";
        }
        request.setPrefix(destFolder);

        request.setCallback(new TaskCallback<PutObjectResult, PutObjectBasicRequest>() {

            @Override
            public void onSuccess(PutObjectResult result) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date dNow = new Date();
                log.error("[" + ft.format(dNow) + "]putObject success:" + result.getObjectKey());
            }

            @Override
            public void onException(ObsException exception, PutObjectBasicRequest singleRequest) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date dNow = new Date();
                log.error("[" + ft.format(dNow) + "]putObject failed:" + singleRequest.getObjectKey());
                log.error("the erro info:" + exception.getMessage());
            }
        });

        request.setUploadObjectsProgressListener(status -> {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date dNow = new Date();

            log.error("\n************************************");
            log.error("[" + ft.format(dNow) + "]TotalSize:" + status.getTotalSize());
            log.error("[" + ft.format(dNow) + "]TransferredSize:" + status.getTransferredSize());
            log.error("[" + ft.format(dNow) + "]AverageSpeed:" + status.getAverageSpeed());
            log.error("[" + ft.format(dNow) + "]InstantaneousSpeed:" + status.getInstantaneousSpeed());
        });
        // 设置触发回调频率 每上传3个文件触发一次回调
        request.setProgressInterval(3);
        // 设置触发回调频率 每上传3MB文件触发一次回调
        request.setTaskProgressInterval(3 * 1024 * 1024L);
    }


    @Override
    public ObsObject getObject(String fileName, String fileType) {
        return getObject(fileName, fileType, null);
    }

    public ObsObject getObject(String fileName, String fileType, String bucketName) {
        return getObject(fileName, fileType, bucketName, null);
    }

    public ObsObject getObject(String fileName, String fileType, String bucketName, String[] directories) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("fileName cannot be null!");
        }
        obsClient = createObsClient();
        if (!obsClient.headBucket(bucketName)) {
            throw new IllegalArgumentException("bucketName doesn't exist!");
        }
        String saveName = StringUtils.isBlank(fileType) ? fileName : fileName + "." + fileType;
        String directory = toDirectory(directories);
        GetObjectRequest request = new GetObjectRequest();
        request.setObjectKey(directory + saveName);
        request.setBucketName(bucketName);
        request.setVersionId(null);
        return obsClient.getObject(request);
    }

    // 检查桶是否存在，不存在则创建
    private void existOrCreateBucket(ObsClient obsClient, String bucketName) throws ObsException {
        if (!obsClient.headBucket(bucketName)) {
            obsClient.createBucket(bucketName);
        }
    }

    // 创建文件夹
    private PutObjectResult createDirectory(String[] directories, String bucketName) throws ObsException {
        obsClient = createObsClient();
        bucketName = StringUtils.isBlank(bucketName) ? obsProperties.getBucket() : bucketName;
        return obsClient.putObject(bucketName, toDirectory(directories), new ByteArrayInputStream(new byte[0]));
    }


    @Override
    public DeleteObjectResult deleteObject(String objectKey) throws ObsException {
        return deleteObject(objectKey, null);
    }

    public DeleteObjectResult deleteObject(String objectKey, String bucketName) throws ObsException {
        if (StringUtils.isBlank(objectKey)) {
            throw new IllegalArgumentException("objectKey cannot be null!");
        }
        obsClient = createObsClient();
        bucketName = StringUtils.isBlank(bucketName) ? obsProperties.getBucket() : bucketName;
        return obsClient.deleteObject(bucketName, objectKey, null);
    }

    @Override
    public DeleteObjectResult deleteObjectByUrl(String url) throws ObsException {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url cannot be null!");
        }
        URI uri = URLUtil.toURI(url);
        String host = uri.getHost();
        String path = uri.getPath().substring(1);
        String endPoint = obsProperties.getEndpoint();
        DeleteObjectRequest request = new DeleteObjectRequest();
        // url一般为两种格式
        // such as: a: bucket.endpoint/objectKey b: endpoint/bucket/objectKey
        if (host.equals(endPoint)) {
            request.setBucketName(path.substring(0, path.indexOf("/")));
            request.setObjectKey(path.substring(path.indexOf("/") + 1));
        } else {
            request.setBucketName(host.replace("." + endPoint, ""));
            request.setObjectKey(path);
        }
        request.setVersionId(null);
        obsClient = createObsClient();
        return obsClient.deleteObject(request);
    }


    @Override
    public ArrayList<ObsObject> listObject() {
        return listObject(null);
    }

    public ArrayList<ObsObject> listObject(String bucketName) {
        return listObject(bucketName, 0);
    }

    public ArrayList<ObsObject> listObject(String bucketName, int maxKey) {
        return listObject(bucketName, maxKey, null);
    }

    public ArrayList<ObsObject> listObject(String bucketName, int maxKey, String marker) {
        obsClient = createObsClient();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        if (StringUtils.isBlank(bucketName)) {
            bucketName = obsProperties.getBucket();
        }
        listObjectsRequest.setBucketName(bucketName);
        if (maxKey != 0) {
            listObjectsRequest.setMaxKeys(maxKey);
        }

        if (!StringUtils.isBlank(marker)) {
            listObjectsRequest.setMarker(marker);
        }
        ObjectListing objectListing = obsClient.listObjects(listObjectsRequest);
        return new ArrayList<>(objectListing.getObjects());
    }


    /**
     * 拼接目录
     *
     * @param directories {"src1", "src2", "src3"}
     * @return "src1/src2/src3/"
     */
    String toDirectory(String[] directories) {
        String rootFolder = obsProperties.getRootFolder();
        // 默认目录
        if (StringUtils.isBlank(rootFolder)) {
            rootFolder = "test";
        }
        if (directories == null || directories.length == 0 || StringUtils.isBlank(directories[0])) {
            return rootFolder + "/";
        }

        return rootFolder + "/" + String.join("/", directories) + "/";
    }
}
