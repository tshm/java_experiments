package hello;
import java.awt.*;
import java.rmi.*;

public class HelloApplet extends java.applet.Applet {
	String message = "";
	public void init() {
		try {
			Hello obj = (Hello)Naming.lookup("//" + getCodeBase().getHost() + "/hdora");
			message = obj.sayHello();
		} catch (Exception e) {
			System.out.println("HelloApplet exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	public void paint(Graphics g) {
		g.drawString("message is " + message, 25, 50);
	}
}
