package com.qiniu.kodo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.UrlSafeBase64;

/**
 * 七牛常用接口
 * @author xuhuanchao
 *
 */
public class QiniuAPI {
	
	//个人密钥
	private static final String ACCESS_KEY = "0ZT-Rd0AswhPQti5lX2Ytt1T6XkyM80eY_4w9Pm9";
	private static final String SECRECT_KEY = "MbscrgLx_FefkUZ21SjY-GRE1oPJcvP2vvN6oXgW";

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
//		getAllBuckets();
		
//		createBucket("admin-bucket-code");
		
//		getDomainListByBucket("image-bucket");
		
//		boolean flag = dropBucketByName("ebook-bucket");
//		System.out.println(flag + "");
		
		
		getResourcesByBucket("test-bucket");
		
	}
	
	
	/**
	 * 获取管理凭证
	 * @return
	 */
	public static String getAccessToken(String interfaceName) {
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		String accessToken = auth.sign(interfaceName);
		return accessToken;
	}
	
	/**
	 * 获取一个账号下的所有空间 名称列表
	 * @return
	 */
	public static List<String> getAllBuckets() {
		List<String> bucketList = new ArrayList<String>();
		String connector = "/buckets\n";
		String url = "http://rs.qbox.me" + connector;
		String accessToken = getAccessToken(connector);
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder()
						.url(url)
						.addHeader("Content-Type", "application/x-www-form-urlencoded")
						.addHeader("Authorization", "QBox " + accessToken)
						.build();
		try{
			Response resp = client.newCall(req).execute();
			System.out.println(resp.code() + ":" + resp.message());
			if(resp.isSuccessful()) {
				String result = resp.body().string();
				System.out.println(result);
			}
		}catch(HTTPException | IOException e) {
			e.printStackTrace();
		}
		return bucketList;
	}
	
	
	/**
	 * 创建一个存储空间
	 * @param bucketName 创建的存储空间名称
	 * @return true:创建成功, false:创建失败
	 */
	public static boolean createBucket(String bucketName) {
		boolean flag = false;
		String encodeBktName = UrlSafeBase64.encodeToString(bucketName);
		String connector = "/mkbucketv2/" + encodeBktName + "/region/z0/global/false\n";
		String url = "http://rs.qiniu.com" + connector;
		String accessToken = getAccessToken(connector);
		
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder()
							.url(url)
							.addHeader("Content-Type", "application/x-www-form-urlencoded")
							.addHeader("Authorization", "QBox " + accessToken)
							.build();
		try {
			Response resp = client.newCall(req).execute();
			System.out.println(resp.code() + ":" + resp.message());
			if(resp.isSuccessful()) {
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/**
	 * 获取一个空间绑定的域名列表
	 * @param bucketName 空间名称
	 * @return List<String>
	 */
	public static List<String> getDomainListByBucket(String bucketName) {
		List<String> domains = null;
		
		String connector = "/v6/domain/list?tbl=" + bucketName + "\n";
		String url = "http://api.qiniu.com" + connector;
		String accessToken = getAccessToken(connector);
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder()
							.url(url)
							.addHeader("Content-Type", "application/x-www-form-urlencoded")
							.addHeader("Authorization", "QBox " + accessToken)
							.build();
		try{
			Response resp = client.newCall(req).execute();
			if(resp.isSuccessful()) {
				String result = resp.body().string();
				System.out.println(result);
			}
		} catch(HTTPException | IOException e) {
			e.printStackTrace();
		}
		
		return domains;
	}
	
	
	/**
	 * 根据空间名称删除空间， 空间删除后， 该空间的内容都会删除
	 * @param bucketName
	 * @return
	 */
	public static boolean dropBucketByName(String bucketName) {
		boolean flag = false;
		String connector = "/drop/" + bucketName + "\n";
		String url = "http://rs.qiniu.com" + connector;
		String accessToken = getAccessToken(connector);
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder()
						.url(url)
						.addHeader("Content-Type", "application/x-www-form-urlencoded")
						.addHeader("Authorization", "QBox " + accessToken)
						.build();
		try {
			Response resp = client.newCall(req).execute();
			flag = resp.isSuccessful();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/**
	 * 获取指定空间里的所有资源条目
	 * @param bucketName 空间名称
	 * @return
	 */
	public static List<String> getResourcesByBucket(String bucketName) {
		List<String> result = new ArrayList<String>();
//		String encodeBucketName = UrlSafeBase64.encodeToString(bucketName);
		String connector = String.format("/list?bucket=%s", bucketName + "\n");//"/list?bucket=" + encodeBucketName + "\n";
		String url = "http://rsf.qbox.me" + connector;
		String accessToken = getAccessToken(connector);
		
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder()
							.url(url)
							.addHeader("Content-Type", "application/x-www-form-urlencoded")
							.addHeader("Authorization", "QBox " + accessToken)
							.build();
		try {
			Response resp = client.newCall(req).execute();
			if(resp.isSuccessful()) {
				System.out.println(resp.body().string());
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	

	

}
