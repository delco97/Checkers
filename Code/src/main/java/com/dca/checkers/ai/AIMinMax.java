
package com.dca.checkers.ai;

import com.dca.checkers.model.Board;
import com.dca.checkers.model.GameState;
import com.dca.checkers.model.Move;
import com.dca.checkers.model.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code AIRandomPlayer} class represents a AI player that updates
 * the board based on MinMax algorithm.
 */
public class AIMinMax implements Player {
	
	/** Depth of the tree to build. */
	private boolean isBlack;
	
	/**
	 * Flag that tells if the move has been performed.
	 */
	private boolean moveDone;
	
	/**
	 * Number of expanded nodes
	 */
	private static int expandedNodes = 0;
	
	/**
	 * limit value for state value
	 */
	private int limitValue = -1000;
	
	/**
	 * limit size for queue
	 */
	private int limitSize = 100000;
	
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
		expandedNodes = 0;
		//Select best move
		MinMaxResult bestResult = minMax(gameState.copy(), null, true, 0);
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
	 * @param isMaxPlayer flag that tells if the current player is max (true) or min (false)
	 * @param depth the depth of the recursion.
	 * @return the result of min max algorithm.
	 */
	private MinMaxResult minMax(GameState g, Move m, boolean isMaxPlayer, int depth) {
		double val = eval(g.getBoard(), isBlack);
		if (g.isGameOver()) return new MinMaxResult(m, val);
		
		val -= (double) depth / 1000;
		if (expandedNodes >= limitSize || val < limitValue) return new MinMaxResult(m, val);
		
		double maxVal;
		double minVal;
		Move bestMove = null;
		double bestValue = 0;
		
		expandedNodes++;
		
		if(isMaxPlayer) {
			maxVal = Integer.MIN_VALUE;
			//Get the available moves
			List<Move> moves = g.getAllMoves();
			Collections.shuffle(moves);
			//Evaluate all games state reachable with each possible move
			for (Move possibleMove : moves) {
				GameState childState = g.copy();
				childState.move(possibleMove.getStartIndex(), possibleMove.getEndIndex());
				MinMaxResult resChild = minMax(childState, possibleMove, false, depth + 1);
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
				MinMaxResult resChild = minMax(childState, possibleMove, true, depth + 1);
				if(resChild.value < minVal) {
					minVal = resChild.value;
					bestMove = possibleMove;
					bestValue = resChild.value;
				}
			}
			return new MinMaxResult(bestMove, bestValue);
		}
		
		
	}
	
	/**
	 * Counts the value of player's pieces and subtracts from it
	 * the value of opponent’s pieces.
	 *
	 * @param b         the board state to use for the evaluation.
	 * @param evalForP1 flag that tells if current game state must be evaluated for player 1 (true) or player 2 (false).
	 * @return current state game value for player 1 or player 2.
	 */
	private double eval(Board b, boolean evalForP1) {
		double value = 0;
		final double W_CHECKER = 1;
		final double W_KING = 2;
		
		if (evalForP1) {
			//Number of pieces
			value += b.find(Board.BLACK_CHECKER).size() * W_CHECKER;
			value += b.find(Board.BLACK_KING).size() * W_KING;
			value -= b.find(Board.WHITE_CHECKER).size() * W_CHECKER;
			value -= b.find(Board.WHITE_KING).size() * W_KING;
		} else {//Eval for P2
			value += b.find(Board.WHITE_CHECKER).size() * W_CHECKER;
			value += b.find(Board.WHITE_KING).size() * W_KING;
			value -= b.find(Board.BLACK_CHECKER).size() * W_CHECKER;
			value -= b.find(Board.BLACK_KING).size() * W_KING;
		}
		
		return value;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[isHuman=" + isHuman() + "]";
	}
}
