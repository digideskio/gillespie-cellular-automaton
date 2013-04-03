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
 * Main class for Gillespie simulator, Gill
 */
public class Gill extends CA {

    RandomSet[] S;             // one for each species
    double[] totalBirthRate;
    double[] totalDeathRate;
    double tau;                // output in increments of tau
    double deltaT;             // is the time past the previous timestep that Gill is currently at

    /**
     * Constructs a Gill simulator
     * @param n   lattice size
     * @param m   number of species
     * @param tau  timestep (for frequency of output)
     * @param draw  boolean - show visualisation?
     */
    public Gill(int n,int m,double tau,boolean draw){
	super(n,m,draw);
	this.tau       = tau;
	S              = new RandomSet[m];
	totalBirthRate = new double[m];
	totalDeathRate = new double[m];
	deltaT         = 0.0;      // at start, we're 0.0 past the previous timestep
	for (int i=0;i<m;i++) S[i] = new RandomSet(n*n);
    }

    /**
     * Adds a new member of given species to the population
     * @param species  identifier of species
     * @throws RandomSetException
     */
    void add(int species) throws RandomSetException {
	int x = freeSpace.delete();
	int i = x/n;
	int j = x%n;
	A[i][j] = species;
	S[species].add(x);
	population[species]++;
	totalPopulation++;
	if (draw) plot(i,j,color[species]);
    }

    /**
     * Executes a death of specified species, decrementing population of that species and total population
     * @param species  identifier of species
     * @throws RandomSetException
     */
    void death(int species) throws RandomSetException {
	int x = S[species].delete();
	int i = x/n;
	int j = x%n;
	if (trace){
	    System.out.println("death at ("+ i +","+ j +") ");
	    if (draw){plot(i,j,Color.CYAN); pause(500); unPlot(i,j); pause(500);}
	}
	A[i][j] = empty;
	population[species]--;
	totalPopulation--;
    }

    /**
     * Executes a birth of specified species, incrementing population of that species and total population
     * @param species   identifier of species
     * @throws RandomSetException
     */
    void birth(int species) throws RandomSetException {
	int x = S[species].select();
	int i = x/n;
	int j = x%n;
	int point = chooseRandomNeighbour(i,j);
	if (trace){
	    System.out.print("birth from ("+ i +","+ j +") ");
	    System.out.println("onto ("+ point/n +","+ point%n +")");
	    if (draw){plot(point/n,point%n,Color.RED); pause(500); plot(i,j,Color.YELLOW); pause(500);}
	}
	if (A[point/n][point%n] == empty){
	    A[point/n][point%n] = species;
	    S[species].add(point);
	    population[species]++;
	    totalPopulation++;
	}
    }

    static double genTau(double lambda){return -Math.log(1 - Math.random()) / lambda;}

    /**
     * Carries out a single event (birth or death)
     */
    void doGeneration(){
	double lambda = 0.0;
    // Calculate total rates per species and over all species (lambda)
	for (int species=0;species<m;species++){
	    totalBirthRate[species] = birthRate[species] * population[species];
	    totalDeathRate[species] = deathRate[species] * population[species];
	    lambda  = lambda + totalBirthRate[species] + totalDeathRate[species];
	}
    // normalise rates to give probabilities
	for (int species=0;species<m;species++){
	    pBirth[species] = totalBirthRate[species]/lambda;
	    pDeath[species] = totalDeathRate[species]/lambda;
	}
	deltaT     = deltaT + genTau(lambda);  // move time on (Gillespie algorithm) (now further past prev timestep output)
	double p   = Math.random();            // select an event proportionally to rate (Gillespie algorithm)
	double lwb = 0.0;
	for (int species=0;species<m;species++){
	    if (p >= lwb && p < pBirth[species]+lwb){birth(species); break;}
	    lwb = lwb + pBirth[species];
	    if (p >= lwb && p < pDeath[species]+lwb){death(species); break;}
	    lwb = lwb + pDeath[species];
	}
    // if newtime larger than output timestep Output intermediate states of the system until next timestep and until maxTime
	while ( (deltaT >= tau && maxTime >= deltaT) ) {
	    time = time + tau;      // move on the actual time gca.time (see CA class)
	    deltaT = deltaT - tau;  // now less far past previous timestep
	    if (draw){plot(); pause(100);}    // update simulation and output
	    show();
	}
	// if we're not getting any more events, just show() from now until maxTime
	if (time + deltaT > maxTime) {		
		while ( (time < maxTime) ) {
		    time = time + tau;
		    if (draw){plot(); pause(100);}    // update simulation and output
		    show();
		}	
	}
    }

    /**
     * Main method - arguments from command line: <GridSize> <maxTime> <timeStep> {<initPopnSize> <bRate> <dRate>}
     * @param args  command line e.g.: java Gill 100 1000 0.5 1000 1.0 0.1 1000 0.5 0.05
     */
    public static void main(String[] args) {
	int n         = Integer.parseInt(args[0]);           // size of grid
	int maxTime   = Integer.parseInt(args[1]);           // number of iterations
	double tau    = Double.parseDouble(args[2]);         // the click of the clock
	int m         = (args.length/3) - 1;                 // n species
	boolean draw  = args[args.length-1].equals("draw");  // do we draw?
	boolean trace = args[args.length-1].equals("trace"); // do we trace and draw?

	int reps = 1;
	if (args[args.length-1].equals("ten")) reps = 10;
	if (args[args.length-1].equals("hundred")) reps = 100;

	while (reps > 0){
	    Gill gca = new Gill(n,m,tau,draw);
	    for (int i=1;i<=m;i++){
		int species = i-1;
		int popn    = Integer.parseInt(args[3*i]);
		gca.setBirthRate(species,Double.parseDouble(args[3*i+1]));
		gca.setDeathRate(species,Double.parseDouble(args[3*i+2]));
		for (int j=0;j<popn;j++) gca.add(species);
	    }
	    
	    gca.setMaxTime(maxTime);
	    gca.setTrace(trace);
	    gca.show();
	    while (gca.time < maxTime) gca.doGeneration();      // keep doing a generation until maxTime
	    reps--;
	}
	System.exit(0);
    }
}
