package BoardGame;
public abstract class Check {
    public boolean checkWin(Board board, int x, int y, ChessType chessType) {
		return true;
	}
	protected boolean checkDirection(Board board, int x, int y, ChessType chessType, int dx, int dy) {
		int count = 1 + countInDirection(board, x, y, chessType, dx, dy)
				+ countInDirection(board, x, y, chessType, -dx, -dy);
		return count >= 5;
	}
	protected int countInDirection(Board board, int x, int y, ChessType chessType, int dx, int dy) {
		int count = 0;
		int i = x + dx, j = y + dy;
		while (board.isValidPosition(i, j) && board.get(i, j) == chessType) {
			count++;
			i += dx;
			j += dy;
		}
		return count;
	}
}
