/**
 * 
 */
package mockit.proxy.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * @author jack
 *
 */
public class MockitServerTest {

	public static void main(String args[]) throws Exception {
		Proxy proxy = null;
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)); // 实例化本地代理对象，端口为
		URL url = new URL("http://www.baidu.com");
		HttpURLConnection action = (HttpURLConnection) url.openConnection(proxy); // 使用代理打开网页
		InputStream in = action.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String lin = System.getProperty("line.separator");
		for (String temp = br.readLine(); temp != null; temp = br.readLine()) {
			sb.append(temp + lin);
		}
		br.close();
		in.close();
		System.out.println(sb);

	}

}
