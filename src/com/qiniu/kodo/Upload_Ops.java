package com.qiniu.kodo;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

public class Upload_Ops {

	public static void main(String[] args) {
//		ops();
		slice();
	}
	
	
	
	
	
	public static void slice() {
		Auth auth = Auth.create("0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9", "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW");
		//上传文件路径
		String filePath = "/Users/ryanxu/Downloads/mayun0.mp4";
		//上传的存储空间
		String bucket = "live-bucket";
		//在存储空间的命名
		String key = "mayun0.mp4";
		//空间的域名， 建议使用自定义域名
		String domain = "osag4za8f.bkt.clouddn.com";
		Configuration cfg = new Configuration(Zone.zone0());
		//创建私有处理队列
		String pipeline = "av-pipeline";
		//处理操作 ： 此处就是关键点， 需要针对视频切片，可以在这设置接口和参数
		StringBuffer opfs = new StringBuffer("avthumb/m3u8/noDomain/1/vb/500k/t/10");
		String uptoken = auth.uploadToken(bucket, key, 3600, 
				new StringMap().putNotEmpty("persistentOps", opfs.toString()).putNotEmpty("persistentPipeline", pipeline));
		UploadManager uploadMgr = new UploadManager(cfg);
		try {
			//上传
			Response resp = uploadMgr.put(filePath, key, uptoken);
			System.out.println(resp.statusCode + "： " + resp.body().toString());
		} catch (QiniuException e) {
			e.printStackTrace();
		}
	}
	
	public static void ops() {
		//上传文件路径
		String filePath = "/Users/ryanxu/Documents/admin/admin1.jpeg";
		//上传的存储空间
		String bucket = "test-bucket";
		//在存储空间的命名
		String key = "admin1.jpeg";
		//空间的域名， 建议使用自定义域名
		String domain = "ortq6qy68.bkt.clouddn.com";
		//创建Auth对象和  cfg对象
		Auth auth = Auth.create("0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9", "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW");
		Configuration cfg = new Configuration(Zone.zone0());
		//创建私有处理队列
		String pipeline = "image-pipeline";
		//处理操作 ： 此处就是关键点， 需要针对视频切片，可以在这设置接口和参数
		StringBuffer opfs = new StringBuffer("imageView2/2/w/300/h/400|saveas/");
		//文件另存 调用saveas接口
		String saveAs = UrlSafeBase64.encodeToString(bucket + ":admin11.jpeg");
		opfs.append(saveAs);
		//另存签名
		String signStr = domain + "/" + key + "?" + opfs.toString();
		String sign = UrlSafeBase64.encodeToString(signStr);
		opfs.append("/sign/" + sign);
		//获取token ， 并指定上传策略， persistentOps 和 persistentPipeline
		String uptoken = auth.uploadToken(bucket, key, 3600, 
				new StringMap().putNotEmpty("persistentOps", opfs.toString()).putNotEmpty("persistentPipeline", pipeline));
		UploadManager uploadMgr = new UploadManager(cfg);
		try {
			//上传
			Response resp = uploadMgr.put(filePath, key, uptoken);
			System.out.println(resp.statusCode + "： " + resp.body().toString());
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		
	}

}
