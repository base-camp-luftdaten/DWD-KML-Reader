# DWD-KML-Reader

Java-Projekt zum einlesen von KML-Dateien des DWD-MOSMIX-Programms: 
https://opendata.dwd.de/weather/local_forecasts/mos/MOSMIX_S/all_stations/kml/. 

Mit: "Reader.take()" lässt sich nun ein Forecast-Objekt mit den aktuellsten Daten erstellen.
Dabei werden keine Daten auf der Harddrive abgelegt, es wird aber eine Internetverbindung vorrausgesetzt.

An dem Forecast-Objekt lässt sich die Funktion:

"StationData station = forecast.getStation(double lat,double lon)" 

aufrufen, die die Vorhersage-Daten der Nächstgelegenen Mess-Station liefert. Diese können in mehreren Funktion
verwendet werden um einen Double-Array mit dem gewünschten Attribut in dem gewünschten Zeitraum zu erschaffen 
(fals in dem 10-Tag-Datensatz vorhanden): 

"double[] temp = forecast.temperatur(Date von, Date bis, station)". 

Die Aufteilung der Anfrage in zwei Funktionen ist notwendig, da die Suche nach der nächstgelegenen Station relativ
rechenaufwendig ist und die entsprechende Referenz auf diese Weise weiterverwendet werden kann.

Ein Beispiel für die Verwendung des Readers findet sich in der "main"-Klasse. (Main-File nicht teil der Anwendung)

edit (07.09.2019 - 03:00):
Nach Überprüfung stellte sich heraus, dass ca. 4% der vom Wetterdienst erhaltenen Daten stark fehlerhaft oder nicht 
vorhanden sind. Dieses Problem sollte nun behoben sein, ohne dass dafür fehlerfreie Daten verloren gehen. Mit der
Funktion: "doIContainErrors()" der Forecast-Klasse kann überprüft werden, ob die vorhandenen Daten trotzdem noch 
Lücken (NaN-Werte) enthalten. Sollte das der Fall sein, ist dies Indikator für einen Bug und sollte mir am besten 
mitgeteilt werden, damit ich den Bug beheben kann.
