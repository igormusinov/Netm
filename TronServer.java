import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.lang.Integer;
import java.util.Scanner;



public class TronServer extends JPanel implements  ActionListener {
	
	ServerSocket servers1;
	//ServerSocket servers2;
	Socket fromclient1;
	Socket fromclient2;
		
	BufferedReader in1;
	PrintWriter out1;
	BufferedReader in2;
	PrintWriter out2;

	String         input1,output1;
	String         input2,output2;
	
	int[][] field;
	private Timer timer;  
	
	private final static int ROWS = 100;         
	private final static int COLUMNS = 100;     
	private final static int BLOCKSIZE = 5;   
	private final static int BORDER_WIDTH = 1;  
	
	

	private final static int UP = 0, LEFT = 1, DOWN = 2, RIGHT = 3, NOT_MOVING = 4; 
	private int direction1, direction2;  

	private int currentColumn1, currentRow1, currentColumn2, currentRow2; 

	private int points1 = 0, points2 = 0;

	public TronServer() {

		
		try {
		  servers1 = new ServerSocket(4444);
		  //servers2 = new ServerSocket(4445);
		} catch (IOException e) {
		  System.out.println("Couldn't listen to port 4444");
		  System.exit(-1);
		}
		
	    try {
		  System.out.print("Waiting for a client...");
		  fromclient1 = servers1.accept();
		  System.out.println("1 Client connected");
		  fromclient2 = servers1.accept();
		  System.out.println("2 Client connected");
		  	in1  = new BufferedReader(new InputStreamReader(fromclient1.getInputStream()));
			out1 = new PrintWriter(fromclient1.getOutputStream(),true);
			in2  = new BufferedReader(new InputStreamReader(fromclient2.getInputStream()));
			out2 = new PrintWriter(fromclient2.getOutputStream(),true);
		} catch (IOException e) {
		  System.out.println("Can't accept");
		  System.exit(-1);
		}
	


		field = new int[ROWS][COLUMNS];	
		newRaund();
	}


	private int performControl(int code, int direct) {
		if ((code == LEFT)&&(direct!=RIGHT))
			return LEFT;
		else if ((code == RIGHT)&&(direct!=LEFT))
			return RIGHT;
		else if ((code == UP)&&(direct!=DOWN))
			return UP;
		else if ((code == DOWN)&&(direct!=UP))
			return DOWN;
		else return direct;
		
	}


	public void actionPerformed(ActionEvent e) {
		
		try{
			input1 = in1.readLine();
			input2 = in2.readLine();
		} catch(IOException exc) {
		}
		if (!input1.equals("N"))
		{
			//System.out.println(input1);
			direction1 = performControl(Integer.parseInt(input1), direction1);
			
		}
		if (!input2.equals("N"))
		{
			//System.out.println(input2);
			direction2 = performControl(Integer.parseInt(input2), direction2);			
		}
		switch (direction1) {
		case UP:
			if (currentRow1 > 0)
				currentRow1--;   
			break;
		case DOWN:
			if (currentRow1 < ROWS-1)
				currentRow1++;   
			break;
		case RIGHT:
			if (currentColumn1 < COLUMNS-1)
				currentColumn1++;  
			break;
		case LEFT:
			if (currentColumn1 > 0)
				currentColumn1--; 
			break;
		}


		switch (direction2) {
		case UP:
			if (currentRow2 > 0)
				currentRow2--;    
			break;
		case DOWN:
			if (currentRow2 < ROWS-1)
				currentRow2++;   
			break;
		case RIGHT:
			if (currentColumn2 < COLUMNS-1)
				currentColumn2++;  
			break;
		case LEFT:
			if (currentColumn2 > 0)
				currentColumn2--;  
			break;
		}
		
		if((isColored(currentRow1, currentColumn1))&&(isColored(currentRow2, currentColumn2))) {
			direction1 = direction2 = NOT_MOVING;
			stopRound();
			newRaund();
			///draw
		}
		else if(isColored(currentRow1, currentColumn1)) {
			direction1 = direction2 = NOT_MOVING;
			points2++;
			stopRound();
			newRaund();
			
		}
		else if(isColored(currentRow2, currentColumn2)) {
			direction1 = direction2 = NOT_MOVING;
			points1++;
			stopRound();
			newRaund();
		}
		else if ((currentRow1==currentRow2)&&(currentColumn1 == currentColumn2)) {
			stopRound();
			newRaund();
		}

		setDot(currentRow1,currentColumn1, 1);
		setDot(currentRow2,currentColumn2, 2);
		output1 = "coor" + " " + currentRow1 + " "+ currentColumn1 + " " + currentRow2 + " " + currentColumn2;
		output2 = output1;
		out1.println(output1);
		out2.println(output2);
		//System.out.println(output1);
	}

	private void setDot(int Row, int Col, int val) {
		if((Row<ROWS)&&(Row>0)&&(Col<COLUMNS)&&(Col>0)) 
			field[Row][Col] = val;
			//send cliens to draw the Dot
	}
	
	private boolean isColored(int Row, int Col) {
		if((Row<ROWS)&&(Row>0)&&(Col<COLUMNS)&&(Col>0)) 
			return field[Row][Col]!=0;
		else 
			return true;
	}

	private void fill(int val) {
		int i, j;
		for(i=0; i<ROWS; i++) {
			for(j=0; j<COLUMNS; j++) {
				field[i][j]=val;
			}
		}
	
	}

	private void loadMap(String filename) {
		try{		
			Scanner sc = new Scanner(new File(filename));
			int i = 0; 
			int j = 0;
			//System.out.println("afafaf");
			while((i < ROWS)) {
				while((j < COLUMNS)&&(sc.hasNextInt())) {
					if(sc.nextInt() != 0) {
						setDot(i, j, 3);
					}
					//System.out.println("afafaf");
					j++;
				}
				j=0;
				i++;
			}
			sc.close();
		} catch (IOException e) {
			System.out.println("ошибка " + e); 
		}
	}

	private void newRaund() {	

		fill(0);
		loadMap("Map.txt");		
  
		currentColumn1 = COLUMNS/4;  
		currentRow1 = ROWS/2;          
		direction1 = UP;               
		currentColumn2 = COLUMNS/4*3;  
		currentRow2 = ROWS/2;          

		setDot(currentRow1,currentColumn1, 1);
		setDot(currentRow2,currentColumn2, 2);  
		direction1 = direction2 = UP;      

		timer = new Timer(30,this); 
		timer.start();
		output1 = "score " + points1 + " " + points2;
		out1.println("new");
		out2.println("new");
		out1.println(output1);
		out2.println(output1);
		
	}


	private void stopRound() {	

		if (timer != null)
			timer.stop();
		timer = null;
	}
	
	public static void main(String[] args) {

		JFrame frame = new JFrame("Tron"); 
		JPanel panel;
		panel = new TronServer();
		frame.setContentPane(panel);
		System.out.println("Done");
		
		frame.pack(); 
		frame.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (screenSize.width - frame.getWidth()) / 2;
		int h = (screenSize.height - frame.getHeight()) / 2;
		frame.setLocation(w,h); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		/*
		out.close();
		in.close();
		fromclient.close();
		servers.close();
		*/
	}
	

} // end class TronPanel
