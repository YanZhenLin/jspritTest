package jspritTest.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.job.Break;
import jsprit.core.problem.solution.route.activity.TimeWindow;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.util.Coordinate;

//read from a property file to create vehicles
public class VehicleGenerator {
	private VehicleRoutingProblem.Builder vrpBuilder;
	private String propertyFileName;
	
	private String outFileName;
	private int noDepots;
	private int noVehicles;
	private int capacity;
	private double startTime;
	private double endTime;
	private int noWithSkills;
	private boolean hasBreak;
	private double breakWindowStart;
	private double breakWindowEnd;
	private double breakDuration;
	
	//constructor takes in a vehicleProblemBuilder and  
	public VehicleGenerator(VehicleRoutingProblem.Builder vrpBuilder, String propertyFileName){
		this.vrpBuilder = vrpBuilder;
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
			capacity = Integer.parseInt(prop.getProperty("capacity"));
			startTime = Double.parseDouble(prop.getProperty("startTime"));
			endTime = Double.parseDouble(prop.getProperty("endTime"));
			noWithSkills = Integer.parseInt(prop.getProperty("noWithSkills"));
			hasBreak = Boolean.parseBoolean(prop.getProperty("hasBreak"));
			breakWindowStart = Double.parseDouble(prop.getProperty("breakWindowStart"));
			breakWindowEnd = Double.parseDouble(prop.getProperty("breakWindowEnd"));
			breakDuration = Double.parseDouble(prop.getProperty("breakDuration"));
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
	
	public void generateVehicles(){
		//first generate the coordinates base on depots
		Coordinate[] coordinates = new Coordinate[noDepots];
		int depotCounter = 1;
		for(Coordinate coord: coordinates){
			double x_coord = VRPXMLGenerator.getRandom(0, 100); 
			double y_coord = VRPXMLGenerator.getRandom(0, 100);
			coord = Coordinate.newInstance(x_coord, y_coord);
			
			for (int i = 0; i < noVehicles; i++) {
                VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
                		.addCapacityDimension(0, capacity)
                		.setCostPerDistance(1.0)
                		.setCostPerTransportTime(1.0)
                		.build();
                VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(depotCounter + "_" + (i + 1) + "_vehicle")
                		.setStartLocation(Location.newInstance(coord.getX(), coord.getY()))
                		.setType(vehicleType)
                		.setEarliestStart(startTime)
                		.setLatestArrival(endTime);
                if(noWithSkills > 0){
                	vehicleBuilder.addSkill("spanish");
                	noWithSkills--;
                }
                if(hasBreak){
                	System.out.println("this vehicle has a break");
                	vehicleBuilder.setBreak(getBreak(depotCounter+"_"+i));
                	System.out.println("finished setting break");
                }
                VehicleImpl vehicle = vehicleBuilder.build(); 
                vrpBuilder.addVehicle(vehicle);
            }
			depotCounter++;
		}
	}
	
	private Break getBreak(String label){
		//double breakStart = VRPXMLGenerator.getRandomStart(breakWindowStart, breakWindowEnd);
		Break lunchBreak = Break.Builder.newInstance("lunchBreak_"+label)
				.setTimeWindow(TimeWindow.newInstance(breakWindowStart, breakWindowEnd )).setServiceTime(breakDuration)
				.build();
		return lunchBreak;
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
