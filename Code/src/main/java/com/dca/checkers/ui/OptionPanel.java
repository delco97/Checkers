/* Name: OptionPanel
 * Author: Devon McGrath
 * Description: This class is a user interface to interact with a checkers
 * game window.
 */

package com.dca.checkers.ui;

import com.dca.checkers.Utilities.SmartScroller;
import com.dca.checkers.ai.AIMinMax;
import com.dca.checkers.ai.AIRandomPlayer;
import com.dca.checkers.model.HumanPlayer;
import com.dca.checkers.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The {@code OptionPanel} class provides a user interface component to control
 * options for the game of checkers being played in the window.
 */
public class OptionPanel extends JPanel {

	private static final long serialVersionUID = -4763875452164030755L;

	/** The checkers window to update when an option is changed. */
	private CheckersWindow window;
	
	/**
	 * The button that when clicked, starts the game.
	 */
	public JButton startBtn;
	
	/**
	 * The button that when clicked, reset the game.
	 */
	public JButton resetBtn;
	
	/**
	 * The button that when clicked, restarts the game if it was previously paused.
	 */
	public JButton resumeBtn;
	
	/**
	 * The button that when clicked, pauses the game.
	 */
	public JButton pauseBtn;
	
	/**
	 * The button that when clicked, undo the last move.
	 */
	public JButton undoBtn;
	
	/**
	 * The button that when clicked, redo the last move.
	 */
	public JButton redoBtn;
	
	/** The combo box that changes what type of player player 1 is. */
	public JComboBox<String> player1Opts;

	/** The combo box that changes what type of player player 2 is. */
	public JComboBox<String> player2Opts;
	
	/**
	 * Flag for tiles ids visibility
	 */
	public JCheckBox cbTilesId;
	
	/**
	 * Flag to show piece that the current player can move
	 */
	public JCheckBox cbShowMovablePieces;
	
	/**
	 * Flag to show next moves of selected piece
	 */
	public JCheckBox cbShowNextMoves;
	
	/**
	 * Console text area used to send messages to user
	 */
	public JTextArea txtConsole;
	
	
	/**
	 * Creates a new option panel for the specified checkers window.
	 * 
	 * @param window	the window with the game of checkers to update.
	 */
	public OptionPanel(CheckersWindow window) {
		super(new GridLayout(0, 1));
		
		this.window = window;
		
		// Initialize the components
		OptionListener ol = new OptionListener();
		final String[] playerTypeOpts = {"Human", "AI - Random", "AI - MinMax"};
		this.startBtn = new JButton("Start");
		this.resumeBtn = new JButton("Resume");
		this.pauseBtn = new JButton("Pause");
		this.resetBtn = new JButton("Reset");
		this.undoBtn = new JButton("Undo");
		this.redoBtn = new JButton("Redo");
		this.player1Opts = new JComboBox<>(playerTypeOpts);
		this.player2Opts = new JComboBox<>(playerTypeOpts);
		this.cbTilesId = new JCheckBox("Show tiles IDs", true);
		this.cbShowMovablePieces = new JCheckBox("Show movable pieces", true);
		this.cbShowNextMoves = new JCheckBox("Show next moves", true);
		this.txtConsole = new JTextArea();
		this.txtConsole.setEditable(false);
		this.txtConsole.setRows(3);
		this.startBtn.addActionListener(ol);
		this.resumeBtn.addActionListener(ol);
		this.pauseBtn.addActionListener(ol);
		this.resetBtn.addActionListener(ol);
		this.undoBtn.addActionListener(ol);
		this.redoBtn.addActionListener(ol);
		this.player1Opts.addActionListener(ol);
		this.player2Opts.addActionListener(ol);
		this.cbTilesId.addActionListener(ol);
		this.cbShowMovablePieces.addActionListener(ol);
		this.cbShowNextMoves.addActionListener(ol);
		JScrollPane pan1 = new JScrollPane(txtConsole);
		new SmartScroller(pan1);
		JPanel pan2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		pan1.setBackground(new Color(214, 34, 28));
		pan2.setBackground(new Color(231, 187, 134));
		pan3.setBackground(new Color(231, 187, 134));
		pan4.setBackground(new Color(231, 187, 134));
		pan5.setBackground(new Color(231, 187, 134));
		pan6.setBackground(new Color(231, 187, 134));
		pan7.setBackground(new Color(231, 187, 134));
		
		
		// Add components to the layout
		//pan1.add(txtConsole);
		JLabel txtP1 = new JLabel("Player 1: ");
		txtP1.setOpaque(true);
		txtP1.setBackground(Color.BLACK);
		txtP1.setForeground(Color.WHITE);
		pan2.add(txtP1);
		pan2.add(player1Opts);
		JLabel txtP2 = new JLabel("Player 2: ");
		txtP2.setOpaque(true);
		txtP2.setBackground(Color.WHITE);
		txtP2.setForeground(Color.BLACK);
		pan3.add(txtP2);
		pan3.add(player2Opts);
		pan4.add(startBtn);
		pan4.add(resumeBtn);
		pan4.add(pauseBtn);
		pan4.add(resetBtn);
		pan4.add(undoBtn);
		pan4.add(redoBtn);
		pan5.add(cbTilesId);
		pan6.add(cbShowMovablePieces);
		pan7.add(cbShowNextMoves);
		this.add(pan1);
		this.add(pan2);
		this.add(pan3);
		this.add(pan4);
		this.add(pan5);
		this.add(pan6);
		this.add(pan7);
	}
	
