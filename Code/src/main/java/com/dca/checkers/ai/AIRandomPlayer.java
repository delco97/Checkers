
package com.dca.checkers.ai;

import com.dca.checkers.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AIRandomPlayer} class represents a AI player who plays randomly
 */
public class AIRandomPlayer extends Player {
	

	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public void updateGame(Game game) {
		
		// Nothing to do
		if (game == null || game.isGameOver()) {
			return;
		}
			
		// Get the available moves
		Game copy = game.copy();
		List<Move> moves = copy.getAllMoves();
		// Choose a random move
		int moveId = (int)(Math.random() * moves.size()-1);
		Move selectedMove = moves.get(moveId);
		game.move(selectedMove.getStart(), selectedMove.getEnd());
	}

}


