package game;

import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.*;

public class MainGameController {
   @FXML
    private AnchorPane root;

    @FXML
    private Pane gameArea;

    @FXML
    private ImageView background;

    @FXML
    private ImageView dino;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button restartButton;

    @FXML
    private Button balikMenu;
    
    private int highScore=0;

    private ImageView kaktus, batu;
    private int score = 0;
    private double obstacleSpeed = 5.0;
    private boolean isGameRunning = true;
    boolean isDown = false;

    private double velocityY = 0;
    private final double GRAVITY = 0.1;

    private final Image kaktusImage = new Image(getClass().getResource("/resource/kaktus.png").toExternalForm());
    private final Image batuImage = new Image(getClass().getResource("/resource/batu.png").toExternalForm());
    private final Random random = new Random();

    private long lastUpdateTime = 0;
    private long lastSpawnTime = 0;
    private static final long SPEED_INCREASE_INTERVAL = 1000000000L;
    private static final long OBSTACLE_SPAWN_INTERVAL = 1500000000L;

    Dir dir = Dir.botbot;
    Message message = new Message();
    AlertAlert alertalert = new AlertAlert();

    enum Dir{
        upup,botbot;
    }

    @FXML
    private void backMenu(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("beranda.fxml"));
            Parent mainMenuRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Dashboard Dinorun !");

            Scene scene = new Scene(mainMenuRoot);
            stage.setScene(scene);

            stage.show();
    } catch (Exception e) {
        e.printStackTrace();
        }
    }
    
    private void setupBackground() {
    // Buat objek Image dengan path gambar background
    Image backgroundImage = new Image(getClass().getResource("/resource/background.png").toExternalForm());
    
    // Buat ImageView untuk menampilkan gambar
    background = new ImageView(backgroundImage);
    
    // Atur properti ImageView
    background.setFitWidth(gameArea.getPrefWidth()); // Sesuaikan lebar dengan gameArea
    background.setFitHeight(gameArea.getPrefHeight()); // Sesuaikan tinggi dengan gameArea
    background.setPreserveRatio(false); // Nonaktifkan preserve ratio agar sesuai dengan ukuran gameArea

    // Tambahkan ke game area sebagai elemen paling belakang
    gameArea.getChildren().add(0, background);
}


    public void initialize() {

        kaktus = new ImageView(kaktusImage);
        batu = new ImageView(batuImage);

        configureObstacle(kaktus, 900);
        configureObstacle(batu, 1100);

        kaktus.setVisible(false);
        batu.setVisible(false);

        gameArea.getChildren().addAll(kaktus, batu);

        root.setOnKeyPressed(this::handleKeyPress);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isGameRunning) {
                    moveObstacle(kaktus);
                    moveObstacle(batu);

                    updateDinoPosition();

                    checkCollision();

                    if (now - lastSpawnTime > OBSTACLE_SPAWN_INTERVAL) {
                        spawnObstacle();
                        lastSpawnTime = now;
                    }

                    if (now - lastUpdateTime > SPEED_INCREASE_INTERVAL) {
                        increaseObstacleSpeed();
                        lastUpdateTime = now;
                    }

                    updateScore();
                }
            }
        };
        
        gameLoop.start();
    }