	/**
	 * Get the type of player select for player 1
	 */
	public Player getPlayer1() {
		return getPlayer(player1Opts);
	}
	
	/**
	 * Get the type of player select for player 2
	 */
	public Player getPlayer2() {
		return getPlayer(player2Opts);
	}
	
	/**
	 * Gets a new instance of the type of player selected for the specified
	 * combo box.
	 * 
	 * @param playerOpts	the combo box with the player options.
	 * @return a new instance of a {@link com.dca.checkers.model.Player} object that corresponds
	 * with the type of player selected.
	 */
	private Player getPlayer(JComboBox<String> playerOpts) {
		
		Player player = new HumanPlayer();
		if (playerOpts == null) {
			return player;
		}
		
		// Determine the type
		String type = "" + playerOpts.getSelectedItem();
		if (type.equals("AI - Random")) {
			player = new AIRandomPlayer();
		}
		if (type.equals("AI - MinMax")) {
			player = new AIMinMax();
		}
		
		return player;
	}
	
	/**
	 * Get the flag that tells tiles id must be shown or not.
	 */
	public boolean getTilesIdVisibility() {
		return cbTilesId.isSelected();
	}
	
	/**
	 * Return the console object.
	 */
	public JTextArea getConsole() {
		return txtConsole;
	}
	
	/**
	 * Get the flag that tells if movable pieces must be shown
	 */
	public boolean getShowMovablePieces() {
		return cbShowMovablePieces.isSelected();
	}
	
	/**
	 * Get the flag that tells if movable pieces must be shown
	 */
	public boolean getShowNextMoves() {
		return cbShowNextMoves.isSelected();
	}
	
	/**
	 * The {@code OptionListener} class responds to the components within the
	 * option panel when they are clicked/updated.
	 */
	private class OptionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// No window to update
			if (window == null) {
				return;
			}
			
			Object src = e.getSource();
			
			// Handle the user action
			if (src == resetBtn) {
				window.resetClick();
			} else if (src == startBtn) {
				window.startClick();
			} else if (src == resumeBtn) {
				window.resumeClick();
			}else if(src == pauseBtn){
				window.pauseClick();
			} else if (src == player1Opts) {
				Player player = getPlayer(player1Opts);
				window.setPlayer1(player);
			} else if (src == player2Opts) {
				Player player = getPlayer(player2Opts);
				window.setPlayer2(player);
			} else if (src == cbTilesId) {
				window.setTileIdVisibiliy(cbTilesId.isSelected());
			} else if (src == cbShowMovablePieces) {
				window.setShowMovablePieces(cbShowMovablePieces.isSelected());
			} else if (src == cbShowNextMoves) {
				window.setShowNextMoves(cbShowNextMoves.isSelected());
			} else if (src == undoBtn) {
				window.undoMove();
			} else if (src == redoBtn) {
				window.redoMove();
			}
		}
	}
}
