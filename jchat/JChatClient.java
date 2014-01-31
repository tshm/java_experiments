package jchat;
import java.rmi.*;
import java.rmi.server.*;
import java.awt.*;
import java.awt.event.*;

public class JChatClient extends java.applet.Applet implements Listener, java.io.Serializable {
	Server server;
	static boolean isApplet = true;
	static String ServerHost;
	static String id;

	// GUI components
	Frame frame;
	TextField inputfield;
	TextArea messageboard;
	Button sendbutton;
	List clientlist;

	public static void main(String argv[]) {
		isApplet = false;
		ServerHost = argv[0];
		id = argv[1];
		new JChatClient();
	}

	JChatClient() {
		frame = new Frame("JChat");
		frame.setFont(new Font("Serif", Font.PLAIN, 14));
		inputfield = new TextField(20);
		messageboard = new TextArea();
		messageboard.setEditable(false);
		sendbutton = new Button("Send");
		Panel inputpanel = new Panel();
		inputpanel.setLayout(new FlowLayout());
		inputpanel.add(inputfield);
		inputpanel.add(sendbutton);
		clientlist = new List();
		setLayout(new BorderLayout());
		add("North", clientlist);
		add("South", inputpanel);
		add("Center", messageboard);
		frame.add(this);
		frame.pack();
		frame.show();
		frame.setSize(400, 400);

		sendbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				writeMessage(inputfield.getText());
			}
		});

		lookupServer();
		register();
	}


	void lookupServer() {
		try {
			if (isApplet) {
				server = (Server)Naming.lookup("//" + 
						getCodeBase().getHost() + "/jc");
			} else {
				server = (Server)Naming.lookup("//" + ServerHost + "/jc");
			}
			UnicastRemoteObject.exportObject(this);
		} catch (Exception e) {
			System.out.println("JChat exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void register() {
		try {
			server.registerListener(id, this);
		} catch (Exception e) {
			System.out.println("JChat exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void writeMessage(String s) {
		try {
			server.writeMessage(id, s);
		} catch (Exception e) {
			System.out.println("JChat Exception: " + e);
			e.printStackTrace();
		}
	}

	public void listen(String id, String s) throws RemoteException {
		messageboard.append(id + " > " + s + "\n");
	}

	public void serverHasDied() throws RemoteException {
		finalize();
	}

	public void finalize() {
		try {
			server.unregisterListener(id);
		} catch (Exception e) {
			System.out.println("JChat exception: can't disconnect\n" + 
					e.getMessage());
			e.printStackTrace();
		}
	}
}
