import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.lang.Integer;
import java.lang.String;
import java.util.Scanner;

public class TronPanel extends JPanel implements FocusListener, KeyListener, ActionListener, MouseListener {	
	public Socket fromserver;
	public BufferedReader in ;
    public PrintWriter    out;
	String input, output;
	String[] parts = new String[5];
 	
	private MosaicPanel arena; 
	private boolean flag = true;
	
	private JLabel message, score;   
	private Timer timer;  
	
	private final static int ROWS = 100;         
	private final static int COLUMNS = 100;     
	private final static int BLOCKSIZE = 5;   
	private final static int BORDER_WIDTH = 1;  
	int currentRow1,currentColumn1,currentRow2,currentColumn2;
	int code;

	private final static int UP = 0, LEFT = 1, DOWN = 2, RIGHT = 3, NOT_MOVING = 4;
	private int direction1, direction2;

	private int points1 = 0, points2 = 0;
	
	public void mynet(String ip, String port) throws IOException{
			this.fromserver = new Socket(ip,Integer.parseInt(port));
			this.in = new BufferedReader(new InputStreamReader(this.fromserver.getInputStream()));
			this.out= new PrintWriter(this.fromserver.getOutputStream(),true);
			
			newRaund();
	}
	

	public TronPanel(String ip, String port) throws IOException{
		arena = new MosaicPanel(ROWS, COLUMNS, BLOCKSIZE, BLOCKSIZE, Color.GRAY, BORDER_WIDTH);
		
		message = new JLabel("Waiting for server", JLabel.CENTER);
		message.setBackground(Color.LIGHT_GRAY);
		message.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

		score = new JLabel("0 : 0", JLabel.CENTER);
		score.setBackground(Color.LIGHT_GRAY);
		score.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));


		JPanel bottom = new JPanel();
		JPanel scoreBottom = new JPanel();
		bottom.setLayout(new BorderLayout());
		bottom.setBackground(Color.LIGHT_GRAY);


		scoreBottom.setLayout(new BorderLayout());
		scoreBottom.setBackground(Color.LIGHT_GRAY);

		setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout(3,3));

		bottom.add(message,BorderLayout.CENTER);
		scoreBottom.add(score,BorderLayout.CENTER);		

		add(bottom, BorderLayout.SOUTH);
		add(scoreBottom, BorderLayout.NORTH);
		add(arena, BorderLayout.CENTER);
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,3));

		arena.setGroutingColor(null);
		arena.addFocusListener(this);
		arena.addKeyListener(this);
		arena.addMouseListener(this);
		
		arena.setBorder(BorderFactory.createLineBorder(Color.CYAN, BORDER_WIDTH));
		message.setText("Waiting 2 player");
		/*
		try{
			this.fromserver = new Socket(ip,Integer.parseInt(port));
			this.in = new BufferedReader(new InputStreamReader(this.fromserver.getInputStream()));
			this.out= new PrintWriter(this.fromserver.getOutputStream(),true);
			newRaund();
		} catch(IOException e){
			  System.exit(-1);
		}
		*/
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

  
		if (key == KeyEvent.VK_LEFT)
			code = LEFT;
		else if (key == KeyEvent.VK_RIGHT)
			code = RIGHT;
		else if (key == KeyEvent.VK_UP)
			code = UP;
		else if (key == KeyEvent.VK_DOWN)
			code = DOWN;
		
		System.out.println(key);
		//out.println(code);
		flag = false;
	}

	public void actionPerformed(ActionEvent e) {
		
		try{
			if (flag){
				 out.println("N");
			}
			else {
				out.println(code);
				flag = true;
			}
		//System.out.println("N");
			input  = in.readLine();
		} catch(IOException exp) {
			
		}
		if (input.equals("new"))
		{
			stopRound();
			newRaund();
			return;
		}
		
		
		parts = input.split(" ");
		if (parts[0].equals("coor"))
		{
			currentRow1 = Integer.parseInt(parts[1]);
			currentColumn1 = Integer.parseInt(parts[2]);
			currentRow2 = Integer.parseInt(parts[3]);
			currentColumn2 = Integer.parseInt(parts[4]);
			
			arena.setColor(currentRow1,currentColumn1,255,0,0);
			arena.setColor(currentRow2,currentColumn2,0,255,0);
			return; 
		}
	}
	
	public void newRaund() {
		
		try{
			input  = in.readLine();
			parts = input.split(" ");
			if (parts[0].equals("score"))
			{
				points1 = Integer.parseInt(parts[1]);
				points2 = Integer.parseInt(parts[2]);
			}
		} catch(IOException exp) {
		}
		
		
	
		arena.setBorder(BorderFactory.createLineBorder(Color.CYAN, BORDER_WIDTH));

		arena.fill(null); 
		loadMap("Map.txt");
		message.setText("Play");
		score.setText(points1 + " : " + points2);
		timer = new Timer(50,this); 
		timer.start();
	}


	private void stopRound() {	
		arena.setBorder(BorderFactory.createLineBorder(Color.GRAY, BORDER_WIDTH));
		if (timer != null)
			timer.stop();
		timer = null;
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
						arena.setColor(i, j, 0, 0, 255);
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

	public void mousePressed(MouseEvent e) {
		if (e.getSource() == arena)
			arena.requestFocus();
		else
			message.requestFocus();
	}

	
	
	public void focusLost(FocusEvent e) {
		
	}
	
	public void focusGained(FocusEvent e) {
		
	}
	


	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
} 
