package com.dca.checkers.model;

import javafx.geometry.Point2D;
import java.util.*;

/**
 * The {@code Board} class represents a checker board state.
 * A checker board usually has one of the following standard layout:
 * <ul>
 *     <li>8 x 8 (64) tiles</li>
 *     <li>10 x 10 (64) tiles</li>
 *     <li>12 x 12 (144) tiles</li>
 * </ul>
 * However this class doesn't enforce to use any particular layout. Is up to
 * the client to define the desired layout.
 * The board is optimized to use as little memory space as possible, therefore it only uses
 * 3 bit to represent the state of a tile. This makes fast and efficient to copy
 * the board state using {@link #clone()}.
 *
 * <p>
 * This class uses integers to represent the state of each tile and
 * specifically uses these constants IDs: {@link #EMPTY},
 * {@link #BLACK_CHECKER}, {@link #WHITE_CHECKER}, {@link #BLACK_KING},
 * {@link #WHITE_KING}.
 * <p>
 * Tile states can be retrieved through {@link #get(int)} and
 * {@link #get(int, int)}. Tile states can be set through
 * {@link #set(int, int)} and {@link #set(int, int, int)}.
 */
public class Board implements Cloneable {
	
	/** The ID of an empty checker board tile. */
	public static final int EMPTY = 0;
	
	/**Number of bits required to represent a tile m_state. */
	private static final int m_nBitsPerTile = 3;
	/*
 		The 3 bits used to represent a tile state have the following meaning:
			0bx2x1x0   (example: 0b110)
			
			x2 = 1 if the tile has a piece on it; otherwise 0.
			x1 = 1 if the tile has a black piece on it (valid only if x2=1);
                   otherwise 0 means it has a white piece on it (valid only if x2=1)
			x0 = 1 if the tile has a a king piece on it (valid only if x2=1).
	*/
	
	/** The ID of a white checker in the checker board. */
	public static final int BLACK_CHECKER = 0b110; //6
	
	/** The ID of a white checker in the checker board. */
	public static final int WHITE_CHECKER = 0b100; //4
	
	/** The ID of a black checker that is also a king. */
	public static final int BLACK_KING = 0b111; //7
	
	/** The ID of a white checker that is also a king. */
	public static final int WHITE_KING = 0b101; //5
	
	private static final Set<Integer> m_IDs = new HashSet(Arrays.asList(EMPTY, BLACK_CHECKER, WHITE_CHECKER, BLACK_KING, WHITE_KING));
	
	/** The current state of the board. */
	private int[] m_state;
	
	/** Number of rows of the board.*/
	private int m_rows;

	/** Number of cols of the board.*/
	private int m_cols;
	
	/**
	 * Create a board using p_board matrix.
	 * @param p_board the matrix to use to build the board, thus the number of elements for each rows must be the same.
	 *                Furthermore, each element can have only one of the following values: {@link #EMPTY},
	 *                {@link #BLACK_CHECKER}, {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}.
	 * Use {@link BoardFactory} methods to create pre-defined board games.
	 * @throws NullPointerException if p_board == null
	 * @throws IllegalArgumentException if p_board.length == 0
	 * @throws IllegalArgumentException if p_board[0].length == 0
	 * @throws IllegalArgumentException if Exist i > 0. p_board[0].length != p_board[i].length
	 */
	Board(int[][] p_board) throws NullPointerException, IllegalArgumentException {
		setState(p_board);
	}
	
	/**
	 * Check if p_board has rows of the same length.
	 * @param p_board the array to check.
	 * @return true if array is good according to the check; false otherwise.
	 */
	private static boolean checkArraySameRowLength(int [][] p_board) {
		for (int i = 1; i < p_board.length; i++)
			if(p_board[i].length !=  p_board[0].length) return false;
		return true;
	}
	
	/**
	 * Check if p_board elements assumes only the following values: {@link #EMPTY},
	 * {@link #BLACK_CHECKER}, {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}.
	 * @param p_board the array to check.
	 * @return true if array is good according to the check; false otherwise.
	 */
	private static boolean checkArrayValidValues(int [][] p_board, Set p_values) {
		for (int i = 1; i < p_board.length; i++) {
			for (int j = 0; j < p_board[j].length; j++)
				if (!p_values.contains(p_board[i][j])) return false;
		}
		return true;
	}
	
