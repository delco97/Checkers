/* Name: OptionPanel
 * Author: Devon McGrath
 * Description: This class is a user interface to interact with a checkers
 * game window.
 */

package com.dca.checkers.ui;

import com.dca.checkers.ai.AIMinMax;
import com.dca.checkers.ai.AIRandomPlayer;
import com.dca.checkers.model.HumanPlayer;
import com.dca.checkers.model.Player;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code OptionPanel} class provides a user interface component to control
 * options for the game of checkers being played in the window {@link CheckersWindow}.
 */
public class OptionPanel extends JPanel {
	
	private static final long serialVersionUID = -4763875452164030755L;
	
	/**
	 * The button that when clicked, starts the game.
	 */
	public JButton btnStart;
	/**
	 * The button that when clicked, reset the game.
	 */
	public JButton btnRest;
	/**
	 * The button that when clicked, restarts the game if it was previously paused.
	 */
	public JButton btnResume;
	/**
	 * The button that when clicked, pauses the game.
	 */
	public JButton btnPause;
	/**
	 * The button that when clicked, undo the last move.
	 */
	public JButton btnUndo;
	/**
	 * The button that when clicked, redo the last move.
	 */
	public JButton btnRedo;
	/**
	 * The combo box that changes what type of player player 1 is.
	 */
	public JComboBox<String> cmbPlayer1Type;
	/**
	 * The combo box that changes what type of player player 2 is.
	 */
	public JComboBox<String> cmbPlayer2Type;
	/**
	 * Flag for tiles ids visibility
	 */
	public JCheckBox chbTilesId;
	/**
	 * Flag to show piece that the current player can move
	 */
	public JCheckBox chbShowMovablePieces;
	/**
	 * Flag to show next moves of selected piece
	 */
	public JCheckBox chbShowNextMoves;
	/**
	 * Console text area used to send messages to user
	 */
	public JTextArea txtAreaConsole;
	/**
	 * Slider to set delay of AI moves
	 */
	public JSlider sliderDelay;
	/**
	 * Used to show delay value
	 */
	public JLabel labelDelayValue;
	/**
	 * The checkers window to update when an option is changed.
	 */
	private CheckersWindow window;
	
