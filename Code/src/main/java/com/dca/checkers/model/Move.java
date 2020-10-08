

package com.dca.checkers.model;

import java.awt.*;
import java.util.Objects;

/**
 * The {@code Move} class represents a move and contains a weight associated
 * with the move.
 */
public class Move {
	
	/** The weight corresponding to an invalid move. */
	public static final double WEIGHT_INVALID = Double.NEGATIVE_INFINITY;
	
	/**
	 * The startClick index of the move.
	 */
	private byte startIndex;
	
	/** The end index of the move. */
	private byte endIndex;
	
	/** The move type */
	private MoveType type;
	
	/** The weight associated with the move. */
	private double weight;
	
	public Move(int startIndex, int endIndex, MoveType type) {
		setStartIndex(startIndex);
		setEndIndex(endIndex);
		this.type = type;
	}
	
	public Move(Point start, Point end, MoveType type) {
		setStartIndex(Board.toIndex(start));
		setEndIndex(Board.toIndex(end));
		this.type = type;
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = (byte) startIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public void setEndIndex(int endIndex) {
		this.endIndex = (byte) endIndex;
	}
	
	public MoveType getType() { return type; }
	
	public void setType(MoveType type) { this.type = type; }
	
	public Point getStart() {
		return Board.toPoint(startIndex);
	}
	
	public void setStart(Point start) {
		setStartIndex(Board.toIndex(start));
	}
	
	public Point getEnd() {
		return Board.toPoint(endIndex);
	}
	
	public void setEnd(Point end) {
		setEndIndex(Board.toIndex(end));
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void changeWeight(double delta) {
		this.weight += delta;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Move move = (Move) o;
		return startIndex == move.startIndex && endIndex == move.endIndex;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(startIndex, endIndex);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[startIndex=" + startIndex + ", "
				+ "endIndex=" + endIndex + ", weight=" + weight + "]";
	}
}
