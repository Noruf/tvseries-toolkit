package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
//import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.w3c.dom.NodeList;

import org.w3c.dom.Node;

import models.TvSeries;
import models.Link;;

public class ImportExportManager {
	File currentDirectory;
	public ImportExportManager() {
		currentDirectory = new File("");
	}

	public void ExportData(List<TvSeries> series) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			// root element
			Element rootElement = doc.createElement("tvseries");
			doc.appendChild(rootElement);

			for (int i = 0; i < series.size(); i++) {
				// series
				Element singleSeries = doc.createElement("series");
				rootElement.appendChild(singleSeries);

				Element friendlyName = doc.createElement("FriendlyName");
				friendlyName.appendChild(doc.createTextNode(series.get(i).FriendlyName));
				singleSeries.appendChild(friendlyName);

				Element name = doc.createElement("Name");
				name.appendChild(doc.createTextNode(series.get(i).Name));
				singleSeries.appendChild(name);

				Element seasons = doc.createElement("Seasons");
				int[] s = series.get(i).Seasons;
				for (int j = 0; j < s.length; j++) {

					Element seasonElement = doc.createElement("season");
					seasonElement.appendChild(doc.createTextNode(Integer.toString(s[j])));

					seasons.appendChild(seasonElement);
				}

				singleSeries.appendChild(seasons);

				Element currentEpisode = doc.createElement("CurrentEpisode");
				currentEpisode.appendChild(doc.createTextNode(Integer.toString(series.get(i).CurrentEpisode)));
				singleSeries.appendChild(currentEpisode);

				Element imgPath = doc.createElement("ImgPath");
				imgPath.appendChild(doc.createTextNode(series.get(i).ImgPath));
				singleSeries.appendChild(imgPath);

				Element musicPath = doc.createElement("MusicPath");
				musicPath.appendChild(doc.createTextNode(series.get(i).MusicPath));
				singleSeries.appendChild(musicPath);

				Element links = doc.createElement("Links");
				List<Link> linkslist = series.get(i).Links;
				for (int j = 0; j < linkslist.size(); j++) {
					Element link = doc.createElement("Link");
					for(int item:linkslist.get(j).Seasons) {
						Element season = doc.createElement("season");
						season.appendChild(doc.createTextNode(Integer.toString(item)));
						link.appendChild(season);
					}
					Element address = doc.createElement("address");
					address.appendChild(doc.createTextNode(linkslist.get(j).Address));
					link.appendChild(address);
					Element n = doc.createElement("name");
					n.appendChild(doc.createTextNode(linkslist.get(j).Name));
					link.appendChild(n);
					Element type = doc.createElement("type");
					type.appendChild(doc.createTextNode(Integer.toString(linkslist.get(j).Type)));
					link.appendChild(type);
					links.appendChild(link);
				}
				singleSeries.appendChild(links);

			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(currentDirectory.getAbsolutePath()+"/data/tvseries.xml"));
			transformer.transform(source, result);

