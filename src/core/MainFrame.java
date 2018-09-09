package core;

import drawing.*;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainFrame {

	private JFrame frame;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public MainFrame() {
		initialize();
	}


	private void initialize() {
		frame = new Window();
	}

}
