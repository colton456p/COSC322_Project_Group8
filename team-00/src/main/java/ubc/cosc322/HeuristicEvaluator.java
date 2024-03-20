package ubc.cosc322;

import java.util.LinkedList;
import java.util.Queue;

public class HeuristicEvaluator {
    private static final int WHITE_QUEEN = 1;
    private static final int BLACK_QUEEN = 2;
    private static final int WHITE_KING = 3;
    private static final int BLACK_KING = 4;
    private static final double TURN_ADVANTAGE = 0.15;

    public static double getHeuristicEval(int[][][] state, int playerTurn) {
        int[][] board = state[0];
        int[][] mobilityMap = state[1];

        double[] queenDistances = calculateQueenDistances(board, playerTurn);
        double queenDistance = queenDistances[0];
        double combinedQueenDistance = queenDistances[1];
        double kingDistance = queenDistances[2];

        double mobilityEval = queenDistance > 10.0 ? calculateMobilityEval(board, mobilityMap, queenDistance) : 0.0;

        return f1(queenDistance) * combinedQueenDistance + f2(queenDistance) * kingDistance + mobilityEval;
    }

    private static double f1(double queenDistance) {
        return (100.0 - queenDistance) / 100.0;
    }

    private static double f2(double queenDistance) {
        return (1.0 - f1(queenDistance)) / 4.0;
    }

    private static double calculateMobilityEval(int[][] board, int[][] mobilityMap, double queenDistance) {
        double whiteScore = 0.0;
        double blackScore = 0.0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == WHITE_QUEEN) {
                    whiteScore += calculateQueenMobilityScore(board, mobilityMap, i, j, queenDistance);
                } else if (board[i][j] == BLACK_QUEEN) {
                    blackScore += calculateQueenMobilityScore(board, mobilityMap, i, j, queenDistance);
                }
            }
        }
        return blackScore - whiteScore;
    }

    private static double calculateQueenMobilityScore(int[][] board, int[][] mobilityMap, int posY, int posX, double queenDistance) {
        double queenEval = 0.0;

        for (int i = 1; i < 10; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (j != 0 || k != 0) {
                        int newX = posX + i * j;
                        int newY = posY + i * k;
                        if (AmazonsUtility.isSpotValid(board, newY, newX)) {
                            queenEval += Math.pow(2, -(i - 1)) * mobilityMap[newY][newX];
                        }
                    }
                }
            }
        }
        return queenEval * f5(queenDistance, queenEval);
    }

    private static double f5(double queenDistance, double mobility) {
        return queenDistance * Math.pow(1.2, -mobility) / 45.0;
    }

    private static double[] calculateQueenDistances(int[][] board, int playerTurn) {
        double minQueenDistance = Double.MAX_VALUE;
        double combinedQueenDistance = 0.0;
        double kingDistance = 0.0;

        int myQueen = (playerTurn == WHITE_QUEEN) ? WHITE_QUEEN : BLACK_QUEEN;
        int opponentQueen = (playerTurn == WHITE_QUEEN) ? BLACK_QUEEN : WHITE_QUEEN;
        int myKing = (playerTurn == WHITE_QUEEN) ? WHITE_KING : BLACK_KING;
        int opponentKing = (playerTurn == WHITE_QUEEN) ? BLACK_KING : WHITE_KING;

        Queue<int[]> queenQueue = new LinkedList<>();
        Queue<int[]> kingQueue = new LinkedList<>();

        boolean[][] visited = new boolean[10][10];

        // Initialize queues with initial positions
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == myQueen) {
                    queenQueue.offer(new int[]{i, j, 0});
                    visited[i][j] = true;
                } else if (board[i][j] == opponentQueen) {
                    queenQueue.offer(new int[]{i, j, Integer.MAX_VALUE});
                    visited[i][j] = true;
                } else if (board[i][j] == myKing) {
                    kingQueue.offer(new int[]{i, j, 0});
                    visited[i][j] = true;
                } else if (board[i][j] == opponentKing) {
                    kingQueue.offer(new int[]{i, j, Integer.MAX_VALUE});
                    visited[i][j] = true;
                }
            }
        }

        // Breadth-first search for queen and king distances
        while (!queenQueue.isEmpty()) {
            int[] queenPos = queenQueue.poll();
            int y = queenPos[0];
            int x = queenPos[1];
            int dist = queenPos[2];

            minQueenDistance = Math.min(minQueenDistance, dist);
            combinedQueenDistance += dist;

            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if ((dy != 0 || dx != 0) && AmazonsUtility.isSpotValid(board, y + dy, x + dx) && !visited[y + dy][x + dx]) {
                        visited[y + dy][x + dx] = true;
                        queenQueue.offer(new int[]{y + dy, x + dx, dist + 1});
                    }
                }
            }
        }

        // Reset visited array for king distance calculation
        visited = new boolean[10][10];

        // Breadth-first search for king distances
        while (!kingQueue.isEmpty()) {
            int[] kingPos = kingQueue.poll();
            int y = kingPos[0];
            int x = kingPos[1];
            int dist = kingPos[2];

            kingDistance += dist;

            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if ((dy != 0 || dx != 0) && AmazonsUtility.isSpotValid(board, y + dy, x + dx) && !visited[y + dy][x + dx]) {
                        visited[y + dy][x + dx] = true;
                        kingQueue.offer(new int[]{y + dy, x + dx, dist + 1});
                    }
                }
            }
        }

        return new double[]{minQueenDistance, combinedQueenDistance, kingDistance};
    }
}