	/**
	 * Set current board state using p_board matrix.
	 * @param p_board the matrix to use to build the board, thus the number of elements for each rows must be the same.
	 *                Furthermore, each element can have only one of the following values: {@link #EMPTY},
	 *                {@link #BLACK_CHECKER}, {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}.
	 * Use {@link BoardFactory} methods to create pre-defined board games.
	 * @throws NullPointerException if p_board == null
	 * @throws IllegalArgumentException if p_board.length == 0
	 * @throws IllegalArgumentException if p_board[0].length == 0
	 * @throws IllegalArgumentException if Exist i > 0. p_board[0].length != p_board[i].length
	 */
	public void setState(int [][] p_board) throws NullPointerException, IllegalArgumentException{
		if(p_board == null) throw new NullPointerException("p_board == null");
		if(p_board.length == 0) throw new IllegalArgumentException("p_board.length == 0");
		if(p_board[0].length == 0) throw new IllegalArgumentException("p_board[0].length == 0");
		if(!checkArraySameRowLength(p_board))
			throw new IllegalArgumentException("p_board must have rows of the same length.");
		if(!checkArrayValidValues(p_board, m_IDs))
			throw new IllegalArgumentException("p_board must have rows of the same length.");
		
		m_rows = p_board.length;
		m_cols = p_board[0].length;
		
		double nTiles = m_rows * m_cols; //number of tiles required
		double bitsPerInt = Integer.BYTES * 8;
		int n_int = (int) Math.ceil( (nTiles * m_nBitsPerTile)/bitsPerInt);
		//Init th board as defined by p_board array.
		m_state = new int[n_int];
		for (int i = 0; i < m_rows; i++) {
			for (int j = 0; j < m_cols; j++)
				set(i,j,p_board[i][j]);
		}
	}
	
	/** Gets the number of rows of the board.
	 * @return number of rows
	 */
	public int getRows() {
		return m_rows;
	}
	
	/** Gets the number of columns of the board.
	 * @return number of columns.
	 */
	public int getCols() {
		return m_cols;
	}
	
	/**
	 * Gets the number of tiles.
	 * @return number of tiles.
	 */
	public int getNumberOfTiles(){
		return m_rows * m_cols;
	}
	
	/**
	 * Searches through the checker board and finds tiles that match the
	 * specified ID.
	 *
	 * @param id the ID to search for.
	 * @return a list of points on the board with the specified ID. If none
	 * exist, an empty list is returned.
	 * @throws IllegalArgumentException if id is not a valid ID. Valid IDs are: {@link #EMPTY}, {@link #BLACK_CHECKER},
	 * 									 {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}
	 */
	public List<Point2D> find(int id) throws IllegalArgumentException{
		if (!m_IDs.contains(id)) throw new IllegalArgumentException("id is not valid");
		
		// Find all black tiles with matching IDs
		List<Point2D> points = new ArrayList<>();
		int nTiles = getNumberOfTiles();
		for (int i = 0; i < nTiles-1; i ++) {
			if (get(i) == id)
				points.add(toPoint(i));
		}
		
		return points;
	}
	
	/**
	 * Sets the ID of a tile on the board at the specified location.
	 *
	 * @param x		the x-coordinate on the board (from 0 to {@link #getCols()} - 1 inclusive).
	 * @param y		the y-coordinate on the board (from 0 to {@link #getRows()} - 1 inclusive).
	 * @param id	the new ID to set the black tile to.
	 * @see {@link #set(int, int)}, {@link #EMPTY}, {@link #BLACK_CHECKER},
	 * {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}
	 * @throws IllegalArgumentException if x < 0 || x >= {@link #getCols()}
	 * @throws IllegalArgumentException if y < 0 || y >= {@link #getRows()}
	 */
	public void set(int x, int y, int id) throws IllegalArgumentException {
		if(x < 0 || x >= m_cols) throw new IllegalArgumentException("x < 0 || x >= getCols()");
		if(y < 0 || y >= m_rows) throw new IllegalArgumentException("y < 0 || y >= getRows()");
		set(toIndex(x, y), id);
	}
	
