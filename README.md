# RacingGame
Three staged car racing game fully developed in Java and swing to a client server setup. Includes 
documentation created as HTML files by auto generation.

The first stage CarSpin simply shows 2 images of a car. The first car loops through an array of
images giving the impression its spinning. The second car can be moved through the image array 
using the left and right keys giving the impression its turning.

The second stage CarAndMap is a working racing game for two users to operate a race car from the
same keyboard. There is a choice between 2 race tracks where the winner is the first to complete 
3 laps. The race begins when a 'GO' button is pressed and a countdown begins. The user perspective
is a 2D overview of the tract and car. The game will also end if the cars collide with each other 
but invokes a bounce reaction if they collide with the track boundary. Tha cars are operated by 
the directional keys and WADZ keys.

The third stage DualRace uses a client and server setup to show a use of distributed programming.
The user provides an ip address and port number to log into the server and the server sends the 
player number before the game starts. The game then behaves the same as the second stage but only
ends when a user completes 3 laps not when the cars collide, instead they now bounce off each other. 
