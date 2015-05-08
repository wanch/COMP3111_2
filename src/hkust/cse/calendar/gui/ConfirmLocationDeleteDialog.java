package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.locationstorage.LocationStorageController;
import hkust.cse.calendar.unit.Location;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConfirmLocationDeleteDialog extends JDialog implements ActionListener {
	
	private JButton confirmButton, noButton;
	private ApptStorageControllerImpl controller;
	private String locationName;
	
	public ConfirmLocationDeleteDialog(ApptStorageControllerImpl controller, String locationName) {
		this.controller = controller;
		this.locationName = locationName;
		
		setTitle("Confirm Location be deleted");
		
		Container contentPane = getContentPane();
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		contentPane.add("Center", top);
		
		JPanel msgPanel = new JPanel();
		msgPanel.add(new JLabel("Location \"" + locationName + "\" will be deleted ."));
		top.add(msgPanel);
		
		JPanel msg2Panel = new JPanel();
		msg2Panel.add(new JLabel("All the appointments with location \"" + locationName + "\" will be deleted"));
		top.add(msg2Panel);
		
		JPanel buttonPanel = new JPanel();
		
		confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(this);
		buttonPanel.add(confirmButton);
		
		noButton = new JButton("MayBe Later");
		noButton.addActionListener(this);
		buttonPanel.add(noButton);
		
		top.add(buttonPanel);
		
		pack();
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == confirmButton) {
			controller.deleteApptByLocation(locationName);
			if(!controller.checkApptLocation(locationName)) {
				LocationStorageController locationController = LocationStorageController.getInstance();
				Location location = locationController.findLocationByName(locationName);
				locationController.manageLocation(location, LocationStorageController.REMOVE);
			}
			setVisible(false); 
			dispose();
			
		}
		else if(e.getSource() == noButton) {
			setVisible(false); 
			dispose(); 
		}
	}
}
