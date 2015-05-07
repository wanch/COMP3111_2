package hkust.cse.calendar.gui;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

class CalCellRenderer extends DefaultTableCellRenderer {
	private int r;
	private int c;

	public CalCellRenderer(Object value) {
		if(value == null) {
			setBackground(new Color(230, 230, 230));
		}
	}
	
	public CalCellRenderer(Object value, boolean hasEvent) {
		
		setBackground(Color.white);
		
		if (value != null) {
			setForeground(Color.red);
		} else{
			setForeground(Color.black);
		}
		
		if(hasEvent){
			setBackground(new Color(255, 255, 153));
		}		

		setHorizontalAlignment(SwingConstants.RIGHT);
		setVerticalAlignment(SwingConstants.TOP);
	}

	public int row() {
		return r;
	}

	public int col() {
		return c;
	}

}
