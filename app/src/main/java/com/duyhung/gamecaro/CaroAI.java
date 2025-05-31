package com.duyhung.gamecaro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CaroAI {

    // Max depth tối đa
    private static final int MAX_DEPTH_EARLY = 3;
    private static final int MAX_DEPTH_MID = 2;
    private static final int MAX_DEPTH_LATE = 1;

    // Giới hạn lân cận xét nước đi (điểm khảo sát)
    private static final int NEIGHBOR_RANGE = 2;

    // Lấy nước đi tốt nhất
    public static int[] getBestMove(CaroBoard board, int aiPlayer, int moveCount) {
        int maxDepth = getAdaptiveDepth(moveCount);

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        List<int[]> candidateMoves = getCandidateMoves(board);

        for (int[] move : candidateMoves) {
            CaroBoard newBoard = board.cloneBoard();
            newBoard.setMove(move[0], move[1], aiPlayer);
            int score = minimax(newBoard, 1, false, aiPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE, maxDepth);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Độ sâu thay đổi theo lượt đã đi
    private static int getAdaptiveDepth(int moveCount) {
        if (moveCount < 20) return MAX_DEPTH_EARLY;
        else if (moveCount < 40) return MAX_DEPTH_MID;
        else return MAX_DEPTH_LATE;
    }

    // Minimax Alpha-Beta pruning
    private static int minimax(CaroBoard board, int depth, boolean isMaximizing, int aiPlayer,
                               int alpha, int beta, int maxDepth) {
        int opponent = (aiPlayer == CaroBoard.PLAYER_X) ? CaroBoard.PLAYER_O : CaroBoard.PLAYER_X;

        if (board.checkWin(aiPlayer)) return 10000 - depth * 10;
        if (board.checkWin(opponent)) return -10000 + depth * 10;

        if (depth >= maxDepth || board.getEmptyCells().isEmpty()) {
            return heuristic(board, aiPlayer);
        }

        List<int[]> candidateMoves = getCandidateMoves(board);

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int[] move : candidateMoves) {
                CaroBoard newBoard = board.cloneBoard();
                newBoard.setMove(move[0], move[1], aiPlayer);
                int eval = minimax(newBoard, depth + 1, false, aiPlayer, alpha, beta, maxDepth);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // cắt tỉa
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int[] move : candidateMoves) {
                CaroBoard newBoard = board.cloneBoard();
                newBoard.setMove(move[0], move[1], opponent);
                int eval = minimax(newBoard, depth + 1, true, aiPlayer, alpha, beta, maxDepth);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // cắt tỉa
            }
            return minEval;
        }
    }

    // Giới hạn nước đi trong vùng lân cận các ô đã đánh
    private static List<int[]> getCandidateMoves(CaroBoard board) {
        Set<String> visited = new HashSet<>();
        List<int[]> candidates = new ArrayList<>();

        for (int r = 0; r < CaroBoard.SIZE; r++) {
            for (int c = 0; c < CaroBoard.SIZE; c++) {
                if (board.getCell(r, c) != CaroBoard.EMPTY) {
                    for (int i = Math.max(0, r - NEIGHBOR_RANGE); i <= Math.min(CaroBoard.SIZE - 1, r + NEIGHBOR_RANGE); i++) {
                        for (int j = Math.max(0, c - NEIGHBOR_RANGE); j <= Math.min(CaroBoard.SIZE - 1, c + NEIGHBOR_RANGE); j++) {
                            if (board.getCell(i, j) == CaroBoard.EMPTY) {
                                String key = i + "," + j;
                                if (!visited.contains(key)) {
                                    candidates.add(new int[]{i, j});
                                    visited.add(key);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            candidates.add(new int[]{CaroBoard.SIZE / 2, CaroBoard.SIZE / 2});
        }

        return candidates;
    }

    // Hàm heuristic đơn giản đánh giá bàn cờ
    private static int heuristic(CaroBoard board, int aiPlayer) {
        int score = 0;
        int opponent = (aiPlayer == CaroBoard.PLAYER_X) ? CaroBoard.PLAYER_O : CaroBoard.PLAYER_X;

        // Cộng điểm cho chuỗi quân của AI, trừ điểm cho chuỗi quân đối thủ
        score += evaluateBoard(board, aiPlayer);
        score -= evaluateBoard(board, opponent);

        return score;
    }

    // Đếm chuỗi liên tiếp (ví dụ chuỗi 2,3,4...) trên bàn
    private static int evaluateBoard(CaroBoard board, int player) {
        int totalScore = 0;

        int[][] directions = {
                {0, 1}, // ngang
                {1, 0}, // dọc
                {1, 1}, // chéo \
                {1, -1} // chéo /
        };

        for (int r = 0; r < CaroBoard.SIZE; r++) {
            for (int c = 0; c < CaroBoard.SIZE; c++) {
                if (board.getCell(r, c) == player) {
                    for (int[] dir : directions) {
                        int count = countSequence(board, r, c, dir[0], dir[1], player);
                        totalScore += getScoreForCount(count);
                    }
                }
            }
        }

        return totalScore;
    }

    // Đếm số quân liên tiếp theo hướng dx, dy từ vị trí (r,c)
    private static int countSequence(CaroBoard board, int r, int c, int dx, int dy, int player) {
        int count = 0;
        int x = r;
        int y = c;

        while (x >= 0 && x < CaroBoard.SIZE && y >= 0 && y < CaroBoard.SIZE && board.getCell(x, y) == player) {
            count++;
            x += dx;
            y += dy;
        }

        return count;
    }

    // Quy ước điểm cho chuỗi liên tiếp
    private static int getScoreForCount(int count) {
        switch (count) {
            case 5:
                return 10000;
            case 4:
                return 1000;
            case 3:
                return 100;
            case 2:
                return 10;
            default:
                return 0;
        }
    }
}
