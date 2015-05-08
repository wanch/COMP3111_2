package hkust.cse.calendar.unit;

import hkust.cse.calendar.gui.Notification;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder implements Serializable {
	private Timestamp mReminder;
	
	public Reminder() {
		mReminder = null;
	}
	
	public void setReminderTimestamp(Timestamp reminder) {
		mReminder = reminder;
	}
	
	public Timestamp getReminderTimestamp() {
		return mReminder;
	}

}
