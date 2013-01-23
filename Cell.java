/*
   Curtis Rasmussen
   2012/11/25

   Cell.java 
*/

import java.util.*;
import java.io.*;

public class Cell
{
   public double cellReward;
   public double cellUtility;
   public double cellNewUtility;
   public boolean isReachable;
   public boolean isTerminal;

   //0 = e, 1 = n, 2 = w, 3 = s, 4 = h 
   //Decided to give them all names, may change it to an array
   public double probActionEast[];
   public double probActionNorth[];
   public double probActionWest[];
   public double probActionSouth[];

   public Cell() {
      probActionSouth = new double[5];
      probActionWest  = new double[5];
      probActionNorth = new double[5];
      probActionEast  = new double[5];

      isReachable = true;
      isTerminal = false;

      cellReward = 0.0;
      cellUtility = 0.0;
      cellNewUtility = 0.0;
   }

   public double[] getDir(int i) {
      switch(i) {
         case 0: return probActionEast;  
         case 1: return probActionNorth; 
         case 2: return probActionWest;  
         case 3: return probActionSouth; 
         default: return null; 
      }
   }

}
