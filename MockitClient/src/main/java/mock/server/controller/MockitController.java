package mock.server.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import mock.server.Request;

@Controller
public class MockitController {

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView get(Model model, @RequestParam(value = "method", required = false) String method,
			@RequestParam(value = "url", required = false) String url,
			@RequestParam(value = "ip", required = false) String ip,
			@RequestParam(value = "requestTime", required = false) String requestTime) {

		List<Request> list = getRequests(method, url, ip, requestTime);
		// List<Request> list = new ArrayList<Request>();

		model.addAttribute("method", method);
		model.addAttribute("url", url);
		model.addAttribute("ip", ip);
		model.addAttribute("requestTime", requestTime);

		model.addAttribute("requests", list);
		return new ModelAndView("/get");

	}

	@RequestMapping(value = "/getjson", method = RequestMethod.GET)
	@ResponseBody
	public String getjson(Model model, @RequestParam(value = "method", required = false) String method,
			@RequestParam(value = "url", required = false) String url,
			@RequestParam(value = "ip", required = false) String ip,
			@RequestParam(value = "requestTime", required = false) String requestTime) {

		List<Request> list = getRequests(method, url, ip, requestTime);
		// List<Request> list = new ArrayList<Request>();
		return JSON.toJSONString(list);

	}

	private List<Request> getRequests(String method, String url, String ip, String requestTime) {
		if (null == method)
			method = "";
		if (null == url)
			url = "";
		if (null == ip)
			ip = "";
		if (null == requestTime)
			requestTime = "";

		List<Request> list = new ArrayList<Request>();

		String driver = "com.mysql.jdbc.Driver";
		String jdbcUrl = "jdbc:mysql://ip:3306/mockit";
		String user = "xx";
		String password = "xx";

		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
			if (!conn.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");
			}
			Statement statement = conn.createStatement();

			String sql = "SELECT * FROM mockit.request " + "where " + "method like '%" + method + "%' and "
					+ "url like '%" + url + "%' and " + "ip like '%" + ip + "%' and " + "request_time like '%"
					+ requestTime + "%' order by gmt_create desc";

			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				int qid = rs.getInt("id");
				String qmethod = rs.getString("method");
				String qurl = rs.getString("url");
				String qip = rs.getString("ip");
				qip = qip.replace("/", "");
				Timestamp qrequestTime = rs.getTimestamp("request_time");
				String strTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(qrequestTime);

				Request Request = new Request();
				Request.setId(qid);
				Request.setMethod(qmethod);
				Request.setUrl(qurl);
				Request.setIp(qip);
				Request.setRequestTime(strTime);

				list.add(Request);
			}

			rs.close();
			conn.close();

		} catch (ClassNotFoundException e) {
			System.out.println("Can't find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
