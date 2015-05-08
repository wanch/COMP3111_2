package hkust.cse.calendar.xmlfactory;

import hkust.cse.calendar.locationstorage.LocationStorage;
import hkust.cse.calendar.unit.Location;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

public class LocationXmlFactory {

	public void loadLocationXml(ArrayList<Location> mLocations) {
		File locationFile = new File(LocationStorage.locationFile);
		

		if(locationFile.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = builderFactory.newDocumentBuilder();
				
				Document document = builder.parse(locationFile);
				document.getDocumentElement().normalize();

				NodeList locations = document.getElementsByTagName("Location");
				
				for(int i = 0; i < locations.getLength(); i++) {
					Node node = locations.item(i);
					if(node.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					if(node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						Node nameNode = element.getElementsByTagName("Name").item(0);
						String name = nameNode.getTextContent();
						Node capNode = element.getElementsByTagName("Capacity").item(0);
						int capacity = Integer.parseInt(capNode.getTextContent());
						
						Location location = new Location(name, capacity);
						mLocations.add(location);
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
	
	public void saveLocationXml(Location location) {
		// TODO Auto-generated method stub

		File file = new File(LocationStorage.locationFile);
		String locationName = location.getLocationName();
		int locationCapacity = location.getCapacity();

		if(file.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {

				builder = builderFactory.newDocumentBuilder();
				Document document = builder.parse(LocationStorage.locationFile);


				Node locations = document.getFirstChild();		// <locations>
				
				Element newLocation = document.createElement("Location");
				Element newLocationName = document.createElement("Name");
				newLocationName.appendChild(document.createTextNode(locationName));
				
				Element newLocationCapacity = document.createElement("Capacity");
				String temp = String.valueOf(locationCapacity);
				newLocationCapacity.appendChild(document.createTextNode(temp));
				
				newLocation.appendChild(newLocationName);
				newLocation.appendChild(newLocationCapacity);

				locations.appendChild(newLocation);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(LocationStorage.locationFile));
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
				Element locations = document.createElement("Locations");
				document.appendChild(locations);

				Element newLocation = document.createElement("Location");
				locations.appendChild(newLocation);
					
				Element newlocationName = document.createElement("Name");
				Node newNode = document.createTextNode(locationName);
				newlocationName.appendChild(newNode);
				newLocation.appendChild(newlocationName);
				
				Element newlocationCapacity = document.createElement("Capacity");
				String capacityString = String.valueOf(locationCapacity);
				Node newCapacity = document.createTextNode(capacityString);
				newlocationCapacity.appendChild(newCapacity);
				newLocation.appendChild(newlocationCapacity);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(LocationStorage.locationFile));

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
	
	public void updateLocationXml(Location location, String locationName, int locationCapacity) {
		// TODO Auto-generated method stub
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {


			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(LocationStorage.locationFile);


			NodeList locations = doc.getElementsByTagName("Location");
			for(int i = 0; i < locations.getLength(); i++) {
				Node locationNode = locations.item(i);
				if(locationNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if(locationNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) locationNode;
					
					Node nameTag = element.getElementsByTagName("Name").item(0);
					Node capacityTag = element.getElementsByTagName("Capacity").item(0);
					
					String locationNameTag = nameTag.getTextContent();
					String locationCapacityString = String.valueOf(locationCapacity);
					
					if(locationName.equals(locationNameTag) == true) {
						nameTag.setTextContent(locationNameTag);
						capacityTag.setTextContent(locationCapacityString);
						break;
					}
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(LocationStorage.locationFile));
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
	
	public void addLocationToToBeDeleteList(Location location) {

		String locationName = location.getLocationName();

		File fileObject = new File(LocationStorage.deleteLocationFile);

		if(fileObject.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {


				builder = builderFactory.newDocumentBuilder();
				Document document = builder.parse(LocationStorage.deleteLocationFile);


				Node locations = document.getFirstChild();
				Element newLocation = document.createElement("Location");
				newLocation.appendChild(document.createTextNode(locationName));

				locations.appendChild(newLocation);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(LocationStorage.deleteLocationFile));
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
				Element locations = document.createElement("Locations");
				document.appendChild(locations);

				Element newLocation = document.createElement("Location");
				locations.appendChild(newLocation);
				newLocation.appendChild(document.createTextNode(locationName));

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(LocationStorage.deleteLocationFile));

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
	
	public Location[] getLocationInDeleteList() {
		String locationFile = LocationStorage.deleteLocationFile;
		File fileObject = new File(locationFile);
		ArrayList<Location> locations = new ArrayList<Location>();
		
		if(fileObject.isFile()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {

				builder = builderFactory.newDocumentBuilder();
				Document document = builder.parse(locationFile);
				document.getDocumentElement().normalize();

				NodeList list = document.getElementsByTagName("Location");
				
				for(int i = 0; i < list.getLength(); i++) {
					Node node = list.item(i);
					if(node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						String name = element.getTextContent();
						
						Location location = new Location(name, 0);
						locations.add(location);
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
		
		return locations.toArray(new Location[locations.size()]);
	}
	
	public void deleteLocationInToBeDelete(Location location) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String locationName = location.getLocationName();
		

		String file = LocationStorage.deleteLocationFile;

		try {

			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			Node locationRootNode = doc.getFirstChild();
			NodeList locations = doc.getElementsByTagName("Location");
			for(int i = 0; i < locations.getLength(); i++) {
				Node locationNode = locations.item(i);
				if(locationNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;	
				}
				if(locationNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) locationNode;
					String elementName = element.getTextContent();

					if(locationName.equals(elementName) == true) {
						locationRootNode.removeChild(locationNode);
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
	
public void removeLocationXml(Location location) {
		String locationName = location.getLocationName();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {


			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(LocationStorage.locationFile);


			Node locationRootNode = doc.getFirstChild();
			NodeList locations = doc.getElementsByTagName("Location");
			for(int i = 0; i < locations.getLength(); i++) {
				Node locationNode = locations.item(i);
				if(locationNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if(locationNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) locationNode;
					Node tempE = element.getElementsByTagName("name").item(0);
					String elementName = tempE.getTextContent();
					if (elementName.equals(locationName) == true){
					
						locationRootNode.removeChild(locationNode);
						break;
					}
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(LocationStorage.locationFile));
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
