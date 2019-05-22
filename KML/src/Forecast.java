import java.util.Date;
import java.util.TreeMap;

public class Forecast 
{
	Coordinate[] positionRegister;
	TreeMap<Coordinate,StationData> cordConect;
	public Date[] times = new Date[240];
	
	public Forecast()
	{
		cordConect = new TreeMap<Coordinate,StationData>();
	}
	
	public Coordinate getNearest(Coordinate c)
	{
		double minD=Double.MAX_VALUE;
		double dist;
		Coordinate minC = null;
		for(int i=0;i<positionRegister.length;i++)
		{
			dist=c.distance(positionRegister[i]);
			if(minD>dist)
			{
				minC=positionRegister[i];
				minD = dist;
			}
		}
		return minC;
	}
	
	public StationData getStation(double lat, double lon)
	{
		Coordinate c = getNearest(new Coordinate(lat,lon));
		return cordConect.get(c);
	}
	
	static private Date roundDate(Date d)
	{
		Date e = (Date) d.clone();
		if(!((d.getMinutes()<29)||((d.getMinutes()==29)&&(d.getSeconds()<=59))))
		{
			e.setHours(d.getHours()+1);
		}
		e.setMinutes(0);
		e.setSeconds(0);
		return e;
	}
	
	private int dateIndex(Date e)
	{
		Date d=roundDate(e);
		long l=((d.getTime()-times[0].getTime())/1000)/3600;
		int i = (int) l;
		return i;
	}
	
	public Date firstAvailableDate()
	{
		return times[0];
	}
	
	public Date lastAvailableDate()
	{
		return times[239];
	}
	
	private double[] dateDouble(Date start, Date end, double[] s)
	{		
		int as = 0;
		int ae = 239;
		
		if(start==null&&end==null)
		{
			return s;
		}
		else if(start==null)
		{
			as = 0;
			ae = dateIndex(end);
		}
		else if(end==null)
		{
			as = dateIndex(start);
			ae = 239;
		}
		else
		{
			as = dateIndex(start);
			ae = dateIndex(end);
		}
		System.out.println(ae-as+1);
		double[] d = new double[ae-as+1];
		
		for(int i=as;i<=ae;i++)
		{
			d[i-as]=s[i];
		}
		return d;
	}
	
	public double[] windgeschwindigkeit(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.windSpeedData);
	}
	
	public double[] maxWindgeschwindigkeit(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.maxWindSpeedData);
	}
	
	public double[] sonnenEinstrahlung(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.sunIntensityData);
	}
	
	public double[] sonnenDauer(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.sunDurationData);
	}
	
	public double[] temperatur(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.temperatureData);
	}
	
	public double[] taupunkt(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.tauPunktData);
	}
	
	public double[] luftdruck(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.luftdruckData);
	}
	
	public double[] niederschlag(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.niederschlagData);
	}
	
	public double[] schneeregenNiederschlag(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.schneeregenNiederschlagData);
	}
	
	public double[] sichtweite(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.visibilityData);
	}
	
	public double[] nebelWahrscheinlichkeit(Date start, Date end, StationData s)
	{		
		return dateDouble(start, end, s.foggProbabilityData);
	}
	
	public Date[] zeitschritte(Date start, Date end)
	{		
		int as = 0;
		int ae = 239;
		
		if(start==null&&end==null)
		{
			return times.clone();
		}
		else if(start==null)
		{
			as = 0;
			ae = dateIndex(end);
		}
		else if(end==null)
		{
			as = dateIndex(start);
			ae = 239;
		}
		else
		{
			as = dateIndex(start);
			ae = dateIndex(end);
		}
		
		Date[] d = new Date[ae-as+1];
		
		for(int i=as;i<=ae;i++)
		{
			d[i-as]=times[i];
		}
		return d;
	}
}