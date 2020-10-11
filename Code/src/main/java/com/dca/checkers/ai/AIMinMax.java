
package com.dca.checkers.ai;

import com.dca.checkers.model.GameState;
import com.dca.checkers.model.Move;
import com.dca.checkers.model.Player;

import java.util.List;

/**
 * The {@code AIRandomPlayer} class represents a AI player that updates
 * the board based one alpha beta algorithm.
 */
public class AIMinMax implements Player {
	
	/**
	 * Depth of the tree to build.
	 */
	private static final int depth = 7;
	
	/** Depth of the tree to build. */
	private boolean isBlack;
	
	/**
	 * Flag that tells if the move has been performed.
	 */
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
		isBlack = gameState.isP1Turn();
		//Select best move
		MinMaxResult bestResult = minMax(gameState.copy(), null, depth, true);
		//Apply best move
		gameState.move(bestResult.move.getStartIndex(), bestResult.move.getEndIndex());
		moveDone = true;
	}
	
	@Override
	public boolean hasSkipped() {
		return false;
	}
	
	@Override
	public boolean hasMoved() {
		return moveDone;
	}
	
	private class MinMaxResult {
		Move move;
		double value;
		
		public MinMaxResult(Move m, double v) {
			move = m;
			value = v;
		}
		
	}
	
	/**
	 * Execute min-max algorithm in order to find the best move.
	 *
	 * @param g the game state to evaluate
	 * @param depth the maximum recursion depth
	 * @param isMaxPlayer flag that tells if the current player is max (true) or min (false)
	 * @return the result of min max algorithm.
	 */
	private MinMaxResult minMax(GameState g, Move m, int depth, boolean isMaxPlayer) {
		if(depth == 0 || g.isGameOver()) return new MinMaxResult(m, g.value(isBlack));
		
		double maxVal;
		double minVal;
		Move bestMove = null;
		double bestValue = 0;
		
		if(isMaxPlayer) {
			maxVal = Integer.MIN_VALUE;
			//Get the available moves
			List<Move> moves = g.getAllMoves();
			//Evaluate all games state reachable with each possible move
			for (Move possibleMove : moves) {
				GameState childState = g.copy();
				childState.move(possibleMove.getStartIndex(), possibleMove.getEndIndex());
				MinMaxResult resChild = minMax(childState, possibleMove, depth - 1, false);
				if(resChild.value > maxVal) {
					maxVal = resChild.value;
					bestMove = possibleMove;
					bestValue = resChild.value;
				}
			}
			return new MinMaxResult(bestMove, bestValue);
			
		} else {//Min player
			minVal = Integer.MAX_VALUE;
			//Get the available moves
			List<Move> moves = g.getAllMoves();
			//Evaluate all games state reachable with each possible move
			for (Move possibleMove : moves) {
				GameState childState = g.copy();
				childState.move(possibleMove.getStartIndex(), possibleMove.getEndIndex());
				MinMaxResult resChild = minMax(childState, possibleMove, depth - 1, true);
				if(resChild.value < minVal) {
					minVal = resChild.value;
					bestMove = possibleMove;
					bestValue = resChild.value;
				}
			}
			return new MinMaxResult(bestMove, bestValue);
		}
		
		
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[isHuman=" + isHuman() + "]";
	}
}
