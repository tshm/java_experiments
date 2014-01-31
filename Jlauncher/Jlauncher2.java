package Jlauncher;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Jlauncher extends WindowAdapter implements Runnable, ActionListener {
	TextField tf;
	TextArea ta;
	Label label;
	Thread thread;
	Class cls;
	Object object;

	static public void main(String argv[]) {
		new Jlauncher(argv[0]);
	}

	Jlauncher(String name) {
		Frame frame = new Frame("Jlauncher");
		Panel top = new Panel();
		Button a = new Button("run");
		tf = new TextField(name);
		ta = new TextArea("Jlauncher!!\n", 20, 50);
		label = new Label();
		top.add(tf);
		top.add(a);
		frame.add("North", top);
		frame.add("Center", ta);
		frame.add("South", label);
		a.addActionListener(this);
		frame.addWindowListener(this);
		frame.show();
		frame.setSize(300,300);
		ta.setCaretPosition(12);

		// load class
		try {
			cls = Class.forName(name);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void run() {
		ta.append("Running!!\n");
		try {
			object = cls.newInstance();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String com = e.getActionCommand();
		if (com.equals("run")) {
			new Thread(this).start();
		}
	}

	public void windowClosing(WindowEvent e) {
		System.exit(1);
	}
}
