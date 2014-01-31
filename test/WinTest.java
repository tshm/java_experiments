import java.awt.*;

public class WinTest extends Frame {
	public WinTest() {
		add(new Button("OK"));
		pack();
	}

	public static void main(String argv[]) {
		WinTest frame = new WinTest();
		frame.show();
		System.out.println("end");
	}

	public void show() {
		super.show();
		System.out.println("frame shown");
	}
}
