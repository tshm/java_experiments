/*
 *    class: Plotter
 *   $Id: Plotter.java,v 1.2 1998/10/28 01:08:02 dora Exp $
 */
package JavaGraph;
import java.awt.*;
import java.util.Vector;

class Plotter {
	// class variables
	static final int LinearScale  = 0,
							 LogScale     = 1,
							 InverseScale = 2;
	static final int NoneMark     = -1,
							 DotMark      = 0,
							 CircleMark   = 1,
							 TriangleMark = 2;

	// reference for graphics and numeric data.
	Graphics g;                   // handling Graphics
	Rectangle geom;              // geometry on parent canvas
	JGraph main;                // parent canvas
	FontMetrics fontmetrics;   // font metrics
	Vector datalist;          // handling datalist (Index Vector)

	// axis scalings
	int Xscale = Plotter.LinearScale;     // x-axis
	int Yscale = Plotter.LinearScale;    //  y-axis

	// canvas properties
	double x0, y0;          // frame origin point(virtual)
	double dx, dy;         // frame size(virtual)

	Plotter(JGraph main, Graphics g, Rectangle geom) {
		x0 = 0;
		y0 = 0;
		dx = 10;
		dy = 10;
		this.main = main;
		this.g = g.create();
		this.g.translate(geom.x, geom.y);
		this.geom = geom;
		fontmetrics = g.getFontMetrics();
		datalist = new Vector();
	}

	void addData(int n) {
		datalist.addElement(new Integer(n));
		rescale();
	}

	void removeData(int n) {
		Integer I = new Integer(n);
		if (datalist.contains(I))
			datalist.removeElement(I);
	}

	void setXScale(int sc) {
		if (sc == LinearScale) {
			Xscale = sc;
		} else if (sc == LogScale) {
			Xscale = sc;
			if (x0 <= 0 || x0 + dx <= 0) rescaleX();
		} else if (sc == InverseScale && x0*(x0+dx) > 0) {
			Xscale = sc;
		} else System.out.println("x:scaling error");
		main.repaint();
	}

	void setYScale(int sc) {
		if (sc == LinearScale) {
			Yscale = sc;
		} else if (sc == LogScale) {
			Yscale = sc;
			if (y0 <= 0 || y0 + dy <= 0) rescaleY();
		} else if (sc == InverseScale && y0*(y0+dy) > 0) {
			Yscale = sc;
		} else System.out.println("y:scaling error");
		main.repaint();
	}

	void rescale(Point p, Point q) {
		double x0, y0, dx, dy;
		try {
			x0 = convertX(p.x - geom.x);
			y0 = convertY(p.y - geom.y);
			dx = convertX(q.x - geom.x) - x0;
			dy = convertY(q.y - geom.y) - y0;
			this.x0 = x0; this.y0 = y0;
			this.dx = dx; this.dy = dy;
			main.repaint();
		} catch(convertingException ce) {}
	}

	void rescale() {
		if (datalist.isEmpty()) return;
		rescaleX();
		rescaleY();
		main.repaint();
	}

	void rescaleX() {
		if (datalist.isEmpty()) return;
		int index = ((Integer)datalist.elementAt(0)).intValue();
		if (Xscale == LinearScale) {
			x0 = ((Data)main.data.elementAt(index)).Xmin;
			dx = ((Data)main.data.elementAt(index)).Xmax - x0;
		} else if (Xscale == LogScale) {
			double tmp = ((Data)main.data.elementAt(index)).getPositiveMinX();
			if (tmp == 0) {
				setXScale(LinearScale);
				return;
			}
			x0 = tmp;
			dx = ((Data)main.data.elementAt(index)).getMaxX() - x0;
		}
	}

	void rescaleY() {
		if (datalist.isEmpty()) return;
		int index = ((Integer)datalist.elementAt(0)).intValue();
		if (Yscale == LinearScale) {
			y0 = ((Data)(main.data.elementAt(index))).Ymin;
			dy = ((Data)(main.data.elementAt(index))).Ymax - y0;
		} else if (Yscale == LogScale) {
			double tmp = ((Data)(main.data.elementAt(index))).getPositiveMinY();
			if (tmp == 0) {
				setYScale(LinearScale);
				return;
			}
			y0 = tmp;
			dy = ((Data)(main.data.elementAt(index))).getMaxY() - y0;
		}
	}

