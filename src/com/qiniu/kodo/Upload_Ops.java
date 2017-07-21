package com.qiniu.kodo;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.processing.OperationStatus;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

public class Upload_Ops {

	public static void main(String[] args) {
//		ops();
	}
	
	
	public static void ops() {
		String filePath = "/Users/ryanxu/Documents/admin/admin1.jpeg";
		String bucket = "test-bucket";
		String key = "admin1.jpeg";
		String domain = "ortq6qy68.bkt.clouddn.com";
		
		Auth auth = Auth.create("0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9", "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW");
		Configuration cfg = new Configuration(Zone.zone0());
		
		String pipeline = "image-pipeline";
		StringBuffer opfs = new StringBuffer("imageView2/2/w/300/h/400|saveas/");
		String saveAs = UrlSafeBase64.encodeToString(bucket + ":admin11.jpeg");
		opfs.append(saveAs);
		String signStr = domain + "/" + key + "?" + opfs.toString();
		String sign = UrlSafeBase64.encodeToString(signStr);
		opfs.append("/sign/" + sign);
		
		String uptoken = auth.uploadToken(bucket, key, 3600, 
				new StringMap().putNotEmpty("persistentOps", opfs.toString()).putNotEmpty("persistentPipeline", pipeline));
		UploadManager uploadMgr = new UploadManager(cfg);
		try {
			Response resp = uploadMgr.put(filePath, key, uptoken);
			System.out.println(resp.statusCode + "： " + resp.body().toString());
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		
	}

}
