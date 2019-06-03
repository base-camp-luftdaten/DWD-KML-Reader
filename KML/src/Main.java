import java.io.File;
import java.util.Date;

public class Main 
{
	public static void main(String[] args)
	{

		Forecast forecast = Reader.take();
		
		System.out.println("Stationen online: "+forecast.positionRegister.length);
		
		Date von = forecast.firstAvailableDate();
		Date bis = forecast.lastAvailableDate();
		
		//Date(int year -1900, int month -1, int date, int hrs, int min)
		Date example = new Date(119, 5, 3, 12, 0); // 03.06.2019, 12:00 
		
		double lat = 53.67;
		double lon = 9.96;
		
		//Aus effizienzgruenden aufgeteilt, um mehre Funktionsaufrufe mit dem selben
		//StationData-Objekt zu ermoeglichen, ohne das dies neu gesucht werden muss.
		StationData station = forecast.getStation(lat,lon);
		double[] temp = forecast.temperatur(von, bis, station);
		//Die zugehoerigen Zeitpunkte zu den Vorhersagen
		Date[] times = forecast.zeitschritte(von, bis);
		
		//Konsolen Ausgabe
		System.out.println(station.name+": "+station.coordinate.toString()+
				", Distanz: "+station.coordinate.distance(new Coordinate(lat,lon)));
		for(int i = 0; i < times.length; i++)
		{
			System.out.println(times[i].toString()+": "+temp[i]+"°C");
		}
		
	}
}
