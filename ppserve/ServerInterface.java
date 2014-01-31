package ppserve;

public interface ServerInterface extends java.rmi.Remote {
	String pop() throws java.rmi.RemoteException;
	void push(String s) throws java.rmi.RemoteException;
}
