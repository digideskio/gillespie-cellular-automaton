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
 * Class CARS extends CA to use rates (R) on entry, converting to give a single (S) birth event (for RFd2S & RR1S)
 */
public abstract class CARS extends CA {

    double tau;           // the unit or measure of time

    /**
     * Constructs a rates-based, single-event CA
     * @param n      size of lattice
     * @param m      number of species
     * @param tau    size of timestep
     * @param draw   boolean - show visualisation?
     */
    public CARS(int n,int m,double tau,boolean draw){
	super(n,m,draw);
	this.tau = tau;
    }

    /**
     * Calculates probability of death per timestep for a species given death rates
     * @param species   identifier of species
     * @param rate      rate to convert
     */
    public void setDeathRate(int species,double rate){
	deathRate[species] = rate;
	pDeath[species] = 1.0 - Math.pow(Math.E,-rate*tau);
    }

    /**
     * Calculates probability of birth per timestep for a species given death rates (single birth per timestep)
     * @param species
     * @param rate
     */
    public void setBirthRate(int species,double rate){
	birthRate[species] = rate;
	pBirth[species] = 1.0 - Math.pow(Math.E,-rate*tau);
    }

}

