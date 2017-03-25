package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class tempTest {
	
	
	WebDriver driver;
	String url = "https://www.yahoo.com/news/weather";
	
	@Test()
	public void base() throws IOException {

		//Get an IP Address
		
		URL whatismyip = new URL ("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		String  IP = in.readLine();
		System.out.println(IP);
		////////////////////////////////////////////////////////////////////////////////////
		
		//Get Location, latitude and longitude
		
		URL ip = new URL("http://www.geoplugin.net/json.gp?ip=" +IP);
		
		final String e_location_lat = "geoplugin_latitude";
		final String e_location_lon = "geoplugin_longitude";
		String locate_latitude = null;
		String locate_longitude = null;
		InputStream is = ip.openStream();
		JsonParser parser = Json.createParser(is);
		while (parser.hasNext()) {
			Event e = parser.next();
			if (e == Event.KEY_NAME) {
				switch (parser.getString()) {
				case e_location_lat:
					parser.next();
					locate_latitude = parser.getString();
					break;
				case e_location_lon:
					parser.next();
					locate_longitude = parser.getString();
					break;
				}
			}
		}
		System.out.println("latitude:" + locate_latitude);
		System.out.println("longitude:" + locate_longitude);
		//////////////////////////////////////////////////////////////////////////////////////
		
		//Get Local Temperature
		URL temp = new URL("http://api.wunderground.com/api/8a75c2aa5ba78758/conditions/q/" + locate_latitude + ","
				+ locate_longitude + ".json");
		
		final String locate = "city";
		final String locate2 = "state";
		final String url_icon = "icon_url";
		final String temperature = "temp_f";
		String location = null;
		String location2 = null;
		String site = null;
		Double temper = null;
		
		InputStream is2 = temp.openStream();
		JsonParser jp = Json.createParser(is2);
		is2 = temp.openStream();
		jp = Json.createParser(is2);

		while (jp.hasNext()) {
			Event e = jp.next();
			if (e == Event.KEY_NAME) {
				switch (jp.getString()) {

				case temperature:
					jp.next();
					temper = Double.parseDouble(jp.getString());
					break;
				case locate:
					jp.next();
					location = jp.getString();
					break;
				case locate2:
					jp.next();
					location2 = jp.getString();
					break;
				case url_icon:
					jp.next();
					site = jp.getString();
					break;

				}
			}
		}
		
		//Rounding and convert to string
		long grade = Math.round(temper);
		String gradeAsString = Long.toString(grade);
		

		System.out.println("temperature:" + grade);
		System.out.println("city:" + location);
		System.out.println("state:" + location2);
		System.out.println("url:" + site);
	
		////////////////////////////////////////////////////////////////////////////
		
		//Validate Web site 
		
		System.setProperty("webdriver.gecko.driver", "./src/resources/geckodriver");
		driver = new FirefoxDriver();
		driver.get(url);
		
		String local_temp = driver.findElement(By.xpath("//span[@class='Va(t)']")).getText();
	
			Assert.assertEquals(local_temp, gradeAsString);
	}
	
	@AfterClass()
	public void driver_quit() {
		driver.quit();
	}
}

