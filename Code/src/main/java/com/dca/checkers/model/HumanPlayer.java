
package com.dca.checkers.model;

import com.dca.checkers.ui.CheckerBoard;
import com.dca.checkers.ui.CheckersWindow;

import java.awt.*;

/**
 * The {@code HumanPlayer} class represents a user of the checkers game that
 * can update the game by clicking on tiles on the board.
 */
public class HumanPlayer extends Thread implements Player {
	
	private Thread t;
	
	private boolean moveSelected;
	private boolean skipMove;
	
	@Override
	synchronized public void run() {
		//Wait user input or signal to skip the wait
		moveSelected = false;
		skipMove = false;
		waitMoveSelection();
	}
	
	@Override
	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}
	
	/**
	 * Just wait that the users select a move using the UI.
	 */
	@Override
	synchronized public void updateGame(GameState gameState) {
		moveSelected = false;
		start();
	}
	
	@Override
	synchronized public boolean hasMoved() {
		return moveSelected || skipMove;
	}
	
	synchronized public void skipNextMove() {
		skipMove = true;
		notifyAll();
	}
	
	synchronized private void waitMoveSelection() {
		while (!hasMoved()) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Thread interrupted");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Handle a click over the board.
	 *
	 * @param sel
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
