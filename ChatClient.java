package chatroom;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
*	Allison Robbins
 **/
public class ChatClient extends ChatWindow
{
	// Inner class used for networking
	// GUI Objects
	private JTextField serverTxt;
	private JTextField nameTxt;
	private JButton connectB;
	private JTextField messageTxt;
	private JButton sendB;
	public ChatClient()
    {
		super();
		this.setTitle("Chat Client");
		printMsg("Chat Client Started.");
		// GUI elements at top of window
		// Need a Panel to store several buttons/text fields
		serverTxt = new JTextField("localhost");
		serverTxt.setColumns(15);
		nameTxt = new JTextField("Name");
		nameTxt.setColumns(10);
		connectB = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(serverTxt);
		topPanel.add(nameTxt);
		topPanel.add(connectB);
		contentPane.add(topPanel, BorderLayout.NORTH);

		// GUI elements and panel at bottom of window
		messageTxt = new JTextField("");
		messageTxt.setColumns(40);
		sendB = new JButton("Send");
		JPanel botPanel = new JPanel();
		botPanel.add(messageTxt);
		botPanel.add(sendB);
		contentPane.add(botPanel, BorderLayout.SOUTH);

		// Resize window to fit all GUI components
		this.pack();

		// Setup the communicator so it will handle the connect button
		Communicator comm = new Communicator();
		connectB.addActionListener(comm);
		sendB.addActionListener(comm);
	}
	class Communicator implements Runnable, ActionListener
	{
        public Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private int port = 2113;

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            if(actionEvent.getActionCommand().compareTo("Connect") == 0)
            {
                connectClient();
            }
            else if(actionEvent.getActionCommand().compareTo("Send") == 0)
            {
                sendMsg(messageTxt.getText());
            }
        }
        public void connectClient()
		{
			try
			{
				socket = new Socket(serverTxt.getText(), port);
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				sendMsg("Hello server");
				connect();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		public void readMsg() throws IOException {
			String s = reader.readLine();
			printMsg(s);
		}
		public void sendMsg(String s)
		{
			if(s.length()>=6)
			{
				String a = s.substring(0,6);
				if(a.equals("/name "))
				{
					a = s.substring(6);
					writer.println(nameTxt.getText()+" changed name to: "+a);
					nameTxt.setText(a);
					return;
				}
			}
			writer.println(nameTxt.getText()+": "+s);
		}
		public void connect()
		{
			Thread t = new Thread(this);
			t.start();
		}
		public void run()
		{
			while(true)
			{
				try
				{
					readMsg();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String args[])
	{
		new ChatClient();
	}
}
