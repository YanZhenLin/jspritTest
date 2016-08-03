package jspritTest;

import java.util.Arrays;
import java.util.Collection;

import jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import jsprit.analysis.toolbox.GraphStreamViewer;
import jsprit.analysis.toolbox.StopWatch;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.Jsprit;
import jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListeners.Priority;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.io.VrpXMLReader;
import jsprit.core.problem.job.Break;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.activity.TimeWindow;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;
import jspritTest.util.Plotter;

public class MultipleDepot_WithTW_WithSkills_WithBreaks {

	public static void main(String... args ){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrpXMLReader(vrpBuilder).read("input/solomon_c101_withTW_withSkills.xml");
        
        //we will manually setup the vehicles and depots
        int nuOfVehicles = 2;
        int capacity = 80;
        Coordinate firstDepotCoord = Coordinate.newInstance(20, 70);
        Coordinate secondDepotCoord = Coordinate.newInstance(65, 35);
        Coordinate thirdDepotCoord = Coordinate.newInstance(54, 87);
        
        int depotCounter = 1;
        //the third vehicle will service all the people that require spanish speaking agents 
        
        //for each depot
        for (Coordinate depotCoord : Arrays.asList(firstDepotCoord, secondDepotCoord)) {
            //add 4 vehicles to each depot
        	for (int i = 0; i < nuOfVehicles; i++) {
                VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
                		.addCapacityDimension(0, capacity)
                		.setCostPerDistance(1.0)
                		.setCostPerTransportTime(1.0)
                		.build();
                VehicleImpl vehicle = VehicleImpl.Builder.newInstance(depotCounter + "_" + (i + 1) + "_vehicle")
                		.setStartLocation(Location.newInstance(depotCoord.getX(), depotCoord.getY()))
                		.setType(vehicleType)
                		.setEarliestStart(0)
                		.setLatestArrival(540).build();
                vrpBuilder.addVehicle(vehicle);
            }
            depotCounter++;
        }
        
        //for the 3rd vehicle that can support spanish clients
        for (int i = 0; i < nuOfVehicles; i++) {
            VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
            		.addCapacityDimension(0, capacity)
            		.setCostPerDistance(1.0)
            		.setCostPerTransportTime(1.0)
            		.build();
            
            Break lunchBreak = Break.Builder.newInstance("lunchBreak")
                    .setTimeWindow(TimeWindow.newInstance(210, 300)).setServiceTime(100).build();
            
            VehicleImpl vehicle = VehicleImpl.Builder.newInstance(depotCounter + "_" + (i + 1) + "_vehicle")
            		.setStartLocation(Location.newInstance(thirdDepotCoord.getX(), thirdDepotCoord.getY()))
            		.setType(vehicleType)
            		.setBreak(lunchBreak)
            		.setEarliestStart(0)
            		.setLatestArrival(600)
            	
            		.addSkill("spanish").build();
            vrpBuilder.addVehicle(vehicle);
        }
        
        vrpBuilder.setFleetSize(FleetSize.FINITE);

		/*
         * build the problem
		 */
        VehicleRoutingProblem vrp = vrpBuilder.build();
        
        VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp).setProperty(Jsprit.Parameter.THREADS, "1").buildAlgorithm();
        vra.getAlgorithmListeners().addListener(new StopWatch(), Priority.HIGH);
        vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("output/progress.png"));
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
        new Plotter(vrp, Solutions.bestOf(solutions)).setLabel(Plotter.Label.ID).plot("output/p01_solution.png", "p01");
        
        //we cannot graph Stream viewer
        //new GraphStreamViewer(vrp, Solutions.bestOf(solutions)).setRenderDelay(50).display();
	}
	
}
