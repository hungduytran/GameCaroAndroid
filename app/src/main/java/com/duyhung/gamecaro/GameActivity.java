package com.duyhung.gamecaro;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "mode";
    public static final int MODE_TWO_PLAYERS = 1;
    public static final int MODE_PLAY_WITH_AI = 2;

    private GridLayout gridBoard;
    private TextView tvStatus;
    private Button btnPlayAgain;

    private CaroBoard caroBoard;
    private Button[][] buttons;

    private int currentPlayer;
    private boolean gameEnded = false;
    private int mode;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridBoard = findViewById(R.id.gridBoard);
        tvStatus = findViewById(R.id.tvStatus);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);

        caroBoard = new CaroBoard();
        buttons = new Button[CaroBoard.SIZE][CaroBoard.SIZE];

        mode = getIntent().getIntExtra(EXTRA_MODE, MODE_TWO_PLAYERS);

        createBoardUI();
        resetGame();

        btnPlayAgain.setOnClickListener(v -> resetGame());
    }

    private void createBoardUI() {
        gridBoard.removeAllViews();

        int boardSize = CaroBoard.SIZE;
        int cellSizeDp = 48; // chinh kich thuoc o

        int cellSizePx = dpToPx(cellSizeDp);

        // GridLayout  scroll
        ViewGroup.LayoutParams params = gridBoard.getLayoutParams();
        params.width = cellSizePx * boardSize;
        params.height = cellSizePx * boardSize;
        gridBoard.setLayoutParams(params);

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                final int row = i;
                final int col = j;
                Button btn = new Button(this);
                btn.setText("");
                btn.setTextSize(18);
                btn.setWidth(cellSizePx);
                btn.setHeight(cellSizePx);
                btn.setMinWidth(0);
                btn.setMinHeight(0);
                btn.setPadding(0,0,0,0);
                btn.setBackgroundResource(android.R.drawable.btn_default);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = cellSizePx;
                lp.height = cellSizePx;
                lp.columnSpec = GridLayout.spec(j);
                lp.rowSpec = GridLayout.spec(i);
                lp.setMargins(1,1,1,1);
                btn.setLayoutParams(lp);

                btn.setOnClickListener(v -> onCellClicked(row, col));

                buttons[i][j] = btn;
                gridBoard.addView(btn);
            }
        }
    }

    private void onCellClicked(int row, int col) {
        if (gameEnded) return;
        if (caroBoard.getCell(row, col) != CaroBoard.EMPTY) return;

        caroBoard.setMove(row, col, currentPlayer);
        updateButtonUI(row, col);

        if (caroBoard.checkWin(currentPlayer)) {
            tvStatus.setText("Người chơi " + playerName(currentPlayer) + " thắng!");
            gameEnded = true;
            btnPlayAgain.setEnabled(true);
            return;
        }

        // swap turn
        if (mode == MODE_TWO_PLAYERS) {
            currentPlayer = (currentPlayer == CaroBoard.PLAYER_X) ? CaroBoard.PLAYER_O : CaroBoard.PLAYER_X;
            tvStatus.setText("Lượt người chơi: " + playerName(currentPlayer));
        } else if (mode == MODE_PLAY_WITH_AI) {
            if (currentPlayer == CaroBoard.PLAYER_X) {
                currentPlayer = CaroBoard.PLAYER_O;
                tvStatus.setText("Lượt người chơi: Máy");
                aiMakeMove();
            } else {
                currentPlayer = CaroBoard.PLAYER_X;
                tvStatus.setText("Lượt người chơi: Bạn");
            }
        }
    }

    private void aiMakeMove() {
        if (gameEnded) return;

        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < CaroBoard.SIZE; i++) {
            for (int j = 0; j < CaroBoard.SIZE; j++) {
                if (caroBoard.getCell(i, j) == CaroBoard.EMPTY) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }

        if (emptyCells.isEmpty()) {
            tvStatus.setText("Hòa rồi!");
            gameEnded = true;
            btnPlayAgain.setEnabled(true);
            return;
        }

        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));

        caroBoard.setMove(cell[0], cell[1], CaroBoard.PLAYER_O);
        updateButtonUI(cell[0], cell[1]);

        if (caroBoard.checkWin(CaroBoard.PLAYER_O)) {
            tvStatus.setText("Máy thắng!");
            gameEnded = true;
            btnPlayAgain.setEnabled(true);
        }
    }

    private String playerName(int player) {
        if (mode == MODE_PLAY_WITH_AI && player == CaroBoard.PLAYER_O) {
            return "Máy";
        } else {
            return player == CaroBoard.PLAYER_X ? "X" : "O";
        }
    }

    private void updateButtonUI(int row, int col) {
        Button btn = buttons[row][col];
        int cell = caroBoard.getCell(row, col);
        if (cell == CaroBoard.PLAYER_X) {
            btn.setText("X");
            btn.setTextColor(Color.RED);
        } else if (cell == CaroBoard.PLAYER_O) {
            btn.setText("O");
            btn.setTextColor(Color.BLUE);
        }
    }

    private void resetGame() {
        caroBoard.reset();
        for (int i = 0; i < CaroBoard.SIZE; i++) {
            for (int j = 0; j < CaroBoard.SIZE; j++) {
                buttons[i][j].setText("");
            }
        }
        gameEnded = false;
        btnPlayAgain.setEnabled(false);
        currentPlayer = CaroBoard.PLAYER_X;
        tvStatus.setText("Lượt người chơi: " + playerName(currentPlayer));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
