package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.locationstorage.LocationStorageController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;
import hkust.cse.calendar.xmlfactory.ApptXmlFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class AppScheduler extends JDialog implements ActionListener, ComponentListener {

	private JLabel startYearLabel;
	public JTextField startYearField;
	private JLabel startMonthLabel;
	public JTextField startMonthField;
	private JLabel startDayLabel;
	public JTextField startDayField;

	private JLabel endYearLabel;
	public JTextField endYearField;
	private JLabel endMonthLabel;
	public JTextField endMonthField;
	private JLabel endDayLabel;
	public JTextField endDayField;

	private JLabel startHourLabel;
	public JTextField startHourField;
	private JLabel startMinuteLabel;
	public JTextField startMinuteField;

	private JLabel endHourLabel;
	public JTextField endHourField;
	private JLabel endMinuteLabel;
	public JTextField endMinuteField;

	private DefaultListModel model;
	private JTextField titleField;
	private JButton jointButton;

	public JRadioButton freqOnce;
	public JRadioButton freqDaily;
	public JRadioButton freqWeekly;
	public JRadioButton freqMonthly;
	private ButtonGroup freqbg;

	private JComboBox<Location> locationlist;
	private JComboBox reminderField;

	private JRadioButton publicAppt;
	private JRadioButton privateAppt;
	private ButtonGroup eventbg;

	private JButton saveButton;
	private JButton cancelButton;
	private JButton acceptButton;
	private JButton rejectButton;

	private Appt NewAppt;
	private CalGrid parent;
	private LocationStorageController locationControl;
	private UserStorageController userControl;
	private boolean isNew = true;
	private boolean isChanged = true;
	private boolean isJoint = false;
	
	private LinkedList<String> invited;
	private User[] invitedppl;

	private JTextArea apptDescripField;

	private JSplitPane pDes;
	JPanel apptDescripFieldPanel;

	//	private JTextField attendField;
	//	private JTextField rejectField;
	//	private JTextField waitingField;
	private int selectedApptId = -1;

	private void commonConstructor(String title, CalGrid cal, boolean inivitation) {
		parent = cal;
		this.setAlwaysOnTop(false);
		setTitle(title);
		setModal(false);

		userControl = UserStorageController.getInstance();
		
		Container contentPane;
		contentPane = getContentPane();

		JPanel psDate = new JPanel();
		Border sdateBorder = new TitledBorder(null, "START DATE");
		psDate.setBorder(sdateBorder);
		startYearLabel = new JLabel("YEAR: ");
		psDate.add(startYearLabel);
		startYearField = new JTextField(5);
		psDate.add(startYearField);
		startMonthLabel = new JLabel("MONTH: ");
		psDate.add(startMonthLabel);
		startMonthField = new JTextField(4);
		psDate.add(startMonthField);
		startDayLabel = new JLabel("DAY: ");
		psDate.add(startDayLabel);
		startDayField = new JTextField(4);
		psDate.add(startDayField);

		/*JPanel pDate = new JPanel();
		pDate.setLayout(new BorderLayout());

		pDate.add("West", psDate);
		pDate.add("East", peDate);*/

		JPanel psTime = new JPanel();
		Border stimeBorder = new TitledBorder(null, "START TIME");
		psTime.setBorder(stimeBorder);
		startHourLabel = new JLabel("HOUR: ");
		psTime.add(startHourLabel);
		startHourField = new JTextField(8);
		psTime.add(startHourField);
		startMinuteLabel = new JLabel("MINUTE: ");
		psTime.add(startMinuteLabel);
		startMinuteField = new JTextField(8);
		psTime.add(startMinuteField);

		JPanel peTime = new JPanel();
		Border etimeBorder = new TitledBorder(null, "END TIME");
		peTime.setBorder(etimeBorder);
		endHourLabel = new JLabel("HOUR: ");
		peTime.add(endHourLabel);
		endHourField = new JTextField(8);
		peTime.add(endHourField);
		endMinuteLabel = new JLabel("MINUTE: ");
		peTime.add(endMinuteLabel);
		endMinuteField = new JTextField(8);
		peTime.add(endMinuteField);

		JPanel pTime = new JPanel();
		pTime.setLayout(new BorderLayout());
		pTime.add("West", psTime);
		pTime.add("East", peTime);

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setBorder(new BevelBorder(BevelBorder.RAISED));

		top.add(psDate, BorderLayout.NORTH);
		top.add(pTime, BorderLayout.CENTER);

		contentPane.add("North", top);

		JPanel freqPanel = new JPanel();
		freqPanel.setLayout(new BorderLayout());

		JPanel frequencyPanel = new JPanel();
		Border frequencyBorder = new TitledBorder(null, "FREQUENCY");
		frequencyPanel.setBorder(frequencyBorder);
		freqOnce = new JRadioButton("One Time ");
		freqDaily = new JRadioButton("Daily ");
		freqWeekly = new JRadioButton("Weekly ");
		freqMonthly = new JRadioButton("Monthly ");
		freqbg = new ButtonGroup();
		freqOnce.setActionCommand("OneTime");
		freqDaily.setActionCommand("Daily");
		freqWeekly.setActionCommand("Weekly");
		freqMonthly.setActionCommand("Monthly");		
		freqbg.add(freqOnce);
		freqbg.add(freqDaily);
		freqbg.add(freqWeekly);
		freqbg.add(freqMonthly);
		frequencyPanel.add(freqOnce);
		frequencyPanel.add(freqDaily);
		frequencyPanel.add(freqWeekly);
		frequencyPanel.add(freqMonthly);

		freqOnce.setSelected(true);
		freqOnce.addActionListener(this);
		freqDaily.addActionListener(this);
		freqWeekly.addActionListener(this);
		freqMonthly.addActionListener(this);

		JPanel peDate = new JPanel();
		Border edateBorder = new TitledBorder(null, "END DATE");
		peDate.setBorder(edateBorder);
		endYearLabel = new JLabel("YEAR: ");
		peDate.add(endYearLabel);
		endYearField = new JTextField(5);
		endYearField.setEditable(false);
		endYearField.setEnabled(false);
		peDate.add(endYearField);
		endMonthLabel = new JLabel("MONTH: ");
		peDate.add(endMonthLabel);
		endMonthField = new JTextField(4);
		endMonthField.setEditable(false);
		endMonthField.setEnabled(false);
		peDate.add(endMonthField);
		endDayLabel = new JLabel("DAY: ");
		peDate.add(endDayLabel);
		endDayField = new JTextField(4);
		endDayField.setEditable(false);
		endDayField.setEnabled(false);
		peDate.add(endDayField);

		freqPanel.add(frequencyPanel, BorderLayout.NORTH);
		freqPanel.add(peDate, BorderLayout.SOUTH);

		JPanel titleAndTextPanel = new JPanel();
		Border titleBorder = new TitledBorder(null, "TITLE");
		titleField = new JTextField(30);
		titleField.setEditable(true);
		jointButton = new JButton("Invite Friend(s)");
		jointButton.addActionListener(this);
		titleAndTextPanel.setBorder(titleBorder);
		titleAndTextPanel.add(titleField);
		titleAndTextPanel.add(jointButton);

		JPanel locationPanel = new JPanel();
		Border locationBorder = new TitledBorder(null, "LOCATION");
		locationPanel.setBorder(locationBorder);
		Location[] l = locationControl.retrieveLocations();
		String[] ls = new String[l.length];
		for(int i = 0; i < l.length; i++) {
			ls[i] = l[i].getLocationName();
		}
		locationlist = new JComboBox<Location>(l);
		locationlist.setEditable(false);
		locationlist.addActionListener(this);
		locationPanel.add(locationlist);

		JPanel reminderPanel = new JPanel();
		Border reminderBorder = new TitledBorder(null, "REMINDER");
		reminderPanel.setBorder(reminderBorder);
		String[] reminderList = {"Off", "0","15","30","45","60"};
		reminderField = new JComboBox<String>(reminderList);
		reminderField.setEditable(false);
		reminderPanel.add(reminderField);

		JPanel info1 = new JPanel();
		info1.setLayout(new BorderLayout());
		info1.add(freqPanel, BorderLayout.NORTH);
		info1.add(titleAndTextPanel, BorderLayout.SOUTH);

		JPanel info2 = new JPanel();
		info2.setLayout(new BorderLayout());
		info2.add(locationPanel, BorderLayout.NORTH);
		info2.add(reminderPanel, BorderLayout.SOUTH);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(info1, BorderLayout.NORTH);
		infoPanel.add(info2, BorderLayout.SOUTH);

		apptDescripFieldPanel = new JPanel();
		apptDescripFieldPanel.setLayout(new BorderLayout());
		Border detailBorder = new TitledBorder(null, "Appointment Description");
		apptDescripFieldPanel.setBorder(detailBorder);
		apptDescripField = new JTextArea(10, 10);

		apptDescripField.setEditable(true);
		JScrollPane detailScroll = new JScrollPane(apptDescripField);
		apptDescripFieldPanel.add(detailScroll);

		pDes = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel,
				apptDescripFieldPanel);

		top.add(pDes, BorderLayout.SOUTH);

		if (NewAppt != null) {
			apptDescripField.setText(NewAppt.getInfo());
		}

		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));


		publicAppt = new JRadioButton("Public");
		privateAppt = new JRadioButton("Private");
		eventbg = new ButtonGroup();
		publicAppt.setActionCommand("publicAppt");
		privateAppt.setActionCommand("privateAppt");	
		eventbg.add(publicAppt);
		eventbg.add(privateAppt);
		panel2.add(publicAppt);
		panel2.add(privateAppt);
		
		privateAppt.setSelected(true);
		publicAppt.addActionListener(this);
		privateAppt.addActionListener(this);
		
		if(inivitation==true){
			acceptButton = new JButton("Accept");
			acceptButton.addActionListener(this);
			panel2.add(acceptButton);		
			
			rejectButton = new JButton("Reject");
			rejectButton.addActionListener(this);
			panel2.add(rejectButton);
			rejectButton.show(true);
		}else{
			saveButton = new JButton("Save");
			saveButton.addActionListener(this);
			panel2.add(saveButton);
		}


		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		panel2.add(cancelButton);

		contentPane.add("South", panel2);
		//NewAppt = new Appt();	//YinYin: I think this a big bug

		if (this.getTitle().equals("Join Appointment Content Change") || this.getTitle().equals("Join Appointment Invitation")){
//			inviteBut.show(false);
			rejectButton.show(true);
			cancelButton.setText("Consider Later");
			saveButton.setText("Accept");
		}
		if (this.getTitle().equals("Someone has responded to your Joint Appointment invitation") ){
//			inviteBut.show(false);
			rejectButton.show(false);
			cancelButton.show(false);
			saveButton.setText("confirmed");
		}
		if (this.getTitle().equals("Join Appointment Invitation") || this.getTitle().equals("Someone has responded to your Joint Appointment invitation") || this.getTitle().equals("Join Appointment Content Change")){
			allDisableEdit();
		}
		pack();

	}

	AppScheduler(String title, CalGrid cal, int selectedApptId) {
		this.selectedApptId = selectedApptId;
		locationControl = LocationStorageController.getInstance();
		if(title.equals("Invitation")){
			commonConstructor(title, cal,true);	
		}else{
			commonConstructor(title, cal,false);
		}
		
	}
	

	AppScheduler(String title, CalGrid cal) {
		locationControl = LocationStorageController.getInstance();
		commonConstructor(title, cal,false);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == freqOnce) {
			endYearField.setEditable(false);
			endYearField.setEnabled(false);
			endMonthField.setEditable(false);
			endMonthField.setEnabled(false);
			endDayField.setEditable(false);
			endDayField.setEnabled(false);
		}
		else if(e.getSource() == freqDaily || e.getSource() == freqMonthly || e.getSource() == freqWeekly) {
			endYearField.setEditable(true);
			endYearField.setEnabled(true);
			endMonthField.setEditable(true);
			endMonthField.setEnabled(true);
			endDayField.setEditable(true);
			endDayField.setEnabled(true);
		}

		if (e.getSource() == jointButton) {
			JointApptUserManager inviteuser = new JointApptUserManager(this.parent, AppScheduler.this, NewAppt);
		}
		else if (e.getSource() == cancelButton) {
			setVisible(false);
			dispose();
		} 
		else if (e.getSource() == saveButton) {
			boolean succeed = saveButtontonResponse();
			if(succeed) {
				/* 
				 * This is for if the appt is modified from the UserApptManager,
				 * we would like to update the Appt List in the manager
				 * */
				if(UserApptManager.getInstance() != null) {
					UserApptManager.updateApptList();
				}
				
				setVisible(false);
				dispose();
			}
		}
		else if (e.getSource() == acceptButton) {
			
			if (JOptionPane.showConfirmDialog(this, "Accept this joint appointment?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0){
				NewAppt.addAttendant(getCurrentUser());
				NewAppt.getRejectList().remove(getCurrentUser().ID());
				NewAppt.getWaitingList().remove(getCurrentUser().ID());
				
				ApptXmlFactory apptxmlfactory = new ApptXmlFactory();
				Appt[] appts = this.parent.controller.RetrieveAppts(NewAppt.getOwner(), NewAppt.getJoinID());
				for(Appt appt : appts){
					apptxmlfactory.removeApptFromXml("appt.xml", appt, NewAppt.getOwner().ID());
					apptxmlfactory.saveApptToXml("appt.xml", appt, NewAppt.getOwner().ID());					
				}

				this.setVisible(false);
				dispose();
			}
			
			/* 
			 * This is for if the appt is modified from the UserApptManager,
			 * we would like to update the Appt List in the manager
			 * */
			if(UserApptManager.getInstance() != null) {
				UserApptManager.updateApptList();
			}
			
			setVisible(false);
			dispose();
			
		} 
		else if (e.getSource() == rejectButton) {
			if (JOptionPane.showConfirmDialog(this, "Reject this joint appointment?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0){
				
				NewAppt.addReject(getCurrentUser());
				NewAppt.getAttendList().remove(getCurrentUser().ID());
				NewAppt.getWaitingList().remove(getCurrentUser().ID());
				this.parent.controller.ManageAppt(NewAppt, ApptStorageControllerImpl.REMOVE);
				ApptXmlFactory apptxmlfactory = new ApptXmlFactory();
				Appt[] appts = this.parent.controller.RetrieveAppts(NewAppt.getOwner(), NewAppt.getJoinID());
				for(Appt appt : appts){
					apptxmlfactory.removeApptFromXml("appt.xml", appt, NewAppt.getOwner().ID());
//					apptxmlfactory.saveApptToXml("appt.xml", appt, NewAppt.getOwner().ID());
				}

			};
			this.setVisible(false);
			dispose();
		}

		
		parent.getAppList().clear();
		parent.updateAppList();
		parent.repaint();
	}

	private JPanel createPartOperaPane() {
		JPanel POperaPane = new JPanel();
		JPanel browsePane = new JPanel();
		JPanel controPane = new JPanel();

		POperaPane.setLayout(new BorderLayout());
		TitledBorder titledBorder1 = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(178, 178, 178)),
				"Add Participant:");
		browsePane.setBorder(titledBorder1);

		POperaPane.add(controPane, BorderLayout.SOUTH);
		POperaPane.add(browsePane, BorderLayout.CENTER);
		POperaPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return POperaPane;

	}

	private int[] getValidDate() {

		int[] date = new int[3];
		date[0] = Utility.getNumber(startYearField.getText());
		date[1] = Utility.getNumber(startMonthField.getText());
		if (date[0] < 1980 || date[0] > 2100) {
			JOptionPane.showMessageDialog(this, "Please input proper year of the start date",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (date[1] <= 0 || date[1] > 12) {
			JOptionPane.showMessageDialog(this, "Please input proper month of the start date",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		date[2] = Utility.getNumber(startDayField.getText());
		int monthDay = CalGrid.monthDays[date[1] - 1];
		if (date[1] == 2) {
			GregorianCalendar c = new GregorianCalendar();
			if (c.isLeapYear(date[0]))
				monthDay = 29;
		}
		if (date[2] <= 0 || date[2] > monthDay) {
			JOptionPane.showMessageDialog(this,
					"Please input proper month day of the start date", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return date;
	}
	private int[] getValidEndDate() {

		// valide and get the end date 
		int[] date = new int[3];
		date[0] = Utility.getNumber(endYearField.getText());
		date[1] = Utility.getNumber(endMonthField.getText());
		if (date[0] < 1980 || date[0] > 2100) {
			JOptionPane.showMessageDialog(this, "Please input proper year of the end date",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (date[1] <= 0 || date[1] > 12) {
			JOptionPane.showMessageDialog(this, "Please input proper month of the end date",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		date[2] = Utility.getNumber(endDayField.getText());
		int monthDay = CalGrid.monthDays[date[1] - 1];
		if (date[1] == 2) {
			GregorianCalendar c = new GregorianCalendar();
			if (c.isLeapYear(date[0]))
				monthDay = 29;
		}
		if (date[2] <= 0 || date[2] > monthDay) {
			JOptionPane.showMessageDialog(this,
					"Please input proper month day of the end date", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return date;
	}


	private int getTime(JTextField h, JTextField min) {

		int hour = Utility.getNumber(h.getText());
		if (hour == -1)
			return -1;
		int minute = Utility.getNumber(min.getText());
		if (minute == -1)
			return -1;

		return (hour * 60 + minute);

	}
	
	private int getReminder(JComboBox reminder) {

		int minute = Utility.getNumber(reminder.getSelectedItem().toString());
		if (minute == -1)
			return -1;

		return (minute);

	}

	private String rollBackReminderTimestamp(Appt appt) {
		if(appt.getReminder().getReminderTimestamp() == null) {
			return "Off";
		}
		else {
			return new Integer(new TimeSpan(appt.TimeSpan().StartTime(),appt.getReminder().getReminderTimestamp()).TimeLength()).toString();
		}	
	}	

	private int[] getValidTimeInterval() {

		int[] result = new int[2];
		result[0] = getTime(startHourField, startMinuteField);
		result[1] = getTime(endHourField, endMinuteField);
		if ((result[0] % 15) != 0 || (result[1] % 15) != 0) {
			JOptionPane.showMessageDialog(this,
					"Minute Must be 0, 15, 30, or 45 !", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (!startMinuteField.getText().equals("0") && !startMinuteField.getText().equals("15") && !startMinuteField.getText().equals("30") && !startMinuteField.getText().equals("45") 
				|| !endMinuteField.getText().equals("0") && !endMinuteField.getText().equals("15") && !endMinuteField.getText().equals("30") && !endMinuteField.getText().equals("45")){
			JOptionPane.showMessageDialog(this,
					"Minute Must be 0, 15, 30, or 45 !", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (result[1] == -1 || result[0] == -1) {
			JOptionPane.showMessageDialog(this, "Please check time",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (result[1] <= result[0]) {
			JOptionPane.showMessageDialog(this,
					"End time should be bigger than \nstart time",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if ((result[0] < AppList.OFFSET * 60)
				|| (result[1] > (AppList.OFFSET * 60 + AppList.ROWNUM * 2 * 15))) {
			JOptionPane.showMessageDialog(this, "Out of Appointment Range !",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return result;
	}

	private boolean saveButtontonResponse() {
		
		// create an appt
		int[] time = getValidTimeInterval();
		int[] date = getValidDate();

		if(time == null || date == null) {
			return false;
		}

		Timestamp start = CreateTimeStamp(date, time[0]);
		Timestamp end = CreateTimeStamp(date, time[1]);
		TimeSpan startTime = new TimeSpan(start, end);
		TimeSpan startDate = new TimeSpan(startTime);
		
		String freq = freqbg.getSelection().getActionCommand();
		TimeSpan endTime = startTime;
		if(freq != "OneTime") {
			int[] enddate = getValidEndDate();
			Timestamp start_of_end_date = CreateTimeStamp(enddate,time[0]);
			Timestamp end_of_end_date = CreateTimeStamp(enddate,time[1]);
			endTime = new TimeSpan(start_of_end_date,end_of_end_date);
		}

		Timestamp reminder = Utility.convertReminderToTimestamp(getReminder(reminderField), start);
				
		NewAppt.setLocation((Location) locationlist.getSelectedItem());
		NewAppt.setTimeSpan(startTime);
		NewAppt.setStartDate(startDate);
		NewAppt.setEndDate(endTime);
		NewAppt.setTitle(titleField.getText());
		NewAppt.setInfo(apptDescripField.getText());
		NewAppt.setReminderTime(reminder);
		NewAppt.setFrequency(freq.trim());
		NewAppt.setOwner(getCurrentUser());
		
		boolean isPublic = eventbg.getSelection().getActionCommand() == "publicAppt" ? true : false;
		NewAppt.setIsPublic(isPublic);
		boolean isJoint = NewAppt.getAllPeople().isEmpty();
		NewAppt.setJoint(!isJoint);
		
		// set id & joint id for old appt		
		if (selectedApptId != -1){ // old appt
			NewAppt.setID(selectedApptId);
			if(isJoint){  
				NewAppt.setJoinID(-1);
			}else if(!isJoint && NewAppt.getJoinID()==-1){
				int joinid = this.parent.controller.getAssignedJointID();
				NewAppt.setJoinID(joinid);
				this.parent.controller.setAssignedApptID(joinid+1);
			}

		}else{ // new appt
			int apptid = this.parent.controller.getAssignedApptID();
			NewAppt.setID(apptid);
			this.parent.controller.setAssignedApptID(apptid+1);
			if(!isJoint){ // !isJoint = joint appt
				int joinid = this.parent.controller.getAssignedJointID();
				NewAppt.setJoinID(joinid);
				this.parent.controller.setAssignedApptID(joinid+1);
			}
		}


		ArrayList<Appt> apptlist = new ArrayList<Appt>();
		apptlist.add(NewAppt);


		int frequency = -1; // that mean one time event
		if (freq == "Daily"){
			frequency = TimeSpan.DAY;
		}
		else if (freq == "Weekly") {
			frequency = TimeSpan.WEEK;
		}
		else if (freq == "Monthly") {
			frequency = TimeSpan.MONTH;
		}
		 // if it is not one time event, we recursively schedule new appts until the end date
		if(frequency != -1) {
			Utility.createRepeatingAppts(NewAppt, frequency, apptlist,getReminder(reminderField));
		}

		// overlap checking
		if(this.parent.controller.checkOverLaps(apptlist)) {
			JOptionPane.showMessageDialog(null, "Appointment Overlapped!" , "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
//		System.out.println(this.parent.controller.checkotherApptsHaveLocation(NewAppt, locationlist.getSelectedItem().toString()));
		if(this.parent.controller.checkotherApptsHaveLocation(NewAppt, locationlist.getSelectedItem().toString())) {
			JOptionPane.showMessageDialog(null, "Appointment Overlapped! Your location has been used by other people in this timeslot." , "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// overlap checking GROUP event
		if(NewAppt.isJoint()){
			invited = NewAppt.getAllPeople();
			invitedppl = initializeUserList(invited);
			if(!this.parent.controller.checkotherUsersTimespan(startTime, invitedppl)){
				JOptionPane.showMessageDialog(null, "Appointment Overlapped! Other people used in this timeslot." , "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			if(!this.parent.controller.checkLocationCapacityEnough(NewAppt)) {
				JOptionPane.showMessageDialog(null, "Location Capacity not enough!" , "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		// if we are modifing an appt, remove the old appts in the memory first
		if(selectedApptId != -1){
			this.parent.controller.removeApptFromXml(NewAppt);
			this.parent.controller.ManageAppt(NewAppt, parent.controller.REMOVE); // for memory
		}

		// store the appts to the memory
		for (Appt appt : apptlist) {
			// System.out.println(this.parent.controller.getAssignedApptID());
			this.parent.controller.ManageAppt(appt, parent.controller.NEW); // for memory
		}

		this.parent.controller.saveApptToXml(NewAppt);

		return true;
	}
	
	private User[] initializeUserList(LinkedList<String> ppl) {
		User[] suggestedpeople = new User[ppl.size()];
		int i = 0;
		User[] all = userControl.retrieveUsers();
		for (User people : all) {
			if (ppl.contains(people.ID())) {
				suggestedpeople[i] = people;
				i++;
			}
		}
		return suggestedpeople;
	}

	private Timestamp CreateTimeStamp(int[] date, int time) {
		Timestamp stamp = new Timestamp(0);
		stamp.setYear(date[0]);
		stamp.setMonth(date[1] - 1);
		stamp.setDate(date[2]);
		stamp.setHours(time / 60);
		stamp.setMinutes(time % 60);
		return stamp;
	}

	// set value of modify view
	public void updateSetApp(Appt appt) {

		NewAppt = appt;
		
		Timestamp start = appt.TimeSpan().StartTime();
		Timestamp end = appt.TimeSpan().EndTime();
		
		if(selectedApptId != -1) {
			locationlist.setSelectedItem(appt.getLocation());
			reminderField.setSelectedItem(rollBackReminderTimestamp(appt));
			if (!appt.getFrequency().equals("OneTime")) {

				if (appt.getFrequency().equals("Daily")) {
					freqDaily.setSelected(true);
				} 
				else if (appt.getFrequency().equals("Weekly")) {
					freqWeekly.setSelected(true);
				} 
				else if (appt.getFrequency().equals("Monthly")) {
					freqMonthly.setSelected(true);
				}
				
				endYearField.setEditable(true);
				endYearField.setEnabled(true);
				endMonthField.setEditable(true);
				endMonthField.setEnabled(true);
				endDayField.setEditable(true);
				endDayField.setEnabled(true);
			}
			
			start = appt.getStartDate().StartTime();
			end = appt.getStartDate().EndTime();
		}
		
		Timestamp start_of_end_date = appt.getEndDate().StartTime();

		setTitle(appt.getTitle());
		titleField.setText(appt.getTitle());
		apptDescripField.setText(appt.getInfo());
		startYearField.setText(String.valueOf(start.getYear()+1900));
		startMonthField.setText(String.valueOf(start.getMonth()+1));
		startDayField.setText(String.valueOf(start.getDate()));

		endYearField.setText(String.valueOf(start_of_end_date.getYear()+1900));
		endMonthField.setText(String.valueOf(start_of_end_date.getMonth()+1));
		endDayField.setText(String.valueOf(start_of_end_date.getDate()));

		startHourField.setText(String.valueOf(start.getHours()));
		startMinuteField.setText(String.valueOf(start.getMinutes()));
		endHourField.setText(String.valueOf(end.getHours()));
		endMinuteField.setText(String.valueOf(end.getMinutes()));
		
		if(appt.isPublic()) {
			publicAppt.setSelected(true);
		}
		else {
			privateAppt.setSelected(true);
		}
	}

	public void componentHidden(ComponentEvent e) {

	}

	public void componentMoved(ComponentEvent e) {

	}

	public void componentResized(ComponentEvent e) {

		Dimension dm = pDes.getSize();
		double width = dm.width * 0.93;
		double height = dm.getHeight() * 0.6;
		apptDescripFieldPanel.setSize((int) width, (int) height);

	}

	public void componentShown(ComponentEvent e) {

	}

	public User getCurrentUser()		// get the id of the current user
	{
		return this.parent.mCurrUser;
	}

	public void allDisableEdit() {
		timeDisableEdit();
		freqDaily.setEnabled(false);
		freqWeekly.setEnabled(false);
		freqMonthly.setEnabled(false);
		freqOnce.setEnabled(false);
		reminderField.setEditable(false);
		publicAppt.setEnabled(false);
		privateAppt.setEnabled(false);
		titleField.setEditable(false);
		apptDescripField.setEditable(false);
	}
	
	public void timeDisableEdit() {
		startYearField.setEditable(false);
		startMonthField.setEditable(false);
		startDayField.setEditable(false);
		endYearField.setEditable(false);
		endMonthField.setEditable(false);
		endDayField.setEditable(false);
		startHourField.setEditable(false);
		startMinuteField.setEditable(false);
		endHourField.setEditable(false);
		endMinuteField.setEditable(false);
	}

	//public static void testu(Appt appt) {
		/*for (Appt lol = appt ; lol.TimeSpan().EndTime().before(new Timestamp(114,10,30,23,59,59,0)) ; lol = lol.clone()){
			System.out.println(lol.TimeSpan().StartTime()+" "+lol.getReminder().getReminderTimestamp());
		}*/
	//}	
	public static Date setTime(int hour,int minute) {
		Calendar c = GregorianCalendar.getInstance();
		Date d = c.getTime();
		d.setMinutes(d.getMinutes()+1);
		return d;

	}
}