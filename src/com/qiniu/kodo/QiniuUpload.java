package com.qiniu.kodo;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Recorder;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;


/**
 * 七牛云上传
 * @author xuhuanchao
 *
 */
public class QiniuUpload {
	
	//个人密钥
	private static final String ACCESS_KEY = "0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9";
	private static final String SECRECT_KEY = "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW";

	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		//1. 通过文件路径进行上传
		String filePath = "F:\\Freehand.jpg";
//		upload(filePath, "img_desktop.jpg", "java-bucket");
		
		//2. 通过文件流转字节数组进行上传
//		File file = new File(filePath);
//		int length = Integer.parseInt(String.valueOf(file.length()));
//		byte[] data = new byte[length];
//		try {
//			FileInputStream fis = new FileInputStream(file);
//			fis.read(data);
//			uploadByByteArray(data, "img_desktop_byte.jpg", "java-bucket");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
//		//3. 表单上传
//		boolean flag = uploadByForm(filePath, "test-bucket");
//		System.out.println(flag + "");
		
		//4. 测试分片上传
		String bigFilePath = "F:\\admin\\Mysql\\navicatformysql.zip";
		uploadBySlice(bigFilePath, "test-bucket");
		
	}
	
	
	/**
	 * 上传文件
	 * @param filePath
	 * @param key 上传到七牛存储空间后， 文件存储的名称
	 * @bucketName 上传存储的七牛空间名称
	 */
	public static void upload(String filePath, String key, String bucketName) {
		//1. 获取授权对象Auth
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		
		//2. 获取Token
		String uploadToken = auth.uploadToken(bucketName);
		
		//3. 上传
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadMgr = new UploadManager(cfg);
		try {
			File file = new File(filePath);
			Response resp = uploadMgr.put(file, key, uploadToken);
			boolean flag = resp.isOK();
			if(flag) {
				System.out.println(resp.bodyString());
			} else {
				System.out.println(resp.error);
			}
		} catch (QiniuException e) {
			//System.out.println(e.response.error);
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传文件， 通过字节数组进行上传
	 * @param data
	 * @param key 上传到七牛空间 存储的文件名称
	 * @param bucketName
	 */
	public static void uploadByByteArray(byte[] data, String key, String bucketName) {
		//1. 获取授权对象Auth
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		
		//2. 获取Token
		String uploadToken = auth.uploadToken(bucketName);
		
		//3. 上传
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadMgr = new UploadManager(cfg);
		try{
			Response resp = uploadMgr.put(data, key, uploadToken);
			if (resp.isOK()) {
				System.out.println(resp.toString());
				System.out.println(resp.bodyString());
			}
		}catch(QiniuException e) {
			System.out.println(e.error());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Post方式提交表单， 通过表单上传文件
	 * @param filePath
	 * @return
	 */
	public static boolean uploadByForm(String filePath, String bucketName) {
		boolean flag = false;
		//1. 获取授权对象Auth
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		OkHttpClient client = new OkHttpClient();
		String url = "http://upload.qiniu.com/";
		String uploadToken = auth.uploadToken(bucketName);
		String key = "Freehand.jpg";
		MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/png");
		RequestBody fileBody = RequestBody.create(MEDIA_TYPE_IMAGE, new File(filePath));
		
		RequestBody reqBody = new MultipartBody.Builder()
        						.setType(MultipartBody.FORM)
        						.addFormDataPart("file", key, fileBody)
        						.addFormDataPart("key", key)
        						.addFormDataPart("token", uploadToken)
        						.build();
		
		Request req = new Request.Builder()
						.url(url)
						.post(reqBody)
						.build();
		try {
			okhttp3.Response resp = client.newCall(req).execute();
			System.out.println(resp.code() + ":" + resp.message());
			flag = resp.isSuccessful();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/**
	 * 分片上传
	 * @param filePath
	 * @param bucketName
	 * @return
	 */
	public static boolean uploadBySlice(String filePath, String bucketName) {
		boolean flag = false;
		//1. 获取授权对象Auth
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		Configuration cfg = new Configuration(Zone.zone0());
		String directory = "D:\\Downloads\\qiniu\\slice_recorder";
		try {
			Recorder recorder = new FileRecorder(directory);		//上传的记录保存 文件路径
			UploadManager uploadMgr = new UploadManager(cfg, recorder);
			File file = new File(filePath);
			
			Response resp = uploadMgr.put(file, "testSliceUpload", auth.uploadToken("test-bucket"));
			System.out.println(resp.statusCode + ":" + resp.isOK() + ":" + resp.bodyString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	

}
