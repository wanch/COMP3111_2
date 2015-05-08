package hkust.cse.calendar.locationstorage;

import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.xmlfactory.LocationXmlFactory;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class LocationStorage {
	public static LocationStorage instance = null;
	public ArrayList<Location> mLocations;
	public LocationXmlFactory xmlFactory;
	public static final String locationFile = "location.xml";
	public static final String deleteLocationFile = "locationToBeDelete.xml";
	
	public abstract void saveLocation(Location location);	//abstract method to save an appointment record

	public abstract void updateLocation(Location location, String newLocationName, int newLocationCapacity);	//abstract method to remove an appointment record
	
	public abstract void removeLocation(Location location);	//abstract method to remove an appointment record
	

	public abstract Location[] retrieveLocation();	//abstract method to retrieve an appointment record by a given timespan


	public abstract boolean locationExists(Location location);	
	
	public abstract Location findLocation(String locationName);
	
	public abstract void addLocationToDeleteList(Location location);
	
	public abstract Location[] getLocationInDeleteList();
	
	public abstract void saveLocationXml(Location location);		//abstract method to load appointment from xml reocrd into hash map
	
	public abstract void updateLocationXml(Location location, String newLocationName, int newLocationCapacity);		//abstract method to load appointment from xml reocrd into hash map
	
	public abstract void removeLocationXml(Location location);
	
	public abstract void loadLocationXml();		//abstract method to load appointment from xml reocrd into hash map
	
	//public abstract void saveLocationToXml(Location location);		//abstract method to load appointment from xml reocrd into hash map
	
	//public abstract void updateLocationInXml(Location location, String newLocationName, int newLocationCapacity);		//abstract method to load appointment from xml reocrd into hash map
	
	//public abstract void removeLocationFromXml(Location location);
	
	//public abstract void loadLocationFromXml();		//abstract method to load appointment from xml reocrd into hash map
	

}
