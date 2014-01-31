package Pickie;
import java.awt.*;
import java.util.Vector;

class ImageCanvas extends Canvas {
	Image image = null;
	InputDialog inputDialog;
	int offset_x = 0;
	int offset_y = 0;
	int orig_x, x, x0, x1;
	int orig_y, y, y0, y1;
	double xr1, xr2, xv1, xv2;
	double yr1, yr2, yv1, yv2;
	int xscale, yscale;
	double th, alpha, beta;
	int ratio = 100;
	int mode;
	Frame parent;
	PickPoints holder;
	boolean dragflag = true;
	Vector data;

	public boolean mouseDown(Event evt, int x, int y) {
		if (evt.clickCount == 2) {
			switch(mode) {
				case 1: x0=x; y0=y; mode=2; 
								holder.message.setText("horizontal correction: input second point");
								holder.message.repaint();
								break;
				case 2: x1=x; y1=y; mode=3; 
								holder.message.setText("X scale setting: input first point");
								th = Math.atan( ((double)(y1-y0))/((double)(x1-x0)) );
								holder.message.repaint();
								break;

				case 3: xr1 = xFix(x, y); mode=4;
								holder.message.setText("X scale setting: input second point");
								holder.message.repaint();
								break;
				case 4: xr2 = xFix(x, y); mode=5;
								dragflag = false;
								inputDialog.show(); 
								xscale = inputDialog.scale;
								xv1 = inputDialog.p;
								xv2 = inputDialog.q;
								alpha = (scaleFunc(xscale, xv2)-scaleFunc(xscale, xv1))/(xr2-xr1);
								holder.message.setText("Y scale setting: input first point");
								holder.message.repaint();
								dragflag = true;
								break;

				case 5: yr1 = yFix(x, y); mode=6; 
								holder.message.setText("Y scale setting: input second point");
								holder.repaint();
								break;
				case 6: yr2 = yFix(x, y); mode=0;
								dragflag = false;
								inputDialog.show(); 
								yscale = inputDialog.scale;
								yv1 = inputDialog.p;
								yv2 = inputDialog.q;
								beta = (scaleFunc(yscale, yv2)-scaleFunc(yscale, yv1))/(yr2-yr1);
								holder.message.setText("ready to point.");
								holder.message.repaint();
								dragflag = true;
								break;

				default: getCoord(x, y, 1);
			}
			return true;
		}
		orig_x = x;
		orig_y = y;
		return true;
	}

	void getCoord(int x, int y, int out) {
		double xv = 0.0;
		double yv = 0.0;
		double fxv = scaleFunc(xscale, xv1) + alpha*(xFix(x, y)-xr1);
		double fyv = scaleFunc(yscale, yv1) + beta*(yFix(x, y)-yr1);

		switch (xscale) {
			case 0: xv = fxv; break;
			case 1: xv = Math.exp(fxv); break;
			case 2: xv = 1/fxv; break;
		}
		switch (yscale) {
			case 0: yv = fyv; break;
			case 1: yv = Math.exp(fyv); break;
			case 2: yv = 1/fyv; break;
		}

		if (out == 0) {
			if (mode == 0) {
				holder.coord.setText("( " + xv + "  ,  " + yv + " )"); 
			} else {
				holder.coord.setText("( " + x + "  ,  " + y + " )");
			}
			holder.coord.repaint();
		} else {
			data.addElement(new Double(xv));
			data.addElement(new Double(yv));
			System.out.println(xv + "\t\t" + yv);
		}
	}

	double xFix(int x, int y) {
		x += offset_x;
		y += offset_y;
		double tmpx = x*100.0/(double)ratio;
		double tmpy = y*100.0/(double)ratio;
		return Math.cos(th) * tmpx + Math.sin(th) * tmpy;
	}

	double yFix(int x, int y) {
		x += offset_x;
		y += offset_y;
		double tmpx = x*100.0/(double)ratio;
		double tmpy = y*100.0/(double)ratio;
		return Math.sin(-th) * tmpx + Math.cos(th) * tmpy;
	}

	double scaleFunc(int scale, double val) {
		switch(scale) {
			case 0: return val;
			case 1: return Math.log(val);
			case 2: return 1/val;
		}
		return 0.0;
	}

	public boolean mouseDrag(Event evt, int x, int y) {
		if (dragflag == false || (x == orig_x && y == orig_y)) return false;
		int tmp;
		offset_x += orig_x - x;
		offset_y += orig_y - y;
		orig_x = x;
		orig_y = y;
		repaint();
		return true;
	}

	ImageCanvas(Frame parent, PickPoints holder) {
		inputDialog = new InputDialog(parent, this);
		this.holder = holder;
		data = new Vector();

	}

	public void paint(Graphics g) {
		g.drawRect(0, 0, size().width-1, size().height-1);
		if (image == null) return;

		Graphics new_g = g.create();
		new_g.translate(-offset_x, -offset_y);
		if (ratio == 100) new_g.drawImage(image, 0, 0, this);
		else new_g.drawImage(image, 0, 0, image.getWidth(this)*ratio/100, 
				image.getHeight(this)*ratio/100, this);
		new_g.dispose();
	}

	public boolean mouseMove(Event evt, int x, int y) {
		getCoord(x, y, 0);
		return true;
	}
}


