
package com.dca.checkers.ai;

import com.dca.checkers.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AIRandomPlayer} class represents a AI player that updates
 * the board based one alpha beta algorithm.
 */
public class AIAlphaBeta extends Player {

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
			
		//TODO
	}
}
