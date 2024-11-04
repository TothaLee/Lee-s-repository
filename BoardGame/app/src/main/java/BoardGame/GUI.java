package BoardGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI extends JFrame {

    private final int WIDTH = 900;
    private final int HEIGHT = 900;
    private final int CELL_SIZE = 40;   // Spacing between intersection points
    private final int GRID_SIZE = 15;   // 15x15 board
    private final int BOARD_MARGIN = CELL_SIZE; // Extra margin around the board
    private final int BOARD_X = 50;     // Initial X margin for board positioning
    private final int BOARD_Y = 50;     // Initial Y margin for board positioning
    private final int CLICK_RADIUS = CELL_SIZE / 2; // Radius for detecting valid clicks
    private final int PIECE_RADIUS = 15; // Radius for the piece
    private final Board board;
    private JLabel currentPlayerLabel;

    private JButton newGameButton;
    private JButton saveGameButton;
    private JButton loadGameButton;

//    private boolean isGameActive = false;
    private GameState gameState;
    private JPanel boardPanel;
    private final DatabaseManager dbManager;
	private WinChecker winChecker;
    void initBoardPane(){
        // Initialize board panel with additional margin
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);     // Draw grid lines
                drawPieces(g);   // Draw the pieces on the board
            }
        };
        // Set boardPanel bounds with extra margin on all sides
        boardPanel.setBounds(BOARD_X, BOARD_Y, (GRID_SIZE - 1) * CELL_SIZE + 2 * BOARD_MARGIN,
                (GRID_SIZE - 1) * CELL_SIZE + 2 * BOARD_MARGIN);
        boardPanel.setBackground(Color.GRAY); // Set background color to gray
        add(boardPanel);

        // Add mouse listener to handle clicks
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameState == GameState.IN_PROGRESS) {
                    handleBoardClick(e);
                    updateView();
                    updateCurrentPlayerLabel(); // Update label for the next player
                }
            }
        });

    }
    void updateView(){
        boardPanel.repaint();
        updateCurrentPlayerLabel();
    }
    void initButtons(){
        // Initialize the Start/Restart Game button
        newGameButton = new JButton("New Game");
        newGameButton.setBounds(WIDTH - 200, 70, 120, 30);
        add(newGameButton);

        // Action listener for Start/Restart Game button
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                startGameButton.setText("Restart Game");
                resetGame();
                updateCurrentPlayerLabel();
            }
        });

        // initialize the Save Game button
        saveGameButton = new JButton("Save Game");
        saveGameButton.setBounds(WIDTH - 200, 120, 120, 30);
        add(saveGameButton);

        //Action
        saveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbManager.saveBoardState(board.getBoard());
            }
        });
        //loadGameButton
        loadGameButton = new JButton("load Game");
        loadGameButton.setBounds(WIDTH - 200, 170, 120, 30);
        add(loadGameButton);

        //Action
        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChessType[][] load_board = dbManager.loadLatestBoardState();
                if(load_board == null){
                    System.out.println("No saved game found");
                    return;
                }
                board.loadBoard(load_board);
                gameState = GameState.IN_PROGRESS;



                updateView();
            }
        });
    }


    void initDadabase(){
        if(dbManager.doesTableExist("BoardState")){
            System.out.println("BoardState Table exists");
        }
        else {
            dbManager.createBoardStateTable();
            System.out.println("BoardState Table be created");
        }
    }
    public GUI() {
        this.board = new Board();
        gameState = GameState.NOT_STARTED;
		winChecker = new WinChecker();
        dbManager = new DatabaseManager();
//        initDadabase();
        setTitle("Gomoku Game");
        setLayout(null);  // Setting layout to null for manual positioning
        setSize(WIDTH, HEIGHT);

        // Initialize label to display the current player's turn
        currentPlayerLabel = new JLabel("Current Player: " + board.getCurrentPlayer());
        currentPlayerLabel.setBounds(WIDTH - 200, 20, 180, 30);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(currentPlayerLabel);

        initButtons();

        initBoardPane();

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Draws the grid lines for the board
    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < GRID_SIZE; i++) {
            int linePos = i * CELL_SIZE + BOARD_MARGIN;
            g.drawLine(linePos, BOARD_MARGIN, linePos, (GRID_SIZE - 1) * CELL_SIZE + BOARD_MARGIN);  // Vertical lines
            g.drawLine(BOARD_MARGIN, linePos, (GRID_SIZE - 1) * CELL_SIZE + BOARD_MARGIN, linePos);  // Horizontal lines
        }
    }

    // Draws the pieces based on the board's state
    private void drawPieces(Graphics g) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                ChessType piece = board.get(i, j);
                if (piece != ChessType.EMPTY) {
                    int centerX = j * CELL_SIZE + BOARD_MARGIN;
                    int centerY = i * CELL_SIZE + BOARD_MARGIN;

                    // Set color based on piece type
                    if (piece == ChessType.BLACK) {
                        g.setColor(Color.BLACK);
                    } else if (piece == ChessType.WHITE) {
                        g.setColor(Color.WHITE);
                    }
                    // Draw circle representing the piece
                    g.fillOval(centerX - PIECE_RADIUS, centerY - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                }
            }
        }
    }

    // Handle mouse click on the board
    private void handleBoardClick(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Iterate over each intersection to check if the click is within radius
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int centerX = j * CELL_SIZE + BOARD_MARGIN;
                int centerY = i * CELL_SIZE + BOARD_MARGIN;

                // Calculate the distance from the click to the intersection point
                int distance = (int) Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));

                // Check if the click is within the allowed radius
                if (distance <= CLICK_RADIUS) {
                    if (board.get(i, j) == ChessType.EMPTY) {
                        System.out.println("player: " + board.getCurrentPlayer() + " (" + i + ", " + j+")");
                        board.set(i, j, board.getCurrentPlayer());
                        if(winChecker.checkWin(board,i,j,board.getCurrentPlayer())){
                            System.out.println("Winner: " + board.getWinner());
                            gameState = GameState.FINISHED;
                        }
                        board.turnNextPlayer();
                    }
                    return; // Exit after finding the first valid intersection
                }
            }
        }
    }

    // Update the label to display the current player's turn
    private void updateCurrentPlayerLabel() {
        currentPlayerLabel.setText("Current Player: " + board.getCurrentPlayer());
    }

    // Reset the game
    private void resetGame() {
        board.initBoard(); // Reset the board state
        //reflash view
        updateView();
        gameState = GameState.IN_PROGRESS;
        System.out.println("Game reset.");
    }
}
