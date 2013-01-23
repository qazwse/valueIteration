/*
   Curtis Rasmussen
   2012/10/13

   valueIteration 
*/

import java.util.*;
import java.io.*;

public class valueIteration
{
   private int boardX;
   private int boardY;
   private Cell boardState[][];
   private double gamma;
   private double epsilon;

   public valueIteration(String mdpfile, String controlfile) {
      try {
         loadMdpfile(mdpfile.trim());
         loadControlfile(controlfile.trim());
         printBoard();
      }
      catch (Exception e) {
         System.out.println("Error opening file.");
         e.printStackTrace(System.err);
         System.exit(1);
      }
      mdpValueIteration();
      printOptimalPolicy();
   }

   private void mdpValueIteration() {
      double delta = 0.0;
      int iteration = 0;

      do {
         delta = 0.0;

         //Set all cellUtilities to cellNewUtility
         for (int i = 0; i < boardX; i++) {
            for (int j = 0; j < boardY; j++) {
               Cell s = boardState[i][j];
               if ( s.isReachable && !s.isTerminal ) {
                  s.cellUtility = s.cellNewUtility; 
               }             
            }
         }

         //For each state s do
         for (int i = 0; i < boardX; i++) {
            for (int j = 0; j < boardY; j++) {
               Cell s = boardState[i][j];
               if ( s.isReachable && !s.isTerminal ) {
                  double maxActionUtility = -100000000000.0;

                  //For each action (E, N, W, S)
                  for (int action = 0; action < 4; action++) {
                     double nextStateSummation = 0.0;
                     double probAction[] = s.getDir(action);

                     //For each probability we end up at a certain state
                     //Eg if Action = N, probAction[1] is the prob we
                     //end up at the tile North of the current tile.
                     for (int prob = 0; prob < 5; prob++) {
                        if (probAction[prob] != 0.0) {
                           switch(prob) {
                              //East
                              case(0): 
                                 nextStateSummation += probAction[prob] * boardState[i][j+1].cellUtility;
                                 break;
                              //North
                              case(1):
                                 nextStateSummation += probAction[prob] * boardState[i+1][j].cellUtility;
                                 break;
                              //West
                              case(2):
                                 nextStateSummation += probAction[prob] * boardState[i][j-1].cellUtility;
                                 break;
                              //South
                              case(3):
                                 nextStateSummation += probAction[prob] * boardState[i-1][j].cellUtility;
                                 break;
                              //Same spot
                              case(4):
                                 nextStateSummation += probAction[prob] * boardState[i][j].cellUtility;
                                 break;
                           }
                        }
                     }
                     if (nextStateSummation > maxActionUtility) {
                        maxActionUtility = nextStateSummation;
                     }
                  }

                  //U`s
                  double newUtil = 0.0;
                  newUtil = s.cellReward;
                  newUtil += gamma * maxActionUtility;

                  s.cellNewUtility = newUtil;

                  double newDelta = Math.abs(s.cellNewUtility - s.cellUtility);
                  if (newDelta > delta) {
                     delta = newDelta;
                  }
               }
            }
         }
         iteration += 1;
         System.out.printf("State Utility (delta = %f)\n", delta);
         printBoard();
      }
      while (delta > epsilon);
   }

