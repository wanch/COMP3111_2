package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorage;
import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.locationstorage.LocationStorageController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.xmlfactory.ApptXmlFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

	public class LocationManager extends JDialog implements ActionListener, ListSelectionListener{
	private ApptStorageControllerImpl apptController;
	private LocationStorageController controller;
	private JList<Location> locationList;
	private DefaultListModel<Location> locationListModel;
	private JScrollPane locationPane;
	private JTextField locationName;
	private JTextField locationCapacity;
	private JButton addButton;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton closeButton;

	public LocationManager(ApptStorageControllerImpl apptControl) {
		apptController = apptControl;
		controller = LocationStorageController.getInstance();

		setTitle("Location Manager");

		Container contentPane;
		contentPane = getContentPane();

		locationListModel = new DefaultListModel<Location>();
		locationList = new JList<Location>(locationListModel);
		locationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locationList.addListSelectionListener(this);

		JPanel upper = new JPanel();
		upper.setLayout(new BorderLayout());
		locationPane = new JScrollPane(locationList);
		upper.add(locationPane);
		contentPane.add("North", upper);

		JPanel bottom = new JPanel();
		locationName = new JTextField(10);
		locationName.setText("-NAME-");
		locationCapacity = new JTextField(7);
		locationCapacity.setText("-CAPACITY-");
		bottom.add(locationName);
		bottom.add(locationCapacity);

		addButton = new JButton("Add");
		addButton.addActionListener(this);
		bottom.add(addButton);

		saveButton = new JButton("Modify");
		saveButton.addActionListener(this);
		bottom.add(saveButton);

		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(this);
		bottom.add(deleteButton);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		bottom.add(closeButton);

		contentPane.add("South", bottom);

		updateLocationList();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		// add part
		if(e.getSource() == addButton) {
			boolean error = false;
			
			if((locationCapacity.getText().equals("-CAPACITY-"))||(locationName.getText().equals("-NAME-"))){
				JOptionPane.showMessageDialog(null, "Please enter the name and capacity.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else if((locationCapacity.getText().equals(""))||(locationName.getText().equals(""))){
				JOptionPane.showMessageDialog(null, "Please enter the name and capacity.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else{
				//String locationInput = locationName.getText();
				//String locationCapacityInput = locationCapacity.getText();
				Location location = new Location(locationName.getText(), Integer.parseInt(locationCapacity.getText()));

				try {
					controller.manageLocation(location, LocationStorageController.NEW);
				}
				catch(IllegalArgumentException exception) {
					JOptionPane.showMessageDialog(null, exception.toString().substring(exception.toString().indexOf(':') + 1), "Error", JOptionPane.ERROR_MESSAGE);
					error = true;
				}
				
				// pupup success
				if(!error) {
					JOptionPane.showMessageDialog(null, "Successfully added.");
				}
			}
			
			// clear textfield
			locationName.setText(null);
			locationCapacity.setText(null);
			updateLocationList();
		}
		
		// modify part
		else if(e.getSource() == saveButton) {
			Location selectedLocation = locationList.getSelectedValue();

			// no location select
			if(selectedLocation == null) {
				JOptionPane.showMessageDialog(null, "Please select a location to modify.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			//popup confirmation
			else {
				Object[] options ={ "Yes", "No" };  
				int option = JOptionPane.showOptionDialog(null, "Are you sure to modify " + selectedLocation + "?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
				if(option == 0) {
					boolean error = false;

					Appt[] CheckAppts = apptController.getApptForLocation(selectedLocation);
					boolean maxed=false;
					for (Appt appts: CheckAppts){
						if (Integer.parseInt(locationCapacity.getText())<appts.getAllPeople().size() + 1){
							maxed = true;
							break;
						}
					}
					if (maxed){
						JOptionPane.showMessageDialog(null, "Not enough place for people,please select a larger venue.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}


					try {
						ApptXmlFactory apptxmlFactory = new ApptXmlFactory();
						apptxmlFactory.updateApptWithLocationName(ApptStorage.apptFile, selectedLocation.getLocationName(), locationName.getText());
						controller.manageLocation(selectedLocation, LocationStorageController.MODIFY, locationName.getText(), Integer.parseInt(locationCapacity.getText()));					
					}
					catch(IllegalArgumentException exception) {
						JOptionPane.showMessageDialog(null, exception.toString().substring(exception.toString().indexOf(':') + 1), "Error", JOptionPane.ERROR_MESSAGE);
						error = true;
					}


					// pupup success
					if(!error) {
						JOptionPane.showMessageDialog(null, "Location modified successfully!");
					}
					
					// clear textfield
					locationName.setText(null);
					locationCapacity.setText(null);
					updateLocationList();
				}
			}
		}
		
		// delete part
		else if(e.getSource() == deleteButton) {
			Location selectedLocation = locationList.getSelectedValue();

			// no location select 
			if(selectedLocation == null) {
				JOptionPane.showMessageDialog(null, "Please select a location to delete.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			//popup confirmation
			else {
				Object[] options ={ "Yes", "No" };  
				int option = JOptionPane.showOptionDialog(null, "Are you sure to delete " + selectedLocation + "?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]); 
				
				if(option == 0) {
					if(apptController.checkApptLocation(selectedLocation.getLocationName())) {
						
						JOptionPane.showMessageDialog(null, "Location is using .", "Error", JOptionPane.ERROR_MESSAGE);
						JOptionPane.showMessageDialog(null, "Location is using.\nLocation will be deleted after confirmed by initiators of related events", "Warning", JOptionPane.ERROR_MESSAGE);
						controller.addLocationToToBeDeleteList(selectedLocation);

					}
					else {
						controller.manageLocation(selectedLocation, LocationStorageController.REMOVE);
					}
				}
			}
			
			// clear textfield
			locationName.setText(null);
			locationCapacity.setText(null);
			updateLocationList();
		}
		
		// close part
		else if(e.getSource() == closeButton) {
			setVisible(false); 
			dispose(); 
		}
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		Location selectedLocation = locationList.getSelectedValue();
		if(selectedLocation != null) {
			locationName.setText(selectedLocation.getLocationName());
			locationCapacity.setText(new Integer(selectedLocation.getCapacity()).toString());
		}
	}

	private void updateLocationList() {
		locationList.setListData(controller.retrieveLocations());
	}
}
