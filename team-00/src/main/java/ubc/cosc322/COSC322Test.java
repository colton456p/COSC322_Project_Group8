
package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javafx.scene.effect.Light.Point;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null;
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
    	COSC322Test player = new COSC322Test(args[0], args[1]);
    	
    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
                }
            });
    	}
    }
	
    /**
     * Any name and passwd
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
		System.out.println(gameClient.getRoomList());
		Scanner sc = new Scanner(System.in);
		System.out.println("What room do you want to join?");
		String roomName = sc.nextLine();
		sc.close();
		gameClient.joinRoom(roomName);
		userName = gameClient.getUserName();
		if(gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
		
		if (messageType.equals(GameMessage.GAME_ACTION_MOVE)){
			gamegui.updateGameState(msgDetails);
		}
		else if(messageType.equals(GameMessage.GAME_STATE_BOARD)){

			Object obj = msgDetails.get(messageType);
			ArrayList<Integer> board = (ArrayList<Integer>) obj;
			gamegui.setGameState(board);
		}
		
		
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 
    	    	
    	// return this.handleGameMessage(messageType, msgDetails);   	
		return true;
    }
    
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return  gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}


//*****NEW CODE WRITTEN HERE*****//

 private int evaluateGameState(int[][] board, List<Point> playerPositions) {
        int score = 0;

        // Evaluate the game state based on various factors such as:
        // - Number of moves available to the current player
        // - Distance of player positions to the opponent's camp
        // - Proximity of player positions to the opponent's queens
        // - Control of key positions on the board

        // Example heuristic:
        // Count the number of moves available to the current player
        int currentPlayerMoves = countAvailableMoves(board, playerPositions);
        
        // Count the number of moves available to the opponent player
        int opponentMoves = countAvailableMoves(board, getOpponentPositions(playerPositions));

        // Calculate the difference in available moves
        score = currentPlayerMoves - opponentMoves;

        // You can add more factors and adjust weights as needed for a more sophisticated evaluation

        return score;
    }

    // Define a method to count the number of available moves for a player
    private int countAvailableMoves(int[][] board, List<Point> playerPositions) {
        int moves = 0;

        // Iterate over each player position
        for (Point position : playerPositions) {
            // Generate legal moves for the current position
            List<Point> legalMoves = generateLegalMoves(board, position);

            // Increment the total moves count
            moves += legalMoves.size();
        }

        return moves;
    }

    // Define a method to generate legal moves for a given position
    private List<Point> generateLegalMoves(int[][] board, Point position) {
        List<Point> legalMoves = new ArrayList<>();

        int x = (int) position.getX();
        int y = (int) position.getY();

        // Define possible directions of movement (8 directions in total)
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };

        // Iterate over each direction
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            // Move along the direction until hitting a barrier or the edge of the board
            int newX = x + dx;
            int newY = y + dy;
            while (isValidMove(board, newX, newY)) {
                legalMoves.add(new Point(newX, newY));
                newX += dx;
                newY += dy;
            }
        }

        return legalMoves;
    }

    // Define a method to get the opponent's positions
    private boolean isValidMove(int[][] board, int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length && board[x][y] == 0;
    }

    // Define a method to get the opponent's positions
    private List<Point> getOpponentPositions(List<Point> playerPositions) {
        List<Point> opponentPositions = new ArrayList<>();

        // Assuming there are only two players
        for (Point position : playerPositions) {
            // Invert the position for the opponent
            opponentPositions.add(new Point(9 - (int) position.getX(), 9 - (int) position.getY()));
        }

        return opponentPositions;
    }

	private int minimax(int depth, int alpha, int beta, boolean maximizingPlayer) {
		// Base case: If reached terminal state or maximum depth, return the evaluation score
		if (depth == 0 /* add condition for terminal state */) {
			return evaluateGameState(/* pass necessary parameters */);
		}
	
		if (maximizingPlayer) {
			int maxEval = Integer.MIN_VALUE;
			// Generate legal moves for the current player
			List<Point> playerPositions = /* get current player positions */;
			for (Point position : playerPositions) {
				List<Point> legalMoves = generateLegalMoves(/* pass necessary parameters */);
				for (Point move : legalMoves) {
					// Apply the move to the game state
					/* apply move to game state */;
					int eval = minimax(depth - 1, alpha, beta, false);
					// Undo the move
					/* undo move */;
					maxEval = Math.max(maxEval, eval);
					alpha = Math.max(alpha, eval);
					if (beta <= alpha) {
						break; // Beta cut-off
					}
				}
			}
			return maxEval;
		} else {
			int minEval = Integer.MAX_VALUE;
			// Generate legal moves for the opponent player
			List<Point> opponentPositions = getOpponentPositions(/* pass necessary parameters */);
			for (Point position : opponentPositions) {
				List<Point> legalMoves = generateLegalMoves(/* pass necessary parameters */);
				for (Point move : legalMoves) {
					// Apply the move to the game state
					/* apply move to game state */;
					int eval = minimax(depth - 1, alpha, beta, true);
					// Undo the move
					/* undo move */;
					minEval = Math.min(minEval, eval);
					beta = Math.min(beta, eval);
					if (beta <= alpha) {
						break; // Alpha cut-off
					}
				}
			}
			return minEval;
		}
	}

	private Point iterativeDeepeningSearch() {
		Point bestMove = null;
		int depth = 1; // Start with shallow depth
	
		long startTime = System.currentTimeMillis();
		long endTime = startTime + TIME_LIMIT; // Define your time limit in milliseconds
	
		while (System.currentTimeMillis() < endTime) {
			int alpha = Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			int maxEval = Integer.MIN_VALUE;
	
			// Generate legal moves for the current player
			List<Point> playerPositions = /* get current player positions */;
			for (Point position : playerPositions) {
				List<Point> legalMoves = generateLegalMoves(/* pass necessary parameters */);
				for (Point move : legalMoves) {
					// Apply the move to the game state
					/* apply move to game state */;
					int eval = minimax(depth, alpha, beta, false);
					// Undo the move
					/* undo move */;
					if (eval > maxEval) {
						maxEval = eval;
						bestMove = move;
					}
					alpha = Math.max(alpha, eval);
				}
			}
	
			depth++; // Increase the depth for the next iteration
		}
	
		return bestMove;
	}


}//end of class
