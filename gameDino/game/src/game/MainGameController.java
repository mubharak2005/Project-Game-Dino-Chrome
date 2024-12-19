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
    
    //mengatur gerak rintangan kaktus dan batu
    private ImageView kaktus, batu;
    private int score = 0;
    private double obstacleSpeed = 5.0;
    private boolean isGameRunning = true;
    boolean isDown = false;

    private double velocityY = 0;
    private final double GRAVITY = 0.1;
    
    //mengambil gambar kaktus dan batu dari file resource
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
