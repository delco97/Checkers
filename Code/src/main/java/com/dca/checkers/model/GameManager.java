package com.dca.checkers.model;

import com.dca.checkers.ui.CheckerBoard;
import com.dca.checkers.ui.OptionPanel;

import java.awt.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The {@code GameManager} represents a sort of referee for a Checker game.
 * It is the joint point between the UI and logic part of the application and it runs on its dedicated thread.
 */
public class GameManager extends Thread {
	
	/**
	 * Flag that tells if the current game is a simulation (true) or not (false). In other words, no UI update is
	 * performed if this flag is set o false and data is collected using System.out.println.
	 */
	private final boolean isSimulation;
	/**
	 * Reference to thread instantiated for GameManager works.
	 */
	public Thread t;
	/**
	 * Number of games to simulate between player 1 and player 2.
	 */
	private int numMatch;
	
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
	private int AIDelay = 1000;
	
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
		this.history.add(this.gameState.copy());
		this.isSimulation = false;
		this.numMatch = 0;
		//Set UI
		updateUI();
	}
	
	public GameManager(int numMatch, Player p1, Player p2) {
		this.gameState = new GameState();
		this.player1 = p1;
		this.player2 = p2;
		this.boardUI = null;
		this.opt = null;
		this.isPaused = false;
		this.isReadyToStart = false;
		this.isOnGoing = true;
		this.isOver = false;
		this.curHistoryIndex = 0;
		this.lastIndexValid = 0;
		this.history = null;
		this.isSimulation = true;
		this.numMatch = numMatch;
	}
	
	@Override
	public void run() {
		System.out.println("Running game manager thread");
		
		if (!isSimulation) { //Current game managed is not a simulation!
			while (true) {
				waitStart();
				handleGameplay();
				//setUIReadyToStart();
			}
		} else { //Current game managed is a simulation!
			handleSimulation();
		}
		
		
	}
	
	@Override
	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
	
	/**
	 * Handle a simulation game (isSimulation = true).
	 */
	private void handleSimulation() {
		int gameDone = 0;
		Player currentPlayer;
		long p1MoveAvg = 0, p1CntMoves = 0, p1Wins = 0, p1DepthAvg = 0;
		long p2MoveAvg = 0, p2CntMoves = 0, p2Wins = 0, p2DepthAvg = 0;
		long cntDraw = 0;
		long startTimeSimulation = System.nanoTime();
		while (gameDone < numMatch) {
			gameDone++;
			System.out.print("Game[Game:" + gameDone + "/" + numMatch + "]: ");
			while (!gameState.isGameOver()) {
				currentPlayer = getCurrentPlayer();
				long startTime = System.nanoTime();
				currentPlayer.updateGame(gameState);
				long stopTime = System.nanoTime();
				if (gameState.isP1Turn()) {
					p1CntMoves++;
					p1DepthAvg += currentPlayer.getLastMaxDepthReached();
					p1MoveAvg += stopTime - startTime;
				} else {
					p2CntMoves++;
					p2DepthAvg += currentPlayer.getLastMaxDepthReached();
					p2MoveAvg += stopTime - startTime;
				}
				
			}
			//Game over, do final report of the last game
			String strResult = "";
			MatchResult res = gameState.getResult();
			switch (res) {
				case P1_WIN:
					strResult = "P1 WIN";
					p1Wins++;
					break;
				case P2_WIN:
					strResult = "P2 WIN";
					p2Wins++;
					break;
				case DRAW:
					strResult = "DRAW";
					cntDraw++;
					break;
			}
			System.out.println(strResult);
			gameState.restart();
		}
		//Final report of all games
		System.out.println("*** FINAL REPORT ***:");
		System.out.println("Player 1: " + player1.getClass().getSimpleName());
		System.out.println("Player 2: " + player2.getClass().getSimpleName());
		System.out.println("Number of games simulated: " + numMatch);
		System.out.println("Time required to simulate all the games: " + (System.nanoTime() - startTimeSimulation));
		System.out.println("Draws: " + cntDraw);
		System.out.println("== P1 ==");
		System.out.println("  - Average time for a move: " + p1MoveAvg / p1CntMoves);
		System.out.println("  - Average max depth for a move: " + p1DepthAvg / p1CntMoves);
		System.out.println("  - Total number of player moves: " + p1CntMoves);
		System.out.println("  - Wins: " + p1Wins);
		System.out.println("  - Defeats: " + p2Wins);
		System.out.println("========");
		
		System.out.println("== P2 ==");
		System.out.println("  - Average time for a move: " + p2MoveAvg / p2CntMoves);
		System.out.println("  - Average max depth for a move: " + p2DepthAvg / p2CntMoves);
		System.out.println("  - Total number of player moves: " + p2CntMoves);
		System.out.println("  - Wins: " + p2Wins);
		System.out.println("  - Defeats: " + p1Wins);
		System.out.println("========");
	}
	
	/** Handle the game until it's over. */
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
					Thread.sleep(AIDelay);
				} catch (InterruptedException e) {
					System.err.println("An error occurred during sleep.\n");
					e.printStackTrace();
				}
			}
			currentPlayer.updateGame(gameState);
			waitPlayerChoice(currentPlayer);
			if (currentPlayer.hasMoved()) addHistory(gameState.copy());
			updateUI();
		}
		gameOver();
	}
	
	/**
	 * Add the game state g to the game history.
	 * @param g the game state to save in history.
	 * */
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
	 * Check if it's currently possible to perform an undo.
	 *
	 * @return true if it's possible, false otherwise.
	 */
	private boolean undoIsPossible() {
		return curHistoryIndex > 0;
	}
	
	/**
	 * Check if it's currently possible to perform a redo.
	 *
	 * @return true if it's possible, false otherwise.
	 */
	private boolean redoIsPossible() {
		return curHistoryIndex < lastIndexValid;
	}
	
	/**
	 * Redo the last move if any is available.
	 */
	public void redo() {
		if (redoIsPossible()) {
			gameState.setGameState(history.get(++curHistoryIndex).getGameState());
			updateUI();
		}
		
	}
	
	/**
	 * Undo the last move if any is available.
	 */
	public void undo() {
		if (undoIsPossible()) {
			gameState.setGameState(history.get(--curHistoryIndex).getGameState());
			updateUI();
		}
	}
	
	/**
	 * Wait the game start.
	 */
	synchronized private void waitStart() {
		while (!isOnGoing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Wait until a player has taken a decision for his turn (skip or move).
	 *
	 * @param player the player to wait.
	 */
	synchronized private void waitPlayerChoice(Player player) {
		while (!(player.hasMoved() || player.hasSkipped())) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Wait the game resume. */
	private synchronized void waitResume() {
		while (!isOnGoing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Set the player 1.
	 *
	 * @param player1 the new player 1.
	 */
	public void setPlayer1(Player player1) {
		System.out.println("Player 1 set.");
		this.player1 = (player1 == null) ? new HumanPlayer() : player1;
		if (gameState.isP1Turn() && !this.player1.isHuman()) {
			boardUI.cancelLastSelection();
		}
		writeToConsole("Player 1 type changed.");
		boardUI.repaint();
	}
	
	/**
	 * Set the player 2.
	 * @param player2 the new player 2.
	 */
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
	 * Return the next player to play.
	 * @return the player who must take a decision.
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
	 * Request to start the game
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
	 * Request to resume the paused game
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
	 * Request to pause the current game
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
	
	/**
	 * Setup for game over state.
	 */
	synchronized public void gameOver() {
		writeToConsole("Game over.");
		this.isPaused = false;
		this.isReadyToStart = false;
		this.isOnGoing = false;
		this.isOver = true;
		updateUI();
	}
	
	/** Setup UI for OnGoing state. */
	synchronized private void setUIOnGoing() {
		opt.cmbPlayer1Type.setEnabled(false);
		opt.cmbPlayer2Type.setEnabled(false);
		opt.btnStart.setEnabled(false);
		opt.btnResume.setEnabled(false);
		opt.btnPause.setEnabled(true);
		opt.btnRest.setEnabled(false);
		opt.btnUndo.setEnabled(false);
		opt.btnRedo.setEnabled(false);
	}
	
	/** Setup UI for ReadyToStart state. */
	synchronized private void setUIReadyToStart() {
		writeToConsole("Press 'Start' to start a game.");
		opt.cmbPlayer1Type.setEnabled(true);
		opt.cmbPlayer2Type.setEnabled(true);
		opt.btnStart.setEnabled(true);
		opt.btnResume.setEnabled(false);
		opt.btnPause.setEnabled(false);
		opt.btnRest.setEnabled(false);
		opt.btnUndo.setEnabled(false);
		opt.btnRedo.setEnabled(false);
	}
	
	/** Setup UI for Paused state. */
	synchronized private void setUIPaused() {
		writeToConsole("Game is paused.");
		opt.cmbPlayer1Type.setEnabled(true);
		opt.cmbPlayer2Type.setEnabled(true);
		opt.btnStart.setEnabled(false);
		opt.btnResume.setEnabled(true);
		opt.btnPause.setEnabled(false);
		opt.btnRest.setEnabled(true);
		opt.btnUndo.setEnabled(undoIsPossible());
		opt.btnRedo.setEnabled(redoIsPossible());
	}
	
	/** Setup UI for Over state. */
	synchronized private void setUIOver() {
		writeToConsole("Game is over.");
		opt.cmbPlayer1Type.setEnabled(true);
		opt.cmbPlayer2Type.setEnabled(true);
		opt.btnStart.setEnabled(false);
		opt.btnResume.setEnabled(false);
		opt.btnPause.setEnabled(false);
		opt.btnRest.setEnabled(true);
	}
	
	/**
	 * Write a message in console.
	 * @param msg the message to append.
	 */
	private void writeToConsole(String msg) {
		String date = new SimpleDateFormat("hh:mm:ss").format(new Date());
		opt.txtAreaConsole.append("[" + date + "]: " + msg + "\n");
	}
	
	/**
	 * Set delay for a AI move.
	 * @param value the new value for AI delay.
	 */
	public void setDelay(int value) {
		AIDelay = value;
	}
	
}
