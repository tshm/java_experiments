package ppserve;

public class ServerObject extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
	private String string;

	public ServerObject() throws java.rmi.RemoteException {
		string = new String("first");
	}

	public synchronized String pop() throws java.rmi.RemoteException {
		return string;
	}

	public synchronized void push(String s) throws java.rmi.RemoteException {
		string = s;
	}
}
