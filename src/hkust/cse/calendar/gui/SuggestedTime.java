package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class SuggestedTime extends JDialog implements ActionListener {
	private CalGrid parent;
	private Appt new_appt;
	private UserStorageController userController;
	private AppScheduler timelock;
	public ApptStorageControllerImpl controller;
	
	private TimeSpan[] suggested = null;
	private LinkedList<String> suggestedppl;
	private User[] suggestedpeople;
	
	
	private JList<TimeSpan> suggestedList;
	private DefaultListModel<TimeSpan> suggestedListModel;
	private JScrollPane suggestedScrollPane;
	
	private JButton saveBut;
	private JButton cancelBut;
	
	public SuggestedTime(CalGrid appt, AppScheduler lock, Appt newappt) {
		parent = appt;
		new_appt = newappt;
		timelock = lock;
		userController = UserStorageController.getInstance();

		setAlwaysOnTop(true);

		Container contentPane;
		contentPane = getContentPane();
		
		JPanel top = new JPanel(new BorderLayout());
		
		suggestedListModel = new DefaultListModel<TimeSpan>();
		suggestedList = new JList<TimeSpan>(suggestedListModel);
		suggestedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
		suggestedScrollPane = new JScrollPane(suggestedList);
		top.add(suggestedScrollPane);
		
	    JPanel bottom = new JPanel();
	    
	    saveBut = new JButton("Save");
		saveBut.addActionListener(this);
		bottom.add(saveBut);
		
		cancelBut = new JButton("Cancel");
		cancelBut.addActionListener(this);
		bottom.add(cancelBut);
		
		contentPane.add("North", top);
		contentPane.add("South", bottom);
		
		initializeUserList();
		initializeTimeSpanList(suggestedpeople, suggested);
		pack();
		setVisible(true);	
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource() == saveBut) {
			TimeSpan option = suggestedList.getSelectedValue();
			if(option == null) {
				JOptionPane.showMessageDialog(null, "No timeslot is selected", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				addTimeSpan(option);
				updateJointTime(option);
				setVisible(false);
			}
		} else if (e.getSource() == cancelBut) {
			setVisible(false);
			dispose();			
		}
		
	}
	
	private void initializeTimeSpanList(User[] user, TimeSpan[] suggestion) {
		Timestamp stamp = new_appt.TimeSpan().StartTime();
		suggestion = parent.controller.getSuggestTimeSpan(user, stamp);
		updateTimeSpanList(suggestion);
		selectfirst();
	}
	
	private void initializeUserList() {
		LinkedList<String> ppl = new_appt.getAllPeople();
		suggestedpeople = new User[ppl.size()];
		int i = 0;
		User[] all = userController.retrieveUsers();
		for (User people : all) {
			if (ppl.contains(people.ID())) {
				suggestedpeople[i] = people;
				i++;
			}
		}
	}
	
	private void selectfirst() {
		suggestedList.setSelectedIndex(0);
		timelock.freqOnce.setSelected(true);
		TimeSpan option = suggestedList.getSelectedValue();
		updateTimeField(option);
	}
	
	private void updateTimeSpanList(TimeSpan[] suggestion) {
		ArrayList<TimeSpan> span = new ArrayList<TimeSpan>();
		for(int i = 0; i < suggestion.length; i++) {
			span.add(suggestion[i]);
		}
		suggestedList.setListData(span.toArray(new TimeSpan[span.size()]));
	}
	private void updateJointTime(TimeSpan option) {
		updateTimeField(option);
				
		timelock.timeDisableEdit();
		timelock.freqDaily.setEnabled(false);
		timelock.freqWeekly.setEnabled(false);
		timelock.freqMonthly.setEnabled(false);
		timelock.freqOnce.setEnabled(false);
	}
	
	private void addTimeSpan(TimeSpan option) {
		new_appt.setTimeSpan(option);
	}
	
	private void updateTimeField(TimeSpan option) {
		timelock.freqOnce.setSelected(true);
		Timestamp stamp = new Timestamp(0);
		stamp = option.StartTime();
		timelock.startYearField.setText(String.valueOf(stamp.getYear()+1900));
		timelock.startMonthField.setText(String.valueOf(stamp.getMonth()+1));
		timelock.startDayField.setText(String.valueOf(stamp.getDate()));
		timelock.startHourField.setText(String.valueOf(stamp.getHours()));
		timelock.startMinuteField.setText(String.valueOf(stamp.getMinutes()));
		
		stamp = option.EndTime();
		timelock.endYearField.setText(String.valueOf(stamp.getYear()+1900));
		timelock.endMonthField.setText(String.valueOf(stamp.getMonth()+1));
		timelock.endDayField.setText(String.valueOf(stamp.getDate()));
		timelock.endHourField.setText(String.valueOf(stamp.getHours()));
		timelock.endMinuteField.setText(String.valueOf(stamp.getMinutes()));
	}
}
