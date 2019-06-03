# DWD-KML-Reader

Java-Projekt zum einlesen von KML-Dateien des DWD-MOSMIX-Programms: 
https://opendata.dwd.de/weather/local_forecasts/mos/MOSMIX_S/all_stations/kml/. 

Mit: "Reader.take()" lässt sich nun ein Forecast-Objekt mit den aktuellsten Daten erstellen.
Dabei werden keine Daten auf der Harddrive abgelegt, es wird aber eine Internetverbindung vorrausgesetzt.

An dem wiederum lässt sich die Funktion:

"StationData station = forecast.getStation(double lat,double lon)" 

aufrufen, die die Vorhersage-Daten der Nächstgelegenen Mess-Station liefert. Diese können in mehreren Funktion
verwendet werden um einen Double-Array mit dem gewünschten Attribut in dem gewünschten Zeitraum zu erschaffen 
(fals in dem 10-Tag-Datensatz vorhanden): 

"double[] temp = forecast.temperatur(Date von, Date bis, station)". 

Die Aufteilung der Anfrage in zwei Funktionen ist notwendig, da die Suche nach der nächstgelegenen Station relativ
rechenaufwendig ist und die entsprechende Referenz auf diese Weise weiterverwendet werden kann.

Ein Beispiel für die Verwendung des Readers findet sich in der "main"-Klasse.

vorerst keine weiteren Änderungen geplant. 
