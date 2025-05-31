package com.duyhung.gamecaro;

import java.util.ArrayList;
import java.util.List;

public class CaroBoard {
    public static final int SIZE = 15;  // kích thước bảng caro
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

    public List<int[]> getEmptyCells() {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] == EMPTY)
                    moves.add(new int[]{i, j});
        return moves;
    }

    public CaroBoard cloneBoard() {
        CaroBoard clone = new CaroBoard();
        for (int i = 0; i < SIZE; i++)
            System.arraycopy(this.board[i], 0, clone.board[i], 0, SIZE);
        return clone;
    }

    public boolean checkWin(int player) {
        // check ngang
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j <= SIZE - 5; j++) {
                boolean win = true;
                for (int k = 0; k < 5; k++)
                    if (board[i][j + k] != player) {
                        win = false;
                        break;
                    }
                if (win) return true;
            }
        // check dọc
        for (int j = 0; j < SIZE; j++)
            for (int i = 0; i <= SIZE - 5; i++) {
                boolean win = true;
                for (int k = 0; k < 5; k++)
                    if (board[i + k][j] != player) {
                        win = false;
                        break;
                    }
                if (win) return true;
            }
        // check chéo \
        for (int i = 0; i <= SIZE - 5; i++)
            for (int j = 0; j <= SIZE - 5; j++) {
                boolean win = true;
                for (int k = 0; k < 5; k++)
                    if (board[i + k][j + k] != player) {
                        win = false;
                        break;
                    }
                if (win) return true;
            }
        // check chéo /
        for (int i = 4; i < SIZE; i++)
            for (int j = 0; j <= SIZE - 5; j++) {
                boolean win = true;
                for (int k = 0; k < 5; k++)
                    if (board[i - k][j + k] != player) {
                        win = false;
                        break;
                    }
                if (win) return true;
            }
        return false;
    }
}
