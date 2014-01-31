package Jlauncher;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Jlauncher extends WindowAdapter implements Runnable, ActionListener {
	TextField tf;
	TextArea ta;
	Label label;
	Thread thread;

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
	}

	public void run() {
		ta.append("Running!!\n");
		long total;
		try {
			String com = new String("/usr/local/java/JDK/bin/java " + tf.getText());
			Runtime runtime = Runtime.getRuntime();
			Process p = runtime.exec(com);
			total = runtime.totalMemory();
			InputStreamReader in = new InputStreamReader(p.getInputStream());
			InputStreamReader err = new InputStreamReader(p.getErrorStream());
			char[] inbuf = new char[1024];
			char[] errbuf = new char[1024];
			while(true) {
				label.setText("free = "+(int)(100.0 * runtime.freeMemory() / total)+" %");
				if (err.ready()) {
					err.read(errbuf, 0, 1024);
					ta.append(String.valueOf(errbuf));
				} else if (in.ready()) {
					in.read(inbuf, 0, 1024);
					ta.append(String.valueOf(inbuf));
				} else thread.sleep(100);
				try {
					p.exitValue();
					ta.append("\nterminated.\n");
					label.setText("");
					return;
				} catch (Exception e) {}
			}
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
