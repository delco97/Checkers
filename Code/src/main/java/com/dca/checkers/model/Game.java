
package com.dca.checkers.model;


import com.dca.checkers.ai.State;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code Game} class represents a game of checkers and ensures that all
 * moves made are valid as per the rules of checkers.
 */
public class Game implements State {

	
	/** The current state of the checker board. */
	private Board board;
	
	/** The flag indicating if it is player 1's turn. */
	private boolean isP1Turn;
	
	/** The index of the last skip, to allow for multiple skips in a turn. */
	private int skipIndex;
	
	/* ----- WEIGHTS ----- */
	/** The weight of being able to skip. */
	private static final double WEIGHT_SKIP = 25;
	
	/** The weight of being able to skip on next turn. */
	private static final double SKIP_ON_NEXT = 20;
	
	/** The weight associated with being safe then safe before and after. */
	private static final double SAFE_SAFE = 5;
	
	/** The weight associated with being safe then unsafe before and after. */
	private static final double SAFE_UNSAFE = -40;
	
	/** The weight associated with being unsafe then safe before and after. */
	private static final double UNSAFE_SAFE = 40;
	
	/** The weight associated with being unsafe then unsafe before and after. */
	private static final double UNSAFE_UNSAFE = -40;
	
	/** The weight of a checker being safe. */
	private static final double SAFE = 3;
	
	/** The weight of a checker being unsafe. */
	private static final double UNSAFE = -5;
	
	/** The factor used to multiply some weights when the checker being
	 * observed is a king. */
	private static final double KING_FACTOR = 2;
	/* ------------ */
	
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
		if (!midValid || board.copy().getPieceSkips(endIndex).isEmpty()) {
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
	 * Determines if the game is over.
	 * 
	 * @return true if the game is over.
	 */
	public boolean isGameOver() {
		return getResult() != MatchResult.UNKNOWN;
	}
	
	/**
	 * Get the current game result.
	 */
	public MatchResult getResult() {
		// Ensure there is at least one of each checker
		List<Point> black = board.find(Board.BLACK_CHECKER);
		black.addAll(board.find(Board.BLACK_KING));

		List<Point> white = board.find(Board.WHITE_CHECKER);
		white.addAll(board.find(Board.WHITE_KING));
		
		if (white.isEmpty() && black.isEmpty())
			return MatchResult.UNKNOWN;
		
		//Now on, at least one of two player must have at least one piece
		
		if(white.isEmpty())
			return  MatchResult.P2_WIN;
		
		if(black.isEmpty())
			return  MatchResult.P1_WIN;
		
		//Both the player have at least one piece
		
		// If the current player can move => game is NOT over
		if(currentPlayerCanMove())  return MatchResult.UNKNOWN;
		
		
		// Current players has no moves => Opponent wins
		return isP1Turn ? MatchResult.P2_WIN:MatchResult.P1_WIN;
	}
	
	/**
	 * Check if the current player can move. I other words, he must have at least one piece on the board
	 * and at least one of them must have one possible move.
	 * @return true if the current player can move: false othrwise.
	 */
	private boolean currentPlayerCanMove() {
		return !getAllMoves().isEmpty();
//		//Get current player pieces
//		List<Point> pieces = getPlayerPieces(isP1Turn);
//
//		for (Point p : pieces) {
//			int i = Board.toIndex(p);
//			if (!board.getPieceMoves(i).isEmpty() || !board.getPieceSkips(i).isEmpty())
//				return true;
//		}
//		return false;
	}
	
	/**
	 * Determines if the specified move is valid based on the rules of checkers.
	 *
	 * @param startIndex the start index of the move.
	 * @param endIndex   the end index of the move.
	 * @return true if the move is legal according to the rules of checkers.
	 */
	public boolean isValidMove(int startIndex, int endIndex) {
		List<Move> allMoves = getAllMoves();
		for (Move m : allMoves)
			if(m.getStartIndex() == startIndex && m.getEndIndex() == endIndex) return true;
		
		return false;
		//return board.isValidMove(isP1Turn(), startIndex, endIndex, getSkipIndex());
	}
	
	public boolean isP1Turn() {
		return isP1Turn;
	}
	
	public void setP1Turn(boolean isP1Turn) {
		this.isP1Turn = isP1Turn;
	}
	
	/**
	 * Gets all the available moves and skips for the current player.
	 *
	 * @return a list of valid moves that the player can make.
	 */
	public List<Move> getAllMoves() {
		
		// The next move needs to be a skip
		if (getSkipIndex() >= 0) {
			
			List<Move> moves = new ArrayList<>();
			List<Point> skips = getSkips(getSkipIndex());
			for (Point end : skips) {
				moves.add(new Move(getSkipIndex(), Board.toIndex(end), MoveType.SKIP));
			}
			
			return moves;
		}
		
		// Get the checkers
		List<Point> checkers = new ArrayList<>();
		Board b = getBoard();
		if (isP1Turn()) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		// Determine if there are any skips
		List<Move> moves = new ArrayList<>();
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			List<Point> skips = getSkips(index);
			for (Point end : skips) {
				Move m = new Move(index, Board.toIndex(end), MoveType.SKIP);
				moves.add(m);
			}
		}
		
		if (moves.isEmpty()) { //No skips found
			// There are no skips, add the regular moves
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				List<Point> movesEnds = b.getPieceMoves(index);
				for (Point end : movesEnds) {
					moves.add(new Move(index, Board.toIndex(end), MoveType.NORMAL));
				}
			}
		}
		
