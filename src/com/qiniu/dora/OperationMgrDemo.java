package com.qiniu.dora;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.processing.OperationManager;
import com.qiniu.processing.OperationStatus;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;


public class OperationMgrDemo {
	
	private static final String AK = "0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9";
	private static final String SK = "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW";

	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		operation();
	}
	
	
	
	/**
	 * 针对已上传的视频，进行 水印处理
	 */
	public static void operation() {
		Auth auth = Auth.create(AK, SK);
		Configuration cfg = new Configuration(Zone.zone0());
		
		String bucketName = "test-bucket";			//空间名称
		String key = "shuiyin";						//已上传文件的名称
		String img = "http://pf.zhaojianfeng.cn/logo.png";			//水印图片
		String imgEncode = UrlSafeBase64.encodeToString(img);		//针对水印图片进行encode
		String saveAs = UrlSafeBase64.encodeToString(bucketName + ":shuiyin_7_4");			//处理后的文件名称为： shuiyin_1
		String fops = "avthumb/mp4/wmImage/" + imgEncode + "|saveas/" + saveAs;				//处理接口 
		String pipeline = "av-pipeline";													//音视频处理队列 ， portal 创建
		String domain = "ortq6qy68.bkt.clouddn.com";										//空间域名
		String signTarget = domain + "/" + key + "?" + fops;								//saveas需要签名 sign 参数， 具体可查看saveas接口
		String sign = UrlSafeBase64.encodeToString(signTarget);								//签名后的结果
		fops += "/sign/" + sign;															//拼接到 处理接口后面
		
		OperationManager operationMgr = new OperationManager(auth, cfg);					//通过OperationManager 对象进行数据处理操作
		try {
			String id = operationMgr.pfop(bucketName, key, fops, 
					new StringMap().putNotNull("persistentPipeline", pipeline));
			System.out.println(id);
		} catch (QiniuException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 视频压缩
	 */
	public static void compressVideo() {
		Auth auth = Auth.create(AK, SK);
		Configuration cfg = new Configuration(Zone.zone0());
		String bucketName = "test-bucket";			//空间名称
		String key = "mayun0.mp4";
		String domain = "test.zhaojianfeng.cn";
		String pipeline = "av-pipeline";
		String mSaveAs = UrlSafeBase64.encodeToString("test-bucket:compress_java.mp4");
		
		String fops = "avthumb/mp4/vb/0.5m|saveas/" + mSaveAs;
		String mSignStr = domain + "/" + key + "?" + fops;
		String mSign = UrlSafeBase64.encodeToString(mSignStr);
		fops = fops + "/sign/" + mSign;
		
		OperationManager om = new OperationManager(auth, cfg);
		try {
			String id = om.pfop(bucketName, key, fops, new StringMap().putNotNull("persistentPipeline", pipeline));
			OperationStatus os = om.prefop(id);
			
			System.out.println(os.code + ":" + os.desc);
		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	

}
