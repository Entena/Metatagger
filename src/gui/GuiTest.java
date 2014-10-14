package gui;

import javax.swing.JWindow;

public class GuiTest {

	public static void main(String[] args) {
		JWindow window = new JWindow();
		window.setContentPane(new Gui());
		window.setSize(400, 600);
		window.pack();
		window.setVisible(true);
	}

}
