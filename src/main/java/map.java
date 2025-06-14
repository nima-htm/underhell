import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

import java.util.Optional;
import java.util.Set;

public class map extends Application {
    private Scene scene;
    ImageView player;
    final int TILE_SIZE = 40;
    final int MAP_WIDTH = 20;
    final int MAP_HEIGHT = 15;

    String[] mapData = {
            "####################",
            "#........#.........#",
            "#.######.#.#####...#",
            "#.#....#.#.....#...#",
            "#.#.##.#.###.#.###.#",
            "#.#....#.....#.....#",
            "#.~~~~~~.###########",
            "#.~~~~~~...........#",
            "#.======..........@#",
            "#.~~~~~~...........#",
            "#.~~~~~~..###......#",
            "#.............######",
            "######........~~~~~#",
            "#....#........~~~~~#",
            "####################"
    };


    private Set<Point2D> puzzleLocations = Set.of(new Point2D(4, 4), new Point2D(8, 2));
    private Set<Point2D> doorLocation = Set.of(new Point2D(18, 8)); // Change coordinates as needed
    private Set<Point2D> keyLocation = Set.of(new Point2D(6, 13));
    private Set<Point2D> hiddenkey = Set.of(new Point2D(14, 13));
    Image playerWalkGif = new Image(getClass().getResourceAsStream("/R.gif")); // Ensure the file is in `resources`
    Image playerIdleImage = new Image(getClass().getResourceAsStream("/download.png"));
    Image water = new Image(getClass().getResourceAsStream("/w.gif"));
    Image wood = new Image(getClass().getResourceAsStream("/wood.jpg"));

    private double initialPlayerX = 1 * TILE_SIZE + 5;
    private double initialPlayerY = 1 * TILE_SIZE + 5;

    public void resetPlayerPosition() {
        player.setTranslateX(initialPlayerX);
        player.setTranslateY(initialPlayerY);
    }

