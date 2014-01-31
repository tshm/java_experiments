/*
 *    class: EventHandler
 *   $Id: EventHandler.java,v 1.2 1998/10/28 01:08:27 dora Exp $
 */
package JavaGraph;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

class EventHandler implements MouseMotionListener, MouseListener {
	JGraph main;
	boolean show;                              // frag: show the rectangle
	Point p, q;                               // mouse dragged area

	EventHandler(JGraph main) {
		this.main = main;
		p = new Point();
		q = new Point();
		show = false;
	}

	public void mouseClicked(MouseEvent e) {
		show = false;
		main.repaint();
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		p.x = e.getX(); 
		p.y = e.getY(); 
	}
	public void mouseReleased(MouseEvent e) {
		q.x = e.getX();
		q.y = e.getY();
		show = false;
	}
	public void mouseDragged(MouseEvent e) {
		q.x = e.getX();
		q.y = e.getY();
		show = true;
		main.repaint();
		mouseMoved(e);
	}
	public void mouseMoved(MouseEvent e) {
		Runtime rt = Runtime.getRuntime();
		int mem = (int)(rt.freeMemory() * 100.0 / rt.totalMemory());
		double x=0, y=0;
		try { x = main.plotter.convertX(e.getX() - main.plotter.geom.x);
		} catch(Plotter.convertingException ce) {x=0; System.out.println("out");}
		try { y = main.plotter.convertY(e.getY() - main.plotter.geom.y);
		} catch(Plotter.convertingException ce) {y=0;}
		main.indicator.setText("free="+mem+"%   (" + x + " , " + y + ")");
	}

	class MenuEventHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String command;
			if (ae.getSource() instanceof MenuItem) {
				if ((command = ae.getActionCommand()).equals("Open File")) {
					main.loadData();
				} else if (command.equals("New Graph")) {
					main.clearData();
				} else if (command.equals("Quit")) {
					main.exit();
				} else if (command.equals("Expand")) {
					main.plotter.rescale(p, q);
				} else if (command.equals("Clear Scale")) {
					main.plotter.rescale();
				} else if (command.equals("Print Page")) {
					main.printPage();
				} else if (command.equals("x-linear")) {
					main.plotter.setXScale(Plotter.LinearScale);
				} else if (command.equals("x-log")) {
					main.plotter.setXScale(Plotter.LogScale);
				} else if (command.equals("x-inverse")) {
					main.plotter.setXScale(Plotter.InverseScale);
				} else if (command.equals("y-linear")) {
					main.plotter.setYScale(Plotter.LinearScale);
				} else if (command.equals("y-log")) {
					main.plotter.setYScale(Plotter.LogScale);
				} else if (command.equals("y-inverse")) {
					main.plotter.setYScale(Plotter.InverseScale);
				} else if (command.equals("sum")) {
					if (main.plotter.datalist.isEmpty()) return;
					int index = ((Integer)main.plotter.datalist.elementAt(0)).intValue();
					try {
						double x0 = main.plotter.convertX(p.x - main.plotter.geom.x);
						double x1 = main.plotter.convertX(q.x - main.plotter.geom.x);
						System.out.println(((Data)main.data.elementAt(index)).sum(x0, x1));
					} catch (Exception e) {
						System.err.println(e);
					}
				}
			}
		}    
	}  

	class ComponentEventHandler extends ComponentAdapter {
		public void componentResized() {
			main.repaint();
			main.setOffImage();
		}
	}

	class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			main.exit();
		}
	}
}
