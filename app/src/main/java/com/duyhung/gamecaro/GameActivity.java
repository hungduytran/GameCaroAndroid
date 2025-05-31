package com.duyhung.gamecaro;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    private int playerSymbol;
    private int aiSymbol;

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

        if (mode == MODE_PLAY_WITH_AI) {
            showSymbolChoiceDialog();
        } else {
            playerSymbol = CaroBoard.PLAYER_X;
            aiSymbol = -1;
            currentPlayer = CaroBoard.PLAYER_X;
            resetGame();
        }

        btnPlayAgain.setOnClickListener(v -> {
            caroBoard.reset();
            resetGame();
            if (mode == MODE_PLAY_WITH_AI) showSymbolChoiceDialog();
        });
    }

    // Hàm đếm số nước đã đi trên bàn
    private int countMoves() {
        int count = 0;
        for (int i = 0; i < CaroBoard.SIZE; i++) {
            for (int j = 0; j < CaroBoard.SIZE; j++) {
                if (caroBoard.getCell(i, j) != CaroBoard.EMPTY) count++;
            }
        }
        return count;
    }

    private void aiMakeMove() {
        if (gameEnded) return;

        int moveCount = countMoves();
        int[] bestMove = CaroAI.getBestMove(caroBoard, aiSymbol, moveCount);

        if (bestMove != null) {
            caroBoard.setMove(bestMove[0], bestMove[1], aiSymbol);
            updateButtonUI(bestMove[0], bestMove[1]);

            if (caroBoard.checkWin(aiSymbol)) {
                tvStatus.setText("Máy thắng!");
                gameEnded = true;
                btnPlayAgain.setEnabled(true);
            } else {
                currentPlayer = playerSymbol;
                tvStatus.setText("Lượt người chơi: Bạn");
            }
        }
    }

    private void showSymbolChoiceDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn ký hiệu của bạn")
                .setMessage("Bạn muốn đi X hay O?")
                .setCancelable(false)
                .setPositiveButton("X", (dialog, which) -> {
                    setPlayerSymbol(CaroBoard.PLAYER_X);
                })
                .setNegativeButton("O", (dialog, which) -> {
                    setPlayerSymbol(CaroBoard.PLAYER_O);
                })
                .show();
    }

    private void setPlayerSymbol(int symbol) {
        playerSymbol = symbol;
        aiSymbol = (symbol == CaroBoard.PLAYER_X) ? CaroBoard.PLAYER_O : CaroBoard.PLAYER_X;
        currentPlayer = CaroBoard.PLAYER_X;
        resetGame();
        tvStatus.setText("Lượt người chơi: " + playerName(currentPlayer));
        if (currentPlayer == aiSymbol) {
            aiMakeMove();
        }
    }

    private void createBoardUI() {
        gridBoard.removeAllViews();

        int boardSize = CaroBoard.SIZE;

        gridBoard.setColumnCount(boardSize);
        gridBoard.setRowCount(boardSize);

        WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        int screenWidth = bounds.width();
        int screenHeight = bounds.height();

        int reservedHeight = dpToPx(80);
        int availableHeight = screenHeight - reservedHeight;

        int cellSize = availableHeight / boardSize;

        int gridWidth = cellSize * boardSize;
        int gridHeight = cellSize * boardSize;

        ViewGroup.LayoutParams params = gridBoard.getLayoutParams();
        params.width = gridWidth;
        params.height = gridHeight;
        gridBoard.setLayoutParams(params);
        gridBoard.requestLayout();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                final int row = i;
                final int col = j;
                Button btn = new Button(this);
                btn.setText("");
                btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, cellSize * 0.5f);
                btn.setWidth(cellSize);
                btn.setHeight(cellSize);
                btn.setMinWidth(0);
                btn.setMinHeight(0);
                btn.setPadding(0, 0, 0, 0);
                btn.setBackgroundResource(android.R.drawable.btn_default);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = cellSize;
                lp.height = cellSize;
                lp.columnSpec = GridLayout.spec(j);
                lp.rowSpec = GridLayout.spec(i);
                lp.setMargins(1, 1, 1, 1);
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

        if (mode == MODE_PLAY_WITH_AI && currentPlayer != playerSymbol) return;

        caroBoard.setMove(row, col, currentPlayer);
        updateButtonUI(row, col);

        if (caroBoard.checkWin(currentPlayer)) {
            tvStatus.setText("Người chơi " + playerName(currentPlayer) + " thắng!");
            gameEnded = true;
            btnPlayAgain.setEnabled(true);
            return;
        }

        if (mode == MODE_TWO_PLAYERS) {
            currentPlayer = (currentPlayer == CaroBoard.PLAYER_X) ? CaroBoard.PLAYER_O : CaroBoard.PLAYER_X;
            tvStatus.setText("Lượt người chơi: " + playerName(currentPlayer));
        } else if (mode == MODE_PLAY_WITH_AI) {
            currentPlayer = aiSymbol;
            tvStatus.setText("Lượt người chơi: Máy");
            aiMakeMove();
        }
    }

    private String playerName(int player) {
        if (mode == MODE_PLAY_WITH_AI && player == aiSymbol) {
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
