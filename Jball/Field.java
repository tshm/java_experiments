// 	$Id: Field.java,v 1.4 1998/09/07 10:18:23 dora Exp dora $	
package Jball;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

class Field extends Canvas { 
	static final int MAXBALLS = 10;
	static boolean run;
	Ball[] balls;                          // java.util.Vector of balls
	Image[] images;
	int num;                              // number of balls
	float thres = 30.0f;                 // interaction threshold length
	int wx, wy;                         // window size
	float[] mp = new float[2];         // mouse-pointer position
	boolean Mouse_Dragged = false;    //  ..
	float mq = 10.0f;                // mouse pointer charge
	Graphics OffGraphics = null;
	Image OffImage;

	public Field() {
		super();
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Mouse_Dragged = false;
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				//	System.out.println("rel");
			}
			public void mousePressed(MouseEvent e) {
				Mouse_Dragged = true;
				mp[0] = (float)e.getX();
				mp[1] = (float)e.getY();
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				//	System.out.println("pressed");
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Mouse_Dragged = true;
				mp[0] = (float)e.getX();
				mp[1] = (float)e.getY();
			}
		});
		balls = new Ball[MAXBALLS];
		images = new Image[MAXBALLS];
		num = 0;
	}

	void setImage() {
		wx = getSize().width;
		wy = getSize().height;
		OffImage = createImage((int)wx+1, (int)wy+1);
		OffGraphics = OffImage.getGraphics();
	}

	void addBall(Ball a, Image image) {
		balls[num] = a;
		images[num] = image;
		num++;
	}

	void move() {
		float dx, dy, r, r2;
		float[] pos = new float[2];
		for (int j=num-1; j>=0; j--) {
			float Ex = 0;
			float Ey = 0;
			pos = balls[j].getPosition();
			for (int i=num-1; i>=0; i--) {
				if (i == j) continue;
				dx = pos[0] - balls[i].pos[0];
				dy = pos[1] - balls[i].pos[1];
				r2 = dx*dx + dy*dy;
				if (r2 < 5. || r2 > thres*thres) continue;
				r = (float)Math.sqrt(r2);
				Ex += balls[i].q/r2 * dx/r;
				Ey += balls[i].q/r2 * dy/r;
			}
			if (Mouse_Dragged) {
				dx = pos[0] - mp[0];
				dy = pos[1] - mp[1];
				r2 = dx * dx + dy * dy;
				if (r2 < thres*thres) {
					r = (float)Math.sqrt(r2);
					Ex += mq/r2 * dx/r;
					Ey += mq/r2 * dy/r;
				}
			}
			if ((r = wx - pos[0]) < thres) Ex -= 1f/(r*r);
			else if (pos[0] < thres) Ex += 1f/(pos[0]*pos[0]);
			if ((r = wy - pos[1]) < thres) Ey -= 1f/(r*r);
			else if (pos[1] < thres) Ey += 1f/(pos[1]*pos[1]);
			balls[j].move(Ex, Ey);
		}

		//    System.out.println("field: "+Ex+" "+Ey);
		//    return new XYVector(0,0);
	}

	public void paint(Graphics g) {
		if (OffGraphics == null) return;
		OffGraphics.setColor(Color.yellow);
		OffGraphics.fillRect(0, 0, wx+1, wy+1);
		synchronized (balls) {
			for (int i=0; i<num; i++) {
				int x0 = ((int)balls[i].pos[0]) - 6;
				int y0 = ((int)balls[i].pos[1]) - 6;
				OffGraphics.drawImage(images[i], x0, y0, null);
			}
		}
		g.drawImage(OffImage, 0, 0, null);
	}

	public void update(Graphics g) {
		paint(g);
	}

	void start() {
		new Thread() {
			public void run() {
				while (run) {
					//	  System.out.println(balls[1].pos[0] +" "+ balls[1].pos[1]);
					synchronized (balls) {
						move();
					}
					repaint();
					try {
						sleep(10);
					} catch (InterruptedException e) {
						System.out.println(e);
					}
				}
			}
		}.start();
	}
}

