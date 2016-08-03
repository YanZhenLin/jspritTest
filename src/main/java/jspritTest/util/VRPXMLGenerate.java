package jspritTest.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public class VRPXMLGenerate {

	public static void main(String... args) throws TransformerException, IOException{
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("input/propertiesConfig/20Services.properties");
			prop.load(input);
			
			VRPXMLGenerator generator = new VRPXMLGenerator();
			generator.setFileName( prop.getProperty("fileName") ); //expects string
			generator.setMaxTimeWindow( Double.parseDouble(prop.getProperty("maxTimeWindow")) ); //double
			generator.setMinTimeWindow( Double.parseDouble(prop.getProperty("minTimeWindow")) ); //double
			generator.setMaxXCoordinate( Double.parseDouble(prop.getProperty("maxXCoordinate")) ); //double
			generator.setMaxYCoordinate( Double.parseDouble(prop.getProperty("maxYCoordinate")) ); //double
			generator.setNoServices(Integer.parseInt( prop.getProperty("noServices")) ); //int
			generator.setServiceDuration( Double.parseDouble(prop.getProperty("serviceDuration"))); //double
			generator.addSkill(prop.getProperty("requiredSkills")); // we only have one skill at the time, but need to account for multiple skills
			System.out.println(generator);
			generator.generateXML();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}finally{
			if(input != null)
				input.close();
		}
	}
	
}
