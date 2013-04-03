/*
*        gillespie-cellular-automaton
*        A program for simulating birth-death processes for multiple species in discrete space. It contains code for the
*        a continuous time (spatial Gillespie) simulator and several discrete time (cellular automaton) simulators.
*
*        Copyright (C) 2013 Patrick Prosser <Patrick.Prosser@glasgow.ac.uk>, Rebecca Mancy <Rebecca.Mancy@glasgow.ac.uk>
*
*        This program is free software: you can redistribute it and/or modify
*        it under the terms of the GNU General Public License as published by
*        the Free Software Foundation, either version 3 of the License, or
*        (at your option) any later version.
*
*        This program is distributed in the hope that it will be useful,
*        but WITHOUT ANY WARRANTY; without even the implied warranty of
*        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*        GNU General Public License for more details.
*
*        You should have received a copy of the GNU General Public License
*        along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.util.*;
import java.io.*;
import java.awt.*;

/**
 * Main class for RFd2M (discrete time CA, synchronous delayed updating using two arrays, multiple births)
 */
public class RFd2M extends CARM {

    Integer[] siteOrder = new Integer[n*n]; // Integer array of site indices (as object so can use Collections.shuffle)

    /**
     * Constructs an RFd2M simulator
     * @param n       size of lattice
     * @param m       number of species
     * @param tau     length of timestep
     * @param draw    boolean - show visualisation?
     */
    public RFd2M(int n,int m,double tau,boolean draw){
	super(n,m,tau,draw);
    }

    /**
     * Conducts births into Y using information on X  (allows multiple births)
     * @param X
     * @param Y
     */
    void doBirths(int[][]X,int[][]Y){
		int species = -1;
		// Shuffle array of indices so we can go through grid in random order
		Collections.shuffle(Arrays.asList(siteOrder));
		// Update each site
		for (int siteindex : siteOrder) {
			int i = siteindex/n;
			int j = siteindex%n;
			species = X[i][j];
			if (species != empty){
			int numberOfBirths = numberOfBirths(birthRate[species]);
			for (int k=0;k<numberOfBirths;k++) birth(species,Y,i,j);
		    }
		}
    }

    /**
     * Carries out single generation (all sites considered for birth & death). Implements Algorithm RFd2M (mathematically equivalent).
     * [This method is actually the same as doGeneration in RFd2S, but calls a different version of doBirths]
     */
    public void doGeneration(){
		//fill arrays for ordering of events (local to the class)
		for (int i=0; i < siteOrder.length; i++) {
			siteOrder[i] = i;
		}

		// Death-birth ordering, newborns may not survive to reproduce
		copy(A,B);		
		doDeaths(A,B);
		doBirths(B,B);
		copy(B,A); 

		time = time + tau;
		if (draw){plot(); pause(100);}
		show();
    }

    /**
     * Main method - arguments from command line: <GridSize> <maxTime> <timeStep> {<initPopnSize> <bRate> <dRate>}
     * @param args  command line e.g.: java RFd2M 100 1000 0.5 1000 1.0 0.1 1000 0.5 0.05
     */
    public static void main(String[] args) {

	int n        = Integer.parseInt(args[0]);          // size of grid
	int maxTime  = Integer.parseInt(args[1]);          // number of iterations
	double tau   = Double.parseDouble(args[2]);        // the click of the clock
	int m        = (args.length/3) - 1;                // n species
	boolean draw = args[args.length-1].equals("draw"); // do we draw?

	int reps = 1;
	if (args[args.length-1].equals("ten")) reps = 10;
	if (args[args.length-1].equals("hundred")) reps = 100;

	while (reps > 0){
	    RFd2M rdb2m = new RFd2M(n,m,tau,draw);
	    for (int i=1;i<=m;i++){
		int species = i-1;
		int pop  = Integer.parseInt(args[3*i]);
		rdb2m.setBirthRate(species,Double.parseDouble(args[3*i+1]));
		rdb2m.setDeathRate(species,Double.parseDouble(args[3*i+2]));
		for (int j=0;j<pop;j++) rdb2m.add(species);
	    }	
	    rdb2m.show();
	    while (rdb2m.time < maxTime) rdb2m.doGeneration();
	    reps--;
	}
	System.exit(0);
    }
}
