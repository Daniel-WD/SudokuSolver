package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JToggleButton;

import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

import java.awt.Dimension;

public class Sudoku_Solver extends JFrame {
    
    final int DX = 8, DY = 30; //Konstanten für das Zeichnen im Frame
    
    static JMyToggleButton[][] field = new JMyToggleButton[9][9]; //Felder
    
    JButton jBSolve, jBNew, jBRefresh;
    int breiteR = 5, breiteL = 3; //breiteL < breiteR
    Point drawStart = new Point(12, 12),
	    builtStart = new Point((int)drawStart.getX()+(breiteR%2==0 ? breiteR/2 : breiteR/2+1), (int)drawStart.getY()+(breiteR%2==0 ? breiteR/2 : breiteR/2+1)); //Startpunkt des Sudokufeldes
    int mass = 50; //Breite und Höhe eines einzelnen Feldes
    Color primColor = new Color(85, 154, 214), sekColor = new Color(133, 177, 221), disabledColor = new Color(92, 136, 181), failColor = new Color(65, 124, 226), //Primär-, Sekundär- und DisabledFarbe des Sudokufelds
	    klickColor = new Color((primColor.getRed()+sekColor.getRed())/2, (primColor.getGreen()+sekColor.getGreen())/2, (primColor.getBlue()+sekColor.getBlue())/2), //Klickfarbe
	    redColor1 = new Color(255, 75, 75), redColor2 = new Color(255, 50, 50), redColor3 = new Color(255, 25, 25); //Fehlerfarbenstufen für Felder/Feldreihen mit fehlerhafter Zahl
    	
    boolean multiS = true, checkField = true, fails = false;
    
    
    int[][] redLevels = new int[9][9]; //speichert die vergebene Fehlerfarbentiefe für alle Felder
    
    String schriftart = "Arial";

    
    ArrayList<JToggleButton> selectedButtons = new ArrayList<JToggleButton>(); //markierte Buttons
    
