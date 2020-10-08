/* Name: Player
 * Author: Devon McGrath
 * Description: This class represents a player of the system.
 */

package com.dca.checkers.model;

/**
 * The {@code Player} class is an abstract class that represents a player in a
 * game of checkers.
 */
public interface Player {

	/**
	 * Determines how the game is updated. If true, the user must interact with
	 * the user interface to make a move. Otherwise, the game is updated via
	 * {@link #updateGame(GameState)}.
	 * 
	 * @return true if this player represents a user.
	 */
	boolean isHuman();
	
	/**
	 * Updates the gameState state to take a move for the current player. If there
	 * is a move available that is multiple skips, it may be performed at once
	 * by this method or one skip at a time.
	 *
	 * @param gameState    the gameState to update.
	 */
	void updateGame(GameState gameState);
	
	/**
	 * Tells if the player has moved.
	 *
	 * @return
	 */
	boolean hasMoved();
	
}
