

package com.dca.checkers.ui;

import com.dca.checkers.model.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The {@code CheckerBoard} class is a graphical user interface component that
 * is capable of drawing any checkers gameState state. It also handles player turns.
 * For human players, this means interacting with and selecting tiles on the
 * checker board. For non-human players, this means using the logic implemented
 * by the specified player object itself is used.
 */
public class CheckerBoard extends JButton {

	private static final long serialVersionUID = -6014690893709316364L;
	
	/** The number of pixels of padding between this component's border and the
	 * actual checker board that is drawn. */
	private static final int PADDING = 16;
	
	/**
	 * The gameState of checkers that is being played on this component.
	 */
	private GameState gameState;
	
	/** The window containing this checker board UI component. */
	private CheckersWindow window;
	
	/** The last point that the current player selected on the checker board. */
	private Point selected;
	
	/** The flag to determine if the selected tile is valid for the current user */
	private boolean selectionValid;
	
	/** The colour of the light tiles (by default, this is white). */
	private Color lightTile;
	
	/**
	 * The colour of the tile id label.
	 */
	private Color tileIdColor;

	/** The colour of the dark tiles (by default, this is black). */
	private Color darkTile;
	
	/**
	 * Tells if the tiles id must be shown
	 */
	private boolean showTilesId;
	
	/**
	 * Console used to comunicate with the user.
	 */
	private TextArea txtConsole;
	
	public CheckerBoard(CheckersWindow window, GameState gameState, boolean showTilesId) {
		
		// Setup the component
		super.setBorderPainted(false);
		super.setFocusPainted(false);
		super.setContentAreaFilled(false);
		super.setBackground(Color.LIGHT_GRAY);
		this.addActionListener(new ClickListener());
		
		// Setup the board settings
		this.lightTile = new Color(254, 234, 184);
		this.darkTile = new Color(79, 124, 38);
		tileIdColor = new Color(61, 118, 46);
		this.window = window;
		this.showTilesId = showTilesId;
		//Setup game
		this.gameState = (gameState == null) ? new GameState() : gameState;
	}
	
