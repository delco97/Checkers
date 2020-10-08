
package com.dca.checkers.ai;

import com.dca.checkers.model.*;

import java.util.List;

/**
 * The {@code AIRandomPlayer} class represents a AI player who plays randomly
 */
public class AIRandomPlayer implements Player {
	
	private boolean moveDone;
	
	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	synchronized public void updateGame(GameState gameState) {
		moveDone = false;
		// Nothing to do
		if (gameState == null || gameState.isGameOver()) {
			moveDone = true;
			return;
		}
			
		// Get the available moves
		GameState copy = gameState.copy();
		List<Move> moves = copy.getAllMoves();
		// Choose a random move
		int moveId = (int)(Math.random() * moves.size()-1);
		Move selectedMove = moves.get(moveId);
		gameState.move(selectedMove.getStart(), selectedMove.getEnd());
		moveDone = true;
	}
	
	@Override
	synchronized public boolean hasMoved() {
		return moveDone;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[isHuman=" + isHuman() + "]";
	}
	
}


