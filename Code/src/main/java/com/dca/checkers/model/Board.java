
package com.dca.checkers.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Board} class represents a game state for checkers. A standard
 * checker board is 8 x 8 (64) tiles, alternating white/black. Checkers are
 * only allowed on black tiles and can therefore only move diagonally.
 * Tile states can be retrieved through {@link #get(int)} and
 * {@link #get(int, int)}. Tile states can be set through
 * {@link #set(int, byte)} and {@link #set(int, int, byte)}. The entire game can
 * be reset with {@link #reset()}.
 */
public class Board {
	/** Number of rows */
	private final int nRows = 8;
	
	/** Number of columns */
	private final int nCols = 8;
	
	/** An ID indicating a point was not on the checker board. */
	public static final byte INVALID = -1;
	
	/** The ID of an empty checker board tile. */
	public static final byte EMPTY = 0b000;
	
	/** The ID of a white checker in the checker board. */
	public static final byte BLACK_CHECKER = 0b110; //4 * 1 + 2 * 1 + 1 * 0 = 6;
	
	/** The ID of a white checker in the checker board. */
	public static final byte WHITE_CHECKER = 0b100; //4 * 1 + 2 * 0 + 1 * 0 = 4;
	
	/** The ID of a black checker that is also a king. */
	public static final byte BLACK_KING = 0b111; //4 * 1 + 2 * 1 + 1 * 1 = 7;
	
	/** The ID of a white checker that is also a king. */
	public static final byte WHITE_KING = 0b101; //4 * 1 + 2 * 0 + 1 * 1 = 5;
	
	/** The current state of the board, represented as three integers. */
	private byte[] state;
	
	/**
	 * Constructs a new checker game board, pre-filled with a new game state.
	 */
	public Board() {
		reset();
	}
	
	/**
	 * Creates an exact copy of the board. Any changes made to the copy will
	 * not affect the current object.
	 *
	 * @return a copy of this checker board.
	 */
	public Board copy() {
		Board copy = new Board();
		copy.state = state.clone();
		return copy;
	}
	
	/**
	 * Resets the checker board to the original game state with black checkers
	 * on top and white on the bottom. There are both 12 black checkers and 12
	 * white checkers.
	 */
	public void reset() {
		
		// Reset the state
		this.state = new byte[32];
		for (int i = 0; i < 12; i ++) {
			set(i, BLACK_CHECKER);
			set(31 - i, WHITE_CHECKER);
		}
	}
	
	/**
	 * Searches through the checker board and finds black tiles that match the
	 * specified ID.
	 *
	 * @param id	the ID to search for.
	 * @return a list of points on the board with the specified ID. If none
	 * exist, an empty list is returned.
	 */
	public List<Point> find(byte id) {
		
		// Find all black tiles with matching IDs
		List<Point> points = new ArrayList<>();
		for (int i = 0; i < 32; i ++) {
			if (get(i) == id) {
				points.add(toPoint(i));
			}
		}
		
		return points;
	}
	
	/**
	 * Sets the ID of a black tile on the board at the specified location.
	 * If the location is not a black tile, nothing is updated. If the ID is
	 * less than 0, the board at the location will be set to {@link #EMPTY}.
	 *
	 * @param x		the x-coordinate on the board (from 0 to 7 inclusive).
	 * @param y		the y-coordinate on the board (from 0 to 7 inclusive).
	 * @param id	the new ID to set the black tile to.
	 * @see  #set(int, byte)
	 */
	public void set(int x, int y, byte id) {
		set(toIndex(x, y), id);
	}
	
	/**
	 * Sets the ID of a black tile on the board at the specified location.
	 * If the location is not a black tile, nothing is updated. If the ID is
	 * less than 0, the board at the location will be set to {@link #EMPTY}.
	 *
	 * @param index	the index of the black tile (from 0 to 31 inclusive).
	 * @param id	the new ID to set the black tile to.
	 * @see #set(int, int, byte)
	 */
	public void set(int index, byte id) {
		
		// Out of range
		if (!isValidIndex(index)) {
			return;
		}
		
		// Invalid ID, so just set to EMPTY
		if (id < 0) {
			id = EMPTY;
		}
		
		// Set tile state
		this.state[index] = id;
	}
	
	/**
	 * Gets the ID corresponding to the specified point on the checker board.
	 *
	 * @param x	the x-coordinate on the board (from 0 to 7 inclusive).
	 * @param y	the y-coordinate on the board (from 0 to 7 inclusive).
	 * @return the ID at the specified location or {@link #INVALID} if the
	 * location is not on the board or the location is a white tile.
	 * @see #get(int)
	 * @see #set(int, byte)
	 * @see #set(int, int, byte)
	 */
	public byte get(int x, int y) {
		return get(toIndex(x, y));
	}
	
	/**
	 * Gets the ID corresponding to the specified point on the checker board.
	 *
	 * @param index	the index of the black tile (from 0 to 31 inclusive).
	 * @return the ID at the specified location or {@link #INVALID} if the
	 * location is not on the board.
	 * @see  #get(int, int)
	 * @see #set(int, byte)
	 * @see #set(int, int, byte)
	 */
	public byte get(int index) {
		if (!isValidIndex(index)) {
			return INVALID;
		}
		return state[index];
	}
	
	/**
	 * Converts a black tile index (0 to 31 inclusive) to an (x, y) point, such
	 * that index 0 is (1, 0), index 1 is (3, 0), ... index 31 is (7, 7).
	 *
	 * @param index	the index of the black tile to convert to a point.
	 * @return the (x, y) point corresponding to the black tile index or the
	 * point (-1, -1) if the index is not between 0 - 31 (inclusive).
	 * @see #toIndex(int, int)
	 * @see #toIndex(Point)
	 */
	public static Point toPoint(int index) {
		int y = index / 4;
		int x = 2 * (index % 4) + (y + 1) % 2;
		return !isValidIndex(index)? new Point(-1, -1) : new Point(x, y);
	}
	
	/**
	 * Converts a point to an index of a black tile on the checker board, such
	 * that (1, 0) is index 0, (3, 0) is index 1, ... (7, 7) is index 31.
	 *
	 * @param x	the x-coordinate on the board (from 0 to 7 inclusive).
	 * @param y	the y-coordinate on the board (from 0 to 7 inclusive).
	 * @return the index of the black tile or -1 if the point is not a black
	 * tile.
	 * @see  #toIndex(Point)
	 * @see #toPoint(int)
	 */
	public static int toIndex(int x, int y) {
		
		// Invalid (x, y) (i.e. not in board, or white tile)
		if (!isValidPoint(new Point(x, y))) {
			return -1;
		}
		
		return y * 4 + x / 2;
	}
	
	/**
	 * Converts a point to an index of a black tile on the checker board, such
	 * that (1, 0) is index 0, (3, 0) is index 1, ... (7, 7) is index 31.
	 *
	 * @param p	the point to convert to an index.
	 * @return the index of the black tile or -1 if the point is not a black
	 * tile.
	 * @see #toIndex(int, int)
	 * @see #toPoint(int)
	 */
	public static int toIndex(Point p) {
		return (p == null)? -1 : toIndex(p.x, p.y);
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 *
	 * @param p1	the first point of a black tile on the checker board.
	 * @param p2	the second point of a black tile on the checker board.
	 * @return the middle point between two points or (-1, -1) if the points
	 * are not on the board, are not distance 2 from each other in x and y,
	 * or are on a white tile.
	 * @see #middle(int, int)
	 * @see #middle(int, int, int, int)
	 */
	public static Point middle(Point p1, Point p2) {
		
		// A point isn't initialized
		if (p1 == null || p2 == null) {
			return new Point(-1, -1);
		}
		
		return middle(p1.x, p1.y, p2.x, p2.y);
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 *
	 * @param index1	the index of the first point (from 0 to 31 inclusive).
	 * @param index2	the index of the second point (from 0 to 31 inclusive).
	 * @return the middle point between two points or (-1, -1) if the points
	 * are not on the board, are not distance 2 from each other in x and y,
	 * or are on a white tile.
	 * @see #middle(Point, Point)
	 * @see #middle(int, int, int, int)
	 */
	public static Point middle(int index1, int index2) {
		return middle(toPoint(index1), toPoint(index2));
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 *
	 * @param x1	the x-coordinate of the first point.
	 * @param y1	the y-coordinate of the first point.
	 * @param x2	the x-coordinate of the second point.
	 * @param y2	the y-coordinate of the second point.
	 * @return the middle point between two points or (-1, -1) if the points
	 * are not on the board, are not distance 2 from each other in x and y,
	 * or are on a white tile.
	 * @see #middle(int, int)}
	 * @see #middle(Point, Point)
	 */
	public static Point middle(int x1, int y1, int x2, int y2) {
		
		// Check coordinates
		int dx = x2 - x1, dy = y2 - y1;
		if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || // Not in the board
				x1 > 7 || y1 > 7 || x2 > 7 || y2 > 7) {
			return new Point(-1, -1);
		} else if (x1 % 2 == y1 % 2 || x2 % 2 == y2 % 2) { // white tile
			return new Point(-1, -1);
		} else if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
			return new Point(-1, -1);
		}
		
		return new Point(x1 + dx / 2, y1 + dy / 2);
	}
	
	/**
	 * Checks if an index corresponds to a black tile on the checker board.
	 *
	 * @param testIndex	the index to check.
	 * @return true if and only if the index is between 0 and 31 inclusive.
	 */
	public static boolean isValidIndex(int testIndex) {
		return testIndex >= 0 && testIndex < 32;
	}
	
	/**
	 * Checks if a point corresponds to a black tile on the checker board.
	 *
	 * @param testPoint	the point to check.
	 * @return true if and only if the point is on the board, specifically on
	 * a black tile.
	 */
	public static boolean isValidPoint(Point testPoint) {
		
		if (testPoint == null) {
			return false;
		}
		
		// Check that it is on the board
		final int x = testPoint.x, y = testPoint.y;
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		
		// Check that it is on a black tile
		return x % 2 != y % 2;
		
	}
	
	/**
	 * Validates all ID related values for the startClick, end, and middle (if the
	 * move is a skip).
	 *
	 * @param isP1Turn   the flag indicating if it is player 1's turn.
	 * @param startIndex the startClick index of the move.
	 * @param endIndex   the end index of the move.
	 * @return true if and only if all IDs are valid.
	 */
	private boolean validateIDs(boolean isP1Turn, int startIndex, int endIndex) {
		
		// Check if end is clear
		if (get(endIndex) != Board.EMPTY) {
			return false;
		}
		
		// Check if proper ID
		int id = get(startIndex);
		if ((isP1Turn && id != Board.BLACK_CHECKER && id != Board.BLACK_KING) || (!isP1Turn && id != Board.WHITE_CHECKER && id != Board.WHITE_KING)) {
			return false;
		}
		
		// Check the middle
		Point middle = Board.middle(startIndex, endIndex);
		int midID = get(Board.toIndex(middle));
		return midID == Board.INVALID || ((isP1Turn || midID == Board.BLACK_CHECKER || midID == Board.BLACK_KING) && (!isP1Turn || midID == Board.WHITE_CHECKER || midID == Board.WHITE_KING));
		
		// Passed all tests
	}
	
	/**
	 * Checks that the move is diagonal and magnitude 1 or 2 in the correct
	 * direction. If the magnitude is not 2 (i.e. not a skip), it checks that
	 * no skips are available by other checkers of the same player.
	 *
	 * @param isP1Turn   the flag indicating if it is player 1's turn.
	 * @param startIndex the startClick index of the move.
	 * @param endIndex   the end index of the move.
	 * @return true if and only if the move distance is valid.
	 */
	private boolean validateDistance(boolean isP1Turn, int startIndex, int endIndex) {
		
		// Check that it was a diagonal move
		Point start = Board.toPoint(startIndex);
		Point end = Board.toPoint(endIndex);
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) > 2 || dx == 0) {
			return false;
		}
		
		// Check that it was in the right direction
		int id = get(startIndex);
		if ((id == Board.WHITE_CHECKER && dy > 0) || (id == Board.BLACK_CHECKER && dy < 0)) {
			return false;
		}
		
		// Check that if this is not a skip, there are none available
		Point middle = Board.middle(startIndex, endIndex);
		int midID = get(Board.toIndex(middle));
		if (midID < 0) {
			
			// Get the correct checkers
			List<Point> checkers;
			if (isP1Turn) {
				checkers = find(Board.BLACK_CHECKER);
				checkers.addAll(find(Board.BLACK_KING));
			} else {
				checkers = find(Board.WHITE_CHECKER);
				checkers.addAll(find(Board.WHITE_KING));
			}
			
			// Check if any of them have a skip available
			for (Point p : checkers) {
				int index = Board.toIndex(p);
				if (!getPieceSkips(index).isEmpty()) {
					return false;
				}
			}
		}
		
		// Passed all tests
		return true;
	}
	
	/**
	 * Checks if the specified checker is safe (i.e. the opponent cannot skip
	 * the checker).
	 *
	 * @param checker the point where the test checker is located at.
	 * @return true if and only if the checker at the point is safe.
	 */
	public boolean isSafe(Point checker) {
		
		// Trivial cases
		if (checker == null) {
			return true;
		}
		int index = Board.toIndex(checker);
		if (index < 0) {
			return true;
		}
		int id = get(index);
		if (id == Board.EMPTY) {
			return true;
		}
		
		// Determine if it can be skipped
		boolean isBlack = (id == Board.BLACK_CHECKER || id == Board.BLACK_KING);
		List<Point> check = new ArrayList<>();
		addPoints(check, checker, Board.BLACK_KING, 1);
		for (Point p : check) {
			int start = Board.toIndex(p);
			int tid = get(start);
			
			// Nothing here
			if (tid == Board.EMPTY || tid == Board.INVALID) {
				continue;
			}
			
			// Check ID
			boolean isWhite = (tid == Board.WHITE_CHECKER || tid == Board.WHITE_KING);
			if (isBlack && !isWhite) {
				continue;
			}
			boolean isKing = (tid == Board.BLACK_KING || tid == Board.BLACK_KING);
			
			// Determine if valid skip direction
			int dx = (checker.x - p.x) * 2;
			int dy = (checker.y - p.y) * 2;
			if (!isKing && (isWhite ^ (dy < 0))) {
				continue;
			}
			int endIndex = Board.toIndex(new Point(p.x + dx, p.y + dy));
			if (isValidSkip(start, endIndex)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets a list of move end-points for a given startClick index.
	 *
	 * @param start the center index to look for moves around.
	 * @return the list of points such that the startClick to a given point
	 * represents a move available.
	 * @see #getPieceMoves(int)
	 */
	public List<Point> getPieceMoves(Point start) {
		return getPieceMoves(Board.toIndex(start));
	}
	
	/**
	 * Gets a list of move end-points for a given startClick index.
	 *
	 * @param startIndex the center index to look for moves around.
	 * @return the list of points such that the startClick to a given point
	 * represents a move available.
	 * @see #getPieceMoves(Point)
	 */
	public List<Point> getPieceMoves(int startIndex) {
		
		// Trivial cases
		List<Point> endPoints = new ArrayList<>();
		if (!Board.isValidIndex(startIndex)) {
			return endPoints;
		}
		
		// Determine possible points
		int id = get(startIndex);
		Point p = Board.toPoint(startIndex);
		addPoints(endPoints, p, id, 1);
		
		// Remove invalid points
		for (int i = 0; i < endPoints.size(); i++) {
			Point end = endPoints.get(i);
			if (get(end.x, end.y) != Board.EMPTY) {
				endPoints.remove(i--);
			}
		}
		
		return endPoints;
	}
	
	/**
	 * Gets a list of skip end-points for a given starting point.
	 *
	 * @param start the center index to look for skips around.
	 * @return the list of points such that the startClick to a given point
	 * represents a skip available.
	 * @see #getPieceSkips(int)
	 */
	public List<Point> getPieceSkips(Point start) {
		return getPieceSkips(Board.toIndex(start));
	}
	
	/**
	 * Gets a list of skip end-points for a given startClick index.
	 *
	 * @param startIndex the center index to look for skips around.
	 * @return the list of points such that the startClick to a given point
	 * represents a skip available.
	 * @see #getPieceSkips(Point)
	 */
	public List<Point> getPieceSkips(int startIndex) {
		
		// Trivial cases
		List<Point> endPoints = new ArrayList<>();
		if (!Board.isValidIndex(startIndex)) {
			return endPoints;
		}
		
		// Determine possible points
		int id = get(startIndex);
		Point p = Board.toPoint(startIndex);
		addPoints(endPoints, p, id, 2);
		
		// Remove invalid points
		for (int i = 0; i < endPoints.size(); i++) {
			
			// Check that the skip is valid
			Point end = endPoints.get(i);
			if (!isValidSkip(startIndex, Board.toIndex(end))) {
				endPoints.remove(i--);
			}
		}
		
		
		
		return endPoints;
	}
	
	/**
	 * Checks if a skip is valid.
	 *
	 * @param startIndex the startClick index of the skip.
	 * @param endIndex   the end index of the skip.
	 * @return true if and only if the skip can be performed.
	 */
	public boolean isValidSkip(int startIndex, int endIndex) {
		
		// Check that end is empty
		if (get(endIndex) != Board.EMPTY) {
			return false;
		}
		
		// Check that middle is enemy
		int id = get(startIndex);
		int midID = get(Board.toIndex(Board.middle(startIndex, endIndex)));
		
		//Check if starting e middle position are valid and not empty
		if (id == Board.INVALID || id == Board.EMPTY) return false;
		if (midID == Board.INVALID || midID == Board.EMPTY) return false;
		
		//Check that midID is an enemy for id
		if ((id == Board.WHITE_KING || id == Board.WHITE_CHECKER) && (midID == Board.WHITE_KING || midID == Board.WHITE_CHECKER))
			return false;
		if ((id == Board.BLACK_KING || id == Board.BLACK_CHECKER) && (midID == Board.BLACK_KING || midID == Board.BLACK_CHECKER))
			return false;
		
		//Check that skip is not performed by a normal checkers versus a king
		return (id != Board.WHITE_CHECKER || midID != Board.BLACK_KING) && (id != Board.BLACK_CHECKER || midID != Board.WHITE_KING);
		
	}
	
	/**
	 * Adds points that could potentially result in moves/skips.
	 *
	 * @param points the list of points to add to.
	 * @param p      the center point.
	 * @param id     the ID at the center point.
	 * @param delta  the amount to add/subtract.
	 */
	public static void addPoints(List<Point> points, Point p, int id, int delta) {
		
		// Add points moving down
		boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		if (isKing || id == Board.BLACK_CHECKER) {
			points.add(new Point(p.x + delta, p.y + delta));
			points.add(new Point(p.x - delta, p.y + delta));
		}
		
		// Add points moving up
		if (isKing || id == Board.WHITE_CHECKER) {
			points.add(new Point(p.x + delta, p.y - delta));
			points.add(new Point(p.x - delta, p.y - delta));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder obj = new StringBuilder(getClass().getName() + "[");
		for (int i = 0; i < 31; i ++) {
			obj.append(get(i)).append(", ");
		}
		obj.append(get(31));
		
		return obj + "]";
	}
	
	public int getRows() {
		return nRows;
	}
	
	public int getCols() {
		return nCols;
	}
}