   private void printOptimalPolicy() {
      String dirEast  = "->";
      String dirWest  = "<-";
      String dirNorth = "^ ";
      String dirSouth = "v ";
      String dirStay  = "_ ";

      for (int i = boardX - 1; i >= 0; i--) {
         for (int j = 0; j < boardY; j++) {
            Cell s = boardState[i][j];
            if (!s.isReachable)
               System.out.print("\t");
            else if (s.isTerminal)
               System.out.printf("%.1f\t", s.cellUtility);
            else {
               double maxUtil = -100000000.0;
               int printDir = -1;
               for (int dir = 0; dir < 5; dir++) {
                  try {
                     switch(dir) {
                        //East
                        case(0): 
                           if (boardState[i][j+1].cellUtility > maxUtil) {
                              maxUtil = boardState[i][j+1].cellUtility;
                              printDir = dir;
                           }
                           break;
                        //North
                        case(1):
                           if (boardState[i+1][j].cellUtility > maxUtil) {
                              maxUtil = boardState[i+1][j].cellUtility;
                              printDir = dir;
                           }
                           break;
                        //West
                        case(2):
                           if (boardState[i][j-1].cellUtility > maxUtil) {
                              maxUtil = boardState[i][j-1].cellUtility;
                              printDir = dir;
                           }
                           break;
                        //South
                        case(3):
                           if (boardState[i-1][j].cellUtility > maxUtil) {
                              maxUtil = boardState[i-1][j].cellUtility;
                              printDir = dir;
                           }
                           break;
                        //Same spot
                        case(4):
                           if (boardState[i][j].cellUtility > maxUtil) {
                              maxUtil = boardState[i][j].cellUtility;
                              printDir = dir;
                           }
                           break;
                     }
                  }
                  catch (Exception e) {
                     continue;
                  }
               }
               switch(printDir) {
                  case(0): System.out.printf("%s\t", dirEast); break;
                  case(1): System.out.printf("%s\t", dirNorth); break;
                  case(2): System.out.printf("%s\t", dirWest); break;
                  case(3): System.out.printf("%s\t", dirSouth); break;
                  case(4): System.out.printf("%s\t", dirStay); break;
               }
            }
         }
         System.out.println();
      }
   }

   private void printBoard() {
      for (int i = boardX - 1; i >= 0; i--) {
         for (int j = 0; j < boardY; j++) {
            double num = boardState[i][j].cellUtility;
            if (num == -99.0)
               System.out.printf("\t", num);
            else
               System.out.printf("%.4f\t", num);
         }
         System.out.println();
      }
      System.out.println();
   }

   private void loadMdpfile(String mdpfile) throws java.io.IOException {
      String parts[];
      String line;

      BufferedReader bf = new BufferedReader( new FileReader(mdpfile) );

      //Read grid size
      parts = readSplitLine(bf);
      boardY = Integer.parseInt(parts[0]);
      boardX = Integer.parseInt(parts[1]);

      //Initialize board
      boardState = new Cell[boardX][boardY];

      for (int i = 0; i < boardX * boardY; i++) {
         Cell c = new Cell();
         int x;
         int y;

         //get blank line
         bf.readLine();

         //Get cell location
         parts = readSplitLine(bf);
         y = Integer.parseInt(parts[0]) - 1; //-1 because grid starts at 1 in file
         x = Integer.parseInt(parts[1]) - 1; //java arrays start at 0

         //Get reward
         parts = readSplitLine(bf);
         c.cellReward = Double.parseDouble(parts[0]);
         if (c.cellReward == -99.0) {
            c.isReachable = false;
         }

         //All the other probabilites - always 4
         //Count for checking if cell is terminal/non-reachable
         int reachCount = 0;
         for (int j = 0; j < 4; j++) {
            double dir[] = c.getDir(j);
            parts = readSplitLine(bf);
            for (int s = 0; s < 5; s++) {
               dir[s] = Double.parseDouble(parts[s]);
               if (dir[s] == -99.0) {
                  reachCount += 1;
               }
            }
         }
         if (reachCount >= 20) {
            c.isTerminal = true;
            c.cellUtility = c.cellReward;
         }
         boardState[x][y] = c;
      }
      bf.close();
   }

   private void loadControlfile(String controlfile) throws java.io.IOException {
      String parts[];
      String line;

      BufferedReader bf = new BufferedReader( new FileReader(controlfile) );

      //Get gamma
      parts = readSplitLine(bf);
      gamma = Double.parseDouble(parts[0]);

      //Get epsilon
      parts = readSplitLine(bf);
      epsilon = Double.parseDouble(parts[0]);

      bf.close();
   }

   private String[] readSplitLine(BufferedReader bf) throws java.io.IOException {
      String line = bf.readLine();
      String[] parts = line.split(" +");
      return parts;
   }

   public static void main(String[] args) {
      if ( args.length < 2 )
         System.out.println("Usage: java valueIteration mdpfile controlfile");
      else
         new valueIteration(args[0], args[1]);
   }
}

