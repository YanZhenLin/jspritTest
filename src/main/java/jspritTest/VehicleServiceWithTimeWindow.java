package jspritTest;

import java.util.Arrays;
import java.util.Collection;

import jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import jsprit.analysis.toolbox.GraphStreamViewer;

import jsprit.analysis.toolbox.StopWatch;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.VehicleRoutingAlgorithmBuilder;
import jsprit.core.algorithm.box.Jsprit;
import jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListeners.Priority;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.io.VrpXMLReader;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.util.Coordinate;
import jsprit.core.util.EuclideanDistanceCalculator;
import jsprit.core.util.Solutions;
import jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import jspritTest.util.Plotter;


//The key difference here is using a different xml input file
public class VehicleServiceWithTimeWindow {
	
	public static void main(String... args ){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrpXMLReader(vrpBuilder).read("input/solomon_c101_withTW_Dispersal1.xml");
        
        //we will manually setup the vehicles and depots
        int nuOfVehicles = 1;
        int capacity = 80;
        Coordinate firstDepotCoord = Coordinate.newInstance(20, 70);
        Coordinate secondDepotCoord = Coordinate.newInstance(65, 35);
        
        int depotCounter = 1;
        //for each depot
        for (Coordinate depotCoord : Arrays.asList(firstDepotCoord, secondDepotCoord)) {
            //add 4 vehicles to each depot
        	for (int i = 0; i < nuOfVehicles; i++) {
                VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type").addCapacityDimension(0, capacity).setCostPerDistance(1.0).setCostPerTransportTime(1.0).build();
                VehicleImpl vehicle = VehicleImpl.Builder.newInstance(depotCounter + "_" + (i + 1) + "_vehicle").setStartLocation(Location.newInstance(depotCoord.getX(), depotCoord.getY())).setType(vehicleType).build();
                vrpBuilder.addVehicle(vehicle);
            }
            depotCounter++;
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

        //new GraphStreamViewer(vrp, Solutions.bestOf(solutions)).setRenderDelay(50).display();
	}
	
	//simple method used to create the cost matrix using Euclidean deistance, we can easily change the euclidean cost matrix however to be of a travel cost instead
	private static VehicleRoutingTransportCostsMatrix createMatrix(VehicleRoutingProblem.Builder vrpBuilder) {
        VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
        for (String from : vrpBuilder.getLocationMap().keySet()) {
            for (String to : vrpBuilder.getLocationMap().keySet()) {
                Coordinate fromCoord = vrpBuilder.getLocationMap().get(from);
                Coordinate toCoord = vrpBuilder.getLocationMap().get(to);
                
                //instead of using a EuclideanDistance calculator we need to use a map distance from some where else to create this 
                double distance = EuclideanDistanceCalculator.calculateDistance(fromCoord, toCoord);
                
                matrixBuilder.addTransportDistance(from, to, distance);
                matrixBuilder.addTransportTime(from, to, (distance / 2.));
            }
        }
        return matrixBuilder.build();
    }
}