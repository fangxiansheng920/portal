package cn.icheer.portal.utils.obs;

import com.obs.services.exception.ObsException;
import com.obs.services.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface OBSService {

	Boolean copyObject(String sourcebucketname, String sourceobjectname, String destbucketname, String destobjectname );
	Boolean newFolder(String bucketname, String folderName);

	Boolean copyFolder(String bucketname, String sourctFolderName, String destFolderName);
	// 上传图片
	PutObjectResult uploadObject(File file, Boolean isCovered) throws ObsException;
	PutObjectResult uploadObject(File file, Boolean isCovered, String bucketName) throws ObsException;
	PutObjectResult uploadObject(File file, Boolean isCovered, String bucketName, String saveName) throws ObsException;
	PutObjectResult uploadObject(File file, Boolean isCovered, String bucketName, String saveName, String[] directories) throws ObsException;

	PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered) throws ObsException;
	PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered, String bucketName) throws ObsException;
	PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered, String bucketName, String saveName) throws ObsException;
	PutObjectResult uploadObjectByPath(String filePath, Boolean isCovered, String bucketName, String saveName, String[] directories) throws ObsException;

	// 批量上传
	UploadProgressStatus batchUpload(List<String> listFilePath);
	UploadProgressStatus batchUpload(List<String> listFilePath, String bucketName);
	UploadProgressStatus batchUpload(List<String> listFilePath, String bucketName, String destFolder);

	// 上传文件夹
	// a: folderPath = "d:/project/res/" destFolder = "test" 上传res中所有文件到test目录 目录结构为test/
	// b: folderPath = "d:/project/res" destFolder = "test" 上传res文件夹以及内部所有文件到test目录 目录结构为test/res/
	UploadProgressStatus batchUploadByFolderPath(String folderPath);
	UploadProgressStatus batchUploadByFolderPath(String folderPath, String bucketName);
	UploadProgressStatus batchUploadByFolderPath(String folderPath, String bucketName, String destFolder);

	// 获取单个对象
	ObsObject getObject(String fileName, String fileType);
	ObsObject getObject(String fileName, String fileType, String bucketName);
	ObsObject getObject(String fileName, String fileType, String bucketName, String[] directories);

	// 删除对象
	DeleteObjectResult deleteObject(String objectKey);
	DeleteObjectResult deleteObject(String objectKey, String bucketName);
	// 通过url删除对象
	DeleteObjectResult deleteObjectByUrl(String url);

	// 列举桶内对象
	ArrayList<ObsObject> listObject();
	ArrayList<ObsObject> listObject(String bucketName);
	ArrayList<ObsObject> listObject(String bucketName, int maxKey);
	ArrayList<ObsObject> listObject(String bucketName, int maxKey, String marker);

}
