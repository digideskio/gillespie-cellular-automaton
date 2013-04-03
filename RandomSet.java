/*
*        gillespie-cellular-automaton
*        A program for simulating birth-death processes for multiple species in discrete space. It contains code for the
*        a continuous time (spatial Gillespie) simulator and several discrete time (cellular automaton) simulators.
*
*        Copyright (C) 2013 Patrick Prosser <Patrick.Prosser@glasgow.ac.uk>
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

import java.util.Random;

public class RandomSet {
    
    private int[] v;
    private int size;
    private int capacity;
    private Random random;
    
    public RandomSet(int n){
	size     = 0;
	capacity = n;
	v        = new int[n];
	random   = new Random();
    }

    private int uniform(int N) {
        return random.nextInt(N);
    }

    private void swap(int[] v,int i,int j){int temp = v[i];v[i] = v[j]; v[j] = temp;}
    
    public void add(int e) throws RandomSetException {
	if (size == capacity) throw new RandomSetException("overflow");
	v[size] = e;
	size++;
	swap(v,uniform(size),size-1);
    }
    //
    // add an element e, assumed to be unique
    //

    public int delete() throws RandomSetException {
	if (size == 0) throw new RandomSetException("underflow");
	int i = uniform(size);
	int e = v[i];
	size--;
	swap(v,i,size);
	return e;
    }
    //
    // select randomly an element e, delete e from the set, then return e
    //

    public int select() throws RandomSetException {
	if (size == 0) throw new RandomSetException("uderflow");
	int i = uniform(size);
	int e = v[i];
	return e;
    }
    //
    // select randomly an element e, then return e
    //
       

    public int size(){return size;}

    public String toString(){
	String s = "{";
	if (size > 0) s = s + v[0];
	for (int i=1;i<size;i++) s = s +"," + v[i];
	return s + "}";
    }


    public static void main(String[] args) throws RandomSetException {
	int n = Integer.parseInt(args[0]);
	RandomSet S = new RandomSet(n);

	for (int i=0;i<n;i++){
	    S.add(i);
	    System.out.println("  " + S);
	}
	
	for (int i=0;i<n;i++){
	    System.out.print(S.delete() + " ");
	    System.out.println(S);
	}

    }
}
