package hello;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class HelloImpl extends UnicastRemoteObject implements Hello {
	private String name;

	public HelloImpl(String s) throws RemoteException {
		super();
		name = s;
	}

	public String sayHello() throws java.rmi.RemoteException {
		return "Hello World!";
	}

	public static void main(String argv[]) {
		System.setSecurityManager(new RMISecurityManager());

		try {
			HelloImpl obj = new HelloImpl("HelloServer");
			Naming.rebind("//www.first.tsukuba.ac.jp/hdora", obj);
			System.out.println("HelloServer bound in registry");
		} catch (Exception e) {
			System.out.println("HelloImpl err: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
