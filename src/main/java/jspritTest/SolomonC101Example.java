package jspritTest;

import java.util.Collection;

import jsprit.analysis.toolbox.Plotter;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.VehicleRoutingAlgorithmBuilder;
import jsprit.core.algorithm.box.Jsprit;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.io.VrpXMLReader;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.util.Coordinate;
import jsprit.core.util.EuclideanDistanceCalculator;
import jsprit.core.util.Solutions;
import jsprit.core.util.VehicleRoutingTransportCostsMatrix;

public class SolomonC101Example {

	
	public static void main(String... args ){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrpXMLReader(vrpBuilder).read("input/solomon_c101.xml");
        VehicleRoutingTransportCostsMatrix costMatrix = createMatrix(vrpBuilder);
        vrpBuilder.setRoutingCost(costMatrix);
        VehicleRoutingProblem vrp = vrpBuilder.build();
        VehicleRoutingAlgorithmBuilder vraBuilder = new VehicleRoutingAlgorithmBuilder(vrp, "input/algorithmConfig_solomon.xml");
        VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp)
//            .setProperty(Jsprit.Parameter.THREADS,"3")
//            .setProperty(Jsprit.Parameter.FAST_REGRET,"true")
            .buildAlgorithm();
//            VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/algorithmConfig.xml");
        vra.setMaxIterations(250);
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
        
        SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);

        new Plotter(vrp, Solutions.bestOf(solutions)).plot("output/plot", "plot");
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
}
