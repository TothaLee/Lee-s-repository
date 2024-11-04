package BoardGame;
import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseManager {
	private final String url = "jdbc:derby://localhost:1527/gameDB;create=true";
	private final String user = "APP";
	private final String password = "APP";
	private Connection conn;

	public DatabaseManager() {
		connect();
//        initializeDatabase();
	}

	// Connect to the database
	private void connect() {
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Database connected successfully.");

			if(!doesTableExist("BoardState"))
			{
				createBoardStateTable();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Close the connection
	public void close() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				System.out.println("Database connection closed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Create a table with the provided SQL statement
	public void createTable(String createTableSQL) {
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(createTableSQL);
			System.out.println("Table created successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Insert data into a table
	public void insertData(String insertSQL) {
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(insertSQL);
			System.out.println("Data inserted successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Update data in a table
	public void updateData(String updateSQL) {
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(updateSQL);
			System.out.println("Data updated successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Delete data from a table
	public void deleteData(String deleteSQL) {
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(deleteSQL);
			System.out.println("Data deleted successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Query data from the database
	public void queryData(String querySQL) {
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(querySQL)) {
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			// Print column names
			for (int i = 1; i <= columnCount; i++) {
				System.out.print(metaData.getColumnName(i) + "\t");
			}
			System.out.println();

			// Print data rows
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					System.out.print(rs.getObject(i) + "\t");
				}
				System.out.println();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Check if a table exists
	public boolean doesTableExist(String tableName) {
		try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toUpperCase(), null)) {
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	// Create the BoardState table
	public void createBoardStateTable() {
		String createTableSQL = "CREATE TABLE BoardState (" +
				"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP PRIMARY KEY, " +
				"board_state VARCHAR(5000))"; // Increased VARCHAR size
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(createTableSQL);
			System.out.println("Table BoardState created successfully.");
			ChessType[][] emptyBoard = createEmptyBoard(15); // Assuming 15x15 board size
			saveBoardState(emptyBoard);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// Save the current board state with a timestamp
	public void saveBoardState(ChessType[][] board) {
		String insertSQL = "INSERT INTO BoardState (timestamp, board_state) VALUES (?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
			Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
			String boardState = convertBoardToString(board);
//            System.out.println("boardState: "+boardState);
			pstmt.setTimestamp(1, currentTimestamp);
			pstmt.setString(2, boardState);
			pstmt.executeUpdate();

			System.out.println("Board state saved successfully at " + currentTimestamp);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// Helper method to create an empty board
	private ChessType[][] createEmptyBoard(int size) {
		ChessType[][] board = new ChessType[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = ChessType.EMPTY;
			}
		}
		return board;
	}
	// Convert the board to a compressed string format for saving
	private String convertBoardToString(ChessType[][] board) {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				switch (board[row][col]) {
					case BLACK:
						sb.append("B");
						break;
					case WHITE:
						sb.append("W");
						break;
					case EMPTY:
						sb.append("E");
						break;
				}
				if (col < board[row].length - 1) {
					sb.append(","); // Separate columns by comma
				}
			}
			if (row < board.length - 1) {
				sb.append(";"); // Separate rows by semicolon
			}
		}
		return sb.toString();
	}

	// Load the latest board state from the database
	public ChessType[][] loadLatestBoardState() {
		String querySQL = "SELECT board_state FROM BoardState ORDER BY timestamp DESC FETCH FIRST ROW ONLY";
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(querySQL)) {
			if (rs.next()) {
				String boardStateStr = rs.getString("board_state");
				return convertStringToBoard(boardStateStr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ChessType[Board.BOARD_WIDTH][Board.BOARD_HEIGHT];
	}

	// Convert the string format back to a ChessType[][] board
	private ChessType[][] convertStringToBoard(String boardStateStr) {
		ChessType[][] board = new ChessType[Board.BOARD_WIDTH][Board.BOARD_HEIGHT];
		String[] rows = boardStateStr.split(";");
		for (int i = 0; i < rows.length; i++) {
			String[] cols = rows[i].split(",");
			for (int j = 0; j < cols.length; j++) {
				if(cols[j].equals("B")){
					board[i][j] = ChessType.BLACK;
				}
				else if(cols[j].equals("W")){
					board[i][j] = ChessType.WHITE;
				}
				else if(cols[j].equals("E")){
					board[i][j] = ChessType.EMPTY;
				}
			}
		}
		return board;
	}
}
