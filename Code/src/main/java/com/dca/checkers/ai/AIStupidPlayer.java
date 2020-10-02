
package com.dca.checkers.ai;

import com.dca.checkers.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AIStupidPlayer} class represents a AI player that updates
 * the board based one some simple empirical evaluations.
 */
public class AIStupidPlayer extends Player {
	

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
		List<Move> moves = game.getAllMoves();
		
		// Find best moves
		int count = 1;
		double bestWeight = Move.WEIGHT_INVALID;
		for (Move m : moves) {
			m.setWeight(copy.copy().getMoveWeight(m));
			if (m.getWeight() > bestWeight) {
				count = 1;
				bestWeight = m.getWeight();
			} else if (m.getWeight() == bestWeight) {
				count++;
			}
		}

		// Randomly select one of the best move
		int move = ((int) (Math.random() * count)) % count;
		for (Move m : moves) {
			if (bestWeight == m.getWeight()) {
				if (move == 0) {
					game.move(m.getStartIndex(), m.getEndIndex());
				} else {
					move--;
				}
			}
		}
		
	}

}