    @Override
    public void start(Stage stage) {

        Pane root = new Pane();
        root.setPrefSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        player = new ImageView(playerIdleImage);
        player.setFitWidth(TILE_SIZE - 10);
        player.setFitHeight(TILE_SIZE - 10);
        player.setTranslateX(1 * TILE_SIZE + 5);
        player.setTranslateY(1 * TILE_SIZE + 5);

        Image wallImage = new Image(getClass().getResourceAsStream("/R.jfif"));
        Image floorImage = new Image(getClass().getResourceAsStream("/OIP.jfif"));
        Image doorImage = new Image(getClass().getResourceAsStream("/dd.png"));

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                char tile = mapData[y].charAt(x);
                ImageView tileView = new ImageView();

                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                tileView.setTranslateX(x * TILE_SIZE);
                tileView.setTranslateY(y * TILE_SIZE);

                if (tile == '#') {
                    tileView.setImage(wallImage);
                } else if (tile == '.') {
                    tileView.setImage(floorImage);
                } else if (tile == '=') {
                    tileView.setImage(wood);
                } else if (tile == '@') {
                    tileView.setImage(doorImage); // Define doorImage like other terrain images
                } else {
                    tileView.setImage(water);
                }

                root.getChildren().add(tileView);
            }
        }


        root.getChildren().add(player);

        this.scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            int dx = 0, dy = 0;
            if (event.getCode() == KeyCode.W) dy = -1;
            if (event.getCode() == KeyCode.S) dy = 1;
            if (event.getCode() == KeyCode.A) dx = -1;
            if (event.getCode() == KeyCode.D) dx = 1;
            movePlayer(dx, dy, stage, root);
        });
        root.setStyle("-fx-background-color: black;");
        stage.setTitle("UnderHell");
        stage.setScene(scene);
        stage.show();
    }

    private void movePlayer(int dx, int dy, Stage stage, Pane root) {
        if (dx == 0 && dy == 0) {
            player.setImage(playerIdleImage); // No movement → idle image
            return;
        }

        int x = (int) (player.getTranslateX() / TILE_SIZE);
        int y = (int) (player.getTranslateY() / TILE_SIZE);

        int newX = x + dx;
        int newY = y + dy;

        Point2D newPos = new Point2D(newX, newY);

        if (doorLocation.contains(newPos)) {
            try {
                switchToBattleScene(stage, root);

                BattleManager battleManager = new BattleManager(this, scene, stage);
                battleManager.start(stage);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (newX < 0 || newY < 0 || newX >= MAP_WIDTH || newY >= MAP_HEIGHT) return;
        if (mapData[newY].charAt(newX) == '#') return;
        if (mapData[newY].charAt(newX) == '~' && !hiddenkey.contains(new Point2D(newX, newY))) {
            gameOver(stage);
        }
        if (hiddenkey.contains(new Point2D(newX, newY))) {
            hiddenKey(stage);
        }

        // Switch to animated walk gif when moving
        player.setImage(playerWalkGif);
        player.setTranslateX(newX * TILE_SIZE + 5);
        player.setTranslateY(newY * TILE_SIZE + 5);


        PauseTransition delay = new PauseTransition(Duration.millis(200));
        delay.setOnFinished(e -> player.setImage(playerIdleImage));
        delay.play();

        if (puzzleLocations.contains(newPos)) {
            showPuzzleDialog(stage);
        }
        if (keyLocation.contains(newPos)) {
            showKey(stage);
        }
    }

    private void switchToBattleScene(Stage stage, Pane gameRoot) {
        Label battleLabel = new Label("Entering Hell...");
        battleLabel.setTextFill(Color.WHITE);
        battleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        StackPane battleRoot = new StackPane(battleLabel);
        battleRoot.setStyle("-fx-background-color: #000000;");

        Scene battleScene = new Scene(battleRoot, 1000, 800); // Larger size

        stage.setScene(battleScene);
        stage.setFullScreen(false);
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.centerOnScreen();
    }

    private void showPuzzleDialog(Stage stage) {
        Random random = new Random();
        int num1 = random.nextInt(100);
        int num2 = random.nextInt(100);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Puzzle Time!");
        dialog.setHeaderText("Solve the puzzle to proceed");
        dialog.setContentText("What is " + num1 + " + " + num2 + " ?");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String answer = result.get().trim();
            if (!answer.equals(num1 + num2)) {
                System.out.println(answer);
            }
        }
    }

    private void showKey(Stage stage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("A Mysterious Door!");
        dialog.setHeaderText("A locked door blocks your path.");
        dialog.setContentText("Enter the key to proceed:");

        Image doorImage = new Image(getClass().getResource("/d.jfif").toExternalForm()); // Your door image
        ImageView imageView = new ImageView(doorImage);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        dialog.setGraphic(imageView);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        TextField inputField = dialog.getEditor();

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(answer -> {
            String trimmed = answer.trim();
            if (trimmed.equalsIgnoreCase("moonkey")) {
                System.out.println("Correct key! Door opens.");
            } else {
                System.out.println("Wrong key.");
            }
        });
    }
    private void hiddenKey(Stage stage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("BRILLIANT!~");
        dialog.setHeaderText("YOU FOUND A  SPECIAL KEY!");

        Image doorImage = new Image(getClass().getResource("/key.png").toExternalForm()); // Your door image
        ImageView imageView = new ImageView(doorImage);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        dialog.setGraphic(imageView);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());


        Optional<String> result = dialog.showAndWait();
        result.ifPresent(answer -> {

        });
    }
    private void gameOver(Stage stage) {

        Label gameOverLabel = new Label("YOU DIED...");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");
        StackPane gameOverRoot = new StackPane(gameOverLabel);
        gameOverRoot.setStyle("-fx-background-color: black;");

        Scene gameOverScene = new Scene(gameOverRoot, 800, 600);
        stage.setScene(gameOverScene);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreenExitHint(" ");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.centerOnScreen();


        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> Platform.exit());
        delay.play();
    }

    public static void main(String[] args) {
        launch();
    }
}
