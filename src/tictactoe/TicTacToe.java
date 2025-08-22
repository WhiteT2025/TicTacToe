 package tictactoe;

import java.util.Objects;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Tic-Tac-Toe — Dolphin vs Whale
 *
 * Uses classpath resources for images & audio.
 *
 * Expected files under a resources source folder (on classpath):
 *   /images/dolphin.png
 *   /images/whale.png
 *   /images/seabackgroundl.png
 *   /images/GoodJob.png
 *   /audio/dolphin.mp3
 *   /audio/whale.mp3
 *
 * Author: Tennie White
 * Version: 19
 */
public final class TicTacToe extends Application {

    /* ---------- Domain model ---------- */

    private enum Player {
        DOLPHIN("D", "/images/dolphin.png", "/audio/dolphin.mp3"),
        WHALE  ("W", "/images/whale.png",   "/audio/whale.mp3");

        final String symbol;
        final String imagePath;
        final String audioPath;
        final Image image;

        Player(String symbol, String imagePath, String audioPath) {
            this.symbol = symbol;
            this.imagePath = imagePath;
            this.audioPath = audioPath;
            var url = Objects.requireNonNull(
                    TicTacToe.class.getResource(imagePath),
                    "Missing image resource: " + imagePath
            );
            this.image = new Image(url.toExternalForm());
        }
    }

    /* ---------- Constants ---------- */

    private static final int BOARD_SIZE = 3;
    private static final double GAP = 8.0;
    private static final double PADDING = 12.0;
    private static final double IMAGE_SCALE = 0.80;
    private static final int SCENE_W = 680;
    private static final int SCENE_H = 760;

    // Transparent cells, dark blue borders
    private static final String CELL_STYLE =
            "-fx-background-color: transparent;" +
            "-fx-border-color: #003a8c;" +
            "-fx-border-width: 3;";

    private static final String BACKGROUND_PATH = "/images/seabackgroundl.png";
    private static final String GOODJOB_PATH    = "/images/GoodJob.png";

    // Button styles
    private static final String BTN_BLUE =
            "-fx-background-color: #4da3ff; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 10; -fx-padding: 10 18;";
    private static final String BTN_PINK =
            "-fx-background-color: #ff69b4; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 10; -fx-padding: 10 18;";

    /* ---------- State ---------- */

    private final Player[][] board = new Player[BOARD_SIZE][BOARD_SIZE];
    private final ImageView[][] cellImageViews = new ImageView[BOARD_SIZE][BOARD_SIZE];

    private int moveCount = 0;
    private Player current = Player.DOLPHIN;
    private boolean gameOver = false;

    /* ---------- UI refs ---------- */

    private javafx.scene.control.Label status;
    private Button playAgainBtn;
    private Button exitBtn;
    private ImageView goodJobView;
    private RotateTransition goodJobSpin;
    private RotateTransition statusTwirl;

    /* ---------- JavaFX lifecycle ---------- */

