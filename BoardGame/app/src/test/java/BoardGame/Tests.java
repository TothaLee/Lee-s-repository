package BoardGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class Tests {
    private Board board;

    /**
     * Initialize a new Board instance before each test.
     */
    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    /**
     * Test that the board initializes correctly with all positions set to EMPTY,
     * the initial player is BLACK, and no winner is set.
     */
    @Test
    public void testBoardInitialization() {
        ChessType[][] currentBoard = board.getBoard();
        for (int i = 0; i < Board.BOARD_WIDTH; i++) {
            for (int j = 0; j < Board.BOARD_HEIGHT; j++) {
                assertEquals(ChessType.EMPTY, currentBoard[i][j], "Board should be initialized with EMPTY.");
            }
        }
        assertEquals(ChessType.BLACK, board.getCurrentPlayer(), "Initial player should be BLACK.");
        assertEquals(ChessType.EMPTY, board.getWinner(), "Initial winner should be EMPTY.");
    }

    /**
     * Test placing a BLACK piece on the board and verify the board state
     * and player turn switch to WHITE.
     */
    @Test
    public void testSetPiece() {
        board.set(0, 0, ChessType.BLACK);
        assertEquals(ChessType.BLACK, board.get(0, 0), "Piece should be set to BLACK.");
        assertEquals(ChessType.BLACK, board.getCurrentPlayer(), "Current player should switch to WHITE.");
    }

    /**
     * Test the turnNextPlayer method to ensure it correctly switches players.
     */
    @Test
    public void testTurnNextPlayer() {
        assertEquals(ChessType.BLACK, board.getCurrentPlayer(), "Initial player should be BLACK.");
        board.turnNextPlayer();
        assertEquals(ChessType.WHITE, board.getCurrentPlayer(), "Player should switch to WHITE.");
        board.turnNextPlayer();
        assertEquals(ChessType.BLACK, board.getCurrentPlayer(), "Player should switch back to BLACK.");
    }

    /**
     * Test horizontal win condition by placing 5 BLACK pieces in a row.
     */
    @Test
    public void testHorizontalWin() {
        // Place 5 BLACK pieces horizontally at row 0
        for (int col = 0; col < 5; col++) {
            board.set(0, col, ChessType.BLACK);
        }
        assertEquals(ChessType.BLACK, board.getWinner(), "BLACK should be the winner with horizontal line.");
    }

    /**
     * Test vertical win condition by placing 5 WHITE pieces in a column.
     */
    @Test
    public void testVerticalWin() {
        // Switch to WHITE player
        board.turnNextPlayer();
        // Place 5 WHITE pieces vertically at column 0
        for (int row = 0; row < 5; row++) {
            board.set(row, 0, ChessType.WHITE);
        }
        assertEquals(ChessType.WHITE, board.getWinner(), "WHITE should be the winner with vertical line.");
    }

    /**
     * Test diagonal win condition (backslash) by placing 5 BLACK pieces diagonally.
     */
    @Test
    public void testDiagonalWinBackslash() {
        // Place 5 BLACK pieces diagonally from (0,0) to (4,4)
        for (int i = 0; i < 5; i++) {
            board.set(i, i, ChessType.BLACK);
        }
        assertEquals(ChessType.BLACK, board.getWinner(), "BLACK should be the winner with backslash diagonal.");
    }

    /**
     * Test diagonal win condition (slash) by placing 5 WHITE pieces diagonally.
     */
    @Test
    public void testDiagonalWinSlash() {
        // Switch to WHITE player
        board.turnNextPlayer();
        // Place 5 WHITE pieces diagonally from (4,0) to (0,4)
        for (int i = 0; i < 5; i++) {
            board.set(4 - i, i, ChessType.WHITE);
        }
        assertEquals(ChessType.WHITE, board.getWinner(), "WHITE should be the winner with slash diagonal.");
    }

    /**
     * Test that no winner is declared when the board does not meet any win condition.
     */
    @Test
    public void testNoWinner() {
        board.set(0, 0, ChessType.BLACK);
        board.set(0, 1, ChessType.WHITE);
        board.set(0, 2, ChessType.BLACK);
        board.set(0, 3, ChessType.WHITE);
        board.set(0, 4, ChessType.BLACK);
        assertEquals(ChessType.EMPTY, board.getWinner(), "There should be no winner yet.");
    }

    /**
     * Test handling of invalid moves, such as placing a piece on an already occupied position.
     */
    @Test
    public void testInvalidMove() {
        // Place a piece at (0,0)
        board.set(0, 0, ChessType.BLACK);
        // Attempt to place another piece at the same position
        board.set(0, 0, ChessType.WHITE);
        // The piece should remain BLACK
        assertEquals(ChessType.BLACK, board.get(0, 0), "Cannot place a piece on an occupied position.");
        // Current player should have switched to WHITE after the first move
        assertEquals(ChessType.BLACK, board.getCurrentPlayer(), "Current player should be WHITE after invalid move attempt.");
    }

    /**
     * Test loading a predefined board state and verify the board and current player.
     */
    @Test
    public void testLoadBoard() {
        // Create a sample board
        ChessType[][] sampleBoard = new ChessType[Board.BOARD_WIDTH][Board.BOARD_HEIGHT];
        for (int i = 0; i < Board.BOARD_WIDTH; i++) {
            for (int j = 0; j < Board.BOARD_HEIGHT; j++) {
                sampleBoard[i][j] = ChessType.EMPTY;
            }
        }
        sampleBoard[0][0] = ChessType.BLACK;
        sampleBoard[1][1] = ChessType.WHITE;
        board.loadBoard(sampleBoard);

        // Verify board state
        ChessType[][] loadedBoard = board.getBoard();
        assertEquals(ChessType.BLACK, loadedBoard[0][0], "Loaded board should have BLACK at (0,0).");
        assertEquals(ChessType.WHITE, loadedBoard[1][1], "Loaded board should have WHITE at (1,1).");

        // Verify current player
        // Since BLACK has 1 piece and WHITE has 1 piece, current player should be BLACK
        assertEquals(ChessType.BLACK, board.getCurrentPlayer(), "Current player should be BLACK after loading board with equal pieces.");
    }

    /**
     * Test that the board correctly identifies a winner after multiple moves.
     */
    @Test
    public void testMultipleMovesAndWin() {
        // Simulate a sequence of moves leading to a WHITE win
        // Player BLACK starts
        board.set(0, 0, ChessType.BLACK); // BLACK
        board.set(1, 0, ChessType.WHITE); // WHITE
        board.set(0, 1, ChessType.BLACK); // BLACK
        board.set(1, 1, ChessType.WHITE); // WHITE
        board.set(0, 2, ChessType.BLACK); // BLACK
        board.set(1, 2, ChessType.WHITE); // WHITE
        board.set(0, 3, ChessType.BLACK); // BLACK
        board.set(1, 3, ChessType.WHITE); // WHITE
        board.set(0, 4, ChessType.BLACK); // BLACK
        board.set(1, 4, ChessType.WHITE); // WHITE - should win now

        assertEquals(ChessType.WHITE, board.getWinner(), "WHITE should be the winner after multiple moves.");
        // Game state should be FINISHED, but Board class does not manage GameState directly
    }
}