	void drawAxisX() {
		// auto scaling
		// drawing X scale
		if (Xscale == LinearScale) {                       // Linear scale
			int d = (int)(Math.log(dx/5.) / Math.log(10));       // digit
			double a = dx / 5. * Math.pow(10, -(double)d);      // precision
			double ddx;
			if (a < 2.0) ddx = 1.0;
			else if (a < 5.0) ddx = 2.0;
			else ddx = 5.0;
			ddx *= Math.pow(10, (double)d);
			if (ddx == 0) {
				System.out.println("ddx = 0 !!");
				return;
			}
			for (double pos = Math.floor(x0 / Math.pow(10, (double)d+1))
					* Math.pow(10, (double)d+1);
					pos <= x0+dx; pos += ddx) {
				try {
					int sc = convertX(pos);
					if (sc < 0 || sc > geom.width) continue;
					g.drawLine(sc, geom.height - 10, sc, geom.height);
					g.drawString(String.valueOf(pos), 
							sc - fontmetrics.stringWidth(String.valueOf(pos)) / 2,
							geom.height + fontmetrics.getAscent());
				} catch (Exception e) { }
					}
		} else if (Xscale == LogScale) {                 // Log scale
			if (x0 <= 0 || x0+dx <= 0) rescaleX();
			boolean big = (dx > 9 * x0);
			for (double pos=(double)(Math.pow(10, Math.floor(Math.log(x0) / Math.log(10))));
					pos < x0 + dx; pos *= 10.0) {
				int d = (int)(Math.log(pos) / Math.log(10));
				for (int i = 1; i < 10; i++) {
					double dpos = (double)i * pos;
					if (dpos < x0 || dpos > x0+dx) continue;
					try {
						int sc = convertX(dpos);
						g.drawLine(sc, geom.height - 10, sc, geom.height);
						if (big && i != 1) continue;
						g.drawString(i + "e" + d, 
								sc - fontmetrics.stringWidth("1e" + Math.floor(Math.log(pos) / Math.log(10))) / 2,
								geom.height + fontmetrics.getAscent());
					} catch (Exception e) { }
				}
					}
		}
	}

	void drawAxisY() {
		// drawing Y scale
		if (Yscale == LinearScale) {
			int d = (int)(Math.log(dy/5.) / Math.log(10));       // digit
			double a = dy / 5. * Math.pow(10, -(double)d);      // precision
			double ddy;
			if (a < 2.0) ddy = 1.0;
			else if (a < 5.0) ddy = 2.0;
			else ddy = 5.0;
			ddy *= Math.pow(10, (double)d);
			if (ddy == 0) {
				System.out.println("ddy = 0 !!");
				return;
			}
			for (double pos = Math.floor(y0 / Math.pow(10, (double)d+1))
					* Math.pow(10, (double)d+1);
					pos <= y0+dy; pos += ddy) {
				try {
					int sc = convertY(pos);
					if (sc > geom.height || sc < 0) continue;
					g.drawLine(0, sc, 10, sc);
					g.drawString(String.valueOf(pos),
							-fontmetrics.stringWidth(String.valueOf(pos)) - 2,
							sc + fontmetrics.getAscent()/2);
				} catch (Exception e) { }
					}
		} else if (Yscale == LogScale) {                 // Log scale
			if (y0 <= 0 || y0+dy <= 0) rescaleY();
			boolean big = (dy > 9 * y0);
			for (double pos=(double)(Math.pow(10, Math.floor(Math.log(y0) / Math.log(10))));
					pos < y0 + dy; pos *= 10.0) {
				int d = (int)(Math.log(pos) / Math.log(10));
				for (int i = 1; i < 10; i++) {
					double dpos = (double)i * pos;
					if (dpos < y0 || dpos > y0+dy) continue;
					try {
						int sc = convertY(dpos);
						g.drawLine(0, sc, 10, sc);
						if (big && i != 1) continue;
						g.drawString(i + "e" + d, 
								- fontmetrics.stringWidth("1e" + Math.floor(Math.log(pos) / Math.log(10))) - 2,
								sc + fontmetrics.getAscent() / 2);
					} catch (Exception e) { }
				}
					}
		}
	}

