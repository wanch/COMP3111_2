package hkust.cse.calendar.gui;

import hkust.cse.calendar.system.TimeMachine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TimeMachineDialog extends JFrame implements ActionListener,ChangeListener{
	private JLabel selectMessage;
	private JRadioButton defaultTime;
	private JRadioButton customTime;
	private ButtonGroup timeButGroup;
	
	private JSpinner yearSpin;
	private JSpinner monthSpin;
	private JSpinner dateSpin;
	private JSpinner hourSpin;
	private JSpinner minuteSpin;
	private JSpinner secondSpin;
	private Timestamp timeStamp;
	private JButton saveButton;
	private CalGrid parent;
	
	private Timestamp today;
	int date[] = new int [3];
	int time[] = new int [3];
	
	public TimeMachineDialog(CalGrid cal) {
		setAlwaysOnTop(true);
		setTitle("Change Time ");
		parent = cal;
		Container contentPane;
		contentPane = getContentPane();
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		
		JPanel selectPanel = new JPanel();
		Border selectBorder = new TitledBorder(null, "SELECT TIME SETTING");
		selectPanel.setBorder(selectBorder);
		
		selectMessage = new JLabel("Please select the time    ");
		selectPanel.add(selectMessage);
		
		defaultTime = new JRadioButton("Use Current Time   ");
		customTime = new JRadioButton("Set Custom Time   ");
		defaultTime.setActionCommand("default");
		customTime.setActionCommand("custom");
		timeButGroup = new ButtonGroup();
		timeButGroup.add(defaultTime);
		timeButGroup.add(customTime);
		selectPanel.add(defaultTime);
		selectPanel.add(customTime);
		defaultTime.setSelected(true);
		defaultTime.addActionListener(this);
		customTime.addActionListener(this);
		top.add(selectPanel, BorderLayout.NORTH);
		
		// panel
		JPanel timePanel = new JPanel();
		Border timeBorder = new TitledBorder(null, "TIME MACHINE");
		timePanel.setBorder(timeBorder);
		timeStamp = TimeMachine.getInstance().getNowTimestamp();

		yearSpin = new JSpinner(new SpinnerNumberModel (timeStamp.getYear()+1900,1900,2100,1));
		monthSpin = new JSpinner(new SpinnerNumberModel (timeStamp.getMonth()+1,1,12,1));
		dateSpin = new JSpinner(new SpinnerNumberModel (timeStamp.getDate(),1,31,1));
		hourSpin = new JSpinner(new SpinnerNumberModel (timeStamp.getHours(),0,23,1));
		minuteSpin = new JSpinner(new SpinnerNumberModel (timeStamp.getMinutes(),0,59,1));
		secondSpin = new JSpinner(new SpinnerNumberModel (timeStamp.getSeconds(),0,59,1));

		yearSpin.addChangeListener(this);
		monthSpin.addChangeListener(this);
		dateSpin.addChangeListener(this);
		hourSpin.addChangeListener(this);
		minuteSpin.addChangeListener(this);
		secondSpin.addChangeListener(this);
		
		JLabel yearLabel = new JLabel("YEAR: ");
		JLabel monthLabel = new JLabel("MONTH: ");
		JLabel dateLabel = new JLabel("DAY: ");
		JLabel hourLabel = new JLabel("HOUR: ");
		JLabel minuteLabel = new JLabel("MINUTE: ");
		JLabel secondLabel = new JLabel("SECOND: ");
		
		// disable all spinner because we default using the system time
		setAllTextFieldEnabled(false);
		setAllEnabled(false);

		timePanel.add(yearLabel);
		timePanel.add(yearSpin);
		timePanel.add(monthLabel);
		timePanel.add(monthSpin);
		timePanel.add(dateLabel);
		timePanel.add(dateSpin);
		timePanel.add(hourLabel);
		timePanel.add(hourSpin);
		timePanel.add(minuteLabel);
		timePanel.add(minuteSpin);
		timePanel.add(secondLabel);
		timePanel.add(secondSpin);

		top.add(timePanel, BorderLayout.SOUTH);
		
		contentPane.add("North", top);
		
		JPanel savePanel = new JPanel();
		saveButton = new JButton("Save Time");
		saveButton.addActionListener(this);
		savePanel.add(saveButton);
		contentPane.add("South", savePanel);
		
		setResizable(false);
		pack();
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub		
		if (e.getSource() == saveButton) {
			boolean succeed = saveButResponse(timeButGroup.getSelection().getActionCommand());
			if (succeed) {
				setVisible(false);
				dispose();
			}
		} 
		
		if (defaultTime.isSelected()) {

			// set spinner to the current time of time machine
			today = new Timestamp(System.currentTimeMillis());
			date[0] = today.getYear() + 1900;
			date[1] = today.getMonth();
			date[2] = today.getDate();
			time[0] = today.getHours();
			time[1] = today.getMinutes();
			time[2] = today.getSeconds();
			
			yearSpin.setValue(date[0]);
			monthSpin.setValue(date[1]);
			dateSpin.setValue(date[2]);
			hourSpin.setValue(time[0]);
			minuteSpin.setValue(time[1]);
			secondSpin.setValue(time[2]);

			// disable the spinner
			setAllEnabled(false);
			setAllTextFieldEnabled(false);


		} else if (customTime.isSelected()) {
			// enable the spinner
			setAllEnabled(true);
			setAllTextFieldEnabled(true);
		}
	}
	
	private int[] getDate() {

		int[] date = new int[3];
		date[0] = (Integer) yearSpin.getValue();
		date[1] = (Integer) monthSpin.getValue();
		date[2] = (Integer) dateSpin.getValue();
		return date;
	}

	private int[] getTime(){
		int[] time = new int[3];
		time[0] = (Integer) hourSpin.getValue();
		time[1] = (Integer) minuteSpin.getValue();
		time[2] = (Integer) secondSpin.getValue();
		return time;
	}

	private boolean saveButResponse(String tmsetting){

		if (tmsetting == "custom") { 
			// custom time
			int date[] = getDate();
			int time[] = getTime();
			TimeMachine timeMachine = TimeMachine.getInstance();
			timeMachine.setTimeMachine(date[0], date[1] - 1, date[2], time[0], time[1], time[2]);

			return true;
		} else {
			// system time
			today = new Timestamp(System.currentTimeMillis());
			date[0] = today.getYear() + 1900;
			date[1] = today.getMonth();
			date[2] = today.getDate();
			time[0] = today.getHours();
			time[1] = today.getMinutes();
			time[2] = today.getSeconds();

			TimeMachine timeMachine = TimeMachine.getInstance();
			timeMachine.setTimeMachine(date[0], date[1], date[2], time[0], time[1], time[2]);

		}
		return true;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		
		if((JSpinner)e.getSource()==yearSpin || (JSpinner)e.getSource()==monthSpin){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR,(Integer) yearSpin.getValue());
			c.set(Calendar.MONTH,(Integer) monthSpin.getValue()-1);
			dateSpin.setModel(new SpinnerNumberModel (1,1,c.getActualMaximum(Calendar.DATE),1));
		}
	}
	
	private void setAllTextFieldEnabled(boolean c){
		((JSpinner.DefaultEditor) yearSpin.getEditor()).getTextField().setEditable(c);
		((JSpinner.DefaultEditor) monthSpin.getEditor()).getTextField().setEditable(c);
		((JSpinner.DefaultEditor) dateSpin.getEditor()).getTextField().setEditable(c);
		((JSpinner.DefaultEditor) hourSpin.getEditor()).getTextField().setEditable(c);
		((JSpinner.DefaultEditor) minuteSpin.getEditor()).getTextField().setEditable(c);
		((JSpinner.DefaultEditor) secondSpin.getEditor()).getTextField().setEditable(c);
	}

	private void setAllEnabled(boolean c){
		yearSpin.setEnabled(c);
		monthSpin.setEnabled(c);
		dateSpin.setEnabled(c);
		hourSpin.setEnabled(c);
		minuteSpin.setEnabled(c);
		secondSpin.setEnabled(c);
	}
}