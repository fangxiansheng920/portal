package cn.icheer.portal.utils.obs;

import lombok.Data;

/**
 * OBS(天翼云对象存储) 属性配置
 * @author : Six yuan
 * @createTime : 2022/11/16 14:18
 */
@Data
public class OBSProperties {

	// 端点
	private String endpoint;

	// AK
	private String accessKey;

	// SK
	private String secretKey;

	// 桶名
	private String bucket;

	// socket超时时间 单位 ms
	private int socketTimeout;

	// 连接超时时间 单位 ms
	private int connectionTimeout;

	// 环境目录
	private String rootFolder;

	//返回的请求地址
	private String requestUrl;

}
