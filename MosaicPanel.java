
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class MosaicPanel extends JPanel {
	
	private int rows;       
	private int columns;    
	private Color defaultColor;   
								
	private Color groutingColor;  
	private boolean alwaysDrawGrouting;  
	private Color[][] grid; 
	private BufferedImage OSI;  
							   
	private boolean needsRedraw;  

	public MosaicPanel(int rows, int columns, int preferredBlockWidth, int preferredBlockHeight, Color borderColor, int borderWidth) {
		this.rows = rows;
		this.columns = columns;
		grid = new Color[rows][columns];
		defaultColor = Color.black;
		groutingColor = Color.gray;
		alwaysDrawGrouting = false;
		setBackground(defaultColor);
		setOpaque(true);
		setDoubleBuffered(false);
		if (borderColor != null) {
			if (borderWidth < 1)
				borderWidth = 1;
			setBorder(BorderFactory.createLineBorder(borderColor,borderWidth));
		}
		else
			borderWidth = 0;
		if (preferredBlockWidth > 0 && preferredBlockHeight > 0)
			setPreferredSize(new Dimension(preferredBlockWidth*columns + 2*borderWidth, preferredBlockHeight*rows + 2*borderWidth));
	}
	
	public void setDefaultColor(Color c) {
		if (c == null)
			c = Color.black;
		if (! c.equals(defaultColor)) {
			defaultColor = c;
			setBackground(c);
			redrawMosaic();
		}
	}
	
	public Color getDefaultColor() {
		return defaultColor;
	}
	
	public void setGroutingColor(Color c) {
		if (c == null || ! c.equals(groutingColor)) {
			groutingColor = c;
			redrawMosaic();
		}
	}
	
	public Color getGroutingColor(Color c) {
		return groutingColor;
	}

	public void setAlwaysDrawGrouting(boolean always) {
		if (alwaysDrawGrouting != always) {
			alwaysDrawGrouting = always;
			redrawMosaic();
		}
	}
	
	public boolean getAlwaysDrawGrouting() {
		return alwaysDrawGrouting; 
	}
	
	public void setGridSize(int rows, 
			int columns, boolean preserveData) {
		if (rows > 0 && columns > 0) {
			Color[][] newGrid = new Color[rows][columns];
			if (preserveData) {
				int rowMax = Math.min(rows,this.rows);
				int colMax = Math.min(columns,this.columns);
				for (int r = 0; r < rowMax; r++)
					for (int c = 0; c < colMax; c++)
						newGrid[r][c] = grid[r][c];
			}
			grid = newGrid;
			this.rows = rows;
			this.columns = columns;
			redrawMosaic();
		}
	}
	
	public int getRowCount() {
		return rows;
	}
	
	public int getColumnCount() {
		return columns;
	}   
	
	public Color getColor(int row, int col) {
		if (row >=0 && row < rows && col >= 0 && col < columns)
			return grid[row][col];
		else
			return null;
	}
	
	public void setColor(int row, int col, Color c) {
		if (row >=0 && row < rows && col >= 0 && col < columns) {
			grid[row][col] = c;
			drawSquare(row,col);
		}
	}
	
	public void setColor(int row, int col, int red, int green, int blue) {
		if (row >=0 && row < rows && col >= 0 && col < columns) {
			red = (red < 0)? 0 : ( (red > 255)? 255 : red);
			green = (green < 0)? 0 : ( (green > 255)? 255 : green);
			blue = (blue < 0)? 0 : ( (blue > 255)? 255 : blue);
			grid[row][col] = new Color(red,green,blue);
			drawSquare(row,col);
		}
	}
	
	public void fill(Color c) {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				grid[i][j] = c;
		redrawMosaic();      
	}
	
	public void fill(int red, int green, int blue) {
		red = (red < 0)? 0 : ( (red > 255)? 255 : red);
		green = (green < 0)? 0 : ( (green > 255)? 255 : green);
		blue = (blue < 0)? 0 : ( (blue > 255)? 255 : blue);
		fill(new Color(red,green,blue));
	}
	
	public void clear() {
		fill(null);
	}
	
	public Object copyColorData() {

		Color[][] copy = new Color[rows][columns];
		// Replace the last row with a longer row.
		if (alwaysDrawGrouting)
			copy[rows-1] = new Color[columns+3];
		else
			copy[rows-1] = new Color[columns+2];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < columns; c++)
				copy[r][c] = grid[r][c];
		copy[rows-1][columns] = defaultColor;
		copy[rows-1][columns+1] = groutingColor;
		return copy;
	}
	
	
	public boolean restoreColorData(Object data) {
		if (data == null || !(data instanceof Color[][]))
			return false;
		Color[][] newGrid = (Color[][])data;
		int newRows = newGrid.length;
		if (newRows == 0 || newGrid[0].length == 0)
			return false;
		int newColumns = newGrid[0].length;
		for (int r = 1; r < newRows-1; r++)
			if (newGrid[r].length != newColumns)
				return false;
		if (newGrid[newRows-1].length != newColumns+2
				&& newGrid[newRows-1].length != newColumns+3)
			return false;
		if (newGrid[newRows-1][newColumns] == null)
			return false;
		rows = newRows;
		columns = newColumns;
		grid = new Color[rows][columns];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				grid[i][j] = newGrid[i][j];
		defaultColor = newGrid[newRows-1][newColumns];
		setBackground(defaultColor);
		groutingColor = newGrid[newRows-1][newColumns+1];
		alwaysDrawGrouting = newGrid[newRows-1].length == 3;
		redrawMosaic();
		return true;
	}
	

	public int xCoordToColumnNumber(int x) {
		Insets insets = getInsets();
		if (x < insets.left)
			return -1;
		double colWidth = (double)(getWidth()-insets.left-insets.right) / columns;
		int col = (int)( (x-insets.left) / colWidth);
		if (col >= columns)
			return -1;
		else
			return col;
	}

	public int yCoordToRowNumber(int y) {
		Insets insets = getInsets();
		if (y < insets.top)
			return -1;
		double rowHeight = (double)(getHeight()-insets.top-insets.bottom) / rows;
		int row = (int)( (y-insets.top) / rowHeight);
		if (row >= rows)
			return -1;
		else
			return row;
	}

	public BufferedImage getImage() {
		return OSI;
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if ( (OSI == null) || OSI.getWidth() != getWidth() || OSI.getHeight() != getHeight() ) {
			OSI = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			needsRedraw = true;
		}
		if (needsRedraw) {
			Graphics OSG = OSI.getGraphics();
			for (int r = 0; r < rows; r++)
				for (int c = 0; c < columns; c++)
					drawSquare(OSG,r,c,false);
			OSG.dispose();
			needsRedraw = false;
		}
		g.drawImage(OSI,0,0,null);
	}
	
	private void redrawMosaic() {
		needsRedraw = true;
		repaint();
	}
		
	private void drawSquare(Graphics g, int row, int col, boolean callRepaint) {
		Insets insets = getInsets();
		double rowHeight = (double)(getHeight()-insets.left-insets.right) / rows;
		double colWidth = (double)(getWidth()-insets.top-insets.bottom) / columns;
		int xOffset = insets.left;
		int yOffset = insets.top; 
		int y = yOffset + (int)Math.round(rowHeight*row);
		int h = Math.max(1, (int)Math.round(rowHeight*(row+1))+yOffset - y);
		int x = xOffset + (int)Math.round(colWidth*col);
		int w = Math.max(1, (int)Math.round(colWidth*(col+1))+xOffset - x);
		Color c = grid[row][col];
		g.setColor( (c == null)? defaultColor : c );
		if (groutingColor == null || (c == null && !alwaysDrawGrouting)) {
			if (c == null)
				g.fillRect(x,y,w,h);
			else
				g.fill3DRect(x,y,w,h,true);
		}
		else {
			if (c == null)
				g.fillRect(x+1,y+1,w-2,h-2);
			else
				g.fill3DRect(x+1,y+1,w-2,h-2,true);
			g.setColor(groutingColor);
			g.drawRect(x,y,w-1,h-1);
		}
		if (callRepaint)
			repaint(x,y,w,h);
	}
	
	private void drawSquare(int row, int col) {
		if (OSI == null)
			repaint();
		else {
			Graphics g = OSI.getGraphics();
			drawSquare(g,row,col,true);
			g.dispose();
		}
	}

	
	public boolean isColored(int row, int col) {
		if (row >=0 && row < rows && col >= 0 && col < columns)
			return grid[row][col] != null;
		else
			return false;
	}
	
} 
