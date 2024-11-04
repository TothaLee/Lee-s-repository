package BoardGame;
public class WinChecker extends Check{
	public boolean checkWin(Board board, int x, int y, ChessType chessType) {
		return checkDirection(board, x, y, chessType, 1, 0) ||
				checkDirection(board, x, y, chessType, 0, 1) ||
				checkDirection(board, x, y, chessType, 1, 1) ||
				checkDirection(board, x, y, chessType, 1, -1);
	}
}
