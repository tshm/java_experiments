package Pickie;
import java.awt.*;
import java.awt.event.*;

class InputDialog extends Dialog implements ActionListener, KeyListener {
	Component caller;
	TextField field_p, field_q;
	Choice choice;
	int scale;
	double p, q;
	Button button;

	// Key Eventhandlers 
	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyChar() == '\n') {
			hide();
		}
	}

	public void keyReleased(KeyEvent evt){}
	public void keyTyped(KeyEvent evt){}

	// Action Eventhandler
	public void actionPerformed(ActionEvent evt) {
		hide();
	}

	public void hide() {
		try {
			scale = choice.getSelectedIndex();
			p = Double.valueOf(field_p.getText()).doubleValue();
			q = Double.valueOf(field_q.getText()).doubleValue();
		} catch(Exception e) {
			return;
		}
		super.hide();
	}

	public void show() {
		super.show();
		requestFocus();
		choice.requestFocus();
		System.out.println("dialog shown");
	}

	InputDialog(Frame parent, Component caller) {
		super(parent, "Input", true);
		addKeyListener(this);
		setBackground(Color.lightGray);
		this.caller = caller;

		Panel tpanel = new Panel();
		choice = new Choice();
		choice.addKeyListener(this);
		choice.addItem("Linear");
		choice.addItem("Log");
		choice.addItem("Inverse");
		tpanel.add(new Label("Scale: "));
		tpanel.add(choice);
		add("North", tpanel);

		Panel cpanel = new Panel();
		cpanel.setLayout(new GridLayout(2,2));
		field_p = new TextField();
		field_q = new TextField();
		field_p.addKeyListener(this);
		field_q.addKeyListener(this);
		cpanel.add(new Label("First :"));
		cpanel.add(field_p);
		cpanel.add(new Label("Second:"));
		cpanel.add(field_q);
		add("Center", cpanel);

		Panel bpanel = new Panel();
		button = new Button("OK");
		button.addKeyListener(this);
		button.addActionListener(this);
		bpanel.add(button);
		add("South", bpanel);
		resize(250, 150);
		pack();
	}
}







