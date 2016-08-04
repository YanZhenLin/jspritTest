package jspritTest;

import java.util.Collection;

import jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import jsprit.analysis.toolbox.GraphStreamViewer;
import jsprit.analysis.toolbox.StopWatch;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.Jsprit;
import jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListeners.Priority;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.io.VrpXMLReader;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.util.Solutions;
import jspritTest.util.Plotter;
import jspritTest.util.VehicleGenerator;

public class FourDepot1000Services {

	public static void main(String... args ){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		String serviceFileName = "1000Services";
		//this file needs to be generated by VRPXMLGenerator first
        new VrpXMLReader(vrpBuilder).read("input/autoGeneratedServiceXML/"+serviceFileName+".xml");
        
        VehicleGenerator vehicleGenerator = new VehicleGenerator(vrpBuilder, "input/vehicleConfig/4Depot.properties");
        vehicleGenerator.generate();
        vrpBuilder.setFleetSize(FleetSize.FINITE); //finite vehicles
        VehicleRoutingProblem vrp = vrpBuilder.build();
        VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp).setProperty(Jsprit.Parameter.THREADS, "4").buildAlgorithm();
        vra.getAlgorithmListeners().addListener(new StopWatch(), Priority.HIGH);
        vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("output/autoGenerated/"+serviceFileName+"progress.png"));
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
        new Plotter(vrp, Solutions.bestOf(solutions)).setLabel(Plotter.Label.ID).plot("output/autoGenerated/"+serviceFileName+"_solution.png", serviceFileName);
        new GraphStreamViewer(vrp, Solutions.bestOf(solutions)).setRenderDelay(50).display();
	}
}
