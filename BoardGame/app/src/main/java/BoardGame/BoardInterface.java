package BoardGame;
public interface BoardInterface {
	public void loadBoard(ChessType[][] board);
	public void initBoard();
	public void set(int x, int y, ChessType chessType);
	public ChessType get(int x, int y);
}
