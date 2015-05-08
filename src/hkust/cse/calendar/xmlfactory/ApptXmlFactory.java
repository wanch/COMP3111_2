package hkust.cse.calendar.xmlfactory;

import hkust.cse.calendar.apptstorage.ApptStorage;
import hkust.cse.calendar.gui.Utility;
import hkust.cse.calendar.locationstorage.LocationStorageController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
	public int loadApptFromXml(String filepath, HashMap<TimeSpan, Appt> mAppts, String userid) {
		File userDataFile = new File(filepath);

		int largeID = -1; // to record the max top appt id from the xml file

		if(userDataFile.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = builderFactory.newDocumentBuilder();
				Document document = builder.parse(userDataFile);
				document.getDocumentElement().normalize(); // normailize nodes

				// get the appts of specific user
				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();
				XPathExpression expression = xPath.compile("/Appts/user[@id='" + userid + "']");  // set id="userid"
				NodeList userlist = (NodeList) expression.evaluate(document, XPathConstants.NODESET); // nodelist of user where id="userid"
				Node user = userlist.item(0); // specific user

				// if it is a new user
				if(user == null){
					// create a new user node, and then save the xml file and terminate the loading
					user = document.createElement("user"); // create the user node
					// get a map containing the attributes of this node
					// inner users
					NamedNodeMap attributes = user.getAttributes(); 
					// get the number of nodes in this map
					int numAttrs = attributes.getLength();
					// set id as attribute
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
						StreamResult result = new StreamResult(new File(filepath));
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
					if(node.getNodeType() != Node.ELEMENT_NODE)
					{
						continue;  // for </xxx>
					}

					// starting to parse all the information of the appt 
					else if(node.getNodeType() == Node.ELEMENT_NODE){
						Element element = (Element) node;
						Element startTime_node = (Element) element.getElementsByTagName("startTime").item(0);
						Element startDate_node = (Element)element.getElementsByTagName("startDateTimeSpan").item(0);
						Element endDate_node = (Element) element.getElementsByTagName("endDateTimeSpan").item(0);
	
						String reminder_node = element.getElementsByTagName("reminder").item(0).getTextContent();
						Timestamp reminder = null;
						if(reminder_node != "") 
							reminder = new Timestamp(Long.parseLong(reminder_node));
	
						long stt = getLongValue(startTime_node,"startTimestamp");
						Timestamp startTime_stt = new Timestamp(stt);
						long ett = getLongValue(startTime_node,"endTimestamp");
						Timestamp startTime_ett = new Timestamp(ett);
						
						long sdt = getLongValue(startDate_node,"startTimestamp");
						Timestamp startDate_stt = new Timestamp(sdt);
						long edt = getLongValue(startDate_node,"endTimestamp");
						Timestamp startDate_ett = new Timestamp(edt);
						
						long sdd = getLongValue(endDate_node,"startTimestamp");
						Timestamp endDate_stt = new Timestamp(sdd);
						long edd = getLongValue(endDate_node,"endTimestamp");
						Timestamp endDate_ett = new Timestamp(edd);
	
						TimeSpan startTime = new TimeSpan(startTime_stt,startTime_ett);
						TimeSpan startDate = new TimeSpan(startDate_stt,startDate_ett);
						TimeSpan endDate = new TimeSpan(endDate_stt,endDate_ett);
	
	
						String mTitle =	getTextValue(element,"mTitle");
						String mInfo = getTextValue(element,"mInfo");
						String mLocation = getTextValue(element,"mLocation");
						String mFreq = getTextValue(element,"mFreq");
						String owner = getTextValue(element,"owner");
	
						int mApptID = getIntValue(element,"mApptID");
						int jointID = getIntValue(element,"jointID");
	
						boolean isJoint = false;
						NodeList nodeListJoint = element.getElementsByTagName("isJoint");
						Node temp = nodeListJoint.item(0);
						String tempString = temp.getTextContent();
						if (tempString.equals("true") == true)
							isJoint = true;
						
						boolean ispublic = false;
						NodeList nodeListPublic = element.getElementsByTagName("ispublic");
						temp = nodeListPublic.item(0);
						tempString = temp.getTextContent();
						if (tempString.equals("true") == true)
							ispublic = true;
	
						//NodeList attendlist = element.getElementsByTagName("attendlist").item(0);
						NodeList attendnodelist = element.getElementsByTagName("attendlist").item(0).getChildNodes();
						//NodeList rejectlist = element.getElementsByTagName("rejectlist").item(0);
						NodeList rejectnodelist = element.getElementsByTagName("rejectlist").item(0).getChildNodes();
						//NodeList waitinglist = element.getElementsByTagName("waitinglist").item(0);
						NodeList waitingnodelist = element.getElementsByTagName("waitinglist").item(0).getChildNodes();
	
						LinkedList<String> attend = null;
						LinkedList<String> reject = null;
						LinkedList<String> waiting = null;
						if(attendnodelist != null){	
							attend = new LinkedList<String>();
							for(int j = 0; j <attendnodelist.getLength(); j++){
								Node user_node = attendnodelist.item(j);
								short temp1 = user_node.getNodeType();
								if(temp1 == Node.ELEMENT_NODE) {
									String attenduser = user_node.getTextContent();
									attend.add(attenduser);
								}
							}
						}
						
						if(rejectnodelist != null){	
							reject = new LinkedList<String>();
							for(int j = 0; j < rejectnodelist.getLength(); j++){
								Node user_node = rejectnodelist.item(j);
								short temp1 = user_node.getNodeType();
								if(temp1 == Node.ELEMENT_NODE) {
									String rejectuser = user_node.getTextContent();
									reject.add(rejectuser);
								}
							}
						}
						if(waitingnodelist!=null){	
							waiting = new LinkedList<String>();
							for(int j = 0; j < waitingnodelist.getLength(); j++){
								Node user_node = waitingnodelist.item(j);
								short temp1 = user_node.getNodeType();
								if(temp1 == Node.ELEMENT_NODE) {
									String waituser = user_node.getTextContent();
									waiting.add(waituser);
								}
							}
						}
	
						// end of parsing 
	
						// create the appt 
						Appt appt = new Appt();
						appt.setTimeSpan(startTime);
						appt.setStartDate(startDate);
						appt.setEndDate(endDate);
						appt.setReminderTime(reminder);
						appt.setTitle(mTitle);
						appt.setInfo(mInfo);
						
						LocationStorageController locController = LocationStorageController.getInstance();
						Location location = locController.findLocationByName(mLocation);
						appt.setLocation(location);
						
						appt.setFrequency(mFreq);
						appt.setID(mApptID);
						appt.setJoinID(jointID);
						appt.setJoint(isJoint);
						appt.setIsPublic(ispublic);
						
						UserStorageController userController = UserStorageController.getInstance();
						User ownerUser = userController.getUserById(owner);
						appt.setOwner(ownerUser);
						
						appt.setAttendList(attend);
						appt.setRejectList(reject);
						appt.setWaitingList(waiting);
						// end of the creation of appt 
	
						mAppts.put(appt.TimeSpan(),appt); // put the appt to the memory
						int frequency = -1;
						
						if (mFreq.equals("Daily") == true){
							frequency = TimeSpan.DAY;
						}
						else if (mFreq.equals("Weekly") == true) {
							frequency = TimeSpan.WEEK;
						}
						else if (mFreq.equals("Monthly") == true) {
							frequency = TimeSpan.MONTH;
						}
						
						// if it is not one time event, we recursively schedule new appts until the end date
						if(!mFreq.equals("Once") == true) {
							ArrayList<Appt> apptlist = new ArrayList<Appt>();
							Utility.createRepeatingAppts(appt, frequency, apptlist, Utility.reminderTimestampToMinutes(appt));
							for(Appt app : apptlist){
								mAppts.put(app.TimeSpan(), app);
							}
						}
	
						if(mApptID > largeID){
							largeID = mApptID; // return the max top appt id to the ApptStorage
							// System.out.ptintln("largeID " + largeID);
						}
	
					}
				}
				int nextID = largeID+1;
				return nextID;

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
			// new xml
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try{
				builder = builderFactory.newDocumentBuilder();
				Document document = builder.newDocument();
				
				Element appts = document.createElement("Appts");
				document.appendChild(appts);
				
				Element user = document.createElement("user");
				
				NamedNodeMap attributes = user.getAttributes(); 
				
				Node attNode = user.getOwnerDocument().createAttribute("id");
				
				attNode.setNodeValue(userid); 
				attributes.setNamedItem(attNode);
				appts.appendChild(user); // append the new user tag to Appts tag

				Transformer xformer = TransformerFactory.newInstance().newTransformer();

				xformer.setOutputProperty(OutputKeys.INDENT, "yes");
				xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(filepath));
				xformer.transform(source, result);	
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			int nextID = largeID+1;
			return nextID;
		}
		int nextID = largeID+1;
		return nextID;
		
	}

	public void updateApptInXml(String file, Appt appt) {

	}

	public void saveApptToXml(String file, Appt appt,String userid) {
		File fileObject = new File(file);
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		Element appts = null;
		Document document = null;
		Element iddata;
		try {
			builder = builderFactory.newDocumentBuilder();

			// for the new xml, we need to create new document and create Appts node
			if(!fileObject.isFile()) { 

				document = builder.newDocument();
				appts = document.createElement("Appts");
				document.appendChild(appts);


			}else{ // for the old one, we need to get the document

				document = builder.parse(fileObject);
				document = builder.parse(file);
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
			Element startTimeTimestamp =  document.createElement("startTimestamp");
			startTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.TimeSpan().StartTime().getTime())));
			Element endTimeTimestamp =  document.createElement("endTimestamp");
			endTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.TimeSpan().EndTime().getTime())));
			startTime.appendChild(startTimeTimestamp);
			startTime.appendChild(endTimeTimestamp);

			startTimeTimestamp =  document.createElement("startTimestamp");
			endTimeTimestamp =  document.createElement("endTimestamp");
			startTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.getStartDate().StartTime().getTime())));
			endTimeTimestamp.appendChild(document.createTextNode(String.valueOf(appt.getStartDate().EndTime().getTime())));
			startDateTimeSpan.appendChild(startTimeTimestamp);
			startDateTimeSpan.appendChild(endTimeTimestamp);

			startTimeTimestamp =  document.createElement("startTimestamp");
			endTimeTimestamp =  document.createElement("endTimestamp");
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

			Element isJoint =  document.createElement("isJoint");
			isJoint.appendChild(document.createTextNode(String.valueOf(appt.isJoint())));

			Element ispublic =  document.createElement("ispublic");
			ispublic.appendChild(document.createTextNode(String.valueOf(appt.isPublic())));		

			Element owner =  document.createElement("owner");
			owner.appendChild(document.createTextNode(String.valueOf(appt.getOwner().ID())));		

			Element attendlist =  document.createElement("attendlist");
			Element rejectlist =  document.createElement("rejectlist");
			Element waitinglist =  document.createElement("waitinglist");

			LinkedList<String> attend = appt.getAttendList();
			for (String name : attend){
				Element ppl = document.createElement("ppl");
				ppl.appendChild(document.createTextNode(name));
				attendlist.appendChild(ppl);
				
			}
			
			LinkedList<String> reject = appt.getRejectList();
			for (String name : reject){
				Element ppl = document.createElement("ppl");
				ppl.appendChild(document.createTextNode(name));
				rejectlist.appendChild(ppl);
			}
			
			LinkedList<String> waiting = appt.getWaitingList();
			for (String name : waiting){
				Element ppl = document.createElement("ppl");
				ppl.appendChild(document.createTextNode(name));
				waitinglist.appendChild(ppl);
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
			apptNode.appendChild(isJoint);
			apptNode.appendChild(ispublic);
			apptNode.appendChild(owner);
			apptNode.appendChild(attendlist);
			apptNode.appendChild(rejectlist);
			apptNode.appendChild(waitinglist);
			user.appendChild(apptNode);

			/* end of the convertion */

			// update the xml file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(file));
			
			xformer.transform(source, result);

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

	public void removeApptFromXml(String file, Appt appt,String userid) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		int removeID = appt.getID();
		try {

			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			// get the appts of specific user
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expression = xpath.compile("/Appts/user[@id='" + userid + "']");
			NodeList user = (NodeList) expression.evaluate(doc, XPathConstants.NODESET); // the user nodelist
			NodeList apptRootNode = user.item(0).getChildNodes(); // the appts nodelist of user
			int length = apptRootNode.getLength();

			for(int i = 0; i < length; i++) {
				Node innerAppt = apptRootNode.item(i); 	
				// the appt
				
				if(innerAppt.getNodeType() != Node.ELEMENT_NODE) 
					continue; 
				if(innerAppt.getNodeType() == Node.ELEMENT_NODE) {
					Document innerApptDoc = (Document) innerAppt;
					Node innerApptID =  innerApptDoc.getElementsByTagName("mApptID").item(0);
					String removeIDString = String.valueOf(removeID); // convert into string
					int innerApptIDInt = Integer.parseInt(innerApptID.getTextContent());
					// compare string
					//if( innerApptID.getTextContent().equals(removeID) ) { 
					if( innerApptIDInt == removeID ) { 
					//if( innerApptID.getTextContent().equals(removeIDString) ) { 
						Node temp = (Node) apptRootNode;
						temp.removeChild(innerAppt);
						System.out.println("innerApptIDInt == removeID");
						System.out.println("innerApptIDInt: " + innerApptIDInt + " removeID: " +removeID);
						break;
					} else {
						// != deltet id
					}
				}

			}

			// for saving
			Transformer xformer = TransformerFactory.newInstance().newTransformer();


			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			
			xformer.transform(source, result);

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

	private String getTextValue(Element element, String tagName) {
		String value = null;
		NodeList nl = element.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			Node elnode = el.getFirstChild();
			value = elnode.getNodeValue();
		}

		return value;
	}

	private int getIntValue(Element ele, String tagName) {
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	private long getLongValue(Element ele, String tagName) {
		return Long.parseLong(getTextValue(ele,tagName));
	}

	public void updateApptWithLocationName(String file, String fromLocation, String toLocation) {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		try {
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList users = doc.getElementsByTagName("user");
			
			Node firstNode = users.item(0);
			
			//if(firstNode.getNodeType() != Node.ELEMENT_NODE) {
				//continue;
			//}
			if(firstNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) firstNode;
				
				NodeList apptList = element.getElementsByTagName("Appt");
				
				for(int i = 0; i < apptList.getLength(); i++) {
					Node apptNode = apptList.item(i);
					if(apptNode.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					if(apptNode.getNodeType() == Node.ELEMENT_NODE) {
						
						Element elementAppt = (Element) apptNode;
						Node locationTag = elementAppt.getElementsByTagName("mLocation").item(0);
						String location = locationTag.getTextContent();
						if(location.equals(fromLocation) == true) {
							//set location
							locationTag.setTextContent(toLocation);
							//elementAppt.getElementsByTagName("mLocation").item(0).setTextContent(toLocation);
						}
					}
				}
			}
					
			for(int i = 1; i < users.getLength(); i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) userNode;
					NodeList apptList = element.getElementsByTagName("Appt");
					
					for(int j = 0; j < apptList.getLength(); j++) {
						Node apptNode = apptList.item(j);
						if(apptNode.getNodeType() != Node.ELEMENT_NODE) {
							continue;
						}
						if(apptNode.getNodeType() == Node.ELEMENT_NODE) {
							Element elementAppt = (Element) apptNode;
							Node locationTag = elementAppt.getElementsByTagName("mLocation").item(0);
							String location = locationTag.getTextContent();
							if(location.equals(fromLocation) == true) {
								locationTag.setTextContent(toLocation);
								//elementAppt.getElementsByTagName("mLocation").item(0).setTextContent(toLocation);
							}
						}
					}
				}
			}


			Transformer xformer = TransformerFactory.newInstance().newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			xformer.transform(source, result);

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

	public void deleteApptWithLocationName(User user, String destination) {
		String file = ApptStorage.apptFile;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String id = user.ID();
		try {
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			Node userRootNode = doc.getFirstChild();
			NodeList users = doc.getElementsByTagName("user");
			for(int i = 0; i < users.getLength(); i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) userNode;
					
					if(id.equals(element.getAttribute("id")) == true) {
						NodeList appts = element.getElementsByTagName("Appt");
						for(int j = 0; j < appts.getLength(); j++) {
							Node apptNode = appts.item(j);
							if(apptNode.getNodeType() != Node.ELEMENT_NODE) {
								continue;
							}
							if(apptNode.getNodeType() == Node.ELEMENT_NODE) {
								Element elementAppt = (Element) apptNode;

								Node locationTag = elementAppt.getElementsByTagName("mLocation").item(0);
								String location = locationTag.getTextContent();
								if(location.equals(destination) == true) {
									userNode.removeChild(apptNode);
								}
							}
						}
					}
				}
			}


			Transformer xformer = TransformerFactory.newInstance().newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			xformer.transform(source, result);

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
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			Node userRootNode = doc.getFirstChild();
			NodeList users = doc.getElementsByTagName("user");
			for(int i = 0; i < users.getLength(); i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) userNode;
					String userID = user.ID();
					String elementID = element.getAttribute("id");
					if(userID.equals(elementID) == true) {
						userRootNode.removeChild(userNode);
					}
				}
			}


			Transformer xformer = TransformerFactory.newInstance().newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			xformer.transform(source, result);

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