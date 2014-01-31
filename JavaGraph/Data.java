/*
 *   class: Data
 *  $Id: Data.java,v 1.2 1998/10/28 01:08:48 dora Exp dora $
 */
package JavaGraph;
import java.io.*;
import java.util.Vector;

class Data {
	private double[] x,y;
	boolean[] mask;
	double Xmax, Ymax, Xmin, Ymin;
	int size;

	Data(double x[], double y[]) {
		if (x.length != y.length) return;
		this.x = x;
		this.y = y;
		size = x.length;
		this.mask = new boolean[size];
		for (int i=0; i<size; i++) mask[i] = false;
	}

	Data(Reader reader) throws IOException, NumberFormatException {
		load(reader);
	}

	void load(Reader reader) throws IOException, NumberFormatException {
		Vector vx = new Vector();
		Vector vy = new Vector();
		try {
			StreamTokenizer st = new StreamTokenizer(reader);
			st.commentChar('#');
			st.ordinaryChars('+', 'z');
			st.wordChars('+', 'z');
			st.whitespaceChars(',', ',');
			st.whitespaceChars('\t', '\t');
			while (st.nextToken() != st.TT_EOF) {
				vx.addElement(new Double(st.sval));
				st.nextToken(); 
				vy.addElement(new Double(st.sval));
				while (st.nextToken() == st.TT_EOL);
				st.pushBack();
			}
			reader.close();
		} catch(IOException e) {
			reader.close();
			throw(e);
		}
		size = vx.size();
		if (size == 0) throw(new IOException("no data"));
		if (size != vy.size()) throw(new IOException("invalid format!"));
		x = new double[size];
		y = new double[size];
		for (int i=0; i<x.length; i++) {
			x[i] = ((Double)vx.elementAt(i)).doubleValue();
			y[i] = ((Double)vy.elementAt(i)).doubleValue();
		}
		mask = new boolean[size];
		for (int i=0; i<size; i++) mask[i] = false;
		estimateDomain();
	}

	boolean masked(int index) {
		return mask[index];
	}

	double getX(int index) {
		return x[index];
	}

	double getY(int index) {
		return y[index];
	}

	double getMaxX() {
		double max;
		int i;
		for (i=0; i < size; i++) if (!mask[i]) break;
		if (mask[i]) return 1;
		max = x[i];
		for (; i<size; i++) 
			if (!mask[i] &&  max < x[i]) max = x[i];
		return max;
	}

	double getMaxY() {
		double max;
		int i;
		for (i=0; i < size; i++) if (!mask[i]) break;
		if (mask[i]) return 1;
		max = y[i];
		for (; i<size; i++) 
			if (!mask[i] &&  max < y[i]) max = y[i];
		return max;
	}

	double getMinX() {
		double min;
		int i;
		for (i=0; i < size; i++) if (!mask[i]) break;
		if (mask[i]) return 1;
		min = x[i];
		for (; i<size; i++) 
			if (!mask[i] &&  min > x[i]) min = x[i];
		return min;
	}

	double getMinY() {  
		double min;
		int i;
		for (i=0; i < size; i++) if (!mask[i]) break;
		if (mask[i]) return 1;
		min = y[i];
		for (; i<size; i++) 
			if (!mask[i] &&  min > y[i]) min = y[i];
		return min;
	}

	double getPositiveMinX() {    // for log scaling
		double min;
		int i;
		for (i=0; i<size; i++) if (!mask[i] && x[i]>0) break;
		min = x[i];
		for (; i<size; i++) 
			if (!mask[i] &&  min > x[i]) min = x[i];
		return min;
	}

	double getPositiveMinY() {
		double min;
		int i;
		for (i=0; i<size; i++) if (!mask[i] && y[i]>0) break;
		min = y[i];
		for (; i<size; i++) 
			if (!mask[i] &&  min > y[i]) min = y[i];
		return min;
	}

	private void estimateDomain() {
		this.Xmax = getMaxX();
		this.Ymax = getMaxY();
		this.Xmin = getMinX();
		this.Ymin = getMinY();
	}

	boolean isInside(int i, double x0, double y0, double x1, double y1) {
		double xmax, xmin, ymax, ymin;
		if (x0 < x1) {
			xmin = x0; xmax = x1;
		} else {
			xmin = x1; xmax = x0;
		}
		if (y0 < y1) {
			ymin = y0; ymax = y1;
		} else {
			ymin = y1; ymax = y0;
		}
		double x = getX(i);
		double y = getY(i);
		if (xmin <= x && x <=xmax && ymin <= y && y <= ymax) return true;
		return false;
	}

	int getQuadrant(int i, double x0, double y0) { // 1 or 2 or 3 or 4
		if (y[i] < y0) return  x[i] < x0 ? 3 : 4;
		return x[i] < x0 ? 2 : 1;
	}

	double sum(double x1, double x2) {
		double sum=0;
		for (int i=0; i<size; i++) {
			if (isInside(i, x1, Ymin, x2, Ymax)) sum += getY(i);
		}
		return sum;
	}

	void mask(double x1, double y1, double x2, double y2) {
		for (int i=0; i<size; i++) 
			if (isInside(i, x1, y1, x2, y2)) mask[i] = true;
	}

	void mask() {
		for (int i=0; i<size; i++) mask[i] = true;
	}

	void unmask(double x1, double y1, double x2, double y2) {
		for (int i=0; i<size; i++) 
			if (isInside(i, x1, y1, x2, y2)) mask[i] = false;
	}

	void unmask() {
		for (int i=0; i<size; i++) mask[i] = false;
	}

	void integrate(double x1, double x2, int method) {}
	void sort(boolean b) {}
}
