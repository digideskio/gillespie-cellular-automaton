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

public class RandomSetException extends RuntimeException {
    public RandomSetException(){};
    public RandomSetException(String msg){super(msg);}
}