    {
	UIManager.put("ToggleButton.select", klickColor);
	UIManager.put("ToggleButton.disabledText", Color.BLACK);
    }
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Sudoku_Solver frame = new Sudoku_Solver();
		    frame.setLocationRelativeTo(null);
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame.
     */
    public Sudoku_Solver() {
	super("Sudoku_Solver");
	this.addKeyListener(new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
		    String key = "0";
		    switch(e.getKeyChar()) {
		    case '1':
			key = "1";
			break;
		    case '2':
			key = "2";
			break;
		    case '3':
			key = "3";
			break;
		    case '4':
			key = "4";
			break;
		    case '5':
			key = "5";
			break;
		    case '6':
			key = "6";
			break;
		    case '7':
			key = "7";
			break;
		    case '8':
			key = "8";
			break;
		    case '9':
			key = "9";
			break;
		    case ' ':
		    case '\u0008':
		    case '\n':
    			key = "";
    			break;
		    }
		    if(!key.equals("0")) {
			for(JToggleButton tb : selectedButtons) {
			    tb.setText(String.valueOf(key));
			    tb.setSelected(false);
			}
			selectedButtons.clear();
			if(checkField) checkField();
		    }
			
		}
	});
	
	jBSolve = new JButton("Solve");
	jBSolve.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    int[][] f = new int[9][9];
		    for(int i = 0; i<9; i++) 
			for(int j = 0; j<9; j++) 
			    f[i][j] = Integer.parseInt(field[i][j].getText().equals("") ? String.valueOf(0) : field[i][j].getText());
		    
		    int[][] l = SudokuSolver.solveSudoku(f);
		    
		    if(l != null) {
			for(int i = 0; i<9; i++) {
			    for(int j = 0; j<9; j++) {
				if(field[i][j].getText() != "") field[i][j].setBackground(disabledColor);
				field[i][j].setSelected(false);
				selectedButtons.clear();
				field[i][j].setText(String.valueOf(l[i][j]));
				field[i][j].setEnabled(false);
			    }
			}
			jBSolve.setEnabled(false);
			jBRefresh.setEnabled(true);
		    } else JOptionPane.showMessageDialog(null, "Dieses Sudoku ist nicht lösbar");
		}
	});
	jBSolve.setFont(new Font("Arial", Font.BOLD, 15));
	jBSolve.setFocusable(false);
	jBSolve.setBounds((int)drawStart.getX()-(breiteR%2==0 ? breiteR/2 : breiteR/2), (int)drawStart.getY()+2*breiteR+3*breiteL+9*mass, (2*breiteR+2*breiteL+9*mass)/3, 26);
	getContentPane().add(jBSolve);
	
	
	jBNew = new JButton("New");
	jBNew.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    for(int i = 0; i<9; i++) {
			for(int j = 0; j<9; j++) {
			    field[i][j].setSelected(false);
			    selectedButtons.clear();
			    field[i][j].setText("");
			    field[i][j].setEnabled(true);
			}
		    }
		    jBRefresh.setEnabled(false);
		    jBSolve.setEnabled(true);
		    reloadColors();
		}
	});
	jBNew.setFocusable(false);
	jBNew.setBounds((int)drawStart.getX()-(breiteR%2==0 ? breiteR/2 : breiteR/2)+(2*breiteR+2*breiteL+9*mass)/3 +3, (int)drawStart.getY()+2*breiteR+3*breiteL+9*mass, (2*breiteR+2*breiteL+9*mass)/3 -5, 26);
	jBNew.setFont(new Font("Arial", Font.BOLD, 15));
	getContentPane().add(jBNew);
	
	jBRefresh = new JButton("Refresh");
	jBRefresh.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    for(int i = 0; i<9; i++) {
			for(int j = 0; j<9; j++) {
			    field[i][j].setSelected(false);
			    selectedButtons.clear();
			    if(field[i][j].getBackground() != disabledColor) field[i][j].setText("");
			    field[i][j].setEnabled(true);
			}
		    }
		    
		    jBRefresh.setEnabled(false);
		    jBSolve.setEnabled(true);
		    reloadColors();
		}
	});
	jBRefresh.setFocusable(false);
	jBRefresh.setBounds((int)drawStart.getX()-(breiteR%2==0 ? breiteR/2 : breiteR/2+1)+(2*breiteR+2*breiteL+9*mass)/3*2 +2, (int)drawStart.getY()+2*breiteR+3*breiteL+9*mass, (2*breiteR+2*breiteL+9*mass)/3, 26);
	jBRefresh.setFont(new Font("Arial", Font.BOLD, 15));
	jBRefresh.setEnabled(false);
	getContentPane().add(jBRefresh);
	
	initializeRedLevels();
	
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 3*(int)drawStart.getX()+2*breiteR+2*breiteL+9*mass, 5*(int)drawStart.getY()+2*breiteR+2*breiteL+9*mass+jBSolve.getHeight());
	setMinimumSize(new Dimension((int)this.getWidth(), (int)this.getHeight()));
	getContentPane().setLayout(null);
	

	

	
	
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		field[i][j] = new JMyToggleButton();
		
		field[i][j].setBounds((int)(j*mass+(j<3 ? 0 : j<6 ? 1 : 2)*breiteL+builtStart.getY()), (int)(i*mass+(i<3 ? 0 : i<6 ? 1 : 2)*breiteL+builtStart.getX()), mass, mass);
		field[i][j].setFocusable(false);
		field[i][j].setBackground((i<3 && j>=3 && j<6)||(i>=3 && i<6 && j<3)||(i>=3 && i<6 && j>=6)||(i>=6 && j>=3 && j<6) ? sekColor : primColor);
		field[i][j].setFont(new Font(schriftart, field[i][j].getFont().getStyle(), mass/2));
		field[i][j].addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof JToggleButton) {
			    if(multiS) {
				if(((JToggleButton)e.getSource()).isSelected()){
				    selectedButtons.add((JToggleButton)e.getSource());
				} else {
				    selectedButtons.remove((JToggleButton)e.getSource());
				}
			    } else {
				if(((JToggleButton)e.getSource()).isSelected()) {
				    selectedButtons.add((JToggleButton)e.getSource());
				    if(selectedButtons.size() >= 2) {
					selectedButtons.get(0).setSelected(false);
					selectedButtons.remove(0);
				    }
				} else {
				    selectedButtons.clear();
				}
			    }
			    
			    
			}
		    }
		});
		getContentPane().add(field[i][j]);
	    }
	}
	
    }
    
    public void paint(Graphics g) {
	super.paint(g);
	Graphics2D g2d = (Graphics2D) g;
	g2d.setStroke(new BasicStroke((float)breiteR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	g2d.drawRect((int)drawStart.getX()+DX, (int)drawStart.getY()+DY, breiteR+2*breiteL+9*mass, breiteR+2*breiteL+9*mass);
	g2d.setStroke(new BasicStroke((float)breiteL));
	
	g2d.drawLine((int)drawStart.getX()+DX, (int)drawStart.getY()+DY+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+3*mass,
		(int)drawStart.getX()+DX+breiteR+9*mass+2*breiteL, (int)drawStart.getY()+DY+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+3*mass); //X1
	g2d.drawLine((int)drawStart.getX()+DX, (int)drawStart.getY()+DY+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+6*mass+breiteL,
		(int)drawStart.getX()+DX+breiteR+9*mass+2*breiteL, (int)drawStart.getY()+DY+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+6*mass+breiteL); //X2
	
	g2d.drawLine((int)drawStart.getX()+DX+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+3*mass, (int)drawStart.getY()+DY,
		(int)drawStart.getX()+DX+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+3*mass, (int)drawStart.getY()+DY+breiteR+9*mass+2*breiteL); //Y1
	g2d.drawLine((int)drawStart.getX()+DX+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+6*mass+breiteL, (int)drawStart.getY()+DY,
		(int)drawStart.getX()+DX+(breiteR%2==0 ? breiteR/2+1 : breiteR/2+2)+6*mass+breiteL, (int)drawStart.getY()+DY+breiteR+9*mass+2*breiteL); //Y2
    }
    
    private void checkField() {
	fails = false;
	reloadColors();
	initializeRedLevels();
	//XXXXX
	
	boolean[] redRows = new boolean[9], redCols = new boolean[9];
	boolean[][] redFields = new boolean[3][3];
	for(int i = 0; i<9; i++ ) {
	    for(int j = 0; j<9; j++) {
		if(field[i][j].getText().equals("")) continue;
		
		//X
		if(!redRows[i]) {
		    for(int l = 0; l<9; l++) {
			if(j==l) continue;
			if(field[i][j].getText().equals(field[i][l].getText())) {
			    for(int k = 0; k<9; k++) 
				redLevels[i][k]++;
			    redRows[i] = true;
			    fails = true;
			    break;
			}
		    }
		}
		
		//Y
		if(!redCols[j]) {
		    for(int l = 0; l<9; l++) {
			if(i==l) continue;
			if(field[i][j].getText().equals(field[l][j].getText())) {
			    for(int k = 0; k<9; k++) 
				redLevels[k][j]++;
			    redCols[j] = true;
			    fails = true;
			    break;
			}	    
		    }
		}
		
		//Felder
		int rowS, colS;
		rowS = i - i%3;
		colS = j - j%3;
		if(!redFields[rowS == 0 ? 0 : rowS == 3 ? 1 : 2][colS == 0 ? 0 : colS == 3 ? 1 : 2]) {
		    b:
			for(int l = 0; l<3; l++) {
			    for(int k = 0; k<3; k++) {
				if(i%3==l && j%3==k) continue;
				if(field[i][j].getText().equals(field[l+rowS][k+colS].getText())) {
				    for(int u = 0; u<3; u++)
					for(int v = 0; v<3; v++)
					    redLevels[u+rowS][v+colS]++;
				    redFields[rowS == 0 ? 0 : rowS == 3 ? 1 : 2][colS == 0 ? 0 : colS == 3 ? 1 : 2] = true;
				    fails = true;
				    break b;
				}
			    }
			}
		}
	    }
	}
	
	//XXXXX
	jBSolve.setEnabled(!fails);
	reloadColors();
	updateRedLevels();
	
    }
    
    private void reloadColors() {
	UIManager.put("ToggleButton.select", fails ? failColor : klickColor);
	updateUIM();
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		field[i][j].setBackground((i<3 && j>=3 && j<6)||(i>=3 && i<6 && j<3)||(i>=3 && i<6 && j>=6)||(i>=6 && j>=3 && j<6) ? sekColor : primColor);
	    }
	}
    }
    
    private void initializeRedLevels() {
	for(int i = 0; i<9; i++)
	    for(int j = 0; j<9; j++)
		redLevels[i][j] = 0;
    }
    
    private void updateRedLevels() {
	UIManager.put("ToggleButton.select", fails ? failColor : klickColor);
	updateUIM();
	for(int i = 0; i<9; i++) {
	    for(int j = 0; j<9; j++) {
		field[i][j].setBackground(redLevels[i][j] == 0 ? field[i][j].getBackground() : redLevels[i][j] == 1 ? redColor1 : redLevels[i][j] == 2 ? redColor2 : redColor3);
	    }
	}
    }
    
    private void updateUIM() {
	for(int i = 0; i<9; i++)
	    for(int j = 0; j<9; j++)
		field[i][j].updateUI();
    }
    
    public static void drawField(int[][] s) {
	for(int i = 0; i<9; i++)
	    for(int j = 0; j<9; j++)
		field[i][j].setText(String.valueOf(s[i][j]));
    }
}
