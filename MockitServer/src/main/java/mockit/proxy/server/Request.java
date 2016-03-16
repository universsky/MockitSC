/**
 * mock-server Request.java mockit.proxy.server
 */
package mockit.proxy.server;

import java.util.Date;

/**
 * @author 一剑 2015年11月26日 下午2:50:33
 */
public class Request {
	private Integer id;

	private String method;

	private String url;

	private String ip;

	private Date gmtCreate;

	private Date gmtModify;

	private Date requestTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method == null ? null : method.trim();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url == null ? null : url.trim();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip == null ? null : ip.trim();
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", method=" + method + ", url=" + url + ", ip=" + ip + ", gmtCreate=" + gmtCreate
				+ ", gmtModify=" + gmtModify + ", requestTime=" + requestTime + "]";
	}

}
