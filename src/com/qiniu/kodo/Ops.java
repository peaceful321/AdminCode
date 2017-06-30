package com.qiniu.kodo;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

public class Ops {
	
	
	private static final String AK = "0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9";
	private static final String SK = "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW";
	

	public static void main(String[] args) {
		avAddWaterMark();
	}
	
	
	/**
	 * 上传自动添加视频水印
	 */
	public static void avAddWaterMark() {
		
		String filePath = "/Users/ryanxu/Downloads/test2.mp4";
		String mvImage = "http://lyt.echohu.top/liyongtao.gif";
		String bucketName = "test-bucket";
		String key = "test2.mp4";
		String pipeline = "av-pipeline";
		String domain = "ortq6qy68.bkt.clouddn.com";
		String mvImageCode = UrlSafeBase64.encodeToString(mvImage);
		
		String saveAs = bucketName + ":testWm.mp4";
		StringBuffer fops = new StringBuffer("avthumb/mp4/wmImage/" + mvImageCode + "/wmGravity/Center|saveas/");
		String saveAsCode = UrlSafeBase64.encodeToString(saveAs);
		fops.append(saveAsCode);
		
		String signStr = domain + "/" + key + "?" + fops.toString();
		
		String sign = UrlSafeBase64.encodeToString(signStr);
		fops.append("/sign/" + sign);
		System.out.println(fops.toString());
		
		Auth auth = Auth.create(AK, SK);
		
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadMgr = new UploadManager(cfg);
		
		String uptoken = auth.uploadToken(bucketName, key, 3600, 
				new StringMap().putNotEmpty("persistentOps", fops.toString()).putNotEmpty("persistentPipeline", pipeline));
		
		try {
			Response resp = uploadMgr.put(filePath, key, uptoken);
			System.out.println(resp.statusCode + ":" + resp.error);
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		
	}

}
