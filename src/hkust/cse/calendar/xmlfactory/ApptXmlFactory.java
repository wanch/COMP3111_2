package hkust.cse.calendar.xmlfactory;

import hkust.cse.calendar.apptstorage.ApptStorage;
import hkust.cse.calendar.gui.Utility;
import hkust.cse.calendar.locationstorage.LocationStorageController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ApptXmlFactory {
	// this will also return the largest appt id + 1 of specific user's appts
	public int loadApptFromXml(String file, HashMap<TimeSpan, Appt> mAppts, String userid) {
		File userDataFile = new File(file);

		int topid = -1; // to record the max top appt id from the xml file

		if(userDataFile.isFile()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document document = docBuilder.parse(userDataFile);
				document.getDocumentElement().normalize();

				// try to get the appts of specific user
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile("/Appts/user[@id='" + userid + "']");
				NodeList userlist = (NodeList) expr.evaluate(document, XPathConstants.NODESET); // nodelist of user where id="userid"
				Node user = userlist.item(0); // the node of specific user

				// if it is a new user, we need create a new user node for it, and then save the xml file and terminate the loading
				if(user==null){
					user = document.createElement("user"); // create the user node
					NamedNodeMap attributes = user.getAttributes(); // set id as attribute
					Node attNode = user.getOwnerDocument().createAttribute("id");
					attNode.setNodeValue(userid);
					attributes.setNamedItem(attNode);
					document.getFirstChild().appendChild(user); // append the user node

					// save the xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer;
					try {
						transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(document);
						StreamResult result = new StreamResult(new File(file));
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
						transformer.transform(source, result);				
					} catch (TransformerConfigurationException e) {
						e.printStackTrace();
					} catch (TransformerException e) {
						e.printStackTrace();
					}

					return -1;

				}

				// extracting the appts information from xml file
				NodeList appts = userlist.item(0).getChildNodes();
				for(int i = 0; i < appts.getLength() ; i++) {

					Node node = appts.item(i);
					if(node.getNodeType() != Node.ELEMENT_NODE) continue;

					/* starting to parse all the information of the appt */

					Element element = (Element) node;
					Element startTime_node = (Element) element.getElementsByTagName("startTime").item(0);
					Element startDate_node = (Element)element.getElementsByTagName("startDateTimeSpan").item(0);
					Element endDate_node = (Element) element.getElementsByTagName("endDateTimeSpan").item(0);

					String reminder_node = element.getElementsByTagName("reminder").item(0).getTextContent();
					Timestamp reminder = null;
					if(reminder_node!="") 
						reminder = new Timestamp(Long.parseLong(reminder_node));

					Timestamp startTime_stt = new Timestamp(getLongValue(startTime_node,"startTimeTimestamp"));
					Timestamp startTime_ett = new Timestamp(getLongValue(startTime_node,"endTimeTimestamp"));
					Timestamp startDate_stt = new Timestamp(getLongValue(startDate_node,"startTimeTimestamp"));
					Timestamp startDate_ett = new Timestamp(getLongValue(startDate_node,"endTimeTimestamp"));
					Timestamp endDate_stt = new Timestamp(getLongValue(endDate_node,"startTimeTimestamp"));
					Timestamp endDate_ett = new Timestamp(getLongValue(endDate_node,"endTimeTimestamp"));

					TimeSpan startTime = new TimeSpan(startTime_stt,startTime_ett);
					TimeSpan startDate = new TimeSpan(startDate_stt,startDate_ett);
					TimeSpan endDate = new TimeSpan(endDate_stt,endDate_ett);


					String mTitle =	getTextValue(element,"mTitle");
					String mInfo = getTextValue(element,"mInfo");
					String mLocation = getTextValue(element,"mLocation");
					String mFreq = getTextValue(element,"mFreq");
					String owner = getTextValue(element,"owner");

					int mApptID = getIntValue(element,"mApptID");
					int joinApptID = getIntValue(element,"joinApptID");

					boolean isjoint = false;
					if (element.getElementsByTagName("isjoint").item(0).getTextContent().equals("true"))
						isjoint = true;
					boolean ispublic = false;
					if (element.getElementsByTagName("ispublic").item(0).getTextContent().equals("true"))
						ispublic = true;

					NodeList attendlist = element.getElementsByTagName("attendlist").item(0).getChildNodes();
					NodeList rejectlist = element.getElementsByTagName("rejectlist").item(0).getChildNodes();
					NodeList waitinglist = element.getElementsByTagName("waitinglist").item(0).getChildNodes();

					LinkedList<String> attend=null, reject=null, waiting=null;
					if(attendlist!=null){	
						attend = new LinkedList<String>();
						for(int j = 0; j <attendlist.getLength(); j++){
							Node user_node = attendlist.item(j);
							if(user_node.getNodeType() == Node.ELEMENT_NODE) {
								attend.add(user_node.getTextContent());
							}
						}
					}
					if(rejectlist!=null){	
						reject = new LinkedList<String>();
						for(int j = 0; j < rejectlist.getLength(); j++){
							Node user_node = rejectlist.item(j);
							if(user_node.getNodeType() == Node.ELEMENT_NODE) {
								reject.add(user_node.getTextContent());
							}
						}
					}
					if(waitinglist!=null){	
						waiting = new LinkedList<String>();
						for(int j = 0; j < waitinglist.getLength(); j++){
							Node user_node = waitinglist.item(j);
							if(user_node.getNodeType() == Node.ELEMENT_NODE) {
								waiting.add(user_node.getTextContent());
							}
						}
					}

					/* end of parsing */

					/* create the appt */
					Appt appt = new Appt();
					appt.setTimeSpan(startTime);
					appt.setStartDate(startDate);
					appt.setEndDate(endDate);
					appt.setReminderTime(reminder);
					appt.setTitle(mTitle);
					appt.setInfo(mInfo);
					appt.setLocation(LocationStorageController.getInstance().findLocationByName(mLocation));
					appt.setFrequency(mFreq);
					appt.setID(mApptID);
					appt.setJoinID(joinApptID);
					appt.setJoint(isjoint);
					appt.setIsPublic(ispublic);
					appt.setOwner(UserStorageController.getInstance().getUserById(owner));
					appt.setAttendList(attend);
					appt.setRejectList(reject);
					appt.setWaitingList(waiting);
					/* end of the creation of appt */

					mAppts.put(appt.TimeSpan(),appt); // put the appt to the memory
					int frequency=-1;
					if (mFreq.equals("Daily")){
						frequency = TimeSpan.DAY;
					}
					else if (mFreq.equals("Weekly")) {
						frequency = TimeSpan.WEEK;
					}
					else if (mFreq.equals("Monthly")) {
						frequency = TimeSpan.MONTH;
					}
					// if it is not one time event, we recursively schedule new appts until the end date
					if(!mFreq.equals("OneTime")) {
						ArrayList<Appt> apptlist = new ArrayList<Appt>();
						Utility.createRepeatingAppts(appt, frequency, apptlist, Utility.reminderTimestampToMinutes(appt));
						for(Appt app : apptlist){
							mAppts.put(app.TimeSpan(), app);
						}
					}

					if(mApptID>topid) topid = mApptID; // return the max top appt id to the ApptStorage

				}

				return (1+topid);

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}else{
			// save a new xml
			try{
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document document = docBuilder.newDocument();
				Element appts = document.createElement("Appts");
				document.appendChild(appts);
				Element user = document.createElement("user");
				NamedNodeMap attributes = user.getAttributes(); // set id as attribute
				Node attNode = user.getOwnerDocument().createAttribute("id");
				attNode.setNodeValue(userid);
				attributes.setNamedItem(attNode);
				appts.appendChild(user); // append the new user tag to Appts tag

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(file));
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.transform(source, result);	
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			return (1+topid);
		}
		return (topid+1);

	}

	public void updateApptInXml(String file, Appt appt) {

	}

	public void saveApptXml(String file, Appt appt,String userid) {
		File fileObject = new File(file);

		Element appts = null;
		Document document = null;
		Element iddata;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			docBuilder = docFactory.newDocumentBuilder();

			// for the new xml, we need to create new document and create Appts node
			if(!fileObject.isFile()) { 

				document = docBuilder.newDocument();
				appts = document.createElement("Appts");
				document.appendChild(appts);


			}else{ // for the old one, we need to get the document

				document = docBuilder.parse(fileObject);
				document = docBuilder.parse(file);
				appts = (Element) document.getFirstChild();
			}

			// try to get the user node, if the user node does't exist than we create a new one
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr;
			Node user = null;

			expr = xpath.compile("/Appts/user[@id='" + userid + "']");
			NodeList userlist = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			user = userlist.item(0);

			if(user==null){
				user = document.createElement("user");
				NamedNodeMap attributes = user.getAttributes(); // set id as attribute
				Node attNode = user.getOwnerDocument().createAttribute("id");
				attNode.setNodeValue(userid);
				attributes.setNamedItem(attNode);
				appts.appendChild(user); // append the new user tag to Appts tag
			}

			/* start converting appt obj to xml */

			Element apptNode = document.createElement("Appt");

			Element startTime =  document.createElement("startTime");
			Element startDateTimeSpan =  document.createElement("startDateTimeSpan");
			Element endDateTimeSpan =  document.createElement("endDateTimeSpan");

			/* get the TimeSpan of startTime, startDate, endDate */
			Element startTimeTimestamp =  document.createElement("startTimeTimestamp");
			startTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.TimeSpan().StartTime().getTime())));
			Element endTimeTimestamp =  document.createElement("endTimeTimestamp");
			endTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.TimeSpan().EndTime().getTime())));
			startTime.appendChild(startTimeTimestamp);
			startTime.appendChild(endTimeTimestamp);

			startTimeTimestamp =  document.createElement("startTimeTimestamp");
			endTimeTimestamp =  document.createElement("endTimeTimestamp");
			startTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.getStartDate().StartTime().getTime())));
			endTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.getStartDate().EndTime().getTime())));
			startDateTimeSpan.appendChild(startTimeTimestamp);
			startDateTimeSpan.appendChild(endTimeTimestamp);

			startTimeTimestamp =  document.createElement("startTimeTimestamp");
			endTimeTimestamp =  document.createElement("endTimeTimestamp");
			startTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.getEndDate().StartTime().getTime())));
			endTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.getEndDate().EndTime().getTime())));
			endDateTimeSpan.appendChild(startTimeTimestamp);
			endDateTimeSpan.appendChild(endTimeTimestamp);
			/* end of getting the TimeSpan of startTime, startDate, endDate */

			Element reminder =  document.createElement("reminder");

			if(appt.getReminder()!=null && appt.getReminder().getReminderTimestamp()!=null){
				reminder.appendChild(document.createTextNode(String.valueOf(appt.getReminder().getReminderTimestamp().getTime())));
			}else{
				reminder.appendChild(document.createTextNode(""));
			}

			Element mTitle =  document.createElement("mTitle");
			mTitle.appendChild(document.createTextNode(appt.getTitle()));

			Element mInfo =  document.createElement("mInfo");
			mInfo.appendChild(document.createTextNode(appt.getInfo()));

			Element mLocation =  document.createElement("mLocation");
			mLocation.appendChild(document.createTextNode(appt.getLocation().getLocationName().toString()));

			Element mFreq =  document.createElement("mFreq");
			mFreq.appendChild(document.createTextNode(appt.getFrequency()));

			Element mApptID =  document.createElement("mApptID");
			mApptID.appendChild(document.createTextNode(String.valueOf(appt.getID())));

			Element joinApptID =  document.createElement("joinApptID");
			joinApptID.appendChild(document.createTextNode(String.valueOf(appt.getJoinID())));

			Element isjoint =  document.createElement("isjoint");
			isjoint.appendChild(document.createTextNode(String.valueOf(appt.isJoint())));

			Element ispublic =  document.createElement("ispublic");
			ispublic.appendChild(document.createTextNode(String.valueOf(appt.isPublic())));		

			Element owner =  document.createElement("owner");
			owner.appendChild(document.createTextNode(String.valueOf(appt.getOwner().ID())));		

			Element attendlist =  document.createElement("attendlist");
			Element rejectlist =  document.createElement("rejectlist");
			Element waitinglist =  document.createElement("waitinglist");

			LinkedList<String> attend = appt.getAttendList();
			for (String name : attend){
				Element ppt = document.createElement("ppt");
				ppt.appendChild(document.createTextNode(name));
				attendlist.appendChild(ppt);
			}
			LinkedList<String> reject = appt.getRejectList();
			for (String name : reject){
				Element ppt = document.createElement("ppt");
				ppt.appendChild(document.createTextNode(name));
				rejectlist.appendChild(ppt);
			}
			LinkedList<String> waiting = appt.getWaitingList();
			for (String name : waiting){
				Element ppt = document.createElement("ppt");
				ppt.appendChild(document.createTextNode(name));
				waitinglist.appendChild(ppt);
			}

			apptNode.appendChild(startTime);
			apptNode.appendChild(startDateTimeSpan);
			apptNode.appendChild(endDateTimeSpan);
			apptNode.appendChild(reminder);
			apptNode.appendChild(mTitle);
			apptNode.appendChild(mInfo);
			apptNode.appendChild(mLocation);
			apptNode.appendChild(mFreq);
			apptNode.appendChild(mApptID);
			apptNode.appendChild(joinApptID);
			apptNode.appendChild(isjoint);
			apptNode.appendChild(ispublic);
			apptNode.appendChild(owner);
			apptNode.appendChild(attendlist);
			apptNode.appendChild(rejectlist);
			apptNode.appendChild(waitinglist);
			user.appendChild(apptNode);

			/* end of the convertion */

			// update the xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(file));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);

		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	public void removeApptXml(String file, Appt appt,String userid) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			// get the appts of specific user
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("/Appts/user[@id='" + userid + "']");
			NodeList user = (NodeList)expr.evaluate(doc, XPathConstants.NODESET); // the user nodelist
			NodeList apptRootNode = user.item(0).getChildNodes(); // the appts nodelist of user

			for(int i = 0; i < apptRootNode.getLength(); i++) {
				Node eAppt = apptRootNode.item(i); // the appt
				if(eAppt.getNodeType() != Node.ELEMENT_NODE) continue; 
				if( ((Element) eAppt).getElementsByTagName("mApptID").item(0).getTextContent().equals(String.valueOf(appt.getID())) ) { // == will return false, so that we need to use equals
					((Node) apptRootNode).removeChild(eAppt);
					break;
				} else {
				}

			}

			// for saving
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	private int getIntValue(Element ele, String tagName) {
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	private long getLongValue(Element ele, String tagName) {
		return Long.parseLong(getTextValue(ele,tagName));
	}

	public void updateApptWithLocationName(String file, String locationName, String newLocationName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			NodeList users = doc.getElementsByTagName("user");
			for(int i = 0; i < users.getLength(); i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eUser = (Element) userNode;
					NodeList apptList = eUser.getElementsByTagName("Appt");
					for(int j = 0; j < apptList.getLength(); j++) {
						Node apptNode = apptList.item(j);
						if(apptNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eAppt = (Element) apptNode;
							if(locationName.equals(eAppt.getElementsByTagName("mLocation").item(0).getTextContent())) {
								eAppt.getElementsByTagName("mLocation").item(0).setTextContent(newLocationName);
							}
						}
					}
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void deleteApptWithLocationName(User user, String locationName) {
		String file = ApptStorage.apptFile;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			Node userRootNode = doc.getFirstChild();
			NodeList users = doc.getElementsByTagName("user");
			for(int i = 0; i < users.getLength(); i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eUser = (Element) userNode;
					if(user.ID().equals(eUser.getAttribute("id"))) {
						NodeList appts = eUser.getElementsByTagName("Appt");
						for(int j = 0; j < appts.getLength(); j++) {
							Node apptNode = appts.item(j);
							if(apptNode.getNodeType() == Node.ELEMENT_NODE) {
								Element eAppt = (Element) apptNode;
								if(eAppt.getElementsByTagName("mLocation").item(0).getTextContent().equals(locationName)) {
									userNode.removeChild(apptNode);
								}
							}
						}
					}
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteUserAppt(User user) {
		String file = ApptStorage.apptFile;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			Node userRootNode = doc.getFirstChild();
			NodeList users = doc.getElementsByTagName("user");
			for(int i = 0; i < users.getLength(); i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eUser = (Element) userNode;
					if(user.ID().equals(eUser.getAttribute("id"))) {
						userRootNode.removeChild(userNode);
					}
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
