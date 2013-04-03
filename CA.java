import java.util.*;
import java.io.*;
import java.awt.*;

/**
 * Abstract discrete time CA class
 */
public abstract class CA {

    int[][] A;
    int[][] B;
    double[] pBirth;      // m birth probabilities   
    double[] pDeath;      // m death probabilities 
    double[] birthRate;   // birth rate
    double[] deathRate;   // death rate
    int n;                // n X n grid, as a torus
    int m;                // m species
    int totalPopulation;  // sum of all populations of species
    double pointSize;
    boolean trace;
    int[] population;     // m population sizes
    RandomSet freeSpace;  // initially all points on the grid
    Random gen;
    boolean draw;         // true <-> plot
    double time;
    int maxTime;
    static int empty = -1;
    static Color background = Color.LIGHT_GRAY;
    static Color[] color = {Color.BLUE,Color.RED,Color.YELLOW,Color.GREEN,Color.CYAN,Color.MAGENTA};
    static int[] deltaY = {-1,-1,-1, 0, 0, 1, 1, 1}; // describes neighbourhood
    static int[] deltaX = {-1, 0, 1,-1, 1,-1, 0, 1}; // describes neighbourhood


    /**
     * Constructs a CA on an nxn grid for m species
     * @param n dimension 1 of the grid
     * @param m number of species
     * @param draw flag indicating whether to draw to the screen
     */
    public CA(int n,int m,boolean draw){
	this.n          = n;
	this.m          = m;
	totalPopulation = 0;
	pointSize       = 0.4;
	gen             = new Random();
	A               = new int[n][n];
	B               = new int[n][n];
	pBirth          = new double[m];
	pDeath          = new double[m];
	birthRate       = new double[m];
	deathRate       = new double[m];
	population      = new int[m];
	freeSpace       = new RandomSet(n*n);
	gen             = new Random();
	this.draw       = draw;
	time            = 0.0;
	maxTime         = 0;
	for (int i=0;i<n;i++)
	    for (int j=0;j<n;j++){
		freeSpace.add(i*n+j);
		A[i][j] = B[i][j] = -1;
	    }
    }

    /**
     * Adds a new member of given species randomly to the population
     * @param species   identifier of species to add
     * @throws RandomSetException
     */
    void add(int species) throws RandomSetException {
	int x = freeSpace.delete();
	int i = x/n;
	int j = x%n;
	A[i][j] = species;
	population[species]++;
	totalPopulation++;
	if (draw) plot(i,j,color[species]);
    }

    /**
     * Executes a single death of a particule member of a given species at location i,j in the grid TODO X
     * @param species  of the individual to kill
     * @param X    CA lattice
     * @param i    coordinate
     * @param j    coordinate
     */
    void death(int species,int[][] X,int i,int j) {
	X[i][j] = empty;
	population[species]--;
	totalPopulation--;
    }

    /**
     * Does deaths on X, new population on Y
     * @param X  CA lattice input
     * @param Y  CA lattice output
     */
    void doDeaths(int[][] X,int[][] Y){
	int species = -1;
	for (int i=0;i<n;i++)
	    for (int j=0;j<n;j++){
		species = X[i][j];
		if (species != empty && gen.nextDouble() <= pDeath[species])
		    death(species,Y,i,j);
	    }
    }

    /**
     * Executes a single birth onto X
     * @param species  of the individual giving birth
     * @param X   CA lattice
     * @param i   coordinate
     * @param j   coordinate
     */
    void birth(int species,int[][] X,int i,int j) {
	int point = chooseRandomNeighbour(i,j);
	if (X[point/n][point%n] == empty){
	    X[point/n][point%n] = species;
	    population[species]++;
	    totalPopulation++;
	}
    }

    /**
     * Selects a random neighbourhood site
     * @param i   coordinate of the focal site
     * @param j   coordinate of the focal site
     * @return    single integer representing the position of the neighbourhood site
     */
    int chooseRandomNeighbour(int i,int j){
	int select = gen.nextInt(8);
	int newX = j + deltaX[select];
	int newY = i + deltaY[select];
	if (newX == n) newX = 0;
	if (newX == -1) newX = n-1;
	if (newY == n) newY = 0;
	if (newY == -1) newY = n-1;
	return newY*n + newX;
    }

    /**
     * Sets up original CA and plot to screen
     */
    public void plot() {
	StdDraw.clear(background);
	StdDraw.setXscale(-2,n);
        StdDraw.setYscale(-2,n);
        StdDraw.show(0);	
	for (int i=0;i<n;i++)
	    for (int j=0;j<n;j++)
		if (A[i][j] != empty){
		    StdDraw.setPenColor(color[A[i][j]]);
		    StdDraw.filledSquare(i,j,pointSize);
		}
	StdDraw.show(0);
    }

    /**
     * Plots changes to CA to the screen
     * @param x    coordinate
     * @param y    coordinate
     * @param color   colour per species
     */
    public void plot(int x,int y,Color color){
	StdDraw.setPenColor(color);
	StdDraw.filledSquare(x,y,pointSize);
	StdDraw.show(0);
    }

    /**
     * Unplots a site (plot back to background colour on death)
     * @param x      coordinate
     * @param y      coordinate
     */
    public void unPlot(int x,int y){
	StdDraw.setPenColor(background);
	StdDraw.filledSquare(x,y,pointSize+0.02);
	StdDraw.show(0);
    }

    /**
     * Outputs time and species counts
     */
    void show(){
	System.out.printf("%.5f",time);
	for (int i=0;i<m;i++) System.out.print(" " + population[i]);
	System.out.println(" "+ totalPopulation);
    }

    /**
     * Pauses running to allow CA visualisation to be visible
     * @param l  length of the pause
     */
    public void pause(long l){
	try{Thread.sleep(l);}
	catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Copies X into Y
     * @param X  input lattice
     * @param Y  output lattice
     */
    void copy(int[][] X,int[][]Y ){
	for (int i=0;i<n;i++)
	    for (int j=0;j<n;j++)
		Y[i][j] = X[i][j];
    }

    /**
     * Updates time by one generation (loop over whole lattice), update births and deaths
     */
    abstract void doGeneration();

    /**
     * Getters and setters
     */
    public boolean getTrace(){return trace;}
    public void    setTrace(boolean trace){this.trace = trace; draw = draw || trace;}
    public void    setPointSize(double x){pointSize = x;}
    public int     size(){return n;}
    public int     getPopulation(){return totalPopulation;}
    public int     getPopulation(int species){return population[species];}
    public void    setPBirth(int species,double p){pBirth[species] = p;}
    public void    setPDeath(int species,double p){pDeath[species] = p;}
    public void    setBirthRate(int species,double rate){birthRate[species] = rate;}
    public void    setDeathRate(int species,double rate){deathRate[species] = rate;}
    public void    setMaxTime(int maxTime){this.maxTime = maxTime;}

}