private void configureObstacle(ImageView obstacle, double x) {
        obstacle.setFitWidth(90);
        obstacle.setFitHeight(60);
        obstacle.setLayoutX(x);
        obstacle.setLayoutY(440);
        obstacle.setPreserveRatio(true);
    }

    private void moveObstacle(ImageView obstacle) {
        if (obstacle.isVisible()) {
            obstacle.setLayoutX(obstacle.getLayoutX() - obstacleSpeed);

            if (obstacle.getLayoutX() + obstacle.getFitWidth() < 0) {
                obstacle.setVisible(false);
            }
        }
    }

    private void spawnObstacle() {
        if (!kaktus.isVisible() && !batu.isVisible()) {
            if (random.nextBoolean()) {
                kaktus.setLayoutX(gameArea.getPrefWidth());
                kaktus.setVisible(true);
                kaktus.setManaged(true);
            } else {
                batu.setLayoutX(gameArea.getPrefWidth());
                batu.setVisible(true);
                batu.setManaged(true);
            }
            ensureMinDistance();
        }
    }

    private void ensureMinDistance() {
        if (Math.abs(kaktus.getLayoutX() - batu.getLayoutX()) < 150) {
            if(kaktus.isVisible()){
                batu.setLayoutX(kaktus.getLayoutX() + 200);
            }else if (batu.isVisible()) {
                kaktus.setLayoutX(batu.getLayoutX() + 200);
            }
        }
    }

    private void increaseObstacleSpeed() {
        obstacleSpeed += 0.1;
    }

    private void updateDinoPosition() {
        velocityY += GRAVITY;

        if(dino.getLayoutY() <= 250){
            dir = Dir.botbot;
        }

        if(isDown) dino.setLayoutY(dino.getLayoutY() + 9);

        if(dir == Dir.upup){
            dino.setLayoutY(dino.getLayoutY() - 13);
        }
        else if(dir == Dir.botbot){
            dino.setLayoutY(dino.getLayoutY() + velocityY);
        }

        if (dino.getLayoutY() > 400) {
            dino.setLayoutY(400);
            velocityY = 0;
        }
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                if (dino.getLayoutY() == 400 ) dir = Dir.upup;
                isDown = false;
                break;
            case DOWN:
                dir = Dir.botbot;
                isDown = true;
                break;
            case P:
                isGameRunning=!isGameRunning;
                break;
            case SPACE:
            if(!isGameRunning){
                restartGame();
            }
        }
    }

    private void checkCollision() {
        double dinoLeft = dino.getLayoutX();
        double dinoRight = dinoLeft + dino.getFitWidth();
        double dinoTop = dino.getLayoutY();
        double dinoBottom = dinoTop + dino.getFitHeight();

        if (kaktus.isVisible()) {
            double kaktusLeft = kaktus.getLayoutX() + 40;
            double kaktusRight = kaktusLeft + kaktus.getFitWidth() - 80;
            double kaktusTop = kaktus.getLayoutY() + 8 ;
            double kaktusBottom = kaktusTop + kaktus.getFitHeight();

            if (dinoRight > kaktusLeft && dinoLeft < kaktusRight &&
                dinoBottom > kaktusTop && dinoTop < kaktusBottom) {
                isGameRunning = false;
                gameOver();
                return;
            }
        }

        if (batu.isVisible()) {
            double batuLeft = batu.getLayoutX() + 40;
            double batuRight = batuLeft + batu.getFitWidth() - 80;
            double batuTop = batu.getLayoutY() + 5;
            double batuBottom = batuTop + batu.getFitHeight();

            if (dinoRight > batuLeft && dinoLeft < batuRight &&
                dinoBottom > batuTop && dinoTop < batuBottom) {
               gameOver();
            }
        }
    }
private void updateScore() {
    double dinoRight = dino.getLayoutX() + dino.getFitWidth();

    if (batu.isVisible()&&batu.isManaged()) {
        double batuRight = batu.getLayoutX() + batu.getFitWidth();
        if (dinoRight > batuRight) {
            score++;
            batu.setManaged(false);
        }
    }

    if (kaktus.isVisible()&& kaktus.isManaged()) {
        double kaktusRight = kaktus.getLayoutX() + kaktus.getFitWidth();
        if (dinoRight > kaktusRight) {
            score++;
            kaktus.setManaged(false);
        }
    }
    scoreLabel.setText("Score: " + score);
}
    @FXML
    private void restartGame() {
        isGameRunning = true;
        score = 0;
        scoreLabel.setText("Score: 0");
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);
        scoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: normal; -fx-text-fill: black; -fx-alignment: center;");

        restartButton.setVisible(false);
        dino.requestFocus();

        kaktus.setLayoutX(700);
        kaktus.setVisible(false);
        kaktus.setManaged(true);
        batu.setLayoutX(800);
        batu.setVisible(false);
        batu.setManaged(true);

        obstacleSpeed = 5.0;
        lastUpdateTime = System.nanoTime();
        lastSpawnTime = System.nanoTime();
    }
    
    private void loadHighScore() {
    try (BufferedReader reader = new BufferedReader(new FileReader("leaderboard.txt"))) {
        highScore = Integer.parseInt(reader.readLine());
    } catch (IOException | NumberFormatException e) {
        highScore = 0;
    }
}

private void saveHighScore() {
    if (score > highScore) {
        highScore = score;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("leaderboard.txt"))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    @FXML
    void downDINO(KeyEvent event) {}
    @FXML
    void moveDINO(KeyEvent event) {}

private void gameOver() {
        isGameRunning = false;
        saveHighScore();
        message.setPesan("    Game Over!\nFinal Score: ");
        scoreLabel.setText(message.getPesan() + score+"\nPress SPACE to Restart");

        scoreLabel.setLayoutX(347);
        scoreLabel.setLayoutY(174);

        scoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: brown; -fx-alignment: center;");

        restartButton.setVisible(true);

        alertalert.setPesan("GAME OVER");
        message.setPesan("Thank you for playing, your score : ");

        showAlert(Alert.AlertType.INFORMATION, alertalert.getPesan(), message.getPesan() + score );
        showAlert(Alert.AlertType.INFORMATION, "Game Over", "Final Score: " + score + "\nHigh Score: " + highScore);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    abstract class Write{
        String pesan;
        public abstract void setPesan(String pesan);
        public abstract String getPesan();
    }

    class Message extends Write{
        @Override
        public void setPesan(String pesan){
            this.pesan = pesan;
        }
        @Override
        public String getPesan(){
            return pesan;
        }
    }

    class AlertAlert extends Write{
        @Override
        public void setPesan(String pesan){
            this.pesan = pesan;
        }

        @Override
        public String getPesan(){
            return pesan;
        }
    }
}
