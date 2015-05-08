package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class DetailsDialog extends JFrame implements ActionListener {
	private JButton exitButton;
	private JTextArea detailsArea;

	public DetailsDialog(String msg, String title) {
		paintContent(title);
		Display(msg);
		this.setSize(500, 350);
		pack();
	}

	public DetailsDialog(Appt appt, String title) {
		paintContent(title);
		this.setSize(500, 350);
		Display(appt);
		pack();

	}

	public void paintContent(String title) {

		Container content = getContentPane();
		setTitle(title);
		
		JScrollPane panel = new JScrollPane();
		Border border = new TitledBorder(null, "Information");
		panel.setBorder(border);

		detailsArea = new JTextArea(25, 40);

		panel.getViewport().add(detailsArea);

		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER));

		p2.add(exitButton);

		content.add("Center", panel);
		content.add("South", p2);

	}

	public void Display(String msg) {
		detailsArea.setFont(new Font("bold", Font.BOLD, 14));

		if (msg.length() == 0)
			msg = new String("No Information Inputed");
		detailsArea.setText(msg);
		detailsArea.setEditable(false);
	}

	public void Display(Appt appt) {

		Timestamp sTime = appt.TimeSpan().StartTime();
		Timestamp eTime = appt.TimeSpan().EndTime();
		String time = sTime.getHours() + ":";
		if (sTime.getMinutes() == 0)
			time = time + "00" + " - " + eTime.getHours() + ":";
		else
			time = time + sTime.getMinutes() + " - " + eTime.getHours() + ":";
		if (eTime.getMinutes() == 0)
			time = time + "00";
		else
			time = time + eTime.getMinutes();
		
		String date = sTime.getDate() + "-" + (sTime.getMonth()+1) + "-" + (sTime.getYear()+1900);
		
		Timestamp rTime = appt.getReminder().getReminderTimestamp();
		String reminderTime = new String();
		if(rTime == null) {
			reminderTime = "";
		}
		else {
			reminderTime = rTime.getHours() + ":";
			if(rTime.getMinutes() == 0) {
				reminderTime = reminderTime + "00";
			}
			else {
				reminderTime = reminderTime + rTime.getMinutes();
			}
		}

		detailsArea.setText("Appointment Information \n");
		detailsArea.append("Title: " + appt.getTitle() + "\n");
		detailsArea.append("Time: " + date + " " + time + "\n");
		detailsArea.append("Location: " + appt.getLocation().getLocationName() + "\n");
		detailsArea.append("Reminder: " + reminderTime +"\n");
		detailsArea.append("\nParticipants:\n");
		detailsArea.append("  Attend:");
		LinkedList<String> attendList = appt.getAttendList();
		if(attendList != null)
		{
			for(int i = 0; i < attendList.size(); i++)
			{
				detailsArea.append("  " + attendList.get(i));
			}
		}
		detailsArea.append("\n\n  Reject:");
		LinkedList<String> rejectList = appt.getRejectList();
		if(rejectList != null)
		{
			for(int i = 0; i < rejectList.size(); i++)
			{
				detailsArea.append("  " + rejectList.get(i));
			}
		}
		detailsArea.append("\n\n  Waiting:");
		LinkedList<String> waitingList = appt.getWaitingList();
		if(waitingList != null)
		{
			for(int i = 0; i < waitingList.size(); i++)
			{
				detailsArea.append("  " + waitingList.get(i));
			}
		}

		detailsArea.append("\n\nDescription: \n" + appt.getInfo());
		detailsArea.setEditable(false);
	}

	public void Display(Vector[] vs, User[] entities) {
		if (vs == null || entities == null)
			return;
		String temp = ((TimeSpan) vs[0].elementAt(0)).StartTime().toString();
		detailsArea.setText("Available Time For Selected participants and room ("
				+ temp.substring(0, temp.lastIndexOf(" ")) + ")\n\n\n");
		String temp1 = null;
		String temp2 = null;

		for (int i = 0; i < entities.length; i++) {
			detailsArea.append((i + 1) + ". " + entities[i].ID() + " :\n\n");
			for (int j = 0; j < vs[i].size(); j++) {
				temp1 = ((TimeSpan) vs[i].elementAt(j)).StartTime().toString();
				temp2 = ((TimeSpan) vs[i].elementAt(j)).EndTime().toString();
				detailsArea.append("   > From: "
						+ temp1.substring(0, temp1.lastIndexOf(':')) + "  To: "
						+ temp2.substring(0, temp2.lastIndexOf(':')) + "\n");

			}
			detailsArea.append("\n");

		}
		detailsArea.setEditable(false);

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == exitButton) {
			dispose();
		}
	}

}
