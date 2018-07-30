package simulation.classes;

import java.util.ArrayList;

public class SimulationBF {
	
	public static double timestep = Planet.ONE_DAY;
	
	public SimulationBF(){}
	
	public void checkForces(ArrayList<Planet> planets){
		
		for(Planet planet: planets){
			planet.resetForces();
			
			for(Planet anotherPlanet: planets){
				if(planet != anotherPlanet){
					planet.addNewForces(anotherPlanet);
				}
			}
			
			planet.setPrevPosX(planet.getPosX());
			planet.setPrevPosY(planet.getPosY());
			
		}
		
		for(Planet planet : planets){
			planet.updateVelAndPos(timestep);
		}
		
		
		
	}

}
