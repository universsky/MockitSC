/**
 * http-proxy HttpSession.java http.proxy
 */
package mockit.proxy.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Types;
import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author 一剑 2015年11月26日 上午11:09:17
 */

public class HttpSession implements Runnable {
	private Socket mySocket;
	static long threadCount = 0;

	private JdbcTemplate jdbcTemplate;

	@SuppressWarnings("resource")
	ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
			"classpath:applicationContext.xml");

	public HttpSession(Socket s) {
		jdbcTemplate = (JdbcTemplate) applicationContext.getBean("jdbcTemplate");

		mySocket = s;
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	public void run() {

		System.out.println("\n-----------------------------------\n");
		try {
			++threadCount;

			InputStream is = mySocket.getInputStream();
			if (is == null)
				return;
			final int bufsize = 8192;
			byte[] buf = new byte[bufsize];
			int splitbyte = 0;
			int rlen = 0;

			int read = is.read(buf, 0, bufsize);
			while (read > 0) {
				rlen += read;
				splitbyte = findHeaderEnd(buf, rlen);
				if (splitbyte > 0)
					break;
				read = is.read(buf, rlen, bufsize - rlen);
			}

			ByteArrayInputStream ByteArrayInputStream = new ByteArrayInputStream(buf, 0, rlen);
			BufferedReader BufferedReader = new BufferedReader(new InputStreamReader(ByteArrayInputStream));
			Host host = new Host();

			String line;
			boolean flag = false;
			while ((line = BufferedReader.readLine()) != null) {
				System.out.println(line);

				filterRequest(line, mySocket);

				if (line.toLowerCase().startsWith("host:")) {
					host.host = line;
					flag = true;
				}
			}

			if (!flag) {
				mySocket.getOutputStream().write("error!".getBytes());
				mySocket.close();
				return;
			}

			host.setHost();

			System.out.println(host.address + ":" + host.port);

			try {
				pipe(buf, rlen, mySocket, mySocket.getInputStream(), mySocket.getOutputStream(), host);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
		}
	}

	/**
	 * 过滤出HTTP请求，记录数据库
	 * 
	 * @param line
	 */
	private void filterRequest(String line, Socket socket) {
		if (isRequest(line)) {
			Request record = new Request();
			record.setGmtCreate(new Date());
			record.setGmtModify(new Date());
			record.setRequestTime(new Date());
			record.setIp(socket.getInetAddress().toString());

			int beginIndex = 0;
			if (line.indexOf("GET") != -1) {
				beginIndex = 4;
				record.setMethod("GET");
			} else if (line.indexOf("POST") != -1) {
				beginIndex = 5;
				record.setMethod("POST");
			} else if (line.indexOf("CONNECT") != -1) {
				beginIndex = 8;
				record.setMethod("CONNECT");
			} else if (line.indexOf("DELETE") != -1) {
				beginIndex = 7;
				record.setMethod("DELETE");
			} else if (line.indexOf("HEAD") != -1) {
				beginIndex = 5;
				record.setMethod("HEAD");
			} else if (line.indexOf("OPTIONS") != -1) {
				beginIndex = 8;
				record.setMethod("OPTIONS");
			} else if (line.indexOf("TARCE") != -1) {
				beginIndex = 7;
				record.setMethod("TARCE");
			} else {
				beginIndex = line.indexOf("http://");

			}

			int endIndex = line.indexOf("HTTP/") - 1;
			String url = line.substring(beginIndex, endIndex);
			record.setUrl(url);

			System.out.println(record);

			final String insertSql = "INSERT INTO `mockit`.`request` (`method`, `url`, `ip`, `gmt_create`, `gmt_modify`, `request_time`) VALUES (?,?, ?, ?, ?, ?)";
			Object[] params = new Object[] { record.getMethod(), record.getUrl(), record.getIp(), record.getGmtCreate(),
					record.getGmtModify(), record.getRequestTime() };
			int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP,
					Types.TIMESTAMP };

			int row = jdbcTemplate.update(insertSql, params, types);
			System.out.println(row + " row inserted.");
		}

	}

	private int findHeaderEnd(final byte[] buf, int rlen) {
		int splitbyte = 0;
		while (splitbyte + 3 < rlen) {
			if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && buf[splitbyte + 2] == '\r'
					&& buf[splitbyte + 3] == '\n')
				return splitbyte + 4;
			splitbyte++;
		}
		return 0;
	}

	void pipe(byte[] request, int requestLen, Socket client, InputStream clientIS, OutputStream clientOS, Host host)
			throws Exception {
		byte bytes[] = new byte[1024 * 32];
		Socket socket = new Socket(host.address, host.port);
		socket.setSoTimeout(3000);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		try {
			do {
				os.write(request, 0, requestLen);
				int resultLen = 0;
				try {
					while ((resultLen = is.read(bytes)) != -1 && !mySocket.isClosed() && !socket.isClosed()) {
						clientOS.write(bytes, 0, resultLen);
					}
				} catch (Exception e) {
				}

			} while (!mySocket.isClosed() && (requestLen = clientIS.read(request)) != -1);
		} catch (Exception e) {
		}

		os.close();
		is.close();
		clientIS.close();
		clientOS.close();
		socket.close();
		mySocket.close();

	}

	private boolean isRequest(String temp) {
		if (null == temp)
			return false;

		if (temp.isEmpty())
			return false;

		return temp.contains("HTTP/");
	}

	final class Host {
		public String address;
		public int port;
		public String host;

		public boolean setHost() {
			if (host == null)
				return false;
			int start = host.indexOf(": ");
			if (start == -1)
				return false;
			int next = host.indexOf(':', start + 2);
			if (next == -1) {
				port = 80;
				address = host.substring(start + 2);
			} else {
				address = host.substring(start + 2, next);
				port = Integer.valueOf(host.substring(next + 1));
			}
			return true;
		}
	}
}
