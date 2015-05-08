package hkust.cse.calendar.apptstorage;//

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;
import hkust.cse.calendar.xmlfactory.ApptXmlFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;


public abstract class ApptStorage {
	public static HashMap<User, HashMap<TimeSpan,Appt>> mUserToAppts = new HashMap<User, HashMap<TimeSpan,Appt>>();
	public HashMap<TimeSpan,Appt> mAppts;		//a hashmap to save every thing to it, write to memory by the memory based storage implementation	
	public User defaultUser;	//a user object, now is single user mode without login
	public int mAssignedApptID;	//a global appointment ID for each appointment record
	public static int mAssignedJointID = 0;	//a global joint appointment ID for each appointment record
	
	public UserStorageController userStorage;
	public static String apptFile = "appt.xml";  // xml file contains appointments
	public ApptXmlFactory apptXml; // link between appointment object and xml file

	public ApptStorage() {	//default constructor
	}
	
	public static HashMap<User, HashMap<TimeSpan,Appt>> getUserToAppts() {
		return ApptStorage.mUserToAppts;
	}

	public abstract void SaveAppt(Appt appt);	//abstract method to save an appointment 

	public abstract Appt[] RetrieveAppts(TimeSpan d);	//abstract method to retrieve an appointment record by a given timespan

	public abstract Appt[] RetrieveAppts(User entity, TimeSpan time);	//overloading abstract method to retrieve an appointment record by a given user object and timespan
	
	public abstract Appt[] RetrieveAppts(User user,int joinApptID);		// overload method to retrieve appointment with the given joint appointment id
	
	public abstract Appt[] RetrieveJointApptsInWaitlist();				//abstract method to retrieve appointment in the wait list

	public abstract void UpdateAppt(Appt appt);	//abstract method to update an appointment record

	public abstract void RemoveAppt(Appt appt);	//abstract method to remove an appointment record
	
	public abstract User getDefaultUser();		//abstract method to return the current user object

	public abstract int getApptID();	// return the appointment id
	
	public abstract void setApptID(int id);	// return the appointment id

	public abstract int getJointID();	// return the assigned joint id
	
	public abstract void setJointID(int id);	// return the assigned joint id

	/* xml thing*/
	public abstract void loadApptXml(User user, HashMap<TimeSpan,Appt> appts);		//abstract method to load appointment from xml to hash map

	public abstract void saveApptXml(Appt appt);		//abstract method to save appointment from hash map to xml 

	public abstract void removeApptXml(Appt appt);	//abstract method to remove appointment from xml to hash map


	public abstract boolean checkOverLap(Appt appt, Appt entry);		
	
	public abstract boolean checkOverLaps(ArrayList<Appt> apptlist);
	
	public abstract boolean checkApptLocation(String locationName);

	public abstract boolean checkOtherApptLocation(Appt appt, String locationName);
	
	public abstract Appt[] retrieveAllAppt(User user);
	
	public abstract boolean checkOtherTimespan(TimeSpan suggestedTimeSpan, User[] users);

	public abstract TimeSpan[] getSuggestedTimeSpan(User[] users, Timestamp stamp);
	
	public abstract boolean checkLocationCapacityEnough(Appt appt);
	
	public abstract void deleteApptWithLocationName(String locationName);
	
	public abstract Appt[] getApptForLocation(Location location);
	
	public abstract Appt[] getApptThatLocationInToBeDelete();
	/*
	 * Add other methods if necessary
	 */

}
