
package com.dca.checkers.model;

import com.dca.checkers.ui.CheckerBoard;
import com.dca.checkers.ui.CheckersWindow;

import java.awt.*;

/**
 * The {@code HumanPlayer} class represents a user of the checkers game that
 * can update the game by clicking on tiles on the board.
 */
public class HumanPlayer implements Player {
	
	/**
	 * Flag that tells if the move has been selected by the user
	 */
	private boolean moveSelected;
	
	/** Flag that tells if the current turn must be skipped (no more wait for input) */
	private boolean skipMove;
	
	@Override
	public int getLastMaxDepthReached() {
		return -1;
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}
	
	@Override
	synchronized public void updateGame(GameState gameState) {
		moveSelected = false;
		skipMove = false;
	}
	
	@Override
	synchronized public boolean hasSkipped() {
		return skipMove;
	}
	
	synchronized public boolean hasMoved() {
		return moveSelected;
	}
	
	/** Tell if the next move is skipped, */
	synchronized public void skipNextMove() {
		skipMove = true;
		notifyAll();
	}
	
	/**
	 * Handle a click over the board.
	 *
	 * @param curGameState the game state to update.
	 * @param boardUI the board UI to update.
	 * @param sel the selec poitn on the board.
	 */
	public synchronized void handleBoardClick(GameState curGameState, CheckerBoard boardUI, Point sel) {
		// The gameState is over or the current player isn't human
		if (curGameState.isGameOver()) return;
		
		// Determine if a move should be attempted
		//if (Board.isValidPoint(sel) && Board.isValidPoint(UI.getLastSelection())) {
		if (curGameState.isValidMove(boardUI.getLastSelection(), sel)) {
			boolean change = curGameState.isP1Turn();
			moveSelected = curGameState.move(boardUI.getLastSelection(), sel);
			if (moveSelected) notifyAll();
			change = (curGameState.isP1Turn() != change);
			boardUI.setLastSelection(change ? null : sel);
		} else {
			boardUI.setLastSelection(sel);
		}
		
		// Check if the selection is valid
		boardUI.setLastSelectionValid(curGameState.hasMove(boardUI.getLastSelection()));
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[isHuman=" + isHuman() + "]";
	}
	
}