    @Override
    public void start(Stage primaryStage) {
        status = new javafx.scene.control.Label(turnText(current));
        status.setPadding(new Insets(10));
        status.setFont(Font.font(18));
        status.setStyle("-fx-text-fill: #0b294a;"); // readable over light bg

        GridPane boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setHgap(GAP);
        boardGrid.setVgap(GAP);
        boardGrid.setPadding(new Insets(PADDING));

        for (int i = 0; i < BOARD_SIZE; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / BOARD_SIZE);
            cc.setHgrow(Priority.ALWAYS);
            boardGrid.getColumnConstraints().add(cc);

            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / BOARD_SIZE);
            rc.setVgrow(Priority.ALWAYS);
            boardGrid.getRowConstraints().add(rc);
        }

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                StackPane cell = buildCell(r, c);
                boardGrid.add(cell, c, r);
            }
        }

        // Buttons: Play Again (Blue) + Exit (Pink)
        playAgainBtn = new Button("Play Again");
        playAgainBtn.setStyle(BTN_BLUE);
        playAgainBtn.setOnAction(e -> resetGame());
        playAgainBtn.setVisible(false);

        exitBtn = new Button("Exit");
        exitBtn.setStyle(BTN_PINK);
        exitBtn.setOnAction(e -> Platform.exit());

        HBox bottom = new HBox(12, playAgainBtn, exitBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10));

        BorderPane ui = new BorderPane();
        ui.setTop(status);
        BorderPane.setAlignment(status, Pos.CENTER);
        ui.setCenter(boardGrid);
        ui.setBottom(bottom);

        // Background
        Image bgImg = loadClasspathImage(BACKGROUND_PATH);
        ImageView bgView = (bgImg != null) ? new ImageView(bgImg) : null;
        if (bgView != null) {
            bgView.setPreserveRatio(false);
            bgView.setFitWidth(SCENE_W);
            bgView.setFitHeight(SCENE_H);
        } else {
            ui.setStyle("-fx-background-color: linear-gradient(to bottom, #cfefff, #79b9ff);");
        }

        // Good Job overlay
        Image goodImg = loadClasspathImage(GOODJOB_PATH);
        goodJobView = new ImageView(goodImg);
        if (goodImg != null) {
            goodJobView.setVisible(false);
            goodJobView.setPreserveRatio(true);
            goodJobView.setFitWidth(320);
            goodJobView.setFitHeight(320);
        }

        // Animations
        goodJobSpin = new RotateTransition(Duration.millis(1600), goodJobView);
        goodJobSpin.setFromAngle(0);
        goodJobSpin.setToAngle(360);
        goodJobSpin.setCycleCount(4);
        goodJobSpin.setInterpolator(Interpolator.EASE_BOTH);

        statusTwirl = new RotateTransition(Duration.millis(1200), status);
        statusTwirl.setFromAngle(0);
        statusTwirl.setToAngle(360);
        statusTwirl.setCycleCount(2);
        statusTwirl.setInterpolator(Interpolator.EASE_BOTH);

        StackPane layeredRoot = new StackPane();
        if (bgView != null) layeredRoot.getChildren().add(bgView);
        layeredRoot.getChildren().addAll(ui, goodJobView);
        StackPane.setAlignment(goodJobView, Pos.CENTER);

        Scene scene = new Scene(layeredRoot, SCENE_W, SCENE_H);

        scene.widthProperty().addListener((obs, ov, nv) -> {
            if (bgView != null) bgView.setFitWidth(nv.doubleValue());
        });
        scene.heightProperty().addListener((obs, ov, nv) -> {
            if (bgView != null) bgView.setFitHeight(nv.doubleValue());
        });

        primaryStage.setTitle("Tic Tac Toe — Dolphin vs Whale");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /* ---------- Build one cell ---------- */

    private StackPane buildCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setStyle(CELL_STYLE);
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(cell.widthProperty().multiply(IMAGE_SCALE));
        iv.fitHeightProperty().bind(cell.heightProperty().multiply(IMAGE_SCALE));
        cell.getChildren().add(iv);

        cellImageViews[row][col] = iv;

        cell.setOnMouseClicked(e -> {
            if (gameOver || board[row][col] != null) return;

            iv.setImage(current.image);
            board[row][col] = current;
            moveCount++;
            playSound(current.audioPath);

            Player winner = findWinner();
            if (winner != null) { endGame(winnerName(winner) + " wins!"); return; }
            if (moveCount == BOARD_SIZE * BOARD_SIZE) { endGame("It's a tie!"); return; }

            current = (current == Player.DOLPHIN) ? Player.WHALE : Player.DOLPHIN;
            status.setFont(Font.font(status.getFont().getFamily(), FontWeight.NORMAL, 18));
            status.setStyle("-fx-text-fill: #0b294a;");
            status.setText(turnText(current));
        });

        return cell;
    }

    /* ---------- Game logic ---------- */

    private Player findWinner() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] != null &&
                board[i][0] == board[i][1] &&
                board[i][1] == board[i][2]) return board[i][0];

            if (board[0][i] != null &&
                board[0][i] == board[1][i] &&
                board[1][i] == board[2][i]) return board[0][i];
        }
        if (board[0][0] != null &&
            board[0][0] == board[1][1] &&
            board[1][1] == board[2][2]) return board[0][0];

        if (board[0][2] != null &&
            board[0][2] == board[1][1] &&
            board[1][1] == board[2][0]) return board[0][2];

        return null;
    }

    /* ---------- UX helpers ---------- */

    private void endGame(String message) {
        gameOver = true;

        // Big, bold announcement and twirl
        status.setText(message);
        status.setFont(Font.font(status.getFont().getFamily(), FontWeight.BOLD, 28));
        status.setStyle("-fx-text-fill: #09213b; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 2);");
        status.setRotate(0);
        statusTwirl.stop();
        statusTwirl.playFromStart();

        showInfo("Game Over", message);

        // GoodJob overlay spin if available
        if (goodJobView.getImage() != null) {
            goodJobView.setVisible(true);
            goodJobView.setRotate(0);
            goodJobSpin.stop();
            goodJobSpin.playFromStart();
        }

        playAgainBtn.setVisible(true);
    }

    private void resetGame() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                board[r][c] = null;
                if (cellImageViews[r][c] != null) cellImageViews[r][c].setImage(null);
            }
        }
        moveCount = 0;
        current = Player.DOLPHIN;
        gameOver = false;

        // Restore normal status styling
        status.setText(turnText(current));
        status.setFont(Font.font(status.getFont().getFamily(), FontWeight.NORMAL, 18));
        status.setStyle("-fx-text-fill: #0b294a;");
        statusTwirl.stop();
        status.setRotate(0);

        playAgainBtn.setVisible(false);
        goodJobSpin.stop();
        goodJobView.setVisible(false);
        goodJobView.setRotate(0);
    }

    private void playSound(String resourcePath) {
        try {
            var url = Objects.requireNonNull(getClass().getResource(resourcePath));
            new MediaPlayer(new Media(url.toExternalForm())).play();
        } catch (Exception ex) {
            System.err.println("Audio error: " + ex.getMessage());
        }
    }

    private static void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private static String winnerName(Player p) {
        return (p == Player.DOLPHIN) ? "Dolphin" : "Whale";
    }

    private static String turnText(Player p) {
        return (p == Player.DOLPHIN) ? "Dolphin’s turn" : "Whale’s turn";
    }

    /* ---------- Resource helper ---------- */

    private static Image loadClasspathImage(String resourcePath) {
        try {
            var url = TicTacToe.class.getResource(resourcePath);
            if (url != null) return new Image(url.toExternalForm());
        } catch (Exception ignored) {}
        return null;
    }

    /* ---------- Entry ---------- */

    public static void main(String[] args) {
        launch(args);
    }
}