			// Output to console for testing
			StreamResult consoleResult = new StreamResult(System.out);
			transformer.transform(source, consoleResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	public List<TvSeries> ImportData() {
		List<TvSeries> seriesList = new ArrayList<TvSeries>();
		try {
			File inputFile = new File(currentDirectory.getAbsolutePath()+"/data/tvseries.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("series");
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String FriendlyName = eElement.getElementsByTagName("FriendlyName").item(0).getTextContent();
					String Name = eElement.getElementsByTagName("Name").item(0).getTextContent();

					Element seas = (Element) eElement.getElementsByTagName("Seasons").item(0);
					NodeList s = seas.getElementsByTagName("season");
					int[] seasons = new int[s.getLength()];
					for (int i = 0; i < s.getLength(); i++) {
						seasons[i] = Integer.valueOf(s.item(i).getTextContent());
					}
					int CurrentEpisode = Integer
							.valueOf(eElement.getElementsByTagName("CurrentEpisode").item(0).getTextContent());
					String ImgPath = eElement.getElementsByTagName("ImgPath").item(0).getTextContent();
					String MusicPath = eElement.getElementsByTagName("MusicPath").item(0).getTextContent();
					NodeList linksTemp = eElement.getElementsByTagName("Links");
					Element eLinks = (Element) linksTemp.item(0);
					NodeList l = eLinks.getElementsByTagName("Link");
					List<Link> links = new ArrayList<Link>();
					for (int i = 0; i < l.getLength(); i++) {
						Element singleLink = (Element) l.item(i);
						//int season = Integer.valueOf(singleLink.getElementsByTagName("season").item(0).getTextContent());
						NodeList linkS = singleLink.getElementsByTagName("season");
						int[] linkSeasons = new int[linkS.getLength()];
						for (int j = 0; j < linkS.getLength(); j++) {
							linkSeasons[j] = Integer.valueOf(linkS.item(j).getTextContent());
						}
						
						String address = singleLink.getElementsByTagName("address").item(0).getTextContent();
						String n = singleLink.getElementsByTagName("name").item(0).getTextContent();
						int type = Integer.valueOf(singleLink.getElementsByTagName("type").item(0).getTextContent());
						links.add(new Link(linkSeasons, address, n, type));
					}
					TvSeries newSeries = new TvSeries(FriendlyName, Name, seasons, CurrentEpisode, ImgPath, MusicPath,
							links);
					seriesList.add(newSeries);
					System.out.println(newSeries.FriendlyName + " " + newSeries.Name + " " + newSeries.Seasons[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			return seriesList;
		}
	}
	
	public List<Link> ImportSearchEngines() {
		List<Link> searchEngines = new ArrayList<Link>();
		try {
			File inputFile = new File(currentDirectory.getAbsolutePath()+"/data/search.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("search");
			System.out.println("----------------------------");

			for (int index = 0; index < nList.getLength(); index++) {
				Node nNode = nList.item(index);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String address = eElement.getElementsByTagName("Address").item(0).getTextContent();
					String name = eElement.getElementsByTagName("Name").item(0).getTextContent();

					Link search = new Link(new int[]{-1},address,name,Link.URL);
					searchEngines.add(search);
					System.out.println(search.Address + " " + search.Name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchEngines;
	}

	public Map<String, Object> ImportSettings() {
		Map<String, Object> settings = new HashMap<String, Object>();
		try {
			File inputFile = new File(currentDirectory.getAbsolutePath()+"/data/settings.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("settings");
			System.out.println("----------------------------");

			for (int index = 0; index < nList.getLength(); index++) {
				Node nNode = nList.item(index);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					NodeList list = eElement.getChildNodes();
					for(int i = 0; i < list.getLength(); i++) {
						Node node = list.item(i);
						String name = node.getNodeName();
						String value = node.getTextContent();
						settings.put(name, value);
						System.out.println(name + ":" + value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return settings;
	}
	
	public void ExportData(Map<String, Object> settings) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			// root element
			Element rootElement = doc.createElement("settings");
			doc.appendChild(rootElement);

			for (Map.Entry<String, Object> entry : settings.entrySet()) {
			    String key = entry.getKey();
			    String value = entry.getValue().toString();
			    
				Element setting = doc.createElement(key);
				rootElement.appendChild(setting);
				setting.appendChild(doc.createTextNode(value));

			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(currentDirectory.getAbsolutePath()+"/data/settings.xml"));
			transformer.transform(source, result);

			// Output to console for testing
			StreamResult consoleResult = new StreamResult(System.out);
			transformer.transform(source, consoleResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String CopyFile(String sourcePath, String name) {
		if(sourcePath.isEmpty()) {return sourcePath;}
		boolean isURL = sourcePath.startsWith("http://") || sourcePath.startsWith("https://");
		name = filenameValidator(name) + "." +  FilenameUtils.getExtension(sourcePath);
		if(isURL)return copyURL(sourcePath,name);
		File source = new File(sourcePath);
		File dest = new File("data/" + name);
		try {
			FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
			name="";
		}
		return name;
	}
	private String copyURL(String sourcePath, String name) {
		
		File dest = new File("data/" + name);
		try {
			URL source = new URL(sourcePath);
			FileUtils.copyURLToFile(source, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			name="";
		}
		return name;
	}
	private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

	private String filenameValidator(String filename) {
		for(char item: ILLEGAL_CHARACTERS) {
			filename = filename.replace(item,'-');
		}
		return filename;
	}
	
	
	public BufferedImage loadImage(String imgPath){
		BufferedImage myPicture = null;
		
		File currentDirectory = new File("");
		try {
			myPicture = ImageIO.read(new File(currentDirectory.getAbsolutePath() + "/data/" + imgPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myPicture;
	}
	
	

}
