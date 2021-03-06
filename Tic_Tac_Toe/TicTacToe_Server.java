package Tic_Tac_Toe;


/**
 * The Server for the
 * TicTacToe
 *
 * Author - kenechukwu Nwabueze and Michael Adelure
 */

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class TicTacToe_Server extends JFrame
{
	private String[] board = new String[9];
	private JTextArea outputArea;
	private Player[] players;
	private ServerSocket server;
	private int currentPlayer;
	private final static int PLAYER_X =0; // constant for first player
	private final static int Player_O =1;//constant for second player
	private final static String[] MARKS = {"X","O"};
	private ExecutorService runGame;
	private Lock gameLock; // to lock game for synchronization
	private Condition otherPlayerConnected;
	private Condition otherPlayerTurn;


	/**
	 * 	Set up tic-tac-toe server and GUI that displays messages
	 */
	
public TicTacToe_Server()
{
	super("Tic-Tac-ToeServer"); //set title of window

	/**
	 * create ExecutorService with a thread for each player
	 *
	 */
	runGame = Executors.newFixedThreadPool(2);
	gameLock = new ReentrantLock(); //create lock for game


	/**
	 * condition variable for both players being connected
	 */
	otherPlayerTurn = gameLock.newCondition();
	
	for (int i =0; i<9; i++)
	board[i] = new String("");
	players = new Player[2]; //create array of players
	currentPlayer = PLAYER_X; //set current player to first player
	
	try 
	{
		server = new ServerSocket(12345,2);
	}
	catch (IOException ioException)
	{
		ioException.printStackTrace();
		System.exit(1);
	}	
	
	outputArea = new JTextArea();// Creating a JText Area for output
	add(outputArea, BorderLayout.CENTER);
	
	outputArea.setText("Server awaiting connections\n");
	
	setSize(300,300);// setting size of window
	
	setVisible(true);
}// end TicTacToeServer constructor

	/**
	 * Waiting for connection, create Player, start runnable
	 */
	public void execute()
{
	/**
	 * waiting for each client to connect
	 */
	for(int i= 0; i< players.length;i++)
{
	try// wait for connection, create Player, start runnable
	{
		players[i] = new Player(server.accept(), i);
		runGame.execute(players[i]);	
	}
	catch(IOException ioException)
	{
		ioException.printStackTrace();
		System.exit(1);
	} //end catch
} // end for

gameLock.lock();// lock game to signal

try
{
	players[PLAYER_X].setSuspended(false);
	otherPlayerConnected.signal(); //wake up player X's thread

}// end try
	
finally
{
	gameLock.unlock();
} //end finally
} // end method execute

//display message in outputArea

private void displayMessage(final String messageDisplay)
{
	//display message from event-dispatch thread of execution
SwingUtilities.invokeLater(
		new Runnable()
		{
			public void run()//updates outputArea
			{
				outputArea.append(messageDisplay);
			} //end method run
		} // end inner class
);
}

	/**
	 * determine if move is valid
	 * @param location
	 * @param player
	 * @return
	 */
	public boolean validateAndMove(int location, int player)
{
	// while not current player, must wait for turn
while (player!= currentPlayer) {
	gameLock.lock();// lock game to wait for the other player to go
	
try
{
	otherPlayerTurn.await();
} // end try
catch(InterruptedException exception)
{
	exception.printStackTrace();
} // end catch
finally
{
	gameLock.unlock(); // unlock game after waiting
}
}

	if (!isOccupied(location))
{
	board[location] = MARKS[currentPlayer];
	currentPlayer = (currentPlayer + 1)% 2;// change player

	/**
	 * let new current player know that move occurred
	 */
	players[currentPlayer].otherPlayerMoved(location);
	
	gameLock.lock();// Lock game to signal the other player to go
	
try
{
	
	otherPlayerTurn.signal();//signal other player to continue
}
finally
{
	gameLock.unlock();
} //end finally

return true; //notify player that move was valid
} //end if

else // move was not valid
return false;
} // end method validate and move

	/**
	 * determine whether location is occupied
	 * @param location
	 * @return
	 */
	public boolean isOccupied( int location )
{
if ( board[ location ].equals( MARKS[ PLAYER_X ] ) ||board [ location ].equals( MARKS[ Player_O ] ) )
return true; // location is occupied
else
return false; // location is not occupied
} // end method isOccupied

	/**
	 * place code in this method to determine whether game over
	 * @return
	 */
	public boolean isGameOver()
{
return false; // this is left as an exercise
} // end method isGameOver
 
// private inner class Player manages each Player as a runnable
private class Player implements Runnable
{
private Socket connection; // connection to client
private Scanner input; // input from client
private Formatter output; // output to client
private int playerNumber; // tracks which player this is
private String mark; // mark for this player
private boolean suspended = true; // whether thread is suspended

//set up Player thread
public Player( Socket socket, int number )
{
playerNumber = number; // store this player's number
mark = MARKS[ playerNumber ]; // specify player's mark
connection = socket; // store socket for client
 
try // obtain streams from Socket
{
input = new Scanner( connection.getInputStream() ); 
output = new Formatter( connection.getOutputStream() );
} // end try
catch ( IOException ioException )
{
ioException.printStackTrace();
System.exit( 1 );
} // end catch
} // end Player constructor

// send message that other player moved
public void otherPlayerMoved( int location )
{
output.format( "Opponent moved\n" ); 
output.format( "%d\n", location ); // send location of move
output.flush(); // flush output 
} // end method otherPlayerMoved

// control thread's execution
public void run()
{
// send client its mark (X or O), process messages from client
try
{
displayMessage( "Player " + mark + " connected\n" );
output.format( "%s\n", mark ); // send player's mark
output.flush(); // flush output 

// if player X, wait for another player to arrive
if ( playerNumber == PLAYER_X )
{
output.format( "%s\n%s", "Player X connected",
"Waiting for another player\n" ); 
output.flush(); // flush output 
 
gameLock.lock(); // lock game to wait for second player
 
try
{
while( suspended )
{
otherPlayerConnected.await(); // wait for player O
} // end while
} // end try
catch ( InterruptedException exception )
{
exception.printStackTrace();
} // end catch
finally
{
gameLock.unlock(); // unlock game after second player
} // end finally

// send message that other player connected
output.format( "Other player connected. Your move.\n" );
output.flush(); // flush output 
} // end if
else
{
output.format( "Player O connected, please wait\n" );
output.flush(); // flush output 
} // end else
// while game not over
while ( !isGameOver() )
{
int location = 0; // initialize move location

if ( input.hasNext() )
location = input.nextInt(); // get move location

// check for valid move
if ( validateAndMove( location, playerNumber ) )
{
displayMessage( "\nlocation: " + location );
output.format( "Valid move.\n" ); // notify client
output.flush(); // flush output 
} // end if
else // move was invalid
{
output.format( "Invalid move, try again\n" );
output.flush(); // flush output 
} // end else
} // end while
} // end try
finally
{
try
{
connection.close(); // close connection to client
} // end try
catch ( IOException ioException )
{
ioException.printStackTrace();
System.exit( 1 );
} // end catch
} // end finally
} // end method run

	/**
	 * // set whether or not thread is suspended
	 * @param status
	 */
	public void setSuspended( boolean status )
{
suspended = status; // set value of suspended
} // end method setSuspended
} // end class Player
//} // end class TicTacToeServer

	

	
}
