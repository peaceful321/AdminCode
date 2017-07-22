package com.qiniu.kodo;

import java.io.File;
import java.io.IOException;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Recorder;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 七牛云上传 
 * @author xuhuanchao
 *
 */
public class QiniuUpload {
	
	//指定 AK, SK
	private static final String ACCESS_KEY = "0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9";
	private static final String SECRECT_KEY = "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW";

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		//1. 七牛简单上传
		String filePath = "/Users/ryanxu/Downloads/imgs/stage.jpg";
		upload(filePath, "test_stage.jpg", "test-bucket");
		
		//2.
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
		
		
//		//3. 
//		boolean flag = uploadByForm(filePath, "test-bucket");
//		System.out.println(flag + "");
		
		//4. 
//		String bigFilePath = "F:\\admin\\Mysql\\navicatformysql.zip";
//		uploadBySlice(bigFilePath, "test-bucket");
		
		
		//5.覆盖上传
//		overrideUpload("test-bucket", "97p58PICV26.jpg", "/Users/ryanxu/Downloads/tooopen_sy.jpg");
		
	}
	
	
	public static void upload(String filePath, String key, String bucketName) {
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		
		String uploadToken = auth.uploadToken(bucketName);
		
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadMgr = new UploadManager(cfg);
		try {
			File file = new File(filePath);
			System.out.println(uploadToken);
			Response resp = uploadMgr.put(file, key, uploadToken);
			System.out.println(resp.statusCode + ":" + resp.error);
			boolean flag = resp.isOK();
			if(flag) {
				System.out.println(resp.bodyString());
			} else {
				System.out.println(resp.error);
			}
		} catch (QiniuException e) {
			System.out.println(e.response.error);
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传文件， 通过字节数组的形式进行上传 
	 * @param data
	 * @param key 存在七牛空间Bucket 中的名称
	 * @param bucketName
	 */
	public static void uploadByByteArray(byte[] data, String key, String bucketName) {
		//1. 构建授权对象
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		//2. 获取上传 
		String uploadToken = auth.uploadToken(bucketName);
		//3. 构建Configuration 对象， 指定上传空间 所在的存储区域
		Configuration cfg = new Configuration(Zone.zone0());
		//4. 构建上传管理对象， 需要 Configuration 对象 作为参数
		UploadManager uploadMgr = new UploadManager(cfg);
		try{
			//5. 简单上传
			Response resp = uploadMgr.put(data, key, uploadToken);
			if (resp.isOK()) {
				//最后打印返回结果 response info 
				System.out.println(resp.toString());
				System.out.println(resp.bodyString());
			}
		}catch(QiniuException e) {
			System.out.println(e.error());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Post 表单上传， 通过OKHttp包
	 * @param filePath
	 * @return
	 */
	public static boolean uploadByForm(String filePath, String bucketName) {
		boolean flag = false;
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
	 * 上传本地文件， 使用分片上传
	 * @param filePath
	 * @param bucketName
	 * @return
	 */
	public static boolean uploadBySlice(String filePath, String bucketName) {
		boolean flag = false;
		
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		Configuration cfg = new Configuration(Zone.zone0());
		String directory = "D:\\Downloads\\qiniu\\slice_recorder";
		try {
			Recorder recorder = new FileRecorder(directory);
			UploadManager uploadMgr = new UploadManager(cfg, recorder);
			File file = new File(filePath);
			
			Response resp = uploadMgr.put(file, "testSliceUpload", auth.uploadToken("test-bucket"));
			System.out.println(resp.statusCode + ":" + resp.isOK() + ":" + resp.bodyString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 覆盖上传
	 * @param bucketName
	 * @param key
	 */
	public static void overrideUpload(String bucketName, String key, String filePath) {
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadMgr = new UploadManager(cfg);
		
		String uploadToken = auth.uploadToken(bucketName, key); //区别于 简单上传最明显的地方， 多了一个 key 作为参数
		try {
			Response response = uploadMgr.put(filePath, key, uploadToken);
			System.out.println(response.bodyString());
		} catch (QiniuException e) {
			e.printStackTrace();
		}
	}
	
	

}
