package hkust.cse.calendar.xmlfactory;

import hkust.cse.calendar.system.UserFactory;
import hkust.cse.calendar.unit.Admin;
import hkust.cse.calendar.unit.RegularUser;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorage;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UserXmlFactory {

	public void loadUserFromXml(String file, HashMap<String, User> mUsers) {
		File userFile = new File(file);
		if(userFile.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = builderFactory.newDocumentBuilder();
				Document document = builder.parse(userFile);
				document.getDocumentElement().normalize();

				NodeList users = document.getElementsByTagName("User");

				for(int i = 0; i < users.getLength(); i++) {
					Node userNode = users.item(i);
					if(userNode.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) userNode;

						Node elementNode = element.getElementsByTagName("ID").item(0);
						String id = elementNode.getTextContent();
						elementNode = element.getElementsByTagName("Password").item(0);
						String password = elementNode.getTextContent();
						elementNode = element.getElementsByTagName("Email").item(0);
						String email = elementNode.getTextContent();
						elementNode = element.getElementsByTagName("Role").item(0);
						String role = elementNode.getTextContent();

						Element name = (Element) element.getElementsByTagName("Name").item(0);
						Node nameNode = name.getElementsByTagName("FirstName").item(0);
						String firstName = nameNode.getTextContent();
						nameNode = name.getElementsByTagName("LastName").item(0);
						String lastName = nameNode.getTextContent();

						/*Element birthday = (Element) element.getElementsByTagName("Birthday").item(0);
						String yearString = birthday.getElementsByTagName("Year").item(0).getTextContent();
						String monthString = birthday.getElementsByTagName("Month").item(0).getTextContent();
						String dateString = birthday.getElementsByTagName("Date").item(0).getTextContent();
						int year = Integer.parseInt(yearString);
						int month = Integer.parseInt(monthString);
						int date = Integer.parseInt(dateString);*/

						User user = UserFactory.getInstance().createAccount(id, password, role, firstName, lastName, email);
						user.setEmail(email);
						user.setName(firstName, lastName);
						/*int[] bday = {year, month, date};
						Timestamp start = TimeSpan.CreateTimeStamp(bday, 0);
						Timestamp end = TimeSpan.CreateTimeStamp(bday, 23 * 60 + 59);
						user.setBirthday(new TimeSpan(start, end));*/

						mUsers.put(user.ID(), user);

					}
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void updateUserInXml(String file, User user) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		String id = user.ID();
		String pw = user.Password();
		String email = user.getEmail();
		String firstname = user.getFirstName();
		String lastname = user.getLastName();
		
		try {

			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList users = doc.getElementsByTagName("User");
			for(int i = 0; i < users.getLength(); i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) userNode;
					Node temp = element.getElementsByTagName("ID").item(0);
					String tempString = temp.getTextContent();
					if(tempString.equals(id) == true) {
						Node tempNode = element.getElementsByTagName("Password").item(0);
						tempNode.setTextContent(pw);
						tempNode = element.getElementsByTagName("Email").item(0);
						tempNode.setTextContent(email);
						
						Node name = element.getElementsByTagName("Name").item(0);
						((Element) name).getElementsByTagName("FirstName").item(0).setTextContent(firstname);
						((Element) name).getElementsByTagName("LastName").item(0).setTextContent(lastname);
						break;
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

	public void saveUserToXml(String file, User user) {
		// TODO Auto-generated method stub
		String id = user.ID();
		String pw = user.Password();
		String role = user.getRole();
		String firstname = user.getFirstName();
		String lastname = user.getLastName();
		String email = user.getEmail();
		
		File fileObject = new File(file);
		if(fileObject.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = builderFactory.newDocumentBuilder();
				Document document = builder.parse(file);

				Node users = document.getFirstChild();
				Element newUser = document.createElement("User");

				Element userId = document.createElement("ID");
				userId.appendChild(document.createTextNode(id));
				newUser.appendChild(userId);

				Element password = document.createElement("Password");
				password.appendChild(document.createTextNode(pw));
				newUser.appendChild(password);

				Element userRole = document.createElement("Role");
				userRole.appendChild(document.createTextNode(role));
				newUser.appendChild(userRole);

				Element name = document.createElement("Name");
				Element firstName = document.createElement("FirstName");
				firstName.appendChild(document.createTextNode(firstname));
				name.appendChild(firstName);
				Element lastName = document.createElement("LastName");
				lastName.appendChild(document.createTextNode(lastname));
				name.appendChild(lastName);
				newUser.appendChild(name);
				
				Element userEmail = document.createElement("Email");
				userEmail.appendChild(document.createTextNode(email));
				newUser.appendChild(userEmail);

				/*Element birthday = document.createElement("Birthday");
				Element year = document.createElement("Year");
				year.appendChild(document.createTextNode(new Integer(user.getBirthday().StartTime().getYear() + 1900).toString()));
				birthday.appendChild(year);
				Element month = document.createElement("Month");
				month.appendChild(document.createTextNode(new Integer(user.getBirthday().StartTime().getMonth() + 1).toString()));
				birthday.appendChild(month);
				Element date = document.createElement("Date");
				date.appendChild(document.createTextNode(new Integer(user.getBirthday().StartTime().getDate()).toString()));
				birthday.appendChild(date);
				newUser.appendChild(birthday);*/

				users.appendChild(newUser);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
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
		else {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {

				builder = builderFactory.newDocumentBuilder();

				Document document = builder.newDocument();
				Element users = document.createElement("Users");
				document.appendChild(users);

				Element newUser = document.createElement("User");
				users.appendChild(newUser);

				Element userId = document.createElement("ID");
				userId.appendChild(document.createTextNode(id));

				Element password = document.createElement("Password");
				password.appendChild(document.createTextNode(pw));

				Element userRole = document.createElement("Role");
				userRole.appendChild(document.createTextNode(role));
				

				Element name = document.createElement("Name");
				
				Element firstName = document.createElement("FirstName");
				firstName.appendChild(document.createTextNode(firstname));
				name.appendChild(firstName);
				Element lastName = document.createElement("LastName");
				lastName.appendChild(document.createTextNode(lastname));
				name.appendChild(lastName);
				
				
				Element userEmail = document.createElement("Email");
				userEmail.appendChild(document.createTextNode(email));
				newUser.appendChild(userEmail);

				/*Element birthday = document.createElement("Birthday");
				Element year = document.createElement("Year");
				year.appendChild(document.createTextNode(new Integer(user.getBirthday().StartTime().getYear() + 1900).toString()));
				birthday.appendChild(year);
				Element month = document.createElement("Month");
				month.appendChild(document.createTextNode(new Integer(user.getBirthday().StartTime().getMonth() + 1).toString()));
				birthday.appendChild(month);
				Element date = document.createElement("Date");
				date.appendChild(document.createTextNode(new Integer(user.getBirthday().StartTime().getDate()).toString()));
				birthday.appendChild(date);
				//newUser.appendChild(birthday);*/
				
				newUser.appendChild(userId);
				newUser.appendChild(password);
				newUser.appendChild(userRole);
				newUser.appendChild(name);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(file));

				transformer.transform(source, result);
			} catch (ParserConfigurationException e) {
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

	public void removeUserFromXml(String file, User user) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String id = user.ID();
		try {

			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			Node userRootNode = doc.getFirstChild();
			NodeList users = doc.getElementsByTagName("User");
			int usersLength = users.getLength();
			
			for(int i = 0; i < usersLength; i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) userNode;
					Node elementID = element.getElementsByTagName("ID").item(0);
					String IDString = elementID.getTextContent();
					if(IDString.equals(id) == true) {
						userRootNode.removeChild(userNode);
						break;
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

	public void addUserToToBeDeletedListXml(String file, User user) {
		File fileObject = new File(file);
		String id = user.ID();
		if(fileObject.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {

				builder = builderFactory.newDocumentBuilder();
				Document doc = builder.parse(file);

				Node users = doc.getFirstChild();
				Element newUser = doc.createElement("User");
				newUser.appendChild(doc.createTextNode(id));

				users.appendChild(newUser);

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
		else {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {

				builder = builderFactory.newDocumentBuilder();

				Document doc = builder.newDocument();
				Element users = doc.createElement("Users");
				doc.appendChild(users);

				Element newUser = doc.createElement("User");
				users.appendChild(newUser);
				newUser.appendChild(doc.createTextNode(id));

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(file));

				transformer.transform(source, result);
			} catch (ParserConfigurationException e) {
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

	public void loadUserFromToBeDeletedListXml(String file, ArrayList<String> ToBeDeleted) {
		File userDataFile = new File(file);
		ArrayList<String> temp = ToBeDeleted;
		if(userDataFile.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {

				builder = builderFactory.newDocumentBuilder();
				Document doc = builder.parse(userDataFile);
				doc.getDocumentElement().normalize();

				NodeList users = doc.getElementsByTagName("User");
				for(int i = 0; i < users.getLength(); i++) {
					Node node = users.item(i);
					Element eUser = (Element) node;
					String userId = eUser.getTextContent();	
					temp.add(userId);
				}
				
				ToBeDeleted = temp;
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void removeUserFromToBeDeletedListXml(String file, User user) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String id = user.ID();
		try {

			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			Node userRootNode = doc.getFirstChild();
			NodeList users = doc.getElementsByTagName("User");
			int usersLength = users.getLength();
			for(int i = 0; i < usersLength; i++) {
				Node userNode = users.item(i);
				if(userNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if(userNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) userNode;
					String temp = element.getTextContent();
					if(temp.equals(id) == true) {
						userRootNode.removeChild(userNode);
						break;
					}
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.transform(source, result);

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