	/**
	 * Sets the ID of a tile on the board at the specified location.
	 *
	 * @param index	the index of the tile (from 0 to {@link #getNumberOfTiles()}-1 inclusive).
	 * @param id	the new ID to set for the tile.
	 * @throws IndexOutOfBoundsException if index is not in the following range [0,{@link #getNumberOfTiles()}-1]
	 * @throws IllegalArgumentException if id is not a valid ID. Valid IDs are: {@link #EMPTY}, {@link #BLACK_CHECKER},
	 * 									 {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}
	 */
	public void set(int index, int id) throws IndexOutOfBoundsException, IllegalArgumentException{
		
		// Out of range
		if (!isValidIndex(index))
			throw new IndexOutOfBoundsException("index is not in the following range [0, getNumberOfTiles()-1]");
		
		// Invalid ID
		if (!m_IDs.contains(id)) throw new IllegalArgumentException("id is not valid");
		
		
		// Set the m_state bits
		for (int i = 0; i < m_state.length; i ++) {
			//TODO: understand this line
			boolean set = ((1 << (m_state.length - i - 1)) & id) != 0;
			this.m_state[i] = setBit(m_state[i], index, set);
		}
	}
	
	/**
	 * Gets the ID corresponding to the specified point on the checker board.
	 *
	 * @param x	the x-coordinate on the board (from 0 to {@link #getCols()} - 1 inclusive).
	 * @param y	the y-coordinate on the board (from 0 to {@link #getRows()} - 1 inclusive).
	 * @return the ID at the specified location.
	 * @see {@link #get(int)}, {@link #set(int, int)},
	 * {@link #set(int, int, int)}
	 * @throws IllegalArgumentException if x < 0 || x >= {@link #getCols()}
	 * @throws IllegalArgumentException if y < 0 || y >= {@link #getRows()}
	 */
	public int get(int x, int y) throws IllegalArgumentException {
		if(x < 0 || x >= m_cols) throw new IllegalArgumentException("x < 0 || x >= getCols()");
		if(y < 0 || y >= m_rows) throw new IllegalArgumentException("y < 0 || y >= getRows()");
		return get(toIndex(x, y));
	}
	
	/**
	 * Gets the ID corresponding to the specified point on the checker board.
	 *
	 * @param index	the index of the  tile (from 0 to {@link #getNumberOfTiles()}-1 inclusive).
	 * @return the ID at the specified location.
	 * @see {@link #get(int, int)}, {@link #set(int, int)},
	 * {@link #set(int, int, int)}
	 * @throws IllegalArgumentException if index < 0 || index >= {@link #getNumberOfTiles()}
	 */
	public int get(int index) throws IllegalArgumentException {
		if (!isValidIndex(index)) throw new IllegalArgumentException("index < 0 || index >= getNumberOfTiles()");
		
		int res = 0;
		//TODO: understand folowing lines
		for (int i = 0; i < m_state.length ; i++) {
			res += getBit(m_state[i], index) * Math.pow(2, m_state.length - i -1);
		}
		
		return res;
	}
	
	/**
	 * Converts a tile index (0 to {@link #getNumberOfTiles() - 1} inclusive) to an (x, y) point, such
	 * that index 0 is (0, 0), index 1 is (1, 0), ... index {@link #getNumberOfTiles()}-1 is
	 * ({@link #getCols()}-1, {@link #getRows()}-1).
	 *
	 * @param index	the index of the tile to convert to a point.
	 * @return the (x, y) point corresponding to the tile index.
	 * @see {@link #toIndex(double, double)}, {@link #toIndex(Point2D)}
	 * @throws IllegalArgumentException if index < 0 || index >= {@link #getNumberOfTiles()}
	 */
	public Point2D toPoint(int index) throws IllegalArgumentException {
		if (!isValidIndex(index)) throw new IllegalArgumentException("index < 0 || index >= getNumberOfTiles()");
		
		int x =  index / m_cols;
		int y = index % m_cols;
		return new Point2D(x, y);
	}
	
