import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;


public class Reader 
{
	public static Forecast independentTake(File file)
	{
		File kmlFile;
		String name = file.getName();
		String kmz ="kmz";
		String kml ="kml";
		String type;
		
		Pattern pattern = Pattern.compile("\\w*\\.(\\w*)");
		Matcher matcher = pattern.matcher(name);
		matcher.find();
		
		type = matcher.group(1);
		
		if(type.equals(kmz)&&file.exists())
		{
			kmlFile = extract(file);
			return take(kmlFile);
		}
		else if(type.equals(kml)&&file.exists())
		{
			return take(file);
		}
		return null;
	}
	
	
	public static Forecast take(File kmlFile)
	{
		
		
		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;
		factory = DocumentBuilderFactory.newInstance();
		try 
		{
			builder = factory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = null;
		try 
		{
			doc = builder.parse(kmlFile);
		} 
		catch (SAXException | IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Forecast forecast = new Forecast();
		
		forecast.times = getTimes(doc);
		
		getData(doc,forecast);
		
		return forecast;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void getData(Document d, Forecast f)
	{
		NodeList stationtable = d.getElementsByTagName("kml:Placemark");
		
		Element[] GerStaArray = reduceStationtableByLocation(stationtable,58.0,44.0,19.0,2.0);
		Element station;
		Node cordNode;
		Coordinate coordinate;
		StationData stationData;
		LinkedList<Coordinate> cordList = new LinkedList<Coordinate>();
		int dupIndex;
		
		for(int i=0; i<GerStaArray.length; i++)
		{
			station = GerStaArray[i];
			
			cordNode = station.getElementsByTagName("kml:coordinates").item(0);
			coordinate = Coordinate.kmlTextToCord(cordNode.getTextContent());
			
			dupIndex = cordList.indexOf(coordinate);

			if(dupIndex==-1)
			{
				cordList.add(coordinate);
				stationData = getStationData(station);
				f.cordConect.put(coordinate, stationData);
			}
			else if((!coordinate.trueEquals(cordList.get(dupIndex)))&&(coordinate.h<cordList.get(dupIndex).h))
			{
				cordList.remove(dupIndex);
				cordList.add(dupIndex,coordinate);
				stationData = getStationData(station);
				f.cordConect.put(coordinate, stationData);
			}	
		}
		
		Coordinate[] cordArray = cordList.toArray(new Coordinate[cordList.size()]);
		f.positionRegister = cordArray;
	}
	
	
	
	
	private static StationData getStationData(Element stationElement) 
	{
		StationData stationData = new StationData(elementName(stationElement));
		stationData.coordinate = Coordinate.kmlTextToCord(stationElement.getElementsByTagName("kml:coordinates").item(0).getTextContent());
		
		Element parameterRoot = (Element) stationElement.getElementsByTagName("kml:ExtendedData").item(0);
		NodeList parameterList = parameterRoot.getElementsByTagName("dwd:Forecast");
		Element parameterElement;
		Node parameterValNode;
		
		
		for(int i=0; i<parameterList.getLength(); i++)
		{
			parameterElement = (Element) parameterList.item(i);
			String att = parameterElement.getAttribute("dwd:elementName");
			
			if(att.equals("FF"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String windSpeedString = parameterValNode.getTextContent();
				double[] windSpeedArray = StationData.stringToDoubArray(windSpeedString);
				
				stationData.windSpeedDataErrorClass = StationData.DoubArrayLinearComplete(windSpeedArray);
				stationData.windSpeedData = windSpeedArray;
			}
			else if(att.equals("FX1"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String maxWindSpeedString = parameterValNode.getTextContent();
				double[] maxWindSpeedArray = StationData.stringToDoubArray(maxWindSpeedString);
				
				stationData.maxWindSpeedDataErrorClass = StationData.DoubArrayLinearComplete(maxWindSpeedArray);
				stationData.maxWindSpeedData = maxWindSpeedArray;
			}
			else if(att.equals("Rad1h"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String sunIntensityString = parameterValNode.getTextContent();
				double[] sunIntensityArray = StationData.stringToDoubArray(sunIntensityString);
				
				stationData.sunIntensityDataErrorClass = StationData.DoubArrayLinearComplete(sunIntensityArray);
				stationData.sunIntensityData = sunIntensityArray;
			}
			else if(att.equals("SunD1"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String sunDurationString = parameterValNode.getTextContent();
				double[] sunDurationArray = StationData.stringToDoubArray(sunDurationString);
				
				stationData.sunDurationDataErrorClass = StationData.DoubArrayLinearComplete(sunDurationArray);
				for(int x=0;x<sunDurationArray.length;x++)
				{
					sunDurationArray[x]=sunDurationArray[x]/60;
				}
				stationData.sunDurationData = sunDurationArray;
			}
			else if(att.equals("TTT"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String temperatureString = parameterValNode.getTextContent();
				double[] temperatureArray = StationData.stringToDoubArray(temperatureString);
				
				stationData.temperatureDataErrorClass = StationData.DoubArrayLinearComplete(temperatureArray);
				for(int x=0;x<temperatureArray.length;x++)
				{
					temperatureArray[x]=Math.floor((temperatureArray[x]-273.15)*1000)/1000;
				}
				stationData.temperatureData = temperatureArray;
			}
			else if(att.equals("Td"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String tauPunktString = parameterValNode.getTextContent();
				double[] tauPunktArray = StationData.stringToDoubArray(tauPunktString);
				
				stationData.tauPunktDataErrorClass = StationData.DoubArrayLinearComplete(tauPunktArray);
				for(int x=0;x<tauPunktArray.length;x++)
				{
					tauPunktArray[x]=Math.floor((tauPunktArray[x]-273.15)*1000)/1000;
				}
				stationData.tauPunktData = tauPunktArray;
			}
			else if(att.equals("PPPP"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String luftdruckString = parameterValNode.getTextContent();
				double[] luftdruckArray = StationData.stringToDoubArray(luftdruckString);
				
				stationData.luftdruckDataErrorClass = StationData.DoubArrayLinearComplete(luftdruckArray);
				stationData.luftdruckData = luftdruckArray;
			}
			else if(att.equals("RR1c"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String niederschlagString = parameterValNode.getTextContent();
				double[] niederschlagArray = StationData.stringToDoubArray(niederschlagString);
				
				stationData.niederschlagDataErrorClass = StationData.DoubArrayLinearComplete(niederschlagArray);
				stationData.niederschlagData = niederschlagArray;
			}
			else if(att.equals("RRS1c"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String schneeregenNiederschlagString = parameterValNode.getTextContent();
				double[] schneeregenNiederschlagArray = StationData.stringToDoubArray(schneeregenNiederschlagString);
				
				stationData.schneeregenNiederschlagDataErrorClass = StationData.DoubArrayLinearComplete(schneeregenNiederschlagArray);
				stationData.schneeregenNiederschlagData = schneeregenNiederschlagArray;
			}
			else if(att.equals("VV"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String visibilityString = parameterValNode.getTextContent();
				double[] visibilityArray = StationData.stringToDoubArray(visibilityString);
				
				stationData.visibilityDataErrorClass = StationData.DoubArrayLinearComplete(visibilityArray);
				stationData.visibilityData = visibilityArray;
			}
			else if(att.equals("wwM"))
			{
				parameterValNode = parameterElement.getElementsByTagName("dwd:value").item(0);
				String foggProbabilityString = parameterValNode.getTextContent();
				double[] foggProbabilityArray = StationData.stringToDoubArray(foggProbabilityString);
				
				stationData.foggProbabilityDataErrorClass = StationData.DoubArrayLinearComplete(foggProbabilityArray);
				stationData.foggProbabilityData = foggProbabilityArray;
			}
		}
		//
		return stationData;
	}
	
	
	
	
	
	private static Element[] reduceStationtableByLocation(NodeList s, double latMax, double latMin, double lonMax, double lonMin)
	{
		Element sta;
		boolean valid;
		LinkedList<Element> list = new LinkedList<Element>();
		Element[] nodeArray = null;
		
		for(int i = 0; i<s.getLength(); i++)
		{
			sta = (Element) s.item(i);
			NodeList cnl = sta.getElementsByTagName("kml:coordinates");
			Node cordNode = cnl.item(0);
			String cordText = cordNode.getTextContent();
			Coordinate cord = Coordinate.kmlTextToCord(cordText);
			valid = cord.insideField(new Coordinate(latMax,lonMax), new Coordinate(latMin,lonMin));
			if(valid)
			{
				list.add(sta);
			}
		}

		nodeArray = (Element[]) list.toArray(new Element[list.size()]);
		return nodeArray;
	}
	
	private static Date[] getTimes(Document d)
	{
		Date[] dates = new Date[240];
		Pattern pattern = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})[.]*");
		Matcher matcher;
		
		Node timetable = d.getElementsByTagName("dwd:ForecastTimeSteps").item(0);
		NodeList timelist = timetable.getChildNodes();
		int datecount = 0;

		for(int i=0; i<timelist.getLength(); i++)
		{
			Node time = timelist.item(i);
			matcher = pattern.matcher(time.getTextContent());
			
			if(matcher.find())
			{
				int year = Integer.parseInt(matcher.group(1))-1900;
				int month = Integer.parseInt(matcher.group(2))-1;
				int day = Integer.parseInt(matcher.group(3));
				int hour = Integer.parseInt(matcher.group(4));
				int min = Integer.parseInt(matcher.group(5));
				int sec = Integer.parseInt(matcher.group(6));
			
			Date date = new Date(year,month,day,hour,min,sec);
			dates[datecount] = date;
			datecount++;
			}
		}
		
		if(datecount<240)
		{
			return null;
		}
		
		return dates;
	}
	
	static private String elementName(Element e)
	{
		Node eNameN = e.getElementsByTagName("kml:description").item(0);
		return eNameN.getTextContent();
	}
	
	static public File extract(File file) 
	{
		File here = new File("");
		File out = null;

		String s = here.getAbsolutePath();
		
 		File dummy = new File("dummy.zip");
		file.renameTo(dummy);
		try {
			out = unZip(dummy, s);
			Thread.sleep(3000);
			dummy.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out;
	}
	
	private static File unZip(File zipFile, String outputFolder) throws Exception {

	    byte[] buffer = new byte[1024];
	    File nameChange = new File("MOSMIX_S_LATEST_240.kml");
	    if(nameChange.exists())
	    {
	    	nameChange.delete();
	    }

	    File folder = new File(outputFolder);
	    if (!folder.exists()) {
	        folder.mkdir();
	    }

	    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    ZipEntry ze = zis.getNextEntry();

	    while (ze != null) {

	        String fileName = ze.getName();

	        File newFile = new File(outputFolder + File.separator + fileName);

	        //System.out.println("file unzip : " + newFile.getAbsoluteFile());

	        new File(newFile.getParent()).mkdirs();

	        if (ze.isDirectory())
	        {
	            newFile.mkdir();
	            ze = zis.getNextEntry();
	            continue;
	        }

	        FileOutputStream fos = new FileOutputStream(newFile);

	        int len;
	        while ((len = zis.read(buffer)) > 0) {
	            fos.write(buffer, 0, len);
	        }

	        fos.close();
	        ze = zis.getNextEntry();
	        newFile.renameTo(nameChange);
	    }

	    zis.closeEntry();
	    zis.close();
	    return nameChange;
	}
}


