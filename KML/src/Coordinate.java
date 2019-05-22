import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinate implements Comparable<Object>
{
	double lat;
	double lon;
	double h;
	
	public Coordinate(double lat1, double lon1)
	{
		lat=lat1;
		lon=lon1;
		h=Double.NaN;
	}
	
	public Coordinate(double lat1, double lon1, double H)
	{
		lat=lat1;
		lon=lon1;
		h=H;
	}
	
	public double distance(Coordinate c) 
	{
		double lat1=this.lat;
		double lon1=this.lon;
		double lat2=c.lat;
		double lon2=c.lon;
		
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 111.18957696;
			
			return dist;
		}
	}
	
	
	public boolean insideField(Coordinate c1, Coordinate c2)
	{
		if(this.lat>(Math.max(c1.lat, c2.lat)+0.002)||this.lon>(Math.max(c1.lon, c2.lon)+0.002)||this.lat<(Math.min(c1.lat, c2.lat)-0.002)||this.lon<(Math.min(c1.lon, c2.lon)-0.002))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	static public Coordinate kmlTextToCord(String s)
	{
		double h1;
		double lat1;
		double lon1;
		Pattern pattern = Pattern.compile("^([-\\d.]*),([-\\d.]*),([-\\d.]*)$");
		Matcher matcher = pattern.matcher(s);
		if(matcher.find())
		{
			lat1 = Double.parseDouble(matcher.group(2));
			lon1 = Double.parseDouble(matcher.group(1));
			
			if(matcher.groupCount()>2)
			{
				h1 = Double.parseDouble(matcher.group(3));
				return new Coordinate(lat1,lon1,h1);
			}
			else
			{
				return new Coordinate(lat1,lon1);
			}
		}
		else
		{
			return null;
		}
	}
	
	public boolean trueEquals(Object c1)
	{
		Coordinate c = (Coordinate) c1;
		double ydif = Math.abs(this.lat-c.lat);
		double xdif = Math.abs(this.lon-c.lon);
		if((xdif < 0.002) && (ydif < 0.002) && (this.h==c.h))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean equals(Object c1)
	{
		Coordinate c = (Coordinate) c1;
		double ydif = Math.abs(this.lat-c.lat);
		double xdif = Math.abs(this.lon-c.lon);
		if((xdif < 0.002) && (ydif < 0.002))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return (int) (this.lat*100000 + this.lon*10);
	}
	
	@Override
	public int compareTo(Object c1)
	{
		Coordinate c = (Coordinate) c1;
		if(this.lat>c.lat || ((this.lat==c.lat)&&(this.lon>c.lon)) || ((this.lat==c.lat)&&(this.lon==c.lon)&&(this.h>c.h)))
		{
			return 1;
		}
		else if((this.lat == c.lat)&&(this.lon == c.lon)&&(this.h == c.h))
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	
	public String toString()
	{
		return "Lat:"+this.lat+", Lon:"+this.lon;
	}

}
