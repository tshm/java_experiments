package Pickie;
import java.applet.Applet;
import java.awt.*;
import java.io.*;

public class PickPoints extends Applet {
	Frame frame;
	FileDialog opendialog;
	FileDialog savedialog;
	ImageCanvas canvas;
	Label message, coord;

	public void init() {
		frame = new Frame("Pickie");
		frame.setBackground(Color.lightGray);
		frame.setLayout(new BorderLayout());
		frame.add("Center", this);
		frame.resize(600, 500);
		frame.show();

		// setting up applet panel
		setLayout(new BorderLayout());
		canvas = new ImageCanvas(frame, this);
		add("Center", canvas);

		Panel toppanel = new Panel();
		toppanel.add(new Button("Open"));
		toppanel.add(new Button("Save"));
		toppanel.add(new Button("Exit"));
		Choice ratio = new Choice();
		ratio.addItem("50");
		ratio.addItem("100");
		ratio.addItem("150");
		ratio.addItem("200");
		ratio.addItem("300");
		ratio.select(1);
		toppanel.add(ratio);
		add("North", toppanel);

		Panel botpanel = new Panel();
		botpanel.setLayout(new GridLayout(2,1));
		message = new Label("message", Label.CENTER);
		coord = new Label("Coordinate", Label.CENTER);
		botpanel.add(message);
		botpanel.add(coord);
		add("South", botpanel);

		opendialog = new FileDialog(frame, "image(gif,jpeg)", FileDialog.LOAD);
		savedialog = new FileDialog(frame, "save data", FileDialog.SAVE);
		frame.validate();
		frame.resize(650, 550);
	}

	public boolean handleEvent(Event evt) {
		if (evt.target instanceof Button) {
			if ("Exit".equals(evt.arg)) {
				System.exit(0);
			} else if ("Open".equals(evt.arg)) {
				openFile();
			} else if ("Save".equals(evt.arg)) {
				saveFile();
			}
		} else if (evt.target instanceof Choice) {
			canvas.ratio = Integer.parseInt((String)evt.arg);
			canvas.offset_x = canvas.offset_y = 0;
			canvas.repaint();
		}
		return super.handleEvent(evt);
	}

	void openFile() {
		opendialog.setFile("*.gif; *.jpg; *.jpeg");
		opendialog.show();
		if (canvas.image != null) canvas.image.flush();
		String filename = new String(opendialog.getDirectory() + opendialog.getFile());
		Image image = Toolkit.getDefaultToolkit().getImage(filename);
		canvas.image = image;
		canvas.offset_x = 0;
		canvas.offset_y = 0;
		canvas.mode = 1;
		canvas.repaint();
		message.setText("horizontal crrection : input first point.");
		repaint();
		image.flush();	
	}

	void saveFile() {
		savedialog.setFile("*.dat; *.txt; *.csv; *.xy");
		savedialog.show();
		PrintStream stream;
		try {
			String filename = 
				new String(savedialog.getDirectory() + savedialog.getFile());
			stream = new PrintStream(new FileOutputStream(filename), true); 
			for (int i=0; i<canvas.data.size()/2; i++) {
				stream.println(canvas.data.elementAt(2*i)
						+ "\t" + canvas.data.elementAt(2*i+1));
			}
			canvas.data.removeAllElements();
			stream.close();
			message.setText("data saved successfully");
		} catch(IOException e) {
			System.out.println(e);
			message.setText("save fault");
		}
	}

	public static void main(String args[]) {
		PickPoints app = new PickPoints();
		app.init();
	}
}
