package ppserve;
import java.rmi.*;
import java.awt.event.*;
import java.awt.*;

public class PPAplet extends java.applet.Applet {
	String string = "";
	ServerInterface obj;

	public void init() {
		//    setLayout(new BorderLayout());
		try {
			obj = (ServerInterface)Naming.lookup("//" + getCodeBase().getHost() + "/pps");
			final TextField text = new TextField("null", 20);
			Button pushButton = new Button("push");
			Button popButton = new Button("pop");
			pushButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						obj.push(new String(text.getText()));
					} catch (Exception e) {
						System.out.println("PPAplet exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			});   
			popButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						text.setText(new String(obj.pop()));
					} catch (Exception e) {
						System.out.println("PPAplet exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
			add(text);
			add(pushButton);
			add(popButton);
			validate();
		} catch (Exception e) {
			System.out.println("PPAplet exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}





