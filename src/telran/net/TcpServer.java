package telran.net;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
public class TcpServer implements Runnable {
private int port;
private ApplProtocol protocol;
private ServerSocket serverSocket;
private ExecutorService executor;   // V.R.
private final int N_THREADS = 2;   // V.R.
private int currentClientId = 1;   // V.R.

public TcpServer(int port, ApplProtocol protocol) throws Exception{
	this.port = port;
	this.protocol = protocol;
	serverSocket = new ServerSocket(port);
//	executor = getRegularThreadExecutor();
	executor = getDeamonThreadExecutor();
}
	@Override
	public void run() {
		System.out.println("Server is listening on the port " + port);
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				TcpClientServer client = new TcpClientServer(socket, protocol, currentClientId);
				System.out.println("Connection with clientId="+currentClientId++);
				executor.execute(client);  // V.R.
				/*
				Thread threadClient = new Thread(client);
				System.out.println(threadClient.getName());
				threadClient.start();
				*/
			} catch (Exception e) {				
				e.printStackTrace();
				break;
			}
		}
		executor.shutdown();
	}
	private class DeamonThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		}		
	}
	
	private ExecutorService getDeamonThreadExecutor() {
		return Executors.newFixedThreadPool(N_THREADS, new DeamonThreadFactory());
		/*
		return Executors.newFixedThreadPool(N_THREADS, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
		*/
	}
	
	private ExecutorService getRegularThreadExecutor() {
		return Executors.newFixedThreadPool(N_THREADS);
	}
}
