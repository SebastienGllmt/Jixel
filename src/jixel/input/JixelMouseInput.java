package jixel.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JixelMouseInput implements MouseListener {

	private boolean held = false;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		held = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		held = false;
	}
	
	public boolean isMouseHeld(){
		return held;
	}

}
