import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

import java.util.Optional;
import java.util.Set;

public class map extends Application {
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

    Image playerWalkGif = new Image(getClass().getResourceAsStream("/R.gif")); // Ensure the file is in `resources`
    Image playerIdleImage = new Image(getClass().getResourceAsStream("/download.png"));
    Image water = new Image(getClass().getResourceAsStream("/w.gif"));
    Image wood = new Image(getClass().getResourceAsStream("/wood.jpg"));
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
        Image doorImage= new Image(getClass().getResourceAsStream("/dd.png"));

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
                }else if (tile == '@') {
                    tileView.setImage(doorImage); // Define doorImage like other terrain images
                }
                else {
                    tileView.setImage(water);
                }

                root.getChildren().add(tileView);
            }
        }


        root.getChildren().add(player);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            int dx = 0, dy = 0;
            if (event.getCode() == KeyCode.W) dy = -1;
            if (event.getCode() == KeyCode.S) dy = 1;
            if (event.getCode() == KeyCode.A) dx = -1;
            if (event.getCode() == KeyCode.D) dx = 1;
            movePlayer(dx, dy, stage,root);
        });
        root.setStyle("-fx-background-color: black;");
        stage.setTitle("UnderHell");
        stage.setScene(scene);
        stage.show();
    }

    private void movePlayer(int dx, int dy, Stage stage,Pane root) {
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
                BattleManager battleManager = new BattleManager();
                Stage currentStage = new Stage();


                battleManager.start(currentStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (newX < 0 || newY < 0 || newX >= MAP_WIDTH || newY >= MAP_HEIGHT) return;
        if (mapData[newY].charAt(newX) == '#') return;
        if (mapData[newY].charAt(newX) == '~') return;

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

    public static void main(String[] args) {
        launch();
    }
}
