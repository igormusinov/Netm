import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.net.*;
import java.lang.Integer;
import java.lang.String;
import java.util.Scanner;

/**
 * This class defines a main() routine that simply opens a window that displays a
 * TronPanel.  The frame is centered on the screen and is non-resizable.
 */
public class Tron {
		
	public static void main(String[] args) {
		/*
		if (equals(args[0], "client"))
			client net = new client(args);
		if (equals(args[0], "server"))
			Server net = new Server(args);
		*/
		JFrame frame = new JFrame("Tron"); 
		TronPanel panel;
		try{
			panel = new TronPanel(args[0], args[1]);			
			frame.setContentPane(panel);
			frame.pack(); 
			frame.setResizable(false);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int w = (screenSize.width - frame.getWidth()) / 2;
			int h = (screenSize.height - frame.getHeight()) / 2;
			frame.setLocation(w,h); 
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			panel.mynet(args[0], args[1]);
			
		} catch(IOException e){
			   System.exit(-1);
		}
		
	}
}
