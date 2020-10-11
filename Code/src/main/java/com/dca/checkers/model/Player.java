/* Name: Player
 * Author: Devon McGrath
 * Description: This class represents a player of the system.
 */

package com.dca.checkers.model;

/**
 * The {@code Player} class is an interface class that represents a player in a
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
	 * @param gameState the game state to update.
	 */
	void updateGame(GameState gameState);
	
	/**
	 * Tells if the player has skipped its turn.
	 *
	 * @return true if the player has skipped his turn, false otherwise.
	 */
	boolean hasSkipped();
	
	/**
	 * Tells if the player has moved
	 *
	 * @return true if the player has moved, false otherwise.
	 */
	boolean hasMoved();
	
}
