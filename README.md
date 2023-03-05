# RacingGame
Three staged car racing game developement to a client server setup.
The first stage CarSpin simply shows 2 images of a car. The first car loops through an array of
images giving the impression its spinning. The second car can be moved through the image array 
using the left and right keys giving the impression its turning.

The second stage CarAndMap is a working racing game for two users to operate a race car from the
same keyboard. There is a choice between 2 race tracks where the winner is the first to complete 
3 laps. The race begins when a 'GO' button is pressed and a countdown begins. The user perspective
is a 2D overview of the tract and car. The game will also end if the cars collide with each other 
but invokes a bounce reaction if they collide with the track boundary. Tha cars are operated by 
the directional keys and WADZ keys.

<In developement>
The third stage DualRace uses a client and server setup to show a use of distributed programming.
The user provides an ip address to log into the server and selects the player number before the 
game starts. The game then behaves the same as the second stage and still ends when a user completes
3 laps or the cars collide with each other. 
