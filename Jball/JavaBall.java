// 	$Id: JavaBall.java,v 1.5 2000/06/02 04:49:58 dora Exp dora $	
package Jball;
import java.awt.*;
import java.awt.event.*;

public class JavaBall extends java.applet.Applet {
	Field field;
	static boolean isApplet = true;

	public static void main(String argv[]) {
		isApplet = false;
		Frame frame = new Frame("JavaBall");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(1);}});
		JavaBall applet = new JavaBall();
		applet.init();
		frame.add(applet);
		frame.pack();
		frame.validate();
		frame.setSize(200, 200);
		frame.show();
		applet.start();
	}

	public void init() {
		setLayout(new BorderLayout());
		field = new Field();
		Image[] images = new Image[10];
		if (isApplet) {
			images[0] = getImage(getCodeBase(), "Jball/red-ball.gif");
			images[1] = getImage(getCodeBase(), "Jball/magenta-ball.gif");
			images[2] = getImage(getCodeBase(), "Jball/blue-ball.gif");
			images[3] = getImage(getCodeBase(), "Jball/yellow-ball.gif");
			images[4] = getImage(getCodeBase(), "Jball/green-ball.gif");
		} else {
			Toolkit tk = Toolkit.getDefaultToolkit();
			images[0] = tk.getImage("Jball/red-ball.gif");
			images[1] = tk.getImage("Jball/magenta-ball.gif");
			images[2] = tk.getImage("Jball/blue-ball.gif");
			images[3] = tk.getImage("Jball/yellow-ball.gif");
			images[4] = tk.getImage("Jball/green-ball.gif");
		}

		field.addBall(new Ball(field, 20, 102, 1, 0, 1, 1), images[0]);
		Ball a = new Ball(field, 100, 100, 0, 0, 1, 30);
		field.addBall(a, images[1]);
		a = new Ball(field, 150, 100, 1, 1, 1, 1);
		field.addBall(a, images[2]);
		a = new Ball(field, 150, 150, -2, 1, 1, 1);
		field.addBall(a, images[3]);
		a = new Ball(field, 50, 50, 0, 0, 1, 5);
		field.addBall(a, images[4]);
		add(field, "Center");
	}

	public void start() {  // called by browser or from this.main()
		field.setImage();
		field.run = true;
		field.start();
	}

	public void stop() {   // called by browser
		field.run = false;
	}

	public void destroy() {  // called by browser
		field.run = false;
	}

	public void update() {}
}

