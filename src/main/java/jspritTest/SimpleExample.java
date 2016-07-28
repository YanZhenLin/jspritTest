package jspritTest;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import jsprit.analysis.toolbox.Plotter;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.VehicleRoutingAlgorithmBuilder;
import jsprit.core.algorithm.box.Jsprit;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.activity.TimeWindow;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.util.Coordinate;
import jsprit.core.util.EuclideanDistanceCalculator;
import jsprit.core.util.Solutions;
import jsprit.core.util.VehicleRoutingTransportCostsMatrix;

public class SimpleExample {

	private static final int xMinBoundary = 0;
	private static final int yMinBoundary = 0;
	private static final int xMaxBoundary = 100;
	private static final int yMaxBoundary = 100;
	
	private static final int timeMin = 0;  //units of 15 minutes each, 0 denotes midnight
	private static final int timeMax = 95; //units of 15 minutes each, 95 denotes 11:45pm
	
	private static Random generator;
	
	static{
		generator = new Random();
	}
	
	private static double randomX(){
		return xMinBoundary + ( generator.nextInt((xMaxBoundary-xMinBoundary)) );
	}
	
	private static double randomY(){
		return yMinBoundary + ( generator.nextInt((yMaxBoundary-yMinBoundary)) );
	}
	
	//the returned window has to be 2 units more than the min window parameter 
	private static int randomWindow(int minWindow, int maxWindow) throws IllegalArgumentException{
		
		if(maxWindow < (minWindow+2) ){ //if less than two
			throw new IllegalArgumentException("The maximum window needs to be bigger");
		}
		
		int ret = minWindow + (generator.nextInt((maxWindow-minWindow)) );
		while(ret < minWindow+2 ){
			ret = minWindow + (generator.nextInt((maxWindow-minWindow)) );
		}
		return ret;
	}
	
	//used to create the cost matrix
	private static VehicleRoutingTransportCostsMatrix createMatrix(VehicleRoutingProblem.Builder vrpBuilder) {
        VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
        for (String from : vrpBuilder.getLocationMap().keySet()) {
            for (String to : vrpBuilder.getLocationMap().keySet()) {
                Coordinate fromCoord = vrpBuilder.getLocationMap().get(from);
                Coordinate toCoord = vrpBuilder.getLocationMap().get(to);
                double distance = EuclideanDistanceCalculator.calculateDistance(fromCoord, toCoord);
                matrixBuilder.addTransportDistance(from, to, distance);
                matrixBuilder.addTransportTime(from, to, (distance / 2.));
            }
        }
        return matrixBuilder.build();
    }
	
	public static void main(String... args){
		//create 2 vehicles starting from 2 separate random locations
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		
		//we don't need the map for this simple example
		//Map<String, Vehicle> vehicleMap = new LinkedHashMap<String, Vehicle>();
		//Map<String, Service> serviceMap = new LinkedHashMap<String, Service>();
		
		//create a random sample of 20 services
		String srv_str = "service_";
		for(int i = 0; i < 20; i++ ){
			String serviceId = srv_str + Integer.toString(i);
			int windowStart = randomWindow(32, 74); //we set the maxthreshold to 74, because after 76 is 7pm, no service is performed after 7pm
			int windowEnd = randomWindow(windowStart, 76); 
			
			//create services with time windows 
			Service service = Service.Builder.newInstance(serviceId).setName(serviceId)
					.setLocation(Location.newInstance( randomX(), randomY() ))
					.setTimeWindow(TimeWindow.newInstance(windowStart, windowEnd)) //set a window period for availability
					.setServiceTime(1) //set the duration to one unit (meaning 15 minutes)
					.build();
			
			//serviceMap.put(serviceId, service);
			vrpBuilder.addJob(service);
		}
		
		VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance("vehicleType")
				.build();
		String vhc_str = "vehicle_";
		for(int i = 0; i < 2; i++){
			String vehicleId = vhc_str + Integer.toString(i);
			VehicleImpl vehicle = VehicleImpl.Builder.newInstance(vehicleId)
					.setType(vehicleType)
					.setEarliestStart(32)
					.setLatestArrival(74)
					.setStartLocation( Location.newInstance( randomX(), randomY() ) )
					.setReturnToDepot(true)
					.build();
			//vehicleMap.put(vehicleId, vehicle);
			vrpBuilder.addVehicle(vehicle);
		}
		
		vrpBuilder.setFleetSize(FleetSize.FINITE);
		VehicleRoutingProblem problem = vrpBuilder.build();
		
		VehicleRoutingAlgorithmBuilder vraBuilder = new VehicleRoutingAlgorithmBuilder(problem, "input/algorithmConfig_solomon.xml");
        VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(problem)
            .buildAlgorithm();

        vra.setMaxIterations(250);
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
        
        SolutionPrinter.print(problem, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);

        new Plotter(problem, Solutions.bestOf(solutions)).plot("output/plot", "plot");
		
	}
}
