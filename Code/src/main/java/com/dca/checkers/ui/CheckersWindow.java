
package com.dca.checkers.ui;

import com.dca.checkers.model.GameManager;
import com.dca.checkers.model.GameState;
import com.dca.checkers.model.Player;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code CheckersWindow} class is responsible for managing a window. This
 * window contains a game of checkers and also options to change the settings
 * of the game with an {@link OptionPanel}.
 */
public class CheckersWindow extends JFrame {

	private static final long serialVersionUID = 8782122389400590079L;
	
	/** The default width for the checkers window. */
	public static final int DEFAULT_WIDTH = 500;
	
	/** The default height for the checkers window. */
	public static final int DEFAULT_HEIGHT = 825;
	
	/** The default title for the checkers window. */
	public static final String DEFAULT_TITLE = "Checkers";
	
	/** The checker board component playing the updatable game. */
	private CheckerBoard board;
	
	/**
	 * Reference to the game manager
	 */
	private GameManager gameManager;
	
	/**
	 * Reference to the option panel
	 */
	private OptionPanel opts;
	
	
	public CheckersWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE);
	}
	
	public CheckersWindow(int width, int height, String title) {
		
		// Setup the window
		super(title);
		super.setSize(width, height);
		super.setLocationByPlatform(true);
		
		// Setup the components
		GameState startState = new GameState();
		JPanel layout = new JPanel(new BorderLayout());
		this.opts = new OptionPanel(this);
		this.board = new CheckerBoard(this, startState, opts.getTilesIdVisibility(), opts.getShowMovablePieces(), opts.getShowNextMoves());
		layout.add(board, BorderLayout.CENTER);
		layout.add(opts, BorderLayout.SOUTH);
		layout.setBackground(new Color(231, 187, 134));
		this.add(layout);
		gameManager = new GameManager(startState, board, opts);
		gameManager.start();
	}
	
	/**
	 * Updates the type of player that is being used for player 1.
	 * 
	 * @param player1	the new player instance to control player 1.
	 */
	public void setPlayer1(Player player1) {
		System.out.println("Requested set of player 1.");
		gameManager.setPlayer1(player1);
	}
	
	/**
	 * Updates the type of player that is being used for player 2.
	 * 
	 * @param player2	the new player instance to control player 2.
	 */
	public void setPlayer2(Player player2) {
		System.out.println("Requested set of player 2.");
		gameManager.setPlayer2(player2);
	}
	
	/**
	 * Handle a click over the game board.
	 *
	 * @param sel the select point on the game board.
	 */
	public void clickOnBoard(Point sel) {
		System.out.println("Requested click request.");
		gameManager.handleBoardClick(sel);
	}
	
	/**
	 * Set tiles id visibility
	 */
	public void setTileIdVisibiliy(boolean isVisible) {
		board.setTileIdVisibiliy(isVisible);
	}
	
	/**
	 * Resets the game of checkers in the window.
	 */
	public void resetClick() {
		gameManager.resetClick();
	}
	
	/**
	 * Start the game
	 */
	public void startClick() {
		gameManager.startClick();
	}
	
	/**
	 * Resume the paused game
	 */
	public void resumeClick() {
		gameManager.resumeClick();
	}
	
	/**
	 * Pause the current game
	 */
	public void pauseClick() {
		gameManager.pauseClick();
	}
	
	/**
	 * Show (show == true) or hide (show == false) pieces that can be moved.
	 */
	public void setShowMovablePieces(boolean show) {
		board.setShowMovablePieces(show);
	}
	
	/**
	 * Show (show == true) or hide (show == false) next moves of the selected piece.
	 */
	public void setShowNextMoves(boolean show) {
		board.setShowNextMoves(show);
	}
	
}
