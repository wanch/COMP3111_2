package hkust.cse.calendar.apptstorage;

import hkust.cse.calendar.locationstorage.LocationStorageController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;
import hkust.cse.calendar.xmlfactory.ApptXmlFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

//import sun.org.mozilla.javascript.internal.ast.Assignment; ???

public class ApptStorageNullImpl extends ApptStorage {

	public ApptStorageNullImpl(User user)
	{
		defaultUser = user;
		mAppts = new HashMap<TimeSpan, Appt>();
		mAssignedApptID = 0;
		mAssignedJointID = 0;
		apptXml = new ApptXmlFactory();
		userStorage = UserStorageController.getInstance();

		loadApptXml(user, mAppts);
		mUserToAppts.put(defaultUser, mAppts);

		ArrayList<Appt> apptlist = new ArrayList<Appt>(); // store the related joint appts from other users

		User[] users = userStorage.retrieveUsers();
		int topid=0;
		for(User u : users){
			if(u == defaultUser) continue;
			HashMap<TimeSpan, Appt> appts = new HashMap<TimeSpan, Appt> ();
			loadApptXml(u, appts);
			mUserToAppts.put(u, appts);
			loadJointAppts(appts,apptlist);

		}
		int id[] = getMaxIDs();
		setJointID(id[1]+1);

		// add the related joint appt from other users to mAppt
		for(Appt appt : apptlist){
			mAppts.put(appt.TimeSpan(),appt);
		}
	}

	@Override
	public void SaveAppt(Appt appt) {
		mAppts.put(appt.TimeSpan(),appt);
	}