	/**
	 * Creates a new option panel for the specified checkers window.
	 *
	 * @param window the window with the game of checkers to update.
	 */
	public OptionPanel(CheckersWindow window) {
		super(new GridLayout(0, 1));
		
		this.window = window;
		
		// Initialize the components
		final String[] playerTypeOpts = {"Human", "AI - Random", "AI - MinMax"};
		this.sliderDelay = new JSlider(JSlider.HORIZONTAL, 0, 2000, 1000);
		this.labelDelayValue = new JLabel(sliderDelay.getValue() + "");
		this.btnStart = new JButton("Start");
		this.btnResume = new JButton("Resume");
		this.btnPause = new JButton("Pause");
		this.btnRest = new JButton("Reset");
		this.btnUndo = new JButton("Undo");
		this.btnRedo = new JButton("Redo");
		this.cmbPlayer1Type = new JComboBox<>(playerTypeOpts);
		this.cmbPlayer2Type = new JComboBox<>(playerTypeOpts);
		this.chbTilesId = new JCheckBox("Show tiles IDs", true);
		this.chbShowMovablePieces = new JCheckBox("Show movable pieces", true);
		this.chbShowNextMoves = new JCheckBox("Show next moves", true);
		this.txtAreaConsole = new JTextArea();
		this.txtAreaConsole.setEditable(false);
		this.txtAreaConsole.setRows(3);
		this.btnStart.addActionListener(e -> window.startClick());
		this.btnResume.addActionListener(e -> window.resumeClick());
		this.btnPause.addActionListener(e -> window.pauseClick());
		this.btnRest.addActionListener(e -> window.resetClick());
		this.btnUndo.addActionListener(e -> window.undoMove());
		this.btnRedo.addActionListener(e -> window.redoMove());
		this.sliderDelay.addChangeListener(e -> {
			labelDelayValue.setText(sliderDelay.getValue() + "");
			window.setDelay(sliderDelay.getValue());
		});
		this.cmbPlayer1Type.addActionListener(e -> window.setPlayer1(getPlayer(cmbPlayer1Type)));
		this.cmbPlayer2Type.addActionListener(e -> window.setPlayer2(getPlayer(cmbPlayer2Type)));
		this.chbTilesId.addActionListener(e -> window.setTileIdVisibility(chbTilesId.isSelected()));
		this.chbShowMovablePieces.addActionListener(e -> window.setShowMovablePieces(chbShowMovablePieces.isSelected()));
		this.chbShowNextMoves.addActionListener(e -> window.setShowNextMoves(chbShowNextMoves.isSelected()));
		JScrollPane pan0 = new JScrollPane(txtAreaConsole);
		new SmartScroller(pan0);
		JPanel pan1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pan7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		pan0.setBackground(new Color(214, 34, 28));
		pan1.setBackground(new Color(231, 187, 134));
		pan2.setBackground(new Color(231, 187, 134));
		pan3.setBackground(new Color(231, 187, 134));
		pan4.setBackground(new Color(231, 187, 134));
		pan5.setBackground(new Color(231, 187, 134));
		pan6.setBackground(new Color(231, 187, 134));
		pan7.setBackground(new Color(231, 187, 134));
		
		
		// Add components to the layout
		JLabel txtDelay = new JLabel("AI Delay (ms): ");
		pan1.add(txtDelay);
		pan1.add(labelDelayValue);
		pan1.add(sliderDelay);
		
		JLabel txtP1 = new JLabel("Player 1: ");
		txtP1.setOpaque(true);
		txtP1.setBackground(Color.BLACK);
		txtP1.setForeground(Color.WHITE);
		pan2.add(txtP1);
		pan2.add(cmbPlayer1Type);
		JLabel txtP2 = new JLabel("Player 2: ");
		txtP2.setOpaque(true);
		txtP2.setBackground(Color.WHITE);
		txtP2.setForeground(Color.BLACK);
		pan3.add(txtP2);
		pan3.add(cmbPlayer2Type);
		pan4.add(btnStart);
		pan4.add(btnResume);
		pan4.add(btnPause);
		pan4.add(btnRest);
		pan4.add(btnUndo);
		pan4.add(btnRedo);
		pan5.add(chbTilesId);
		pan6.add(chbShowMovablePieces);
		pan7.add(chbShowNextMoves);
		this.add(pan0);
		this.add(pan1);
		this.add(pan2);
		this.add(pan3);
		this.add(pan4);
		this.add(pan5);
		this.add(pan6);
		this.add(pan7);
	}
	
	/**
	 * Get the type of player select for player 1.
	 * @return the player 1 object.
	 */
	public Player getPlayer1() {
		return getPlayer(cmbPlayer1Type);
	}
	
	/**
	 * Get the type of player select for player 2.
	 * @return the player 2 object.
	 */
	public Player getPlayer2() {
		return getPlayer(cmbPlayer2Type);
	}
	
	/**
	 * Gets a new instance of the type of player selected for the specified
	 * combo box.
	 *
	 * @param playerOpts the combo box with the player options.
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
	 * @return true if the flag that tells tiles id must be shown or not is checked, otherwise return false.
	 */
	public boolean getTilesIdVisibility() {
		return chbTilesId.isSelected();
	}
	
	/**
	 * Get the flag that tells if movable pieces must be shown.
	 * @return true if the flag that tells if movable pieces must be shown is checked, otherwise return false.
	 */
	public boolean getShowMovablePieces() {
		return chbShowMovablePieces.isSelected();
	}
	
	/**
	 * Get the flag that tells if moves of movable pieces must be shown.
	 * @return true if the flag that tells if moves of movable pieces must be shown is checked, otherwise return false.
	 */
	public boolean getShowNextMoves() {
		return chbShowNextMoves.isSelected();
	}
	
}
