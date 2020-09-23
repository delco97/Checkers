
package com.dca.checkers.model;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Game} class represents a game of checkers and ensures that all
 * moves made are valid as per the rules of checkers.
 */
public class Game {

	/** The current state of the checker board. */
	private Board board;
	
	/** The flag indicating if it is player 1's turn. */
	private boolean isP1Turn;
	
	/** The index of the last skip, to allow for multiple skips in a turn. */
	private int skipIndex;
	
	public Game() {
		restart();
	}
	
	public Game(String state) {
		setGameState(state);
	}
	
	public Game(Board board, boolean isP1Turn, int skipIndex) {
		this.board = (board == null)? new Board() : board;
		this.isP1Turn = isP1Turn;
		this.skipIndex = skipIndex;
	}
	
	/**
	 * Creates a copy of this game such that any modifications made to one are
	 * not made to the other.
	 * 
	 * @return an exact copy of this game.
	 */
	public Game copy() {
		Game g = new Game();
		g.board = board.copy();
		g.isP1Turn = isP1Turn;
		g.skipIndex = skipIndex;
		return g;
	}
	
	/**
	 * Resets the game of checkers to the initial state.
	 */
	public void restart() {
		this.board = new Board();
		this.isP1Turn = true;
		this.skipIndex = -1;
	}
	
	/**
	 * Attempts to make a move from the start point to the end point.
	 * 
	 * @param start	the start point for the move.
	 * @param end	the end point for the move.
	 * @return true if and only if an update was made to the game state.
	 * @see #move(int, int)
	 */
	public boolean move(Point start, Point end) {
		if (start == null || end == null) {
			return false;
		}
		return move(Board.toIndex(start), Board.toIndex(end));
	}
	
	/**
	 * Attempts to make a move given the start and end index of the move.
	 * 
	 * @param startIndex	the start index of the move.
	 * @param endIndex		the end index of the move.
	 * @return true if and only if an update was made to the game state.
	 * @see #move(Point, Point)
	 */
	public boolean move(int startIndex, int endIndex) {
		
		// Validate the move
		if (!isValidMove(startIndex, endIndex)) {
			return false;
		}
		
		// Make the move
		Point middle = Board.middle(startIndex, endIndex);
		int midIndex = Board.toIndex(middle);
		this.board.set(endIndex, board.get(startIndex));
		this.board.set(midIndex, Board.EMPTY);
		this.board.set(startIndex, Board.EMPTY);
		
		// Make the checker a king if necessary
		Point end = Board.toPoint(endIndex);
		int id = board.get(endIndex);
		boolean switchTurn = false;
		if (end.y == 0 && id == Board.WHITE_CHECKER) {
			this.board.set(endIndex, Board.WHITE_KING);
			switchTurn = true;
		} else if (end.y == 7 && id == Board.BLACK_CHECKER) {
			this.board.set(endIndex, Board.BLACK_KING);
			switchTurn = true;
		}
		
		// Check if the turn should switch (i.e. no more skips)
		boolean midValid = Board.isValidIndex(midIndex);
		if (midValid) {
			this.skipIndex = endIndex;
		}
		if (!midValid || board.getSkips(endIndex).isEmpty()) {
			switchTurn = true;
		}
		if (switchTurn) {
			this.isP1Turn = !isP1Turn;
			this.skipIndex = -1;
		}
		
		return true;
	}
	
	/**
	 * Gets a copy of the current board state.
	 * 
	 * @return a non-reference to the current game board state.
	 */
	public Board getBoard() {
		return board.copy();
	}
	
	/**
	 * Determines if the game is over. The game is over if one or both players
	 * cannot make a single move during their turn.
	 * 
	 * @return true if the game is over.
	 */
	public boolean isGameOver() {

		// Ensure there is at least one of each checker
		List<Point> black = board.find(Board.BLACK_CHECKER);
		black.addAll(board.find(Board.BLACK_KING));
		if (black.isEmpty()) {
			return true;
		}
		List<Point> white = board.find(Board.WHITE_CHECKER);
		white.addAll(board.find(Board.WHITE_KING));
		if (white.isEmpty()) {
			return true;
		}
		
		// Check that the current player can move
		List<Point> test = isP1Turn? black : white;
		for (Point p : test) {
			int i = Board.toIndex(p);
			if (!board.getMoves(i).isEmpty() || !board.getSkips(i).isEmpty()) {
				return false;
			}
		}
		
		// No moves
		return true;
	}
	
	/**
	 * Determines if the specified move is valid based on the rules of checkers.
	 *
	 * @param startIndex the start index of the move.
	 * @param endIndex   the end index of the move.
	 * @return true if the move is legal according to the rules of checkers.
	 */
	public boolean isValidMove(int startIndex, int endIndex) {
		return board.isValidMove(isP1Turn(), startIndex, endIndex, getSkipIndex());
	}
	
	public boolean isP1Turn() {
		return isP1Turn;
	}
	
	public void setP1Turn(boolean isP1Turn) {
		this.isP1Turn = isP1Turn;
	}
	
	/**
	 * Gets a list of skip end-points for a given start index.
	 *
	 * @param startIndex the center index to look for skips around.
	 * @return the list of points such that the start to a given point
	 * represents a skip available.
	 */
	public List<Point> getSkips(int startIndex) {
		return board.getSkips(startIndex);
	}
	
	public int getSkipIndex() {
		return skipIndex;
	}
	
	/**
	 * Gets the current game state as a string of data that can be parsed by
	 * {@link #setGameState(String)}.
	 * 
	 * @return a string representing the current game state.
	 * @see #setGameState(String)
	 */
	public String getGameState() {
		
		// Add the game board
		StringBuilder stateBuilder = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			stateBuilder.append(board.get(i));
		}
		String state = stateBuilder.toString();
		
		// Add the other info
		state += (isP1Turn? "1" : "0");
		state += skipIndex;
		
		return state;
	}
	
	/**
	 * Parses a string representing a game state that was generated from
	 * {@link #getGameState()}.
	 *
	 * @param state    the game state.
	 * @see #getGameState()
	 */
	public void setGameState(String state) {
		
		restart();
		
		// Trivial cases
		if (state == null || state.isEmpty()) {
			return;
		}
		
		// Update the board
		int n = state.length();
		for (int i = 0; i < 32 && i < n; i ++) {
			try {
				int id = Integer.parseInt("" + state.charAt(i));
				this.board.set(i, id);
			} catch (NumberFormatException e) {
				System.err.println("Impossible to parse character: " + i);
			}
		}
		
		// Update the other info
		if (n > 32) {
			this.isP1Turn = (state.charAt(32) == '1');
		}
		if (n > 33) {
			try {
				this.skipIndex = Integer.parseInt(state.substring(33));
			} catch (NumberFormatException e) {
				this.skipIndex = -1;
			}
		}
	}
}
