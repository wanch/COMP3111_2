package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.Position;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class JointApptUserManager extends JDialog implements ActionListener {
	private UserStorageController userController;	
	private CalGrid parent;
	private AppScheduler lock;
	
	private LinkedList<String> allList;
	
	private JList<User> remainingList;
	private DefaultListModel<User> remainingListModel;
	private JScrollPane remainingSP;
	
	private JList<User> invitedList;
	private DefaultListModel<User> invitedListModel;
	private JScrollPane invitedSP;

	private JLabel remaininguserL;
	private JLabel inviteduserL;
	
	private JButton addButton;
	private JButton removeButton;
	private JButton addallButton;
	
	private JButton saveButton;
	private JButton cancelButton;

	private ArrayList<User> invitedUser = new ArrayList<User>();
	private ArrayList<User> remainingUser = new ArrayList<User>();
	
	private Appt newAppt;
	
	public JointApptUserManager(CalGrid appt, AppScheduler appScheduler, Appt newappt) {
		parent = appt;
		newAppt = newappt;
		lock = appScheduler;
		userController = UserStorageController.getInstance();
		setAlwaysOnTop(true);
		setTitle("Select Participants: ");
		Container contentPane;
		contentPane = getContentPane();
		
		remainingListModel = new DefaultListModel<User>();
		remainingList = new JList<User>();
		remainingList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		invitedListModel = new DefaultListModel<User>();
		invitedList = new JList<User>();
		invitedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JPanel top = new JPanel();
		
	    JPanel leftPanel = new JPanel(new BorderLayout());
	    remaininguserL = new JLabel("Available Users:");
	    leftPanel.add(remaininguserL);
		remainingSP = new JScrollPane(remainingList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		remainingSP.setPreferredSize(new Dimension(200, 300));
		leftPanel.add(remainingSP);

	    JPanel rightPanel = new JPanel(new BorderLayout());
	    inviteduserL = new JLabel("Invited Users:");
	    rightPanel.add(inviteduserL);
		invitedSP = new JScrollPane(invitedList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		invitedSP.setPreferredSize(new Dimension(200, 300));
		rightPanel.add(invitedSP);

	    top.add("West", leftPanel);
	    top.add("East", rightPanel);
	    
		JPanel center = new JPanel();
		
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		addallButton = new JButton("Add all on list");
		addallButton.addActionListener(this);
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		center.add(addButton);
		center.add(addallButton);
		center.add(removeButton);

	    JPanel bottom = new JPanel();
		
	    saveButton = new JButton("Invite");
		saveButton.addActionListener(this);
		bottom.add(saveButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		bottom.add(cancelButton);
		
		contentPane.add("North", top);
		contentPane.add("Center", center);
		contentPane.add("South", bottom);

		updateUserList(invitedUser, remainingUser);
		pack();
		setVisible(true);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if (e.getSource() == addButton) {
			if(remainingList.getSelectedValuesList() == null) {
				JOptionPane.showMessageDialog(null, "No user is selected", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else {
				ArrayList<User> selecteduser = new ArrayList<User> (remainingList.getSelectedValuesList());
				addItem(remainingUser, invitedUser, selecteduser, true);
			}
		} else if (e.getSource() == addallButton) {
			if(remainingList.getComponentCount() == 0) {
				JOptionPane.showMessageDialog(null, "All users are invited", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				addallItem(true);
			}
		} else if (e.getSource() == removeButton) {
			if(invitedList.getSelectedValuesList() == null) {
				JOptionPane.showMessageDialog(null, "No user is selected", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else {
				ArrayList<User> selecteduser = new ArrayList<User> (invitedList.getSelectedValuesList());
				addItem(invitedUser, remainingUser, selecteduser, false);
			}
		}
		
		if (e.getSource() == saveButton) {
			User[] empty = null;
			if (invitedUser.size() > 0) {
				User[] wait = new User[invitedUser.size()];
				for (int i = 0; i < invitedUser.size(); i++) {
					wait[i] = invitedUser.get(i);
				}
				newAppt.setWaitingList(wait);
			} else {
				newAppt.setWaitingList(empty);
			}
			newAppt.setAttendList(empty);
			newAppt.setRejectList(empty);
			setVisible(false);
			if (newAppt.getAllPeople().size() > 0) {
				SuggestedTime suggestion = new SuggestedTime(parent, lock, newAppt);
			}
		} else if (e.getSource() == cancelButton) {
			setVisible(false);
			dispose();
		}
	}
	
	private void updateUserList(ArrayList<User> invitedUser, ArrayList<User> remainingUser) {
		if (invitedUser.isEmpty() && remainingUser.isEmpty()) {
			initializeList();
		} else {
			remainingList.setListData(remainingUser.toArray(new User[remainingUser.size()]));
			invitedList.setListData(invitedUser.toArray(new User[invitedUser.size()]));
		}
	}
	
	private void initializeList() {
		allList = newAppt.getAllPeople();
		User[] users = userController.retrieveUsers();
		invitedUser = new ArrayList<User>();
		remainingUser = new ArrayList<User>();
		for(int i = 0; i < users.length; i++) {
			if(users[i] != parent.mCurrUser){
				if(checkuserattended(allList, users[i].ID())) {
					invitedUser.add(users[i]);
				} else {
					remainingUser.add(users[i]);
				}
			}
		}
		remainingList.setListData(remainingUser.toArray(new User[remainingUser.size()]));
		invitedList.setListData(invitedUser.toArray(new User[invitedUser.size()]));
	}
	
	private void addItem(ArrayList<User> addfromM, ArrayList<User> addtoM, ArrayList<User> nametoadd, boolean option) {
		for (int i = 0; i < nametoadd.size(); i++) {
			addfromM.remove(nametoadd.get(i));
			addtoM.add(nametoadd.get(i));
		}
		if (option) {
			updateUserList(addtoM, addfromM);
		} else {
			updateUserList(addfromM, addtoM);
		}
	}

	private void addallItem(boolean option) {
		if (option) {
			remainingList.setSelectionInterval(0, remainingList.getLastVisibleIndex());
			ArrayList<User> selecteduser = new ArrayList<User> (remainingList.getSelectedValuesList());
			addItem(remainingUser, invitedUser, selecteduser, true);
		} else {
			invitedList.setSelectionInterval(0, invitedList.getLastVisibleIndex());
			ArrayList<User> selecteduser = new ArrayList<User> (invitedList.getSelectedValuesList());
			addItem(invitedUser, remainingUser, selecteduser, false);
		}
			
	}
	
	private boolean checkuserattended(LinkedList<String> attended, String userid) {
		for (int i = 0; i < attended.size(); i++){
			if (attended.contains(userid)) {
				return true;
			}
		}
		return false;
	}
}