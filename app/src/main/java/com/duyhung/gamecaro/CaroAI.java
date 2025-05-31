package com.duyhung.gamecaro;

import java.util.*;

public class CaroAI {

    private static final int MAX_DEPTH = 2;   // Có thể chỉnh xuống 3 nếu lag
    private static final int NEIGHBOR_RANGE = 3;

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

    private static int getAdaptiveDepth(int moveCount) {
        if (moveCount < 20) return MAX_DEPTH;
        else if (moveCount < 40) return 3;
        else return 2;
    }

    private static int minimax(CaroBoard board, int depth, boolean isMaximizing, int aiPlayer,
                               int alpha, int beta, int maxDepth) {
        int opponent = (aiPlayer == CaroBoard.PLAYER_X) ? CaroBoard.PLAYER_O : CaroBoard.PLAYER_X;

        if (board.checkWin(aiPlayer)) return 1000000 - depth * 1000;
        if (board.checkWin(opponent)) return -1000000 + depth * 1000;
        if (depth >= maxDepth || board.getEmptyCells().isEmpty()) {
            return heuristic(board, aiPlayer, opponent);
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
                if (beta <= alpha) break;
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
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

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

    // Hàm heuristic đánh giá trạng thái bàn, ưu tiên chặn và tấn công
    private static int heuristic(CaroBoard board, int aiPlayer, int opponent) {
        int aiScore = evaluateBoard(board, aiPlayer, false);
        int opponentScore = evaluateBoard(board, opponent, true);
        return aiScore - opponentScore * 5; // Nhân 5 để ưu tiên chặn
    }

    private static int evaluateBoard(CaroBoard board, int player, boolean isOpponent) {
        int score = 0;
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}};

        for (int r = 0; r < CaroBoard.SIZE; r++) {
            for (int c = 0; c < CaroBoard.SIZE; c++) {
                if (board.getCell(r, c) == player) {
                    for (int[] dir : directions) {
                        int count = countSequence(board, r, c, dir[0], dir[1], player);
                        boolean openStart = isOpen(board, r - dir[0], c - dir[1]);
                        boolean openEnd = isOpen(board, r + count * dir[0], c + count * dir[1]);
                        score += getScoreForCount(count, openStart, openEnd, isOpponent);
                    }
                }
            }
        }
        return score;
    }

    // Đếm số quân liên tiếp theo hướng (dx, dy)
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

    // Kiểm tra vị trí có mở (EMPTY) hay không (dùng cho open-ended sequence)
    private static boolean isOpen(CaroBoard board, int r, int c) {
        if (r < 0 || r >= CaroBoard.SIZE || c < 0 || c >= CaroBoard.SIZE) return false;
        return board.getCell(r, c) == CaroBoard.EMPTY;
    }

    // Tính điểm theo độ dài chuỗi và trạng thái 2 đầu chuỗi
    private static int getScoreForCount(int count, boolean openStart, boolean openEnd, boolean isOpponent) {
        if (count >= 5) return 1000000;

        int baseScore = 0;

        if (openStart && openEnd) { // chuỗi mở 2 đầu - rất mạnh
            switch (count) {
                case 4: baseScore = 100000; break;
                case 3: baseScore = 10000; break;
                case 2: baseScore = 1000; break;
                case 1: baseScore = 100; break;
            }
        } else if (openStart || openEnd) { // chuỗi mở 1 đầu
            switch (count) {
                case 4: baseScore = 10000; break;
                case 3: baseScore = 1000; break;
                case 2: baseScore = 100; break;
                case 1: baseScore = 10; break;
            }
        } else { // chuỗi đóng (2 đầu bị chặn)
            switch (count) {
                case 4: baseScore = 1000; break;
                case 3: baseScore = 100; break;
                case 2: baseScore = 10; break;
                case 1: baseScore = 1; break;
            }
        }

        return isOpponent ? baseScore * 5 : baseScore;
    }
}
