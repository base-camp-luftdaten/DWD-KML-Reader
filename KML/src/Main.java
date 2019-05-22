import java.io.File;
import java.util.Date;

public class Main 
{
	public static void main(String[] args)
	{
		File file = new File("MOSMIX_S_2019052016_240.kml");
		Forecast forecast = Reader.take(file);
		
		Date von = new Date(119, 4, 23, 6, 11);
		Date bis = new Date(119, 4, 27, 13, 48);
		
		StationData station = forecast.getStation(51.0,9.0);
		double[] temp = forecast.temperatur(von, bis, station);
		Date[] times = forecast.zeitschritte(von, bis);
		
		System.out.println(station.name+": "+station.coordinate.toString());
		for(int i = 0; i < times.length; i++)
		{
			System.out.println(times[i].toString()+": "+temp[i]+"°C");
		}
		
	}
}
