/**  ObjectPanel.java
 *  3D-object has its own coords.
 *              rotation & draw method.
 *         Toshihide Shimayama 1997.
 */

package Fullerene;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Image;
import java.awt.Color;

class ObjectPanel extends Panel implements Runnable {
	int sleep;                 // sleep time of ratation thread
	int step;                  // step of the rotation
	float a[][];
	int connect[][];
	int points;
	int center;
	int x0, y0;
	double scale;             // to get delta theta
	Thread thread;
	Image OffScreen;
	Graphics OffGraphics;

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		OffGraphics.setColor(getBackground());
		OffGraphics.fillRect(0, 0, size().width, size().height);
		OffGraphics.setColor(Color.blue);

		int ovalsize;
		int x0 = (int)a[0][0] + center,
				y0 = (int)a[0][1] + center,
				x1, y1;
		for (int i = 0; i < points; i++) {
			ovalsize = (int)((a[i][2] / (float)center + 1.8f) * 5.0f);
			x1 = (int)a[i][0] + center;
			y1 = (int)a[i][1] + center;
			//    OffGraphics.drawLine(x0, y0, x1, y1);
			OffGraphics.fillOval(x1 - ovalsize, y1 - ovalsize,
					ovalsize*2, ovalsize*2);
			//    String str = String.valueOf(i);
			//    OffGraphics.drawString(str, x1, y1);
			x0 = x1;
			y0 = y1;
		}
		for (int i = 0; i < connect.length; i++) {
			x0 = connect[i][0];
			x1 = connect[i][1];
			OffGraphics.drawLine(
					(int)a[x0][0] + center, (int)a[x0][1] + center,
					(int)a[x1][0] + center, (int)a[x1][1] + center);
		}
		g.drawImage( OffScreen, 0, 0, null);
	}

	public ObjectPanel(float tmp[][], int con[][]) {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		if ( 3 != tmp[0].length ) {
			System.out.println("invalid dimension!!(must be 3)");
			System.exit(1);
		}
		a = new float[tmp.length][3];
		a = tmp;
		connect = con;
		points = tmp.length;
		x0 = 0; y0 = 0;
		sleep = 500; 
		step = 1;
		try {
			thread = new Thread(this);
			thread.setPriority(Thread.MAX_PRIORITY);
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	void setSize() {
		int width = size().width,
				height = size().height;
		OffScreen = createImage(width, height);
		OffGraphics = OffScreen.getGraphics();

		int size = Math.min(width, height);
		float maxlength = 0;
		for (int i = 0; i < a.length; i++) {
			float r = (float)Math.sqrt(a[i][0] * a[i][0] + 
					a[i][1] * a[i][1] + 
					a[i][2] * a[i][2]   );
			maxlength = (float)Math.max(maxlength, r); 
		}

		float sd = 0.4f * (float)size;
		for (int i = 0; i < a.length; i++) {
			a[i][0] *= sd / maxlength;
			a[i][1] *= sd / maxlength;
			a[i][2] *= sd / maxlength;
		}
		center = (int)size / 2;
		scale = (double)Math.max(size().width, size().height)/3.0;
	}

	void rotateX(double th) {
		float tmp;
		float sin = (float)Math.sin(th);
		float cos = (float)Math.cos(th);		
		for (int i = 0; i < points; i++) {
			tmp     = cos * a[i][1] - sin * a[i][2];
			a[i][2] = sin * a[i][1] + cos * a[i][2];
			a[i][1] = tmp;
		}  
	}

	void rotateY(double th) {
		float tmp;
		float sin = (float)Math.sin(th);
		float cos = (float)Math.cos(th);
		for (int i = 0; i < points; i++) {
			tmp     = cos * a[i][2] - sin * a[i][0];
			a[i][0] = sin * a[i][2] + cos * a[i][0];
			a[i][2] = tmp;
		}	
	}

	public boolean mouseDown(Event evt, int x, int y) {
		if (evt.clickCount == 2 && thread.isAlive()) {
			thread.stop();
			return true;
		}
		if (thread.isAlive()) thread.suspend();
		x0 = x;
		y0 = y;
		return true;
	}

	public boolean mouseDrag(Event evt, int x, int y) {
		rotateY((double)(x-x0)/scale);
		rotateX((double)(y0-y)/scale);
		repaint();
		x0 = x;
		y0 = y;
		return true;
	}

	public boolean mouseUp(Event evt, int x, int y) {
		if (thread.isAlive()) thread.resume();
		return true;
	}

	public void run()  {
		while(true) {
			try {
				thread.sleep(sleep);
			} catch(Exception e) {
				return;
			}
			rotateY((double)step/scale);
			rotateX((double)step/scale);
			repaint();
		}
	}

	public void setSleepTime(int sleep) {
		this.sleep = sleep;
	}

	public void setStep(int step) {
		this.step = step;
	}
}