	/**
	 * Converts a point to an index of a tile on the checker board, such
	 * that (0, 0) is index 0, (0, 1) is index 1, ... ({@link #getCols()} -1, {@link #getRows()} -1) is index {@link #getNumberOfTiles()}-1.
	 *
	 * @param x	the x-coordinate on the board (from 0 to {@link #getCols()}-1 inclusive).
	 * @param y	the y-coordinate on the board (from 0 to {@link #getRows()}-1 inclusive).
	 * @return the index of the tile.
	 * @see {@link #toIndex(Point2D)}, {@link #toPoint(int)}
	 * @throws IllegalArgumentException if x < 0 || x >= {@link #getCols()}
	 * @throws IllegalArgumentException if y < 0 || y >= {@link #getRows()}
	 */
	public int toIndex(double x, double y) throws IllegalArgumentException{
		if(x < 0 || x >= m_cols) throw new IllegalArgumentException("x < 0 || x >= getCols()");
		if(y < 0 || y >= m_rows) throw new IllegalArgumentException("y < 0 || y >= getRows()");
		
		return (int)(y * m_cols + x);
	}
	
	/**
	 * Converts a point to an index of a tile on the checker board, such
	 * that (0, 0) is index 0, (0, 1) is index 1, ... ({@link #getCols()} -1, {@link #getRows()} -1) is index {@link #getNumberOfTiles()}-1.
	 *
	 * @param p	the point to convert to an index.
	 * @return the index of the tile.
	 * @see {@link #toIndex(double, double)}, {@link #toPoint(int)}
	 * @throws IllegalArgumentException if p.getX() < 0 || p.getX() >= {@link #getCols()}
	 * @throws IllegalArgumentException if p.getY() < 0 || p.getY() >= {@link #getRows()}
	 */
	public int toIndex(Point2D p) throws IllegalArgumentException {
		if(p.getX() < 0 || p.getX() >= m_cols) throw new IllegalArgumentException("p.getX() < 0 || p.getX() >= getCols()");
		if(p.getY() < 0 || p.getY() >= m_rows) throw new IllegalArgumentException("p.getY() < 0 || p.getY() >= getRows()");
		
		return toIndex(p.getX(), p.getY());
	}
	
	/**
	 * Sets or clears the specified bit in the target value and returns
	 * the updated value.
	 *
	 * @param target	the target value to update.
	 * @param bit		the bit to update (from 0 to (Integer.BYTES * 8)-1 inclusive).
	 * @param set		true to set the bit, false to clear the bit.
	 * @return the updated target value with the bit set or cleared.
	 * @see {@link #getBit(int, int)}
	 * @throws IllegalArgumentException if bit < 0 || bit > (Integer.BYTES * 8)-1
	 */
	private static int setBit(int target, int bit, boolean set) throws IllegalArgumentException {
		//Invalid bit position for target int number
		if (bit < 0 || bit > (Integer.BYTES * 8)-1)
			throw new IllegalArgumentException("bit < 0 || bit > (Integer.BYTES * 8)-1");
		
		if (set) {// Set the bit
			target |= (1 << bit);
		} else {// Clear the bit
			target &= (~(1 << bit));
		}
		
		return target;
	}
	
