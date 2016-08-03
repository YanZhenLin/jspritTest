package jspritTest.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.vehicle.VehicleImpl;

//read from a property file to create vehicles
public class VehicleGenerator {

	private VehicleRoutingProblem.Builder builder;
	private String propertyFileName;
	private VehicleImpl[] vehicles;
	
	private String outFileName;
	private int noDepots;
	private int noVehicles;
	private double capacity;
	private double startTime;
	private double endTime;
	private int noWithSkills;
	private boolean hasBreak;
	private double breakWindowStart;
	private double breakWindowEnd;
	private double breakDuration;
	
	//constructor takes in a vehicleProblemBuilder and  
	public VehicleGenerator(VehicleRoutingProblem.Builder builder, String propertyFileName){
		this.builder = builder;
		this.propertyFileName = propertyFileName; //read form the vehicleConfig directory
	}
	
	public void generate(){
		Properties prop = new Properties();
		InputStream input = null;
	
		try {
			input = new FileInputStream(propertyFileName);
			prop.load(input);
			
			outFileName = prop.getProperty("fileName");
			noDepots = Integer.parseInt(prop.getProperty("noDepots"));
			noVehicles = Integer.parseInt(prop.getProperty("noVehicles"));
			capacity = Double.parseDouble(prop.getProperty("capacity"));
			startTime = Double.parseDouble(prop.getProperty("startTime"));
			endTime = Double.parseDouble(prop.getProperty("endTime"));
			noWithSkills = Integer.parseInt(prop.getProperty("noWithSkills"));
			hasBreak = Boolean.parseBoolean(prop.getProperty("hasBreak"));
			breakWindowStart = Double.parseDouble(prop.getProperty("breakWindowStart"));
			breakWindowEnd = Double.parseDouble(prop.getProperty("breakWindowEnd"));
			breakDuration = Double.parseDouble(prop.getProperty("breakDuration"));
			//the coordinates we generate randomly
			System.out.println(this);
			generateVehicles();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	//
	public void generateVehicles(){
	}

	@Override
	public String toString() {
		return "VehicleGenerator [propertyFileName=" + propertyFileName + ", outFileName=" + outFileName + ", noDepots="
				+ noDepots + ", noVehicles=" + noVehicles + ", capacity=" + capacity + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", noWithSkills=" + noWithSkills + ", hasBreak=" + hasBreak
				+ ", breakWindowStart=" + breakWindowStart + ", breakWindowEnd=" + breakWindowEnd + ", breakDuration="
				+ breakDuration + "]";
	}
	
}
