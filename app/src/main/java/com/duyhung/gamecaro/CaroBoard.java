package com.duyhung.gamecaro;

public class CaroBoard {
    public static final int SIZE = 15;  // Số ô theo chiều ngang/dọc (vuông)

    public static final int EMPTY = 0;
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;

    private int[][] board;

    public CaroBoard() {
        board = new int[SIZE][SIZE];
        reset();
    }

    public void reset() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = EMPTY;
    }

    public boolean setMove(int row, int col, int player) {
        if (board[row][col] == EMPTY) {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public boolean checkWin(int player) {
        // Kiểm tra ngang
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j <= SIZE - 5; j++) {
                int count = 0;
                for (int k = 0; k < 5; k++)
                    if (board[i][j + k] == player) count++;
                if (count == 5) return true;
            }
        // Kiểm tra dọc
        for (int j = 0; j < SIZE; j++)
            for (int i = 0; i <= SIZE - 5; i++) {
                int count = 0;
                for (int k = 0; k < 5; k++)
                    if (board[i + k][j] == player) count++;
                if (count == 5) return true;
            }
        // Kiểm tra chéo \
        for (int i = 0; i <= SIZE - 5; i++)
            for (int j = 0; j <= SIZE - 5; j++) {
                int count = 0;
                for (int k = 0; k < 5; k++)
                    if (board[i + k][j + k] == player) count++;
                if (count == 5) return true;
            }
        // Kiểm tra chéo /
        for (int i = 4; i < SIZE; i++)
            for (int j = 0; j <= SIZE - 5; j++) {
                int count = 0;
                for (int k = 0; k < 5; k++)
                    if (board[i - k][j + k] == player) count++;
                if (count == 5) return true;
            }
        return false;
    }
}