	/**
	 * Gets the state of a bit and determines if it is set (1) or not (0).
	 *
	 * @param target	the target value to get the bit from.
	 * @param bit		the bit to get (from 0 to (Integer.BYTES * 8)-1 inclusive).
	 * @return 1 if and only if the specified bit is set, 0 otherwise.
	 * @see {@link #setBit(int, int, boolean)}
	 * @throws IllegalArgumentException if bit < 0 || bit > (Integer.BYTES * 8)-1

	 */
	private static int getBit(int target, int bit) throws IllegalArgumentException {
		
		// Out of range
		if (bit < 0 || bit > (Integer.BYTES * 8)-1)
			throw new IllegalArgumentException("bit < 0 || bit > (Integer.BYTES * 8)-1");
		
		return (target & (1 << bit)) != 0? 1 : 0;
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 *
	 * @param p1	the first point of a tile on the checker board.
	 * @param p2	the second point of a tile on the checker board.
	 * @return the middle point between two points.
	 * @see {@link #middle(int, int)}, {@link #middle(double, double, double, double)}
	 * @throws NullPointerException if p1 == null || p2 == null
	 * @throws NullPointerException if p1 == null || p2 == null
	 */
	public Point2D middle(Point2D p1, Point2D p2) throws NullPointerException, IllegalArgumentException {
		
		// A point isn't initialized
		if (p1 == null || p2 == null) throw new NullPointerException("A point isn't initialized.");
		return middle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 *
	 * @param index1	the index of the first point (from 0 to {@link #getNumberOfTiles()} -1 inclusive).
	 * @param index2	the index of the second point (from 0 to {@link #getNumberOfTiles()} -1 inclusive).
	 * @return the middle point between two points.
	 * @see {@link #middle(Point2D, Point2D)}, {@link #middle(double, double, double, double)}
	 * @throws IllegalArgumentException if !isValidIndex(index1) || !isValidIndex(index2)
	 */
	public Point2D middle(int index1, int index2) throws IllegalArgumentException {
		if(!isValidIndex(index1) || !isValidIndex(index2))
			throw new IllegalArgumentException("An index is not valid.");
		return middle(toPoint(index1), toPoint(index2));
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 *
	 * @param x1	the x-coordinate of the first point.
	 * @param y1	the y-coordinate of the first point.
	 * @param x2	the x-coordinate of the second point.
	 * @param y2	the y-coordinate of the second point.
	 * @return the middle point between two points.
	 * @see {@link #middle(int, int)}, {@link #middle(Point2D, Point2D)}
	 */
	public Point2D middle(double x1, double y1, double x2, double y2) throws IllegalArgumentException {
		
		if(!isValidIndex(toIndex(x1,y1)) || !isValidIndex(toIndex(x2,y2)))
			throw new IllegalArgumentException("A point isn't valid.");
		
		double dx = x2 - x1, dy = y2 - y1;
		if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2)
			throw new IllegalArgumentException("Points are not at required distance.");

		return new Point2D(x1 + dx / 2, y1 + dy / 2);
	}
	
	/**
	 * Checks if the a tile is white (true) or not (false).
	 * @param index the index of tiles to check.
	 * @return true if it is white; false if it is black
	 * @throws IllegalArgumentException if !isValidIndex(index)
	 */
	public boolean isWhite(int index) throws IllegalArgumentException {
		if(!isValidIndex(index))
			throw new IllegalArgumentException("index is not valid.");
		return isWhite(toPoint(index));
	}
	
	/**
	 * Checks if the a tile is white (true) or not (false).
	 * @param p the coordinates to the tile to check.
	 * @return true if it is white; false if it is black.
	 * @throws IllegalArgumentException if !isValidPoint(index)
	 */
	public boolean isWhite(Point2D p) throws IllegalArgumentException {
		if(!isValidPoint(p))
			throw new IllegalArgumentException("p is not valid.");
		return isWhite(p.getX(), p.getY());
	}
	
	/**
	 * Checks if the a tile is white (true) or not (false).
	 * @param x	the x-coordinate of the point.
	 * @param y	the y-coordinate of the point.
	 * @return true if it is white; false if it is black.
	 * @throws IllegalArgumentException if !isValidPoint(index)
	 */
	public boolean isWhite(double x, double y) throws IllegalArgumentException{
		if(!isValidIndex(toIndex(x,y)))
			throw new IllegalArgumentException("point coordinates are not valid");
		return x % 2 == y % 2;
	}
	
	/**
	 * Checks if an index corresponds to a black tile on the checker board.
	 *
	 * @param testIndex	the index to check.
	 * @return true if and only if the index is between 0 and 31 inclusive.
	 */
	public boolean isValidIndex(int testIndex) {
		return testIndex >= 0 && testIndex < getNumberOfTiles();
	}
	
	/**
	 * Checks if a point corresponds to a black tile on the checker board.
	 *
	 * @param testPoint	the point to check.
	 * @return true if and only if the point is on the board, specifically on
	 * a black tile.
	 */
	public static boolean isValidPoint(Point2D testPoint) {
		
		if (testPoint == null) {
			return false;
		}
		
		// Check that it is on the board
		final double x = testPoint.getX(), y = testPoint.getY();
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		
		// Check that it is on a black tile
		if (x % 2 == y % 2) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Deep copy of this so any updates over the clone doesn't affect the original board.
	 * @return the copy of this.
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException {
		Board b = (Board) super.clone();
		b.m_state = this.m_state.clone();
		return b;
	}
	
	@Override
	public String toString() {
		String obj = getClass().getName() + "[";
		int nTiles = getNumberOfTiles();
		for (int i = 0; i < nTiles -1 ; i ++) {
			obj += get(i) + ", ";
		}
		obj += get(nTiles -1);
		
		return obj + "]";
	}
}
