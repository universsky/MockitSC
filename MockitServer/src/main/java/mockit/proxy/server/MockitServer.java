/**
 * 
 */
package mockit.proxy.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 
 * @author 一剑 2015年11月26日 上午10:49:08
 */
public class MockitServer {

	public MockitServer(int port) throws IOException {
		myTcpPort = port;
		myServerSocket = new ServerSocket(myTcpPort);
		myThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (true)
						new HttpSession(myServerSocket.accept());
				} catch (IOException ioe) {
				}
			}
		});
		myThread.setDaemon(true);
		myThread.start();
	}

	public static void main(String[] args) {
		try {
			new MockitServer(8888);
		} catch (IOException ioe) {
			System.err.println("MockitServer start FAILED:\n" + ioe);
			System.exit(-1);
		}
		System.out.println("MockitServer start ...");
		try {
			System.in.read();
		} catch (Throwable t) {
		}
		System.out.println("MockitServer stop ...");
	}

	public void stop() {
		try {
			myServerSocket.close();
			myThread.join();// Waits for this thread to die.
		} catch (IOException ioe) {
		} catch (InterruptedException e) {
		}
	}

	int myTcpPort = 8888;
	private ServerSocket myServerSocket;
	private Thread myThread;

}
