/* Name: OptionPanel
 * Author: Devon McGrath
 * Description: This class is a user interface to interact with a checkers
 * game window.
 */

package com.dca.checkers.ui;

import com.dca.checkers.ai.AIMinMax;
import com.dca.checkers.ai.AIStupidPlayer;
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
	
	/** The button that when clicked, restarts the game. */
	private JButton restartBtn;
	
	/** The combo box that changes what type of player player 1 is. */
	private JComboBox<String> player1Opts;

	/** The combo box that changes what type of player player 2 is. */
	private JComboBox<String> player2Opts;
	
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
		final String[] playerTypeOpts = {"Human", "AI - Empirica", "AI - MinMax"};
		this.restartBtn = new JButton("Restart");
		this.player1Opts = new JComboBox<>(playerTypeOpts);
		this.player2Opts = new JComboBox<>(playerTypeOpts);
		this.restartBtn.addActionListener(ol);
		this.player1Opts.addActionListener(ol);
		this.player2Opts.addActionListener(ol);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		top.setBackground(new Color(231, 187, 134));
		middle.setBackground(new Color(231, 187, 134));
		bottom.setBackground(new Color(231, 187, 134));
		
		
		// Add components to the layout
		top.add(restartBtn);
		JLabel txtP1 = new JLabel("Player 1: ");
		txtP1.setOpaque(true);
		txtP1.setBackground(Color.BLACK);
		txtP1.setForeground(Color.WHITE);
		middle.add(txtP1);
		middle.add(player1Opts);
		JLabel txtP2 = new JLabel("Player 2: ");
		txtP2.setOpaque(true);
		txtP2.setBackground(Color.WHITE);
		txtP2.setForeground(Color.BLACK);
		bottom.add(txtP2);
		bottom.add(player2Opts);
		this.add(top);
		this.add(middle);
		this.add(bottom);
	}

	public CheckersWindow getWindow() {
		return window;
	}

	public void setWindow(CheckersWindow window) {
		this.window = window;
	}
	
	
	/**
	 * Gets a new instance of the type of player selected for the specified
	 * combo box.
	 * 
	 * @param playerOpts	the combo box with the player options.
	 * @return a new instance of a {@link com.dca.checkers.model.Player} object that corresponds
	 * with the type of player selected.
	 */
	private static Player getPlayer(JComboBox<String> playerOpts) {
		
		Player player = new HumanPlayer();
		if (playerOpts == null) {
			return player;
		}
		
		// Determine the type
		String type = "" + playerOpts.getSelectedItem();
		if (type.equals("AI - Empirica")) {
			player = new AIStupidPlayer();
		}
		if (type.equals("AI - MinMax")) {
			player = new AIMinMax();
		}
		
		return player;
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
			if (src == restartBtn) {
				window.restart();
			} else if (src == player1Opts) {
				Player player = getPlayer(player1Opts);
				window.setPlayer1(player);
			} else if (src == player2Opts) {
				Player player = getPlayer(player2Opts);
				window.setPlayer2(player);

			}
		}
	}
}