	@Override
	public boolean checkOverLap(Appt appt,Appt entry){

		// if the user is in waiting list, don't check that appt
		if(appt.TimeSpan().Overlap(entry.TimeSpan())){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean checkOverLaps(ArrayList<Appt> appts){
		//  only check that user appointment overlap
		for (Appt appt: appts) {
			for (Entry<TimeSpan, Appt> entry : mAppts.entrySet()) {
				// if it is an old appt, then don't check overlap
				if(appt.getID() == entry.getValue().getID()) continue;
				// if overlap, then return true. otherwise, check next appt
				if(checkOverLap(appt,entry.getValue())){
					return true;
				}
			}	
			if(appt.isJoint()){
				// check all appointment overlap with joint appointment

				for(String people : appt.getAllPeople()){
					HashMap<TimeSpan,Appt> apptslist = mUserToAppts.get(UserStorageController.getInstance().getUserById(people));
					for(Appt mAppt : apptslist.values()){

						// if the joint appointment owner modified his appt, don't check itself 
						if(appt.isJoint() && (mAppt.getJoinID()==appt.getJoinID() )) continue;

						// Otherwise, check if the time overlapped
						if( mAppt.TimeSpan().Overlap(appt.TimeSpan())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}


	@Override
	public Appt[] RetrieveAppts(TimeSpan d) {
		ArrayList<Appt> temp = new ArrayList<Appt>();

		for (Entry<TimeSpan, Appt> entry : mAppts.entrySet()) {
			// if the user is in waiting list, don't retrieve that appt
			if(	isCurrUserInTheList(defaultUser.ID(),entry.getValue().getWaitingList()) ||	isCurrUserInTheList(defaultUser.ID(),entry.getValue().getRejectList())) continue; 
			// if overlap, then retrieve that appt
			if (d.Overlap(entry.getKey()))
				temp.add(entry.getValue());			
		}
		return temp.toArray(new Appt[temp.size()]);
	}

	@Override
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		// TODO Auto-generated method stub
		ArrayList<Appt> appts = new ArrayList<Appt>();

		for(Entry<TimeSpan, Appt> entry : mAppts.entrySet()) {
			// if the user is in waiting list, don't retrieve that appt
			if(isCurrUserInTheList(defaultUser.ID(), entry.getValue().getWaitingList()) || isCurrUserInTheList(defaultUser.ID(),entry.getValue().getRejectList()) || entry.getValue().getWaitingList().size() > 0 || entry.getValue().getRejectList().size() > 0) 
				continue; 
			// if overlap, then retrieve that appt
			if (time.Overlap(entry.getKey()))
				appts.add(entry.getValue());			
		}

		for(Entry<User,HashMap<TimeSpan, Appt>> entry : mUserToAppts.entrySet()) {
			if(!entry.getKey().ID().equals(defaultUser.ID())) {
				for(Entry<TimeSpan, Appt> appt : entry.getValue().entrySet()) {
					if (appt.getValue().isPublic() && time.Overlap(appt.getKey()) && appt.getValue().getWaitingList().size() == 0 && appt.getValue().getRejectList().size() == 0)
						appts.add(appt.getValue());
				}
			}
		}
		return appts.toArray(new Appt[appts.size()]);
	}

	public Appt[] RetrieveAppts2(User entity, TimeSpan time) {
		// TODO Auto-generated method stub
		ArrayList<Appt> appts = new ArrayList<Appt>();
		HashMap<TimeSpan, Appt> userAppts = mUserToAppts.get(entity);
		for(Appt appt : userAppts.values()) {
			if(time.Overlap(appt.TimeSpan())) {
				appts.add(appt);
			}

		}
		return appts.toArray(new Appt[appts.size()]);
	}


	@Override
	public Appt[] RetrieveJointApptsInWaitlist() {
		ArrayList<Appt> temp = new ArrayList<Appt>();
		for(Iterator<Entry<TimeSpan, Appt>>it=mAppts.entrySet().iterator();it.hasNext();){
			Entry<TimeSpan, Appt> entry = it.next();
			Appt appt = entry.getValue();
			if(appt.isJoint() && isCurrUserInTheList(defaultUser.ID(), appt.getWaitingList())){
				temp.add(appt);
			}	
		}
		return temp.toArray(new Appt[temp.size()]);
	}	


	@Override
	public Appt[] RetrieveAppts(User user, int joinApptID) {
		ArrayList<Appt> temp = new ArrayList<Appt>();
		HashMap<TimeSpan,Appt>appts = ApptStorage.mUserToAppts.get(user);
		for(Iterator<Entry<TimeSpan, Appt>>it=appts.entrySet().iterator();it.hasNext();){
			Entry<TimeSpan, Appt> entry = it.next();
			Appt appt = entry.getValue();
			if(joinApptID == appt.getJoinID()){
				temp.add(appt);
			}	
		}
		return temp.toArray(new Appt[temp.size()]);
	}

	@Override
	public void UpdateAppt(Appt appt) {
		for(Iterator<Entry<TimeSpan, Appt>>it=mAppts.entrySet().iterator();it.hasNext();){
			Entry<TimeSpan, Appt> entry = it.next();
			Appt oldAppt = entry.getValue();
			if(oldAppt.getID() == appt.getID()){
				it.remove();
				mAppts.put(appt.TimeSpan(),appt);
			}	
		}
	}

	@Override
	public void RemoveAppt(Appt appt) {
		mAppts.remove(appt.TimeSpan());
		for(Iterator<Entry<TimeSpan, Appt>>it = mAppts.entrySet().iterator(); it.hasNext();){
			Entry<TimeSpan, Appt> entry = it.next();
			if(entry.getValue().getID() == appt.getID()){
				it.remove();
			}		
		}
	}

	@Override
	public User getDefaultUser() {
		// TODO Auto-generated method stub
		return defaultUser;
	}

	//xml functions
	@Override
	public void loadApptXml(User user, HashMap<TimeSpan,Appt> appts) {
		int id = apptXml.loadApptFromXml(ApptStorage.apptFile, appts, user.ID());
		// if we are loading the current user's appt, then we set mAssignedApptID equal to the largest appt id +1 of the user
		if(user.ID().equals(defaultUser.ID())) 
			mAssignedApptID = id;
	}

	@Override
	public void saveApptXml(Appt appt) {
		apptXml.saveApptXml(ApptStorage.apptFile, appt, defaultUser.ID());
	}
	@Override
	public void removeApptXml(Appt appt) {
		apptXml.removeApptXml(ApptStorage.apptFile, appt, defaultUser.ID());
	}

	@Override 
	public int getApptID(){
		return mAssignedApptID;
	}
	@Override
	public void setApptID(int id) {
		mAssignedApptID = id;
	}
	@Override 
	public int getJointID(){
		return mAssignedJointID;
	}
	@Override
	public void setJointID(int id) {
		mAssignedJointID = id;
	}
	@Override
	public boolean checkApptLocation(String locationName) {
		for(HashMap<TimeSpan, Appt> apptMap: mUserToAppts.values()) {
			for(Appt appt : apptMap.values()) {
				if(appt.getLocation().getLocationName().equals(locationName)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean checkOtherApptLocation(Appt appt, String locationName) {

		for(Entry<User, HashMap<TimeSpan, Appt>> apptslistEntry : mUserToAppts.entrySet()){
			HashMap<TimeSpan,Appt> apptslist = apptslistEntry.getValue(); 
			for(Appt mAppt : apptslist.values()) {


				// if the joint appointment owner modified his appt, don't check it 
				if(appt.isJoint() && !isCurrUserInTheList(defaultUser.ID(), mAppt.getAllPeople()) ) continue;
				else if (mAppt.getID()==appt.getID()) continue;

				// modified: non-joint to joint, joint to joint, don't check itself
				if((appt.isJoint() && (mAppt.getJoinID()==appt.getJoinID()||(mAppt.getOwner() == defaultUser && mAppt.getID()==appt.getID())))) continue;
				// modified: non-joint to non-joint don't check itself 
				if(!appt.isJoint() && mAppt.getOwner() == defaultUser && mAppt.getID()==appt.getID()) continue;

				// Otherwise, check if the locations are same or time overlapped
				boolean isSameLocation = mAppt.getLocation().toString().equals(locationName);
				boolean isOverLap = mAppt.TimeSpan().Overlap(appt.TimeSpan());
				if(isSameLocation && isOverLap) {
					return true;
				}
			}	
		}

		return false;
	}


	@Override
	public Appt[] retrieveAllAppt(User user) {
		// TODO Auto-generated method stub
		ArrayList<Appt> appts = new ArrayList<Appt>();
		HashMap<TimeSpan, Appt> userAppt = ApptStorage.mUserToAppts.get(user);
		ArrayList<Integer> addedAppt = new ArrayList<Integer>();

		if(userAppt == null) {
			return null;
		}

		for(Appt appt : userAppt.values()) {
			boolean added = false;			

			for(int i = 0; i < addedAppt.size(); i++) {
				if(addedAppt.get(i).intValue() == appt.getID()) {
					added = true;
					break;
				}
			}

			if(!added) {
				addedAppt.add(new Integer(appt.getID()));
				//				addedAppt.sort(null);
				appts.add(appt);
			}
		}
		return appts.toArray(new Appt[appts.size()]);
	}

	@Override
	public boolean checkOtherTimespan(TimeSpan suggestedTimeSpan, User[] users){
		for(User user : users) {
			Appt[] apptsOfUser = RetrieveAppts2(user, suggestedTimeSpan);
			if(apptsOfUser.length != 0) {
				return false;
			}
		}
		return true;
	}

	public void loadJointAppts(HashMap<TimeSpan, Appt> appts, ArrayList<Appt> apptlist){
		for(Iterator<Entry<TimeSpan, Appt>>it=appts.entrySet().iterator();it.hasNext();){
			Entry<TimeSpan, Appt> entry = it.next();
			Appt appt = entry.getValue();
			if(appt.isJoint() && (isCurrUserInTheList(defaultUser.ID(), appt.getAttendList()) || isCurrUserInTheList(defaultUser.ID(), appt.getWaitingList()))) {				
				Appt clone = entry.getValue().clone(entry.getValue().TimeSpan());
				clone.setID(getApptID()+1);
				apptlist.add(clone);
			}
		}
	}

	public boolean isCurrUserInTheList(String username, LinkedList<String> list){
		for(String name : list){
			if(username.equals(name)){
				return true;
			}
		}
		return false;
	}

	@Override
	public TimeSpan[] getSuggestTimeSpan(User[] users, Timestamp stampstart) {

		ArrayList<TimeSpan> suggestedTimeSpanList = new ArrayList<TimeSpan>();

		Timestamp suggestedStartTimestamp = stampstart;
		suggestedStartTimestamp.setMinutes((suggestedStartTimestamp.getMinutes() / 15) * 15);
		suggestedStartTimestamp.setSeconds(0);

		Timestamp suggestedEndTimestamp = new Timestamp(0);
		suggestedEndTimestamp.setYear(suggestedStartTimestamp.getYear()+1900);
		suggestedEndTimestamp.setMonth(suggestedStartTimestamp.getMonth());
		suggestedEndTimestamp.setDate(suggestedStartTimestamp.getDate());
		suggestedEndTimestamp.setHours(suggestedStartTimestamp.getHours()+1);
		// TODO may be have some problem
		suggestedEndTimestamp.setMinutes((suggestedStartTimestamp.getMinutes() / 15) * 15);
		suggestedEndTimestamp.setSeconds(0);

		TimeSpan suggestedTimeSpan = new TimeSpan(suggestedStartTimestamp, suggestedEndTimestamp);

		if(suggestedTimeSpan.StartTime().getHours() > 17 || (suggestedTimeSpan.StartTime().getHours() == 5 && suggestedTimeSpan.StartTime().getMinutes() > 0)) {
			suggestedTimeSpan = TimeSpan.addTime(suggestedTimeSpan, TimeSpan.DAY, 1);

			suggestedTimeSpan.StartTime().setHours(8);
			suggestedTimeSpan.StartTime().setMinutes(0);
			suggestedTimeSpan.StartTime().setSeconds(0);

			suggestedTimeSpan.EndTime().setHours(9);
			suggestedTimeSpan.EndTime().setMinutes(0);
			suggestedTimeSpan.EndTime().setSeconds(0);
		}

		Appt[] appts = RetrieveAppts2(defaultUser, suggestedTimeSpan);
		while(appts.length != 0) {
			suggestedTimeSpan = TimeSpan.addTime(suggestedTimeSpan, TimeSpan.MINUTE, 15);
			appts = RetrieveAppts2(defaultUser, suggestedTimeSpan);
		}

		int i = 0;
		while(true) {
			//TODO: Generate suggested timespan
			if(checkOtherTimespan(suggestedTimeSpan, users)) {
				suggestedTimeSpanList.add(suggestedTimeSpan);
				i++;
			}

			if(suggestedTimeSpan.StartTime().getHours() > 17 || (suggestedTimeSpan.StartTime().getHours() == 5 && suggestedTimeSpan.StartTime().getMinutes() > 0)) {
				suggestedTimeSpan = TimeSpan.addTime(suggestedTimeSpan, TimeSpan.DAY, 1);

				suggestedTimeSpan.StartTime().setHours(8);
				suggestedTimeSpan.StartTime().setMinutes(0);
				suggestedTimeSpan.StartTime().setSeconds(0);

				suggestedTimeSpan.EndTime().setHours(9);
				suggestedTimeSpan.EndTime().setMinutes(0);
				suggestedTimeSpan.EndTime().setSeconds(0);
			}
			suggestedTimeSpan = TimeSpan.addTime(suggestedTimeSpan, TimeSpan.MINUTE, 15);
			appts = RetrieveAppts2(defaultUser, suggestedTimeSpan);
			while(appts.length != 0) {
				suggestedTimeSpan = TimeSpan.addTime(suggestedTimeSpan, TimeSpan.MINUTE, 15);
				appts = RetrieveAppts2(defaultUser, suggestedTimeSpan);
			}
			if(i == 5) {
				break;
			}
		}
		return suggestedTimeSpanList.toArray(new TimeSpan[suggestedTimeSpanList.size()]);
	} 

	public int[] getMaxIDs(){
		int[] id = new int[2];
		id[0] = -1;
		id[1] = -1;
		for(Entry<User, HashMap<TimeSpan, Appt>> apptslistEntry : mUserToAppts.entrySet()){
			HashMap<TimeSpan,Appt> apptslist = apptslistEntry.getValue(); 
			for(Appt mAppt : apptslist.values()) {
				if(id[0]<mAppt.getID())
					id[0] = mAppt.getID();
				if(id[1]<mAppt.getJoinID()) 
					id[1] = mAppt.getJoinID();
			}	
		}
		return id;
	}

	@Override
	public boolean checkLocationCapacity(Appt appt) {
		int locationCapacity = appt.getLocation().getCapacity();
		int peoplesize = appt.getAllPeople().size() + 1;
		if (peoplesize <= locationCapacity) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void deleteApptByLocation(String locationName) {
		// TODO Auto-generated method stub
		Iterator<Entry<TimeSpan, Appt>> it = mAppts.entrySet().iterator();
		while(it.hasNext()) {
			Appt appt = it.next().getValue();
			if(appt.getLocation().getLocationName().equals(locationName) && appt.getOwner().ID().equals(defaultUser.ID())) {
				it.remove();
				apptXml.deleteApptWithLocationName(defaultUser, locationName);
			}
		}
	}

	public Appt[] getApptForLocation(Location location) {
		// TODO Auto-generated method stub
		LocationStorageController locationController = LocationStorageController.getInstance();
		ArrayList<Appt> appts = new ArrayList<Appt>();
		ArrayList<Integer> addedId = new ArrayList<Integer>();

		for(Appt appt : mAppts.values()) {
			boolean added = false;
			for(Integer id : addedId) {
				if(id.intValue() == appt.getID()) {
					added = true;
					break;
				}
			}
			if(appt.getLocation().getLocationName().equals(location.getLocationName()) && !added) {
				appts.add(appt);
				addedId.add(new Integer(appt.getID()));
			}
		}
		return appts.toArray(new Appt[appts.size()]);
	}

	@Override
	public Appt[] getApptInDeleteLocation() {
		// TODO Auto-generated method stub
		LocationStorageController locationController = LocationStorageController.getInstance();
		Location[] locationsToBeDelete = locationController.getLocationInDeleteList();
		ArrayList<Appt> appts = new ArrayList<Appt>();
		ArrayList<Integer> addedId = new ArrayList<Integer>();

		for(Appt appt : mAppts.values()) {
			for(Location location : locationsToBeDelete) {
				Appt[] ApptsInLocation = getApptForLocation(location);
				for (int i=0;i<ApptsInLocation.length;i++)
					appts.add(ApptsInLocation[i]);				
			}
		}
		return appts.toArray(new Appt[appts.size()]);
	}
}
