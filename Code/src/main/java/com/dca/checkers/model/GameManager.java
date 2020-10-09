package com.dca.checkers.model;

import com.dca.checkers.ui.CheckerBoard;
import com.dca.checkers.ui.OptionPanel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GameManager extends Thread {
	
	private Thread t;
	
	/**
	 * The player in control of the black checkers.
	 */
	private Player player1;
	
	/**
	 * The player in control of the white checkers.
	 */
	private Player player2;
	
	/**
	 * Board boardUI reference
	 */
	private CheckerBoard boardUI;
	
	/**
	 * Current game state managed
	 */
	private GameState gameState;
	
	/**
	 * Tells if game is paused
	 */
	private boolean isPaused;
	
	/**
	 * Tells if game is ready to start
	 */
	private boolean isReadyToStart;
	
	/**
	 * Tells if game is on going
	 */
	private boolean isOnGoing;
	
	/**
	 * Tells if game is over
	 */
	private boolean isOver;
	
	/**
	 * Option panel r
	 */
	private OptionPanel opt;
	
	/**
	 * The amount of milliseconds before a computer player takes a move.
	 */
	private static final int DELAY = 1000;
	
	/**
	 * The history of the game
	 */
	private List<GameState> history;
	
	/**
	 * Track the current game state position in history
	 */
	private int curHistoryIndex;
	
	/**
	 * Track the last valid index in history.
	 * When a some undos are performed and a new move is taken, all
	 * state saved in history are no more useful.
	 */
	private int lastIndexValid;
	
	public GameManager(GameState gameState, CheckerBoard boardUI, OptionPanel opt) {
		this.gameState = new GameState();
		this.player1 = opt.getPlayer1();
		this.player2 = opt.getPlayer2();
		this.boardUI = boardUI;
		this.gameState = gameState == null ? new GameState() : gameState;
		this.opt = opt;
		this.isPaused = false;
		this.isReadyToStart = true;
		this.isOnGoing = false;
		this.isOver = false;
		this.curHistoryIndex = 0;
		this.lastIndexValid = 0;
		this.history = new ArrayList<>();
		this.history.add(gameState.copy());
		//Set UI
		updateUI();
	}
	
	@Override
	public void run() {
		System.out.println("Running game manager thread");
		
		while (true) {
			waitStart();
			handleGameplay();
			//setUIReadyToStart();
		}
		
	}
	
	synchronized private void waitStart() {
		while (!isOnGoing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
	
	public void handleGameplay() {
		Player currentPlayer;
		while (!gameState.isGameOver()) {
			//If game is paused wait
			waitResume();
			currentPlayer = getCurrentPlayer();
			//Write to console who must take next move
			if (gameState.isP1Turn()) writeToConsole("It's Player 1's turn.");
			else writeToConsole("It's Player 2's turn.");
			//Wait only if next to move is a computer player
			if (!currentPlayer.isHuman()) {
				System.out.println("Current player is not human! Wait a bit ...");
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					System.err.println("An error occurred during sleep.\n");
					e.printStackTrace();
				}
			}
			currentPlayer.updateGame(gameState);
			waitPlayerChoice(currentPlayer);
			addHistory(gameState.copy());
			updateUI();
		}
		gameOver();
	}
	
	private void addHistory(GameState g) {
		if (curHistoryIndex < history.size()) {
			history.add(++curHistoryIndex, g);
		} else { //curHistoryIndex == history.size()
			history.add(g);
			curHistoryIndex++;
		}
		lastIndexValid = curHistoryIndex;
	}
	
	/**
	 * Redo the last move if any
	 */
	public void redo() {
		if (redoIsPossible()) {
			gameState.setGameState(history.get(++curHistoryIndex).getGameState());
			updateUI();
		}
		
	}
	
	/**
	 * Undo the last move if any
	 */
	public void undo() {
		if (undoIsPossible()) {
			gameState.setGameState(history.get(--curHistoryIndex).getGameState());
			updateUI();
		}
	}
	
	synchronized private void waitPlayerChoice(Player currentPlayer) {
		while (!currentPlayer.hasMoved()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void waitResume() {
		while (!isOnGoing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setPlayer1(Player player1) {
		System.out.println("Player 1 set.");
		this.player1 = (player1 == null) ? new HumanPlayer() : player1;
		if (gameState.isP1Turn() && !this.player1.isHuman()) {
			boardUI.cancelLastSelection();
		}
		writeToConsole("Player 1 type changed.");
		boardUI.repaint();
	}
	
	public void setPlayer2(Player player2) {
		System.out.println("Player 2 setted.");
		this.player2 = (player2 == null) ? new HumanPlayer() : player2;
		if (!gameState.isP1Turn() && !this.player2.isHuman()) {
			boardUI.cancelLastSelection();
		}
		writeToConsole("Player 2 type changed.");
		boardUI.repaint();
	}
	
	/**
	 * Return the player next to play
	 */
	public Player getCurrentPlayer() {
		if (gameState.isP1Turn()) return player1;
		else return player2;
	}
	
	/**
	 * Handles a click performed on the board. If the current
	 * player is not human, this method does nothing. Otherwise, current human player is
	 * infomed about the event.
	 *
	 * @param sel the selected point on the board.
	 */
	synchronized public void handleBoardClick(Point sel) {
		// The gameState is over or the current player isn't human
		if (!isOnGoing || gameState.isGameOver() || !getCurrentPlayer().isHuman()) {
			return;
		}
		HumanPlayer currentPlayer = (HumanPlayer) getCurrentPlayer();
		//Communicate to the current human player object the selection on the board
		currentPlayer.handleBoardClick(gameState, boardUI, sel);
		updateUI();
		notifyAll();
	}
	
	/**
	 * If boardUI is available, update it
	 */
	private void updateUI() {
		if (isPaused) setUIPaused();
		if (isReadyToStart) setUIReadyToStart();
		if (isOnGoing) setUIOnGoing();
		if (isOver) setUIOver();
		boardUI.repaint();
	}
	
	/**
	 * Request to resetClick the game
	 */
	synchronized public void resetClick() {
		writeToConsole("Board reset done.");
		this.isPaused = false;
		this.isReadyToStart = true;
		this.isOnGoing = false;
		this.isOver = false;
		this.gameState.restart();
		this.curHistoryIndex = 0;
		this.history = new ArrayList<>();
		this.history.add(gameState.copy());
		this.lastIndexValid = 0;
		updateUI();
	}
	
	/**
	 * Start the game
	 */
	synchronized public void startClick() {
		writeToConsole("Game started.");
		this.isPaused = false;
		this.isReadyToStart = false;
		this.isOnGoing = true;
		player1 = opt.getPlayer1();
		player2 = opt.getPlayer2();
		updateUI();
		notifyAll();
	}
	
	/**
	 * Resume the paused game
	 */
	synchronized public void resumeClick() {
		writeToConsole("Game resumed.");
		this.isPaused = false;
		this.isReadyToStart = false;
		this.isOnGoing = true;
		player1 = opt.getPlayer1();
		player2 = opt.getPlayer2();
		updateUI();
		notifyAll();
	}
	
	/**
	 * Pause the current game
	 */
	synchronized public void pauseClick() {
		writeToConsole("Game pausing...");
		this.isPaused = true;
		this.isReadyToStart = false;
		this.isOnGoing = false;
		//If the current player is a Human, skip the wait for his move
		Player current = getCurrentPlayer();
		if (current.isHuman()) {
			((HumanPlayer) current).skipNextMove();
		}
		notifyAll();
	}
	
	synchronized public void gameOver() {
		writeToConsole("Game over.");
		this.isPaused = false;
		this.isReadyToStart = false;
		this.isOnGoing = false;
		this.isOver = true;
		updateUI();
	}
	
	synchronized private void setUIOnGoing() {
		opt.player1Opts.setEnabled(false);
		opt.player2Opts.setEnabled(false);
		opt.startBtn.setEnabled(false);
		opt.resumeBtn.setEnabled(false);
		opt.pauseBtn.setEnabled(true);
		opt.resetBtn.setEnabled(false);
		opt.undoBtn.setEnabled(false);
		opt.redoBtn.setEnabled(false);
	}
	
	synchronized private void setUIReadyToStart() {
		writeToConsole("Press 'Start' to start a game.");
		opt.player1Opts.setEnabled(true);
		opt.player2Opts.setEnabled(true);
		opt.startBtn.setEnabled(true);
		opt.resumeBtn.setEnabled(false);
		opt.pauseBtn.setEnabled(false);
		opt.resetBtn.setEnabled(false);
		opt.undoBtn.setEnabled(false);
		opt.redoBtn.setEnabled(false);
	}
	
	synchronized private void setUIPaused() {
		writeToConsole("Game is paused.");
		opt.player1Opts.setEnabled(true);
		opt.player2Opts.setEnabled(true);
		opt.startBtn.setEnabled(false);
		opt.resumeBtn.setEnabled(true);
		opt.pauseBtn.setEnabled(false);
		opt.resetBtn.setEnabled(true);
		opt.undoBtn.setEnabled(undoIsPossible());
		opt.redoBtn.setEnabled(redoIsPossible());
	}
	
	/**
	 * Check if it's currently possible to perform an undo
	 */
	private boolean undoIsPossible() {
		return curHistoryIndex > 0;
	}
	
	/**
	 * Check if it's currently possible to perform a redo
	 */
	private boolean redoIsPossible() {
		return curHistoryIndex < lastIndexValid;
	}
	
	synchronized private void setUIOver() {
		writeToConsole("Game is over.");
		opt.player1Opts.setEnabled(true);
		opt.player2Opts.setEnabled(true);
		opt.startBtn.setEnabled(false);
		opt.resumeBtn.setEnabled(false);
		opt.pauseBtn.setEnabled(false);
		opt.resetBtn.setEnabled(true);
	}
	
	/**
	 * Write a message in console.
	 *
	 * @param msg the message to append.
	 */
	private void writeToConsole(String msg) {
		String date = new SimpleDateFormat("hh:mm:ss").format(new Date());
		opt.txtConsole.append("[" + date + "]: " + msg + "\n");
	}
	
}
