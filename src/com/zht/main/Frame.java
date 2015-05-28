package com.zht.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.TableColumnModel;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.zht.entity.Stu;
import com.zht.entity.StuInfo;
import com.zht.url.UrlFactory;

public class Frame extends JFrame {

	private static Logger log = Logger.getLogger(Frame.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Icon icon;
	
	final JLabel label = new JLabel();

	final JLabel validCode = new JLabel();

	private String cookie;

	private UrlFactory url = new UrlFactory();

	public Stu stu;

	public StuInfo stuInfo;

	TextField code = new TextField();

	TextField queryCode = new TextField();

	ObjectMapper mapper = new ObjectMapper();
	
	public Frame() {
		
		String[][] rowData = new String[6][6];
		rowData[0][0] = "日期";
		rowData[0][1] = "星期一";
		rowData[0][2] = "星期二";
		rowData[0][3] = "星期三";
		rowData[0][4] = "星期四";
		rowData[0][5] = "星期五";
		
		rowData[1][0] = "2015-05-19";
		rowData[2][0] = "2015-05-20";
		rowData[3][0] = "2015-05-21";
		rowData[4][0] = "2015-05-22";
		rowData[5][0] = "2015-05-23";
		
		String[] columnNames = new String[5];
		columnNames[0] = "2015-05-19";
		columnNames[1] = "2015-05-20";
		columnNames[2] = "2015-05-21";
		columnNames[3] = "2015-05-22";
		columnNames[4] = "2015-05-23";
		
		final JTable table = new JTable(rowData,columnNames);
		
		this.add(table);
		
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				log.debug("选中行-->"+table.getSelectedRowCount());
				int[] a = table.getSelectedRows();
				log.debug(a.length);
				log.debug(a[0]);
				
//				log.debug(table.getRowCount());
				log.debug(table.getColumnName(a[0]+1));
				
			}
		});
		
		init();
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(300, 300);
		
		JButton button = new JButton("换一张");
		this.add(button, BorderLayout.WEST);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				init();
			}
		});
		this.add(label);

		// Dimension dimension = new Dimension();
		// dimension.height = 20;
		// dimension.width = 100;
		// textCode.setPreferredSize(dimension);
		code.setText("输入验证码");
		this.add(code);

		// JButton enter = new JButton("确认");
		// enter.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // TODO Auto-generated method stub
		// System.out.println("有动作");
		// }
		// });
		// this.add(enter);

		final TextField user = new TextField();
		user.setText("11099677");

		final TextField pwd = new TextField();
		pwd.setText("05040");
		this.add(user);
		this.add(pwd);

		JButton submit = new JButton("登录");

		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				log.debug("账号-->" + user.getText());
				log.debug("密码-->" + pwd.getText());
				log.debug("验证码-->" + code.getText());
				log.debug("Cookie-->" + cookie);
				Map<String, String> loginParam = new HashMap<String, String>();
				loginParam.put("Account", user.getText());
				loginParam.put("Pwd", pwd.getText());
				loginParam.put("ValidCode", code.getText());
				loginParam.put("AjaxMethod", "LOGIN");
				loginParam.put("Cookie", cookie);
				try {
					// 登录成功
					String loginResult = url.login(loginParam);
					log.debug("登录结果--->" + loginResult);
					
					// 读取个人信息
					String jsonStuInfo = url.userInfo(cookie, "jbxx");
					log.debug("个人信息--->" + jsonStuInfo);
					if (jsonStuInfo == null) {
						log.debug("获取信息失败");
						return;
					}
					ObjectMapper mapper = new ObjectMapper();
					stu = mapper.readValue(jsonStuInfo, Stu[].class)[0];
					log.debug("name-->" + stu.getFchrStudentName());

					// 刷新查询验证码
					validCode();
					// 获取个人约车信息
					stu();
					// 查询可约车辆
					while(true){
						try {
							Thread.sleep(3000);
						} catch (Exception e2) {
							log.error("定时器出问题!!!",e2);
							// TODO: handle exception
						}
						browser();
					}

				} catch (Exception e1) {
					log.debug("登录失败,出现异常",e1);
					e1.printStackTrace();
				}
			}
		});
		this.add(submit);
		// this.add(validCode);//上面验证码只需请求即可不用显示

		Dimension di = new Dimension();
		di.height = 25;
		di.width = 70;
		queryCode.setPreferredSize(di);
		this.add(queryCode);

	}

	public void init() {
		try {
			HttpResponse response = url.validCode();
			InputStream input = response.getEntity().getContent();
			byte[] data = new byte[(int) response.getEntity()
					.getContentLength()];
			input.read(data);
			input.close();
			cookie = response.getLastHeader("Set-Cookie").getValue();
			log.debug("请求验证码Cookie--->" + cookie);
			icon = new ImageIcon(data);
			label.setText("验证码");
			label.setIcon(icon);
		} catch (Exception e) {
			log.debug("初始化验证码",e);
			e.printStackTrace();
		}
	}
	
	public void validCode() {
		try {
			HttpResponse response = url.validCode();
			InputStream input = response.getEntity().getContent();
			byte[] data = new byte[(int) response.getEntity()
					.getContentLength()];
			input.read(data);
			input.close();
			// cookie = response.getLastHeader("Set-Cookie").getValue();
			// log.debug("请求验证码Cookie--->"+cookie);
			icon = new ImageIcon(data);
			validCode.setText("查询验证码");
			validCode.setIcon(icon);
		} catch (Exception e) {
			log.debug("查询初始化验证码",e);
			e.printStackTrace();
		}
	}

	public Map<String, Set<String>> appoint() {
		// 获取用户指定时间
		Map<String, Set<String>> date = new HashMap<String, Set<String>>();
		Set<String> time = new HashSet<String>();
		time.add("2");// ,9-13
		time.add("3");// ,13-17
		time.add("4");// ,17-19
		time.add("5");// ,19-21
		date.put("2015-05-31(星期日)", time);
		
		Set<String> time1 = new HashSet<String>();
		time1.add("2");// ,9-13
		time1.add("3");// ,13-17
		time1.add("4");// ,17-19
		time1.add("5");// ,19-21
		date.put("2015-05-28(星期四)", time1);
		
		Set<String> time2 = new HashSet<String>();
		time2.add("2");// ,9-13
		time2.add("3");// ,13-17
		time2.add("4");// ,17-19
		time2.add("5");// ,19-21
		date.put("2015-05-29(星期五)", time2);
		
		Set<String> time3 = new HashSet<String>();
		time3.add("2");// ,9-13
		time3.add("3");// ,13-17
		time3.add("4");// ,17-19
		time3.add("5");// ,19-21
		date.put("2015-05-30(星期六)", time3);
		
		Set<String> time4 = new HashSet<String>();
		time4.add("2");// ,9-13
		time4.add("3");// ,13-17
		time4.add("4");// ,17-19
		time4.add("5");// ,19-21
		date.put("2015-06-01(星期一)", time4);
		
		Set<String> time5 = new HashSet<String>();
		time5.add("2");// ,9-13
		time5.add("3");// ,13-17
		time5.add("4");// ,17-19
		time5.add("5");// ,19-21
		date.put("2015-06-02(星期二)", time5);
		return date;
	}

	/**
	 * 约车信息
	 */
	public void browser() throws Exception {
		String vc = code.getText();
		Map<String, String> param = new HashMap<String, String>();
		param.put("stuid", stu.getFchrStudentID());
		param.put("cartypeid", "01");
		param.put("carid", "");
		param.put("ValidCode", vc);
		param.put("Cookie", cookie);
		try {
			String yuecheCase = url.browser(param);

			log.debug("约车情况-->" + yuecheCase);

			// 0:成功标志，1：表头数据(用于HTML显示)，2：表体数据(分析)
			String[] yuecheArray = yuecheCase.split("\\|\\|");
			
			log.debug("约车情况--->" + yuecheArray[2]);
			
			log.error("约车情况--->" + yuecheArray[2]);
			
			if (!yuecheArray[0].equalsIgnoreCase("success")) {
				log.debug("查询约车情况失败！！");
				return;
			}
			
			Map<String, String>[] carResult = mapper.readValue(yuecheArray[2],Map[].class);

			Map<String, Set<String>> userDate = appoint();

			Set<String> userDateSet = userDate.keySet();// 获取用户所有日期

			for (Map<String, String> map : carResult) {
				String sDate = map.get("fchrdate");
				if (!userDateSet.contains(sDate)) {
					continue;
				}
				// 用户指定时间
				Set<String> userTimeSet = userDate.get(sDate);
				for (String str : userTimeSet) {

					String yueCheVal = map.get(str);
					log.debug("传入yueche参数"+str);
					log.debug("约车---->" + yueCheVal);
					// 根据显示值判断是否已经越过车（越过车显示车号，没有“/”符号）
					if (!yueCheVal.contains("/")) {
						continue;
					}
					String[] valArray = yueCheVal.split("\\/");
					if (valArray[1].equalsIgnoreCase("0")) {
						log.debug("不可约车-->" + sDate + "-->" + valArray[0]);
						continue;
					};
					
					yueche(sDate,str);
				}
			}
		} catch (Exception e) {
			log.debug("查询可约车辆有误！！！",e);
			e.printStackTrace();
		}
	}

	/**
	 * 用户约车信息(里面有约车令牌)
	 */
	public void stu() {
		try {
			Map<String, String> shuParam = new HashMap<String, String>();
			shuParam.put("loginType", "2");
			shuParam.put("method", "stu");
			shuParam.put("stuid", stu.getFchrStudentID());
			shuParam.put("sfznum", "");
			shuParam.put("carid", "");
			shuParam.put("ValidCode", code.getText());
			shuParam.put("Cookie", cookie);
			String stuArray = url.stuHdl(shuParam);
			log.debug("个人约车信息--->" + stuArray);
			String[] stuAr = stuArray.split("\\|\\|\\|\\|");
			stuInfo = mapper.readValue(stuAr[0], StuInfo[].class)[0];
			// 个人约车结果(Map方式)这里需要grid UI
			// Map<String, String>[] stuInfoMap = mapper.readValue(stuAr[1],Map[].class);
		} catch (Exception e) {
			// TODO: handle exception
			log.debug("获取用户约车信息,失败!!!",e);
			e.printStackTrace();
		}
	}
	/**
	 * 约车
	 * @param str2 
	 * @param sDate 
	 * @throws Exception
	 */
	int threadCount = 0;
	public void yueche(String sDate, String time) {
		Map<String, String> yueCheParam = new HashMap<String, String>();
		yueCheParam.put("loginType", "2");
		yueCheParam.put("method", "yueche");
		yueCheParam.put("stuid", stuInfo.getFchrStudentID());
		yueCheParam.put("bmnum", stuInfo.getFchrRegistrationID());
		// yueCheParam.put("start", "13");//开始时间
		// yueCheParam.put("end", "17");//结束时间
		yueCheParam.put("lessionid", stuInfo.getFchrLessonID());
		yueCheParam.put("trainpriceid", stuInfo.getFchrTrainPriceID());
		yueCheParam.put("lesstypeid", stuInfo.getFchrLessonTypeID());// 约车类型？
		// yueCheParam.put("date", "2015-05-26");//约车日期
		yueCheParam.put("id", "1");// 固定值 ,约车id=0:自选车号；id=1：随机分配
		yueCheParam.put("carid", "");
		yueCheParam.put("ycmethod", "03");// 约车方法？
		yueCheParam.put("cartypeid", stuInfo.getFchrCarTypeID());
		// yueCheParam.put("trainsessionid", "03"); //课程时段ID 和上面 时间 对比成立
		yueCheParam.put("ReleaseCarID", "");
		yueCheParam.put("ValidCode", code.getText());
		yueCheParam.put("Cookie", cookie);
		yueCheParam.put("date", sDate.split("\\(")[0]);
		Map<String,String> map = new HashMap<String, String>();
		map.put("1", "7-9");
		map.put("2", "9-13");
		map.put("3", "13-17");
		map.put("4", "17-19");
		map.put("5", "19-21");
		yueCheParam.put("start", map.get(time).split("-")[0]);// 开始时间
		yueCheParam.put("end", map.get(time).split("-")[1]);// 结束时间
		
		yueCheParam.put("trainsessionid", "0"+time);
		try {
			int count = 0;
			while(count < 2){
				log.error("尝试约车!!");
				count++;
				String yuecheResult = url.yueche(yueCheParam);
				log.error("尝试结果--->"+yuecheResult);
//				JOptionPane.showMessageDialog(null, "正在尝试", "test", JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			log.error("约车错误!!!",e);
			e.printStackTrace();
		}
		
//		ThreadPool tp = new ThreadPool(yueCheParam, threadCount++);
//		tp.start();
	}
}
