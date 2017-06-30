package com.qiniu.kodo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.ws.http.HTTPException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.qiniu.util.Auth;

/**
 * 七牛云 下载 
 * @author xuhuanchao
 *
 */
public class QiniuDownload {
	
	//个人密钥
	private static final String ACCESS_KEY = "Yf1g-y2WlerdVBX-YtQ6-N7wrsSeQubxTesvzvwf";
	private static final String SECRECT_KEY = "7KBRN1YDgbDLRaVWxziOrA_YtFRqguNpSsmbtzpy";

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		String downloadPath = "D:\\Downloads\\qiniu\\demo\\";
//		String url = "http://orfsrm5qv.bkt.clouddn.com/img_desktop.jpg";
//		downloadByGet(url, downloadPath);
		
		String privateUrl = "http://orh27r69f.bkt.clouddn.com/Freehand.jpg";
		getPrivateResource(privateUrl, downloadPath);
		
		
	}
	
	
	/**
	 * ͨ��http get �����������
	 * @param targetUrl ���ص��ļ�����
	 * @param downloadPath ���ش洢·��
	 */
	public static void downloadByGet(String targetUrl, String downloadPath) {
		OkHttpClient client = new OkHttpClient();
		OutputStream os = null;
		try{
			Request req = new Request.Builder().url(targetUrl).build();
			Response resp = client.newCall(req).execute();
			if (resp.isSuccessful()) {
				byte[] data = resp.body().bytes();
				String[] temp = targetUrl.split("/");
				String fileName = temp[temp.length-1] ;
				File file = new File(downloadPath + fileName);
				os = new FileOutputStream(file);
				os.write(data);
			}
		} catch(HTTPException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ����˽�пռ���ļ���Դ
	 * @param url
	 * @param downloadPath
	 */
	public static void getPrivateResource(String url, String downloadPath) {
		Auth auth = Auth.create(ACCESS_KEY, SECRECT_KEY);
		String realUrl = auth.privateDownloadUrl(url, 3600);
		String[] temp = url.split("/");
		String fileName = temp[temp.length - 1];
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder().url(realUrl).build();
		InputStream is = null;
		OutputStream os = null;
		try{
			Response resp = client.newCall(req).execute();
			if (resp.isSuccessful()) {
				is = resp.body().byteStream();
				byte[] data = readInputStream(is);
				File file = new File(downloadPath + fileName);
				os = new FileOutputStream(file);
				os.write(data);
				os.flush();
			}
		} catch(IOException | HTTPException e) {
			e.printStackTrace();
		} finally {
			try{
				os.close();
				is.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * ��ȡ����Ϣ�� תΪ�ֽ�����
	 * @param is
	 * @return
	 */
	public static byte[] readInputStream(InputStream is) {
		byte[] buff = new byte[2 * 1024];
		if(is == null){
			return buff;
		}
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		int len = 0;
		try {
			while((len = is.read(buff)) != -1) { //����is �е��ֽڻ��嵽 buff ����ֽ�������
				writer.write(buff, 0, len);		 //���ֽ�����buff �е����ݣ� д�뵽 writer������
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return writer.toByteArray();
	}

}
