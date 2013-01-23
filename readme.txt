Assignment 3 - Finding the Optimal Movement Policy in a Grid World

Markov Decision Process for an intelligent agent to use to
map out the optimal way to move around a grid world. 

The world is a rectangular grid, and the agent does not have full control
over where it is going; there is a chance they either move in the opposite
direction, or do not move at all. The mdp files list the probabilities
of moving in a certain direction, their reward, etc. The control files 
contain the epsilon and gamma values.

Compile With:

javac *.java

Usage:

java valueIteration mdpfile controlfile

Sample Output: 

java valueIteration grid-mdp2.txt control-4.txt

...

State Utility (delta = 0.000203)
-5.7845 -3.4553 -1.3524 1.0000
-7.8319         -3.1368 -1.0000
-9.0907 -7.2165 -5.1612 -3.3329

State Utility (delta = 0.000091)
-5.7846 -3.4553 -1.3524 1.0000
-7.8321         -3.1368 -1.0000
-9.0908 -7.2165 -5.1612 -3.3329

->      ->      ->      1.0
^               ->      -1.0
->      ->      ^       ^
