gillespie-cellular-automaton
----------------------------

Introduction
------------

The code of gillespie-cellular-automaton allows the user to run the simulations used in the following academic publication:

Rebecca Mancy, Patrick Prosser, Simon Rogers (2013) Discrete and continuous time simulations of spatial ecological
processes predict different final population sizes and interspecific competition outcomes. Ecological Modelling.

A pdf of the pre-publication version of this paper (Mancy_Prosser_Rogers_2013.pdf) is included in the release.

This document explains the licensing, compilation and execution of the code.


Licensing and Citation
----------------------
The code is released under the GNU General Public License, version 3 (GPLv3). The full terms of this licence
are included in the file LICENSE.txt of this release and a boilerplate text highlighting this is included in
each of the source files. One of the conditions of the licence is that derivative works should be released under the
same licence.

All code was written by the Patrick Prosser and Rebecca Mancy with the exception of the class StdDraw.java, written by
Robert Sedgewick and Kevin Wayne and included in the package stdlib.jar. This code is licensed under the GNU General
Public License, version 3 (GPLv3) and is available from http://introcs.cs.princeton.edu/java/stdlib/.

The code was developed in the context of the paper Mancy, Prosser & Rogers (2013) (full reference above) and the authors
would be grateful if researchers using the ideas from this paper would reference this paper in the usual format. If the
the code is used for entirely unrelated applications, please cite the code itself. An example of how to do this is
provided below:

In text:
gillespie-cellular-automaton (Prosser & Mancy, 2013) is a piece of software used for simulating spatial ecological
processes using a range of algorithms.

In Reference list:
Prosser, P. and Mancy, R. (2013) gillespie-cellular-automaton Spatial ecological simulators (Version 1.0) [Computer
program]. Available at https://github.com/rebeccamancy/gillespie-cellular-automaton (Accessed 10 May 2013)


Compilation
-----------
At the command line, enter:
> javac *.java


Execution
---------

Once compiled, the code can be executed from the command line using the following information.

> java <algorithmName> <gridSize> <maxTime> <timeStep> {<initPopnSize> <bRate> <dRate>}  {optional parameters}

Where <algorithmName> is the name of the simulation algorithm (Gill, RFd2S, RFd2M, RR1S, RR1M). The other parameters are
explained below. The species parameters <intiPopnSize>, <bRate>, <dRate> must be provided for each species.

Required parameters

- gridSize: size of lattice in number of sites per side (implemented on a taurus using a Moore neighbourhood)
- maxTime: maximum time to run the simulation
- timeStep: time step for discrete time simulations and for output timestep for Gill
- For each species:
    - initPopnSize: number of organisms of this species at t=0
    - bRate: birth rate of the species
    - dRate: death rate of the species

Optional parameters (one only allowed)

- draw: show the visualisation of the simulation
- ten: repeat the simulation 10 times
- hundred: repeat the simulation 100 times
- trace: implemented for Gill only; outputs details of each event (site from and to for births, site of deaths)


Examples
--------
> java Gill 100 1000 0.1 30 0.2 0.15 100 0.1 0.09 200 0.2 0.19 draw

This runs Gill with 3 species

  - lattice 100x100
  - 1000 maximum time (arbitrary units)
  - output results every 0.1 time units (i.e. tau)
  - species 1 with population 30,  birth rate 0.2,  death rate 0.15
  - species 2 with population 100, birth rate 0.1,  death rate 0.09
  - species 3 with population 200, birth rate 0.2,  death rate 0.19
  - draw the output

> java RFd2M 100 1000 0.1 30 0.2 0.15 100 0.1 0.09 ten

This runs RFd2M with 2 species, running this for ten replicates of the simulator. For discrete time simulators, the
timestep (0.1, third argument above) is employed as the timestep in the simulator, as well as for outputting population
sizes.

The other algorithms are executed analogously.


Sample output
-------------
Sample output for the following command line call is provided below
> java Gill 100 6 0.5 30 0.2 0.15 100 0.1 0.09 200 0.2 0.19
0.00000 30 100 200 330
0.50000 28 100 197 325
1.00000 29 101 199 329
1.50000 30 97 198 325
2.00000 29 100 208 337
2.50000 29 97 210 336
3.00000 34 95 197 326
3.50000 30 92 188 310
4.00000 31 88 198 317
4.50000 30 90 196 316
5.00000 33 92 192 317
5.50000 34 89 205 328
6.00000 35 92 205 332

Columns represent:
1: time
2: size of population for species 1
3: size of population for species 2
4: size of population for species 3
5: total population size (sum of cols 2, 3, 4)

Additional information on implementation
----------------------------------------

The class hierarchy consists of an abstract class, CA, which is extended in CARS and CARM to employ rates (CA is a
standard CA that uses probabilities). The letter 'R' in CARS and CARM indicates the use of rates in these algorithms
while the letter 'S' in CARS indicates single births and the 'M' in CARM multiple births (see paper listed in
introduction for explanation).

There are five main classes
- Gill which directly extends CA
- RR1S and RFd2S which extend CARS
- RR1M and RFd2M which extend CARM

                                   (CA)
                                   / | \
                                  /  |  \
                                 /   |   \
                             (CARS) Gill (CARM)
                             /  \         /  \
                            /    \       /    \
                       RFd2S    RR1S   RFd2M   RR1M

The class hierarchy is shown above in diagrammatic form, where abstract classes are shown in brackets.

The main differences between the algorithms can be found in the methods doGeneration and doBirths. In Gill, the method
doGeneration executes a single event while for the discrete time algorithms, it executes a generation in the sense that
each site is considered for birth and death.

Note that the symbol 'tau' is used differently in the code from its use in the paper listed in the introduction section.
In the code, tau represents length of the timestep, while in the paper it represents the inter-event time. Note that in
Gill, tau does not have a functional role in the calculation of events, but is used only for outputting population sizes
at regular intervals to allow comparisons with the other algorithms.