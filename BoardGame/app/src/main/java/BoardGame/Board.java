package BoardGame;
public class Board implements BoardInterface{
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_HEIGHT = 15;
    private ChessType[][] board = new ChessType[BOARD_WIDTH][BOARD_HEIGHT];
    private ChessType winner;
    private ChessType currentPlayer;

    public int getWidth() {
        return BOARD_WIDTH;
    }

    public int getHeight() {
        return BOARD_HEIGHT;
    }

    public ChessType[][] getBoard() {
        return board;
    }

    public ChessType getCurrentPlayer() {
        return currentPlayer;
    }
    public void turnNextPlayer(){
        if (currentPlayer == ChessType.BLACK) {
            currentPlayer = ChessType.WHITE;
        } else {
            currentPlayer = ChessType.BLACK;
        }
    }
    public Board() {
        initBoard();
    }

    public void loadBoard(ChessType[][] board) {
        this.board = board;
        //update current player
        int white_chess_num = 0;
        int black_chess_num = 0;
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                if (board[i][j] == ChessType.WHITE) {
                    white_chess_num++;
                } else if (board[i][j] == ChessType.BLACK) {
                    black_chess_num++;
                }
            }
        }
        if(white_chess_num == black_chess_num) currentPlayer = ChessType.BLACK;
        else currentPlayer = ChessType.WHITE;
    }

    // Initialize the board with empty spaces
    public void initBoard() {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j] = ChessType.EMPTY;
            }
        }
        winner = ChessType.EMPTY;
        currentPlayer = ChessType.BLACK;
    }

    // Print the board
    public void print() {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Set a piece at the specified coordinates
    public void set(int x, int y, ChessType chessType) {
        if (isValidPosition(x, y) && board[x][y] == ChessType.EMPTY) {
            board[x][y] = chessType;
            if (checkWin(x, y, chessType)) {
                winner = chessType;
            }
        }
    }

    // Get the piece at the specified coordinates
    public ChessType get(int x, int y) {
        return board[x][y];
    }

    // Check if there is a winner
    public ChessType getWinner() {
        return winner;
    }

    // Check if the given position is within bounds
	boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT;
    }

    // Check for five in a row
    private boolean checkWin(int x, int y, ChessType chessType) {
        return checkDirection(x, y, chessType, 1, 0) || // Horizontal
                checkDirection(x, y, chessType, 0, 1) || // Vertical
                checkDirection(x, y, chessType, 1, 1) || // Diagonal \
                checkDirection(x, y, chessType, 1, -1);  // Diagonal /
    }

    // Helper function to check a specific direction
    private boolean checkDirection(int x, int y, ChessType chessType, int dx, int dy) {
        int count = 1;
        count += countInDirection(x, y, chessType, dx, dy);
        count += countInDirection(x, y, chessType, -dx, -dy);
        return count >= 5;
    }

    // Count consecutive pieces in a direction
    private int countInDirection(int x, int y, ChessType chessType, int dx, int dy) {
        int count = 0;
        int i = x + dx;
        int j = y + dy;
        while (isValidPosition(i, j) && board[i][j] == chessType) {
            count++;
            i += dx;
            j += dy;
        }
        return count;
    }
}
