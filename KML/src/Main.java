import java.io.File;
import java.util.Date;

public class Main 
{
	public static void main(String[] args)
	{
		File kmz = new File("MOSMIX_S_LATEST_240.kmz");
		
		Forecast forecast = Reader.independentTake(kmz);
		if(forecast==null)
		{
			forecast = Reader.independentTake(new File("MOSMIX_S_LATEST_240.kml"));
		}
		
		System.out.println(forecast.positionRegister.length);
		
		Date von = forecast.firstAvailableDate();
		Date bis = forecast.lastAvailableDate();
		
		StationData station = forecast.getStation(51.0,8.5);
		double[] temp = forecast.temperatur(von, bis, station);
		Date[] times = forecast.zeitschritte(von, bis);
		
		System.out.println(station.name+": "+station.coordinate.toString());
		for(int i = 0; i < times.length; i++)
		{
			System.out.println(times[i].toString()+": "+temp[i]+"°C");
		}
		
	}
}
