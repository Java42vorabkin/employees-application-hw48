package telran.net;
import java.io.*;
import java.net.*;

import telran.net.dto.*;
public class TcpClientServer implements Runnable {
Socket socket;
ObjectInputStream reader;
ObjectOutputStream writer;
ApplProtocol protocol;
private int clientId;
public TcpClientServer(Socket socket, ApplProtocol protocol, int clientId) throws Exception{
	this.socket = socket;
	reader = new ObjectInputStream(socket.getInputStream());
	writer = new ObjectOutputStream(socket.getOutputStream());
	this.protocol = protocol;
	this.clientId = clientId;
}
	@Override
	public void run() {
		try {
			while(true) {
				Request request = (Request) reader.readObject();
				Response response = protocol.getResponse(request);
				writer.writeObject(response);
				System.out.println(
					Thread.currentThread().getName() 
					+ " clientId="+clientId
					+ " isDaemon=" + Thread.currentThread().isDaemon());  // V.R.
			}
		} catch(EOFException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