		return moves;
	}
	
	/**
	 * Determines the weight of a move based on a number of factors (e.g. how
	 * safe the checker is before/after, whether it can take an opponents
	 * checker after, etc).
	 *
	 * @param m		the move to test.
	 * @return the weight corresponding to move m.
	 */
	public double getMoveWeight(Move m) {
		double w = 0; //weight calculated
		Point start = m.getStart(), end = m.getEnd();
		int startIndex = Board.toIndex(start), endIndex = Board.toIndex(end);
		Board b = getBoard();
		boolean changed = isP1Turn();
		boolean safeBefore = b.isSafe(start);
		int id = b.get(startIndex);
		boolean isKing;
		
		// Set the initial weight
		if(m.getType() == MoveType.SKIP) w += WEIGHT_SKIP;
		w += (getSafetyWeight(isP1Turn()));
		
		// Make the move
		if (!move(m.getStartIndex(), m.getEndIndex())) {
			return Move.WEIGHT_INVALID;
		}
		b = getBoard();
		changed = (changed != isP1Turn());
		id = b.get(endIndex);
		isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		boolean safeAfter = true;
		
		// Determine if a skip could be made on next move
		if (changed) {
			safeAfter = b.isSafe(end);
			int depth = getSkipDepth(endIndex, !isP1Turn());
			if (safeAfter) {
				w += (SKIP_ON_NEXT * depth * depth);
			} else {
				w += (SKIP_ON_NEXT);
			}
		}
		
		// Check how many more skips are available
		else {
			int depth = getSkipDepth(startIndex, isP1Turn());
			w += (WEIGHT_SKIP * depth * depth);
		}
		
		// Add the weight appropriate to how safe the checker is
		if (safeBefore && safeAfter) {
			w += (SAFE_SAFE);
		} else if (!safeBefore && safeAfter) {
			w += (UNSAFE_SAFE);
		} else if (safeBefore && !safeAfter) {
			w += (SAFE_UNSAFE * (isKing? KING_FACTOR : 1));
		} else {
			w += (UNSAFE_UNSAFE);
		}
		w += (getSafetyWeight(changed != isP1Turn()));
		
		return w;
	}
	
	/**
	 * Calculates the 'safety' state of the game for the player specified. The
	 * player has 'safe' and 'unsafe' checkers, which respectively, cannot and
	 * can be skipped by the opponent in the next turn.
	 *
	 * @param isBlack	the flag indicating if black checkers should be observed.
	 * @return the weight corresponding to how safe the player's checkers are.
	 */
	public double getSafetyWeight(boolean isBlack) {
		
		// Get the checkers
		double weight = 0;
		List<Point> checkers = new ArrayList<>();
		if (isBlack) {
			checkers.addAll(board.find(Board.BLACK_CHECKER));
			checkers.addAll(board.find(Board.BLACK_KING));
		} else {
			checkers.addAll(board.find(Board.WHITE_CHECKER));
			checkers.addAll(board.find(Board.WHITE_KING));
		}
		
		// Determine conditions for each checker
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			int id = board.get(index);
			boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
			if (board.isSafe(checker)) {
				weight += SAFE;
			} else {
				weight += UNSAFE * (isKing? KING_FACTOR : 1);
			}
		}
		
		return weight;
	}
	
	/**
	 * Gets the number of skips that can be made in one turn from a given start
	 * index.
	 *
	 * @param startIndex	the start index of the skips.
	 * @param isP1Turn		the original player turn flag.
	 * @return the maximum number of skips available from the given point.
	 */
	private int getSkipDepth(int startIndex, boolean isP1Turn) {
		
		// Trivial case
		if (isP1Turn != isP1Turn()) {
			return 0;
		}
		
		// Recursively get the depth
		List<Point> skips = getSkips(startIndex);
		int depth = 0;
		for (Point end : skips) {
			int endIndex = Board.toIndex(end);
			move(startIndex, endIndex);
			int testDepth = getSkipDepth(endIndex, isP1Turn);
			if (testDepth > depth) {
				depth = testDepth;
			}
		}
		
		return depth + (skips.isEmpty()? 0 : 1);
	}
	
	/**
	 * Gets a list of skip end-points for a given start index.
	 *
	 * @param startIndex the center index to look for skips around.
	 * @return the list of points such that the start to a given point
	 * represents a skip available.
	 */
	public List<Point> getSkips(int startIndex) {
		return board.getPieceSkips(startIndex);
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
	
	/**
	 * Static evaluation of the current state for the current player perspective.
	 */
	@Override
	public double value(boolean evalForP1) {
		//Game is not over
		if(isEndingPhase())
			return endStateValue1(evalForP1);
		else
			return stateValue1(evalForP1);
	}
	
	/**
	 * Tell if the game is going to end soon.
	 * In others words, it tells if on the board are present only kings.
	 * @return true if the game is ending; false otherwise.
	 */
	private boolean isEndingPhase() {
		List <Point> checkers;
		checkers = board.find(Board.BLACK_CHECKER);
		if(checkers.size() > 0) return false;
		checkers = board.find(Board.WHITE_CHECKER);
		if(checkers.size() > 0) return false;
		return true;
	}
	
	/**
	 * Counts the value of player's pieces and subtracts from it
	 * the value of opponent’s pieces.
	 * @param evalForP1 flag that tells if current game state must be evaluated for player 1 (true) or player 2 (false).
	 * @return current state game value for player 1 or player 2.
	 */
	private double stateValue1(boolean evalForP1) {
		double value = 0;
		final double W_CHECKER = 1;
		final double W_KING = 2;
		
		if(evalForP1) {
			//Number of pieces
			value += board.find(Board.BLACK_CHECKER).size() * W_CHECKER;
			value += board.find(Board.BLACK_KING).size() * W_KING;
			value -= board.find(Board.WHITE_CHECKER).size() * W_CHECKER;
			value -= board.find(Board.WHITE_KING).size() * W_KING;
		} else {//Eval for P2
			value += board.find(Board.WHITE_CHECKER).size() * W_CHECKER;
			value += board.find(Board.WHITE_KING).size() * W_KING;
			value -= board.find(Board.BLACK_CHECKER).size() * W_CHECKER;
			value -= board.find(Board.BLACK_KING).size() * W_KING;
		}
		
		return value;
	}
	
	/**
	 * Advanced pawns are more threatening than pawns that are on the back of the board.
	 * Therefore, since advanced pawns are much closer to become Kings, they got extra value.
	 * Of course, kings are still evaluated more than any pawn.
	 *
	 * @param evalForP1 flag that tells if current game state must be evaluated for player 1 (true) or player 2 (false).
	 * @return current state game value for player 1 or player 2.
	 */
	private double stateValue2(boolean evalForP1) {
		double value = 0;
		final double W_CHECKER_PLAYER_SIDE = 5;
		final double W_CHECKER_OPPONENT_SIDE = 7;
		final double W_KING = 10;
		List<Point> kings;
		List<Point> checkers;
		int countPlayerSides = 0;
		int countOpponentSide = 0;
		
		if(evalForP1) {
			kings = board.find(Board.BLACK_KING);
			value = kings.size() * W_KING;
			checkers = board.find(Board.BLACK_CHECKER);
			
			for (Point p : checkers) {
				if(Board.toIndex(p) >= 16)
					countOpponentSide++;
				else
					countPlayerSides++;
			}
			value += countOpponentSide * W_CHECKER_OPPONENT_SIDE;
			value += countPlayerSides * W_CHECKER_PLAYER_SIDE;
		} else {//Eval for P2
			kings = board.find(Board.WHITE_KING);
			value = kings.size() * W_KING;
			checkers = board.find(Board.WHITE_CHECKER);
			for (Point p : checkers) {
				if(Board.toIndex(p) < 16) countOpponentSide++;
				else  countPlayerSides++;
			}
			value += countOpponentSide * W_CHECKER_OPPONENT_SIDE;
			value += countPlayerSides * W_CHECKER_PLAYER_SIDE;
		}
		
		return value;
	}
	
	/**
	 * For each piece (king) of the player we sum all the distances between it and all the opponent’s pieces. If the
	 * player has more kings than the opponent will prefer a game position that minimizes this sum (he wants to
	 * attack), otherwise he will prefer this sum to be as big as possible (run away).
	 *
	 * @param evalForP1 flag that tells if current game state must be evaluated for player 1 (true) or player 2 (false).
	 * @return current state game value for player 1 or player 2.
	 */
	private double endStateValue1(boolean evalForP1) {
		//Get pieces
		List<Point> playerPieces = getPlayerPieces(evalForP1);
		List<Point> opponentPieces = getPlayerPieces(!evalForP1);
		double distanceOverall = 0;
		//Calculate overall distance
		for (Point cP : playerPieces) {
			for (Point oP : opponentPieces) {
				distanceOverall += cP.distance(oP);
			}
		}
		//Check if current player has more pieces
		double maxDistance = Math.sqrt(Math.pow(board.getRows(),2) + Math.pow(board.getCols(),2));
		if(playerPieces.size()  > opponentPieces.size()) {
			//Current player has more pieces, so he should aim to minimize the distance
			return (maxDistance * 12 * 12) - (distanceOverall);
		}else {
			//Current player has less pieces, so he should aim to maximise the distance
			return distanceOverall;
		}
	}
	
	
	private List<Point> getPlayerPieces(boolean isP1) {
		List<Point> pieces;
		if(isP1) {
			pieces = board.find(Board.WHITE_CHECKER);
			pieces.addAll(board.find(Board.WHITE_KING));
		} else{ //Player 2 turn
			pieces = board.find(Board.BLACK_CHECKER);
			pieces.addAll(board.find(Board.BLACK_KING));
		}
		return pieces;
	}
	
	
}
