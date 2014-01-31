/* 
 *    class: JGraph (Main)
 *   $Id: JGraph.java,v 1.2 1998/10/28 01:07:28 dora Exp $
 */
package JavaGraph;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.util.Vector;

public class JGraph extends Canvas {
	Frame frame;
	Image OffScreen;
	Graphics OffGraphics;
	EventHandler eh;
	Vector data;
	FileDialog ld;
	Label indicator;
	Vector plotterlist;                 // list of plotters
	Plotter plotter;                   // current plotter

	public static void main(String argv[]) {
		JGraph jgraph = new JGraph();
	}

	JGraph() {
		frame = new Frame("JavaGraph");
		frame.setLayout(new BorderLayout());
		frame.setFont(new Font("Serif", Font.PLAIN, 14));
		eh = new EventHandler(this);
		addMouseListener(eh);
		addMouseMotionListener(eh);
		ld = new FileDialog(frame, "load", FileDialog.LOAD);
		indicator = new Label("( , )");
		frame.add("South", indicator);

		// creating Menu
		class FrameMenuItem extends MenuItem {
			FrameMenuItem(String name, ActionListener al) {
				super(name);
				setActionCommand(name);
				addActionListener(al);
			}
			FrameMenuItem(String name, char sc, ActionListener al) {
				this(name, al);
				setShortcut(new MenuShortcut(sc));
			}
			FrameMenuItem(String name, String com, ActionListener al) {
				this(name, al);
				setActionCommand(com);
			}
		}
		ActionListener al = eh.new MenuEventHandler();
		MenuBar mb = new MenuBar();
		Menu menu = new Menu("File");           // Menu: File
		menu.add(new FrameMenuItem("Open File", 'o', al));
		menu.add(new FrameMenuItem("New Graph", 'n', al));
		menu.add(new FrameMenuItem("Print Page", 'p', al));
		menu.add(new FrameMenuItem("Reset", 'r', al));
		menu.add(new FrameMenuItem("Quit", 'q', al));
		mb.add(menu);
		menu = new Menu("Graph");             // Menu: Graph
		menu.add(new FrameMenuItem("Expand", 'e', al));
		menu.add(new FrameMenuItem("Clear Scale", 'c', al));
		Menu inmenu = new Menu("x-axis scale");
		inmenu.add(new FrameMenuItem("linear", "x-linear", al));
		inmenu.add(new FrameMenuItem("log", "x-log", al));
		inmenu.add(new FrameMenuItem("inverse", "x-inverse", al));
		menu.add(inmenu);
		inmenu = new Menu("y-axis scale");
		inmenu.add(new FrameMenuItem("linear", "y-linear", al));
		inmenu.add(new FrameMenuItem("log", "y-log", al));
		inmenu.add(new FrameMenuItem("inverse", "y-inverse", al));
		menu.add(inmenu);
		mb.add(menu);
		menu = new Menu("Analysys");
		menu.add(new FrameMenuItem("sum", al));
		menu.add(new FrameMenuItem("chi fitting", "chi", al));
		mb.add(menu);
		frame.setMenuBar(mb);

		ScrollPane sp = new ScrollPane();
		sp.add(this);
		frame.add("Center", sp);
		frame.addWindowListener(eh.new WindowEventHandler());
		setBackground(Color.white);
		setSize(1000,1000);
		frame.pack();
		frame.show();
		frame.setSize(800, 650);
		setOffImage();
		addComponentListener(eh.new ComponentEventHandler());
		plotterlist = new Vector();
		data = new Vector();
		clearData();
		plotterlist.addElement(plotter);
	}

	void clearData() {
		plotter = new Plotter(this, OffGraphics, new Rectangle(100, 100, 500, 400));
		data.removeAllElements();
		plotterlist.removeAllElements();
		plotterlist.addElement(plotter);
		repaint();
	}

	void setOffImage() {
		OffScreen = createImage(getSize().width, getSize().height);
		OffGraphics = OffScreen.getGraphics();
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (!eh.show) {
			OffGraphics.clearRect(0, 0, getSize().width, getSize().height);
			for (int i=0; i<plotterlist.size(); i++) {
				((Plotter)(plotterlist.elementAt(i))).draw();
			}
			g.drawImage(OffScreen, 0, 0, null);
		} else {
			g.drawImage(OffScreen, 0, 0, null);
			int x = eh.p.x, 
					y = eh.p.y,
					width = eh.q.x - x,
					height = eh.q.y - y;
			g.setColor(Color.blue);
			if (width < 0) {
				width *= -1;
				x -= width;
			} if (height < 0) {
				height *= -1;
				y -= height;
			}
			g.drawRect(x, y, width, height);
			g.setColor(Color.black);
		}
	}

	void printPage() {
		PrintJob pjob = getToolkit().getPrintJob(frame, "JGraph", null);
		if (null != pjob) {
			Graphics pg = pjob.getGraphics();
			if (null != pg) {
				printAll(pg);
				pg.dispose();
			}
			pjob.end();
			pjob.finalize();
		}    
	}

	void loadData() {
		ld.show();
		try {
			File file = new File(ld.getDirectory() + ld.getFile());
			Data d = new Data(new FileReader(file));
			data.addElement(d);
			plotter.addData(data.lastIndexOf(d));
		} catch(NumberFormatException ne) {
			printError("NumberFormatException");
			return;
		} catch(FileNotFoundException fe) {
			return;
		} catch(IOException ioe) {
			System.out.println(ioe);
			exit();
		}
		plotter.rescale();
	}

	void printError(String s) {
		System.out.println(s + "\n");
	}

	void exit() {
		System.exit(1);
	}
}
