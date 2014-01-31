package ppserve;
import java.rmi.*;

public class PPServer {
	public static void main(String argv[]) {
		System.setSecurityManager(new RMISecurityManager());
		try {
			ServerObject obj = new ServerObject();
			Naming.rebind("//www.first.tsukuba.ac.jp/pps", obj);
			System.out.println("PPServer bound in registry");
		} catch (Exception e) {
			System.out.println("PPServer Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
