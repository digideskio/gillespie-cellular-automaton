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
 * Main class for RR1S (discrete time CA, asynchronous using a single array)
 */
public class RR1S extends CARS {

    Integer[] siteEventOrder;

    /**
     * Constructs an RR1S simulator
     * @param n       size of lattice
     * @param m       number of species
     * @param tau     length of timestep
     * @param draw    boolean - show visualisation?
     */
    public RR1S(int n,int m,double tau,boolean draw){
	super(n,m,tau,draw);
	siteEventOrder = new Integer[2*n*n]; // Integer array of site indices (as object so can use Collections.shuffle)
	for (int i=0;i < (siteEventOrder.length/2);i++) siteEventOrder[i] = i+1; 
	for (int i=(siteEventOrder.length/2);i < siteEventOrder.length;i++) siteEventOrder[i] = -(siteEventOrder.length - i); 
    }

    /**
     * Carries out single generation (all sites considered for birth & death). Implements Algorithm RR1S (mathematically equivalent).
     */
    public void doGeneration(){
	int species = -1;
	Collections.shuffle(Arrays.asList(siteEventOrder));
	for (int eventindex : siteEventOrder) {
	    int i = (Math.abs(eventindex)-1)/n; // zero-indexing correction
	    int j = (Math.abs(eventindex)-1)%n;
	    species = A[i][j];
	    if (species != empty) {
		if (eventindex < 0 && gen.nextDouble() <= pDeath[species]) death(species,A,i,j);
		if (eventindex > 0 && gen.nextDouble() <= pBirth[species]) birth(species,A,i,j);
	    }
	}
	time = time + tau;
	if (draw){plot(); pause(100);}
	show();
    }

    /**
     * Main method - arguments from command line: <GridSize> <maxTime> <timeStep> {<initPopnSize> <bRate> <dRate>}
     * @param args  command line e.g.: java RR1S 100 1000 0.5 1000 1.0 0.1 1000 0.5 0.05
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
	    RR1S rallr1 = new RR1S(n,m,tau,draw);
	    for (int i=1;i<=m;i++){
		int species = i-1;
		int pop  = Integer.parseInt(args[3*i]);
		rallr1.setBirthRate(species,Double.parseDouble(args[3*i+1]));
		rallr1.setDeathRate(species,Double.parseDouble(args[3*i+2]));
		for (int j=0;j<pop;j++) rallr1.add(species);
	    }	
	    rallr1.show();
	    while (rallr1.time < maxTime) rallr1.doGeneration();
	    reps--;
	}
	System.exit(0);
    }
}