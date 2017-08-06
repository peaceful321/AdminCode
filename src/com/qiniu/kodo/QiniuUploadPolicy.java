package com.qiniu.kodo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 七牛上传策略
 * @author xuhuanchao
 *
 */
public class QiniuUploadPolicy {
	
	//AK, SK
	private static final String ACCESS_KEY = "0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9";
	private static final String SECRET_KEY = "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW";

	
	/**
	 * main method 
	 * @param args
	 */
	public static void main(String[] args) {

		//测试七牛 魔法变量， 通过上传策略中的returnBody 
//		testReturnBody("test-bucket", new File("/Users/ryanxu/Downloads/imgs/gif7.gif"));
		
		//测试七牛 自定义变量， 也是通过上传策略中的returnBody， 也测试了 callbackUrl & callbackBody
		testUserDefinedVar("test-bucket", new File("/Users/ryanxu/Downloads/imgs/a0.jpg"));
		
		//测试七牛 上传策略中的 mimeType(文件类型)、 fsizeMin(文件大小最小值)、fsizeLimit(文件大小最大值)
//		qualifyFileType("test-bucket", new File("/Users/ryanxu/Downloads/imgs/gif7.gif"));
		
		
		
	}
	

	/**
	 * 限制上传的文件类型和大小
	 * @param bucketName
	 * @param target
	 */
	public static void qualifyFileType(String bucketName, File target) {
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadMgr = new UploadManager(cfg);
		StringMap putPolicy = new StringMap();
		putPolicy.put("fsizeMin", 1024)						//文件大小最小为 1KB
				 .put("fsizeLimit", 1024 * 1024 * 10)		//文件大小最大为 10M
				 .putNotEmpty("mimeType", "image/jpg");		//文件类型为jpg图片, 所有图片 设置为 image/*
		//获取upload token， 在token中设置上传策略中的 fsizeMin, fsizeLimit, mimeType
		String token = auth.uploadToken(bucketName, "qualify11/" + target.getName(), 3600, putPolicy);
		try {
			Response resp = uploadMgr.put(target, "qualify11/" + target.getName(), token);
			System.out.println(resp.statusCode + ":" +resp.bodyString());
		} catch (QiniuException e) {
			System.out.println(e.code() + ":" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 测试七牛 自定义变量， 也是通过上传策略中的returnBody 来设置
	 * @param bucket
	 * @param target
	 */
	public static void testUserDefinedVar(String bucket, File target) {
		//1.构建Auth 对象
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

		//2. 通过OKHttp 构建一个表单上传
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("image/jpg");
		RequestBody fileBody = RequestBody.create(mediaType, target);
		
		//3. 构建上传策略， 指定自定义变量 currTime and content 
		StringMap putPolicy = new StringMap();
		//设置 callbackUrl and callbackBody
		String domain = "3fd73076.ngrok.io";
		putPolicy.putNotNull("callbackBody", "{\"currTime\":\"$(x:currTime)\", \"content\":\"$(x:content)\", \"domain\":\"$(x:domain)\",\"key\":\"$(key)\"}")
			     .putNotNull("callbackUrl", "http://" + domain + "/WebProject/callback")
			     .putNotNull("callbackBodyType", "application/json");
		//设置 returnBody 返回自定义变量的结果
//		putPolicy.putNotEmpty("returnBody", "{\"currTime\":\"$(x:currTime)\", \"content\":\"$(x:content)\", \"key\":\"$(key)\"}");
		
		//4. 获取token， 包含了上传策略（putPolicy）中的returnBody
		String token = auth.uploadToken(bucket, "20170803/"+target.getName(), 3600, putPolicy);
		RequestBody reqBody = new MultipartBody.Builder()
								.setType(MultipartBody.FORM)
								.addFormDataPart("file", target.getName(), fileBody)
								.addFormDataPart("key", "20170803/"+target.getName())
								.addFormDataPart("x:currTime", new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").format(new Date()))
								.addFormDataPart("x:content", new String("Test User-Defined var by ReturnBody"))
								.addFormDataPart("x:domain", domain)
								.addFormDataPart("token", token)
								.build();
		Request req = new Request.Builder()
							.url("http://upload.qiniu.com/")
							.post(reqBody)
							.build();
		try {
			okhttp3.Response resp = client.newCall(req).execute();
			System.out.println(new String(resp.body().bytes()));
			System.out.println(resp.code() + ":" + resp.message());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 返回七牛 提供的魔法变量value， 给业务提供部分需要的信息
	 * @param bucket
	 * @param target
	 */
	public static void testReturnBody(String bucket, File target) {
		//1.构建Auth 对象
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		//2.构建Configuration
		Configuration cfg = new Configuration(Zone.zone0());
		//3. 构建上传管理对象
		UploadManager uploadMgr = new UploadManager(cfg);
		StringMap policy = new StringMap();
		policy.putNotEmpty("returnBody", 
				"{\"key\":\"$(key)\","
				+ "\"hash\":\"$(etag)\","
				+ "\"bucket\":\"$(bucket)\","
				+ "\"fsize\":\"$(fsize)\","
				+ "\"hash\":\"$(etag)\","
				+ "\"fname\":\"$(fname)\","
				+ "\"mimeType\":\"$(mimeType)\","
				+ "\"endUser\":\"$(endUser)\","
				+ "\"exif\":\"$(exif)\","
				+ "\"imageAve\":\"$(imageAve)\","
				+ "\"ext\":\"$(ext)\","
				+ "\"uuid\":\"$(uuid)\","
				+ "\"width\":\"$(imageInfo.width)\","
				+ "\"height\":\"$(imageInfo.height)\","
				+ "\"year\":\"$(year)\","			//文档写明： 年月日时分秒 不支持在 returnBody and callbackBody 中使用， 此处为展示效果，结果都为null
				+ "\"mon\":\"$(mon)\","
				+ "\"day\":\"$(day)\","
				+ "\"hour\":\"$(hour)\","
				+ "\"min\":\"$(min)\","
				+ "\"sec\":\"$(sec)\","
				+ "}");
		//4. 获取上传token ，并指定上传策略 
		String token = auth.uploadToken(bucket, "20170721/" + target.getName(), 3600, policy);
		try {
			//5. 执行上传操作
			Response resp = uploadMgr.put(target, "20170721/" +target.getName(), token);
			//6. 查看魔法变量返回结果， 可以用Gson包 进行 json 解析， 转换成一个 上传策略中的ReturnBody 对象
			System.out.println(resp.bodyString());
		} catch (QiniuException e) {
			e.printStackTrace();
		}
	}

}
