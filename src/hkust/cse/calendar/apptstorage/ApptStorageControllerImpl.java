package hkust.cse.calendar.apptstorage;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/* This class is for managing the Appt Storage according to different actions */
public class ApptStorageControllerImpl {

	/* Remove the Appt from the storage */
	public final static int REMOVE = 1;

	/* Modify the Appt the storage */
	public final static int MODIFY = 2;

	/* Add a new Appt into the storage */
	public final static int NEW = 3;

	/*
	 * Add additional flags which you feel necessary
	 */
	
	/* The Appt storage */
	private ApptStorage mApptStorage;


	/* Create a new object of ApptStorageControllerImpl from an existing storage of Appt */
	public ApptStorageControllerImpl(ApptStorage storage) {
		mApptStorage = storage;
	}


	/* Retrieve the Appt's in the storage for a specific user within the specific time span */
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		return mApptStorage.RetrieveAppts(entity, time);
	}

	// overload method to retrieve appointment with the given joint appointment id
	public Appt[] RetrieveAppts(User user, int joinApptID) {
		return mApptStorage.RetrieveAppts(user,joinApptID);
	}
	
	public Appt[] RetrieveJointApptsInWaitlist() {
		return mApptStorage.RetrieveJointApptsInWaitlist();
	}	
	
	/* Manage the Appt in the storage
	 * parameters: the Appt involved, the action to take on the Appt */
	
	// part arraylist appt
	public void ManageAppt(Appt appt, int action) {

		if (action == NEW) {				// Save Appointment
			if (appt == null)
				return;
			mApptStorage.SaveAppt(appt);
		} else if (action == MODIFY) {		// Update Appointment
			if (appt == null)
				return;
			mApptStorage.UpdateAppt(appt);
		} else if (action == REMOVE) {		// Remove Appointment 
			mApptStorage.RemoveAppt(appt);
		} 
	}

	/* Manage the Appt in the storage
	 * parameters: the Appt involved, the action to take on the Appt */
	
	public boolean checkApptLocation(String locationName) {
		return mApptStorage.checkApptLocation(locationName);
	}

	/* Get the defaultUser of mApptStorage */
	public User getDefaultUser() {
		return mApptStorage.getDefaultUser();
	}
	public int getAssignedApptID() {
		return mApptStorage.getApptID();
	}
	public void setAssignedApptID(int id){
		mApptStorage.setApptID(id);
	}
	public int getAssignedJointID() {
		return mApptStorage.getJointID();
	}
	public void setAssignedJointID(int id){
		mApptStorage.setJointID(id);
	}
	
	public boolean checkOverLap(Appt appt, Appt entry){
		return mApptStorage.checkOverLap(appt,entry);
	}
	
	public boolean checkOverLaps(ArrayList<Appt> apptlist){
		return mApptStorage.checkOverLaps(apptlist);
	}
	
	/*  xml functions*/
	// method used to load appointment from xml 
	public void loadApptFromXml(User user, HashMap<TimeSpan,Appt> appts){
		mApptStorage.loadApptXml(user,appts);
	}
	// method used to save appointment to xml 
	public void saveApptXml(Appt appt) {
		mApptStorage.saveApptXml(appt);
	}
	// method used to remove appointment from xml 
	public void removeApptXml(Appt appt) {
		mApptStorage.removeApptXml(appt);
	}
	
	/* delete the old appointment in the xml  */
	public void closeSaving(){
		for(Iterator<Entry<TimeSpan, Appt>>it=mApptStorage.mAppts.entrySet().iterator();it.hasNext();){
		     Entry<TimeSpan, Appt> entry = it.next();
				removeApptXml(entry.getValue());
		}
		for(Iterator<Entry<TimeSpan, Appt>>it=mApptStorage.mAppts.entrySet().iterator();it.hasNext();){
		     Entry<TimeSpan, Appt> entry = it.next();
				saveApptXml(entry.getValue());
		}
	}

	public Appt[] retrieveAllAppt(User user) {
		return mApptStorage.retrieveAllAppt(user);
	}

	public boolean checkOtherApptLocation(Appt appt, String locationName) {
		return mApptStorage.checkOtherApptLocation(appt, locationName);
	}
	
	public TimeSpan[] getSuggestTimeSpan(User[] users, Timestamp stamp) {
		return mApptStorage.getSuggestTimeSpan(users, stamp);
	}

	public boolean checkOtherTimespan(TimeSpan suggestedTimeSpan, User[] users) {
		return mApptStorage.checkOtherTimespan(suggestedTimeSpan, users);
	}
	
	public boolean checkLocationCapacity(Appt appt) {
		return mApptStorage.checkLocationCapacity(appt);
	}
	
	public void deleteApptByLocation(String locationName) {
		mApptStorage.deleteApptByLocation(locationName);
	}
	
	public Appt[] getApptForLocation(Location location) {
		return mApptStorage.getApptForLocation(location);
}
	
	public Appt[] getApptInDeleteLocation() {
		return mApptStorage.getApptInDeleteLocation();
	}
}
