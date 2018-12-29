package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Allison Robbins
 */
public class ChatServer extends ChatWindow
{
	ArrayList<ClientHandler> clientList = new ArrayList<>();
	public ChatServer()
	{
		super();
		this.setTitle("Chat Server");
		this.setLocation(80,80);
		try {
			ServerSocket srv = new ServerSocket(2113);
			while (true)
			{
				printMsg("Waiting for a connection");
				Socket socket = srv.accept();
				ClientHandler handler = new ClientHandler(socket);
				handler.connect();
				clientList.add(handler);
			}
		} catch (IOException e)
		{
			System.out.println(e);
		}
	}
	class ClientHandler implements Runnable
	{
		PrintWriter writer;
		BufferedReader reader;
		public ClientHandler(Socket socket)
		{
			try
			{
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (IOException e)
			{
					printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}
		public void handleConnection()
		{
			try {
				while(true) {
					String s = readMsg();
					sendMsg(s);
				}
			}
			catch (IOException e){
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}
		public String readMsg() throws IOException
		{
			String s = reader.readLine();
			printMsg(s);
			return s;
		}
		public void sendMsg(String s)
		{
			for(int i=0; i<clientList.size(); i++)
			{
				clientList.get(i).writer.println(s);
			}
		}
		@Override
		public void run()
		{
			this.handleConnection();
		}
		public void connect()
		{
			Thread t = new Thread(this);
			t.start();
		}
	}
	public static void main(String args[])
	{
		new ChatServer();
	}
}
