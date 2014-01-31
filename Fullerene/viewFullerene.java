/**  viewFullerene.java
 *     main applet class
 *  load coords & connections of C60.
 *     Toshihide Shimayama 1997.
 */ 

package Fullerene;
import java.applet.Applet;
import java.awt.Scrollbar;
import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Graphics;
import java.io.StreamTokenizer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class viewFullerene extends Applet {
	ObjectPanel object;
	Scrollbar speedbar, stepbar;
	float a[][];
	int connect[][];

	public void init() {
		readData(getParameter("datafile"));
		object = new ObjectPanel(a, connect);
		setLayout(new BorderLayout());
		speedbar = new Scrollbar(Scrollbar.VERTICAL, 500, 50, 5, 1500);
		stepbar = new Scrollbar(Scrollbar.VERTICAL, -1, 1, -5, 0);
		add("Center", object);
		add("West", stepbar);
		add("East", speedbar);
		show();
		validate();
		object.setSize();
	}

	public void readData(String filename) {
		try {
			InputStream is = new URL(getDocumentBase(), filename).openStream();
			StreamTokenizer st = new StreamTokenizer(is);
			st.commentChar('#');
			st.eolIsSignificant(false);
			st.parseNumbers();
			st.whitespaceChars(',', ',');
			st.whitespaceChars('\t' ,'\t');
			int mode = 0;
			int i = 0;
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				switch (st.ttype) {
					case StreamTokenizer.TT_WORD:
						if (mode==0 && st.sval.equals("point")) {
							st.nextToken();
							a = new float[(int)st.nval][3];
						} else if (mode==1 && st.sval.equals("connect")) {
							st.nextToken();
							connect = new int[(int)st.nval][2];
						} else {
							System.out.println("Bad format!!");
							System.exit(1);
						}
						mode++; i=0;
						break;
					case StreamTokenizer.TT_NUMBER:
						switch (mode) {
							case 1: 
								if (i == a.length) break;
								a[i][0] = (float)st.nval; st.nextToken();
								a[i][1] = (float)st.nval; st.nextToken();
								a[i][2] = (float)st.nval; i++;
								break;
							case 2:
								if (i == connect.length) break;
								connect[i][0] = (int)st.nval; st.nextToken();
								connect[i][1] = (int)st.nval; i++;
								break;
							default: 
								System.out.print("bad format!!");
								break;
						}
						break;
					default: 
						System.out.println("bad format!!"+st.ttype);
						break;
				}
			}
			is.close();
		} catch(IOException e) {
			System.out.println("IO Error: " + e + "; might have bad format.");
		}
	}

	public boolean handleEvent(Event evt) {
		if (!object.thread.isAlive()) {
			speedbar.hide();
			stepbar.hide();
			repaint();
			return true;
		}
		if (evt.target instanceof Scrollbar && evt.arg != null) {
			int val = ((Integer)evt.arg).intValue();
			if (val > 0) {
				object.setSleepTime(val);
				return true;
			} else if (val <= 0) {
				object.setStep(-val);
				return true;
			}
			return true;
		}
		return super.handleEvent(evt);
	}			    

	public void start() {
		object.thread.start();
	}

	public void stop() {
		if (object.thread.isAlive()) object.thread.stop();
	}

	public void update(Graphics g) {
	}
}