	void draw() {
		drawAxisX();
		drawAxisY();
		// plotting
		Graphics gin = g.create(0, 0, geom.width, geom.height);
		gin.drawRect(0, 0, geom.width-1, geom.height-1);
		gin.setColor(Color.red);
		if (datalist.isEmpty()) return;
		for (int j=0; j<datalist.size(); j++) {
			if (j==1) gin.setColor(Color.blue);
			else if (j==2) gin.setColor(Color.green);
			int n = ((Integer)datalist.elementAt(j)).intValue();
			for (int i=0; i<((Data)main.data.elementAt(n)).size-1; i++) {
				if (!(((Data)main.data.elementAt(n)).isInside(i, x0, y0, x0+dx, y0+dy)
							|| ((Data)main.data.elementAt(n)).isInside(i+1, x0, y0, x0+dx, y0+dy)) ) continue;
				try {
					gin.drawLine(convertX(((Data)main.data.elementAt(n)).getX(i)), 
							convertY(((Data)main.data.elementAt(n)).getY(i)),
							convertX(((Data)main.data.elementAt(n)).getX(i+1)), 
							convertY(((Data)main.data.elementAt(n)).getY(i+1)));
				} catch(convertingException e) {
					System.out.println("conversion out"); 
				}
			}
		}
		//    gin.dispose();
	}

	void drawMark(double x, double y, int type, int size) {}

	class convertingException extends Exception {
		convertingException(String val) {
			super(val);
		}
	}

	// virtual position 2 graphical..
	int convertX(double x) throws convertingException {
		double tmp=0;
		if (Xscale == LinearScale) {
			tmp = (x-x0)/dx;
		} else if (Xscale == LogScale) {
			if (x0 <= 0 || x0+dx <= 0) throw(new convertingException("x = infinity"));
			tmp = (Math.log(x) - Math.log(x0)) / (Math.log(x0+dx) - Math.log(x0));
		} else if (Xscale == InverseScale) {
			tmp = (1./x - 1./x0) / (1./(x0+dx) - 1./x0);
		} else throw(new convertingException("x:virtual to real"));
		return (int)(tmp * (double)geom.width);
	}

	int convertY(double y) throws convertingException {
		double tmp = 0;
		if (Yscale == LinearScale) {
			tmp = ((y-y0) / dy);
		} else if (Yscale == LogScale) {
			if (y0 <= 0 || y0+dy <= 0) throw(new convertingException("y = infinity"));
			tmp = (Math.log(y) - Math.log(y0)) / (Math.log(y0+dy) - Math.log(y0));
		} else if (Yscale == InverseScale) {
			tmp = (1./y - 1./y0) / (1./(y0+dy) - 1./y0);
		} else throw(new convertingException("y:virtual to real"));
		return geom.height - (int)(tmp * (double)geom.height);
	}

	// Graphical position 2 virtual..
	double convertX(int x) throws convertingException {
		double tmp = 0;
		if (Xscale == LinearScale) {
			tmp = (double)x / (double)geom.width * dx + x0;
		} else if (Xscale == LogScale) {
			if (x0 <= 0 || x0+dx <= 0) throw(new convertingException("x = infinity"));
			tmp = Math.exp((double)x / (double)geom.width * 
					(Math.log(x0+dx) - Math.log(x0)) + Math.log(x0));
		} else if (Xscale == InverseScale) {
			tmp = 1/((double)x / (double)geom.width * (1/(x0+dx) - 1/x0) + 1/x0);
		} else throw(new convertingException("x:real to virtual"));
		return tmp;
	}

	double convertY(int y) throws convertingException {
		double tmp = 0;
		if (Yscale == LinearScale) {
			//      tmp = (double)y / (double)geom.height * dy + y0;
			tmp = (double)(geom.height-y) / (double)geom.height * dy + y0;
		} else if (Yscale == LogScale) {
			if (y0 <= 0 || y0+dy <= 0) throw(new convertingException("y = infinity"));
			tmp = Math.exp((double)(geom.height - y) / (double)geom.height * 
					(Math.log(y0+dy) - Math.log(y0)) + Math.log(y0));
		} else if (Yscale == InverseScale) {
			tmp = 1/((double)(geom.height - y) / 
					(double)geom.height * (1/(y0+dy) - 1/y0) + 1/y0);
		} else throw(new convertingException("y:real to virtual"));
		return tmp;
	}
}
