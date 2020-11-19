package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JToggleButton;

public class JMyToggleButton extends JToggleButton {
    
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.setColor(Color.RED);
    }
}