	/**
	 * Draws the current checkers gameState state.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GameState gameState = this.gameState.copy();
		
		// Perform calculations
		final int BOX_PADDING = 8;
		final int W = getWidth(), H = getHeight();
		final int DIM = W < H ? W : H, BOX_SIZE = (DIM - 2 * PADDING) / 8;
		final int OFFSET_X = (W - BOX_SIZE * 8) / 2 + 5;
		final int OFFSET_Y = (H - BOX_SIZE * 8) / 2 +5;
		final int CHECKER_SIZE = Math.max(0, BOX_SIZE - 2 * BOX_PADDING);
		
		// Draw checker board
		g.setColor(Color.BLACK);
		g.drawRect(OFFSET_X - 1, OFFSET_Y - 1, BOX_SIZE * 8 + 1, BOX_SIZE * 8 + 1);
		g.setColor(lightTile);
		g.fillRect(OFFSET_X, OFFSET_Y, BOX_SIZE * 8, BOX_SIZE * 8);
		g.setColor(darkTile);
		for (int y = 0; y < 8; y ++) {
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				g.fillRect(OFFSET_X + x * BOX_SIZE, OFFSET_Y + y * BOX_SIZE,
						BOX_SIZE, BOX_SIZE);
			}
		}
		
		// Highlight the selected tile if valid
		if (Board.isValidPoint(selected)) {
			g.setColor(selectionValid? Color.GREEN : Color.RED);
			g.fillRect(OFFSET_X + selected.x * BOX_SIZE,
					OFFSET_Y + selected.y * BOX_SIZE,
					BOX_SIZE, BOX_SIZE);
		}
		
		// Draw the checkers
		int balckCount = 0;
		Board b = gameState.getBoard();
		for (int y = 0; y < 8; y ++) {
			int cy = OFFSET_Y + y * BOX_SIZE + BOX_PADDING;
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				int id = b.get(x, y);
				int cx = OFFSET_X + x * BOX_SIZE + BOX_PADDING;
				
				//Set tile id
				if (showTilesId) {
					g.setColor(lightTile);
					g.drawString(balckCount + "", cx - 7, cy + 2);
					balckCount++;
				}
				
				// Empty, just skip
				if (id == Board.EMPTY) {
					continue;
				}
				
				// Black checker
				if (id == Board.BLACK_CHECKER) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.BLACK);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Black king
				else if (id == Board.BLACK_KING) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.BLACK);
					g.fillOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// White checker
				else if (id == Board.WHITE_CHECKER) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.WHITE);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// White king
				else if (id == Board.WHITE_KING) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.WHITE);
					g.fillOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Any king (add some extra highlights)
				if (id == Board.BLACK_KING || id == Board.WHITE_KING) {
					g.setColor(new Color(255, 63, 43));
					g.drawOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
					g.drawOval(cx + 1, cy, CHECKER_SIZE - 4, CHECKER_SIZE - 4);
					g.drawString("K",cx+10, cy+15);
				}
			}
		}
		
		// Draw the player turn sign
		String msg = gameState.isP1Turn() ? "Player 1's turn" : "Player 2's turn";
		int width = g.getFontMetrics().stringWidth(msg);
		Color back = gameState.isP1Turn() ? Color.BLACK : Color.WHITE;
		Color front = gameState.isP1Turn() ? Color.WHITE : Color.BLACK;
		g.setColor(back);
		g.fillRect(W / 2 - width / 2 - 5, OFFSET_Y - 17, width + 10, 15);
		g.setColor(front);
		g.drawString(msg, W / 2 - width / 2, OFFSET_Y - 5);
		
		// Draw a gameState over sign
		if (gameState.isGameOver()) {
			MatchResult result = gameState.getResult();
			g.setFont(new Font("Arial", Font.BOLD, 20));
			switch (result) {
				case P1_WIN:
					msg = "Player 1 WIN!";
					break;
				case P2_WIN:
					msg = "Player 2 WIN!";
					break;
				case DRAW:
					msg = "DRAW!";
					break;
				default:
					msg = "UNKOWN RESULT";
			}
			
			
			width = g.getFontMetrics().stringWidth(msg);
			g.setColor(new Color(240, 240, 255));
			g.fillRoundRect(W / 2 - width / 2 - 5,
					OFFSET_Y + BOX_SIZE * 4 - 16,
					width + 10, 30, 10, 10);
			g.setColor(Color.RED);
			g.drawString(msg, W / 2 - width / 2, OFFSET_Y + BOX_SIZE * 4 + 7);
		}
	}
	
	/**
	 * Cancel last selection (if any).
	 */
	public void cancelLastSelection() {
		selected = null;
	}
	
	/**
	 * Cancel last selection (if any).
	 */
	public Point getLastSelection() {
		return selected;
	}
	
	/**
	 * Cancel last selection (if any).
	 */
	public void setLastSelection(Point p) {
		selected = p;
	}
	
	/**
	 * Set tiles id visibility
	 */
	public void setTileIdVisibiliy(boolean isVisible) {
		showTilesId = isVisible;
		repaint();
	}
	
	/**
	 * Set last selection as valid (if any).
	 */
	public void setLastSelectionValid(boolean selectionValid) {
		this.selectionValid = selectionValid;
	}
	
	/**
	 * The {@code ClickListener} class is responsible for responding to click
	 * events on the checker board component. It uses the coordinates of the
	 * mouse relative to the location of the checker board component.
	 */
	private class ClickListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// Get the new mouse coordinates and handle the click
			Point p = CheckerBoard.this.getMousePosition();
			if (p != null) {
				int x = p.x;
				int y = p.y;
				// Determine what square (if any) was selected
				final int W = getWidth(), H = getHeight();
				final int DIM = W < H ? W : H, BOX_SIZE = (DIM - 2 * PADDING) / 8;
				final int OFFSET_X = (W - BOX_SIZE * 8) / 2;
				final int OFFSET_Y = (H - BOX_SIZE * 8) / 2;
				x = (x - OFFSET_X) / BOX_SIZE;
				y = (y - OFFSET_Y) / BOX_SIZE;
				Point sel = new Point(x, y);
				window.clickOnBoard(sel);
			}
		}
	}
}
