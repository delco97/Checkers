package com.dca.checkers.model;
		
		
import static com.dca.checkers.model.Board.*;

/**
 * The {@code BoardFactory} is a factory class used to build pre-made boards.
 */
public class BoardFactory {
	
	/**
	 * Create a board configuration valid for a italian checkers game.
	 * @return the italian board
	 */
	static Board getItalianBoard() {
		int aux[][] = new int[8][8];
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				aux[i][j] = j <= 2 && ((i + j) % 2 != 0) ? BLACK_CHECKER : EMPTY;
				aux[i][j] = j >= 5 && ((i + j) % 2 != 0) ? WHITE_CHECKER : EMPTY;
			}
		}
		
		return new Board(aux);
	}
}
