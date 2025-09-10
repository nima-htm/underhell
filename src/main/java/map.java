import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;

import java.util.*;

public class map extends Application {
    private Scene scene;
    private ImageView player;
    private final int TILE_SIZE = 40;
    private final int MAP_WIDTH = 50;
    private final int MAP_HEIGHT = 30;
    private boolean RightDir=true;

    // CHANGED: چند «درِ معما» با کاراکتر '?' به نقشه اضافه شد (طول ثابت ماند)
    private String[] mapData = {
            "##################################################",
            "#.............M?.................................#", // ? در این ردیف
            "#@$......?.......................................#", // ? در این ردیف
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#.~.................?...........#................#", // ? در این ردیف
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#",
            "#................................................#"
    };

    //coordinates
    private final Set<Point2D> hiddenKeysClaimed = new HashSet<>();
    private Set<Point2D> puzzleLocations= Set.of(new Point2D(3, 2), new Point2D(MAP_WIDTH - 5, 5));

    private Set<Point2D> doorLocation  = Set.of(new Point2D(2, 2));  // top-right D
    private Set<Point2D> doorLocation2 = Set.of(new Point2D(1, 2));  // bottom-left D

    private Set<Point2D> keyLocation = Set.of(new Point2D(3, 1));
    private Set<Point2D> hiddenkey= Set.of(new Point2D(MAP_WIDTH / 2 + 1, MAP_HEIGHT / 2));

    private Image playerWalkGif = new Image(getClass().getResourceAsStream("/R.gif"));
    private Image playerWalkGiff = new Image(getClass().getResourceAsStream("/L.gif"));

    private Image playerIdleImageRight = new Image(getClass().getResourceAsStream("/Stand(R).png"));
    private Image playerIdleImageLeft = new Image(getClass().getResourceAsStream("/Stand(L).png"));
    private Image water = new Image(getClass().getResourceAsStream("/w.gif"));
    private Image wood = new Image(getClass().getResourceAsStream("/wood.jpg"));

    private double initialPlayerX = 1 * TILE_SIZE + 500;
    private double initialPlayerY = 1 * TILE_SIZE + 500;
    Player p = new Player("mari",100,1);
    Item items = new Item(p);

    private Group world = new Group(); // Holds entire world (tiles + player)

    // NEW: برای تغییر تصویرِ یک خانه بعد از حل معما
    private ImageView[][] tileViews = new ImageView[MAP_HEIGHT][MAP_WIDTH];

    // NEW: ردیف/ستون‌هایی از درهای معما که حل شده‌اند
    private final Set<Point2D> puzzleDoorsCleared = new HashSet<>();

    public void resetPlayerPosition() {
        player.setTranslateX(initialPlayerX);
        player.setTranslateY(initialPlayerY);
    }

    @Override
    public void start(Stage stage) {
        Pane root = new Pane(world);
        GameSession.get().setPlayer(p);
        GameSession.get().setItems(items);
        Image wallImage = new Image(getClass().getResourceAsStream("/R.jfif"));
        Image floorImage = new Image(getClass().getResourceAsStream("/OIP.jfif"));
        Image doorImage = new Image(getClass().getResourceAsStream("/dd.png"));
        Image RedDoorImage = new Image(getClass().getResourceAsStream("/reddoor.png"));
        Image New = new Image(getClass().getResourceAsStream("/flower.gif"));
        Image ClosedDoorImage = new Image(getClass().getResourceAsStream("/trap.png"));

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                char tile = mapData[y].charAt(x);
                ImageView tileView = new ImageView();
                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                tileView.setTranslateX(x * TILE_SIZE);
                tileView.setTranslateY(y * TILE_SIZE);

                switch (tile) {
                    case '#': tileView.setImage(wallImage); break;
                    case '.': tileView.setImage(floorImage); break;
                    case '~': tileView.setImage(water); break;
                    case '=': tileView.setImage(wood); break;
                    case '@': tileView.setImage(doorImage); break;
                    case '$': tileView.setImage(RedDoorImage); break;
                    case 'M': tileView.setImage(New); break;
                    case '?': tileView.setImage(ClosedDoorImage); break;
                    default: tileView.setImage(floorImage); break;
                }
                world.getChildren().add(tileView);
                tileViews[y][x] = tileView;
            }
        }

        player = new ImageView(playerIdleImageRight);
        player.setFitWidth(TILE_SIZE - 10);
        player.setFitHeight(TILE_SIZE - 10);
        player.setTranslateX(initialPlayerX);
        player.setTranslateY(initialPlayerY);
        world.getChildren().add(player);

        this.scene = new Scene(root, 1280, 720); // fixed screen size
        scene.setOnKeyPressed(event -> {
            int dx = 0, dy = 0;
            if (event.getCode() == KeyCode.W) dy = -1;
            if (event.getCode() == KeyCode.S) dy = 1;
            if (event.getCode() == KeyCode.A) dx = -1;
            if (event.getCode() == KeyCode.D) dx = 1;
            movePlayer(dx, dy, stage);
        });

        root.setStyle("-fx-background-color: black;");
        stage.setTitle("UnderHell");
        stage.setScene(scene);
        stage.show();

        centerCamera(stage); // initial centering
    }

    private void movePlayer(int dx, int dy, Stage stage) {

        if (dx == 0 && dy == 0)
            player.setImage(playerIdleImageRight);

        int x = (int) (player.getTranslateX() / TILE_SIZE);
        int y = (int) (player.getTranslateY() / TILE_SIZE);
        int newX = x + dx;
        int newY = y + dy;
        Point2D newPos = new Point2D(newX, newY);

        if (newX < 0 || newY < 0 || newX >= MAP_WIDTH || newY >= MAP_HEIGHT) return;
        char nextTile = mapData[newY].charAt(newX);

        // NEW: درِ معما — اگر قبلاً باز نشده:
        if (nextTile == '?' && !puzzleDoorsCleared.contains(newPos)) {
            boolean ok = showPuzzleDialog(stage); // حل معما
            if (ok) {
                puzzleDoorsCleared.add(newPos);      // علامت‌گذاری به‌عنوان باز شده
                replaceMapChar(newX, newY, '.');     // در را به کف تبدیل کن
                tileViews[newY][newX].setImage(new Image(getClass().getResourceAsStream("/OIP.jfif"))); // رندر کف
                // اجازه‌ی عبور می‌دهیم و ادامه‌ی حرکت پایین انجام می‌شود
            } else {
                return; // پاسخ غلط → اجازه عبور نده
            }
        }

        if (nextTile == '#') return;
        if (nextTile == '~' && !hiddenkey.contains(newPos)) {
            gameOver(stage);
            return;
        }

        if (doorLocation.contains(newPos)) {
            switchToBattleScene(stage);
            BattleManager battleManager = new BattleManager(this, scene, stage);
            battleManager.start(stage);
        }

        if (doorLocation2.contains(newPos)) {
            switchToBattleScene(stage);
            UndyneStage battleManager = new UndyneStage(this, scene, stage);
            battleManager.start(stage);
        }

        if (hiddenkey.contains(newPos) && !hiddenKeysClaimed.contains(newPos)) {
            hiddenKeysClaimed.add(newPos);  // mark this spot as already picked up
            hiddenKey(stage);               // show your OK dialog
        }

        if (dy == 0 && dx < 0) {
            player.setImage(playerWalkGiff);
            RightDir = false;
        }
        else if (dy == 0 && dx > 0){
            player.setImage(playerWalkGif);
            RightDir = true;
        }
        else if(dy != 0 && x<=25){
            player.setImage(playerWalkGif);
        }
        else if(dy != 0 && x>25){
            player.setImage(playerWalkGiff);
        }

        player.setTranslateX(newX * TILE_SIZE + 5);
        player.setTranslateY(newY * TILE_SIZE + 5);

        PauseTransition delay = new PauseTransition(Duration.millis(200));
        if(dy == 0 && RightDir)
            delay.setOnFinished(e -> player.setImage(playerIdleImageRight));
        else if(dy == 0 && !RightDir)
            delay.setOnFinished(e -> player.setImage(playerIdleImageLeft));
        else if(dy != 0 && x<=25)
            delay.setOnFinished(e -> player.setImage(playerIdleImageRight));
        else if(dy != 0 && x>25)
            delay.setOnFinished(e -> player.setImage(playerIdleImageLeft));

        delay.play();

        centerCamera(stage);

        if (puzzleLocations.contains(newPos))
            showPuzzleDialog(stage); // (این یکی فقط پیام می‌ده—می‌تونی حذفش کنی)
        if (keyLocation.contains(newPos))
            showKey(stage);
    }

    // NEW: جایگزینی کاراکتر در mapData (برای باز کردن دائمی درِ معما)
    private void replaceMapChar(int x, int y, char c) {
        char[] row = mapData[y].toCharArray();
        row[x] = c;
        mapData[y] = new String(row);
    }

    private void centerCamera(Stage stage) {
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();

        double playerX = player.getTranslateX();
        double playerY = player.getTranslateY();

        double offsetX = sceneWidth / 2 - playerX;
        double offsetY = sceneHeight / 2 - playerY;

        double maxOffsetX = 0;
        double maxOffsetY = 0;
        double minOffsetX = sceneWidth - MAP_WIDTH * TILE_SIZE;
        double minOffsetY = sceneHeight - MAP_HEIGHT * TILE_SIZE;

        offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, offsetX));
        offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, offsetY));

        world.setTranslateX(offsetX);
        world.setTranslateY(offsetY);
    }

    private void switchToBattleScene(Stage stage) {
        Label battleLabel = new Label("Entering Hell...");
        battleLabel.setTextFill(Color.WHITE);
        battleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        StackPane battleRoot = new StackPane(battleLabel);
        battleRoot.setStyle("-fx-background-color: black;");

        Scene battleScene = new Scene(battleRoot, 1000, 800);

        stage.setScene(battleScene);
        stage.setFullScreen(false);
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.centerOnScreen();
    }

    private void applyDarkTheme(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        dialog.setGraphic(null);
        pane.setGraphic(null);
        pane.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 2px;");
        var body = pane.lookup(".content.label");
        if (body instanceof Label lbl) {
            lbl.setTextFill(Color.WHITE);
            lbl.setStyle("-fx-font-size: 16px;");
        }
        var headerLabel = pane.lookup(".header-panel .label");
        if (headerLabel instanceof Label lbl2) {
            lbl2.setTextFill(Color.WHITE);
            lbl2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        }
        var textField = pane.lookup(".text-input");
        if (textField instanceof TextField tf) {
            tf.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-prompt-text-fill: #ffffff; -fx-border-color: white; -fx-border-width: 1px;");
        }
        List<ButtonType> keep = new ArrayList<>();
        for (ButtonType bt : pane.getButtonTypes()) if (bt == ButtonType.OK) keep.add(bt);
        if (keep.isEmpty()) keep.add(ButtonType.OK);
        pane.getButtonTypes().setAll(keep);
        for (ButtonType bt : pane.getButtonTypes()) {
            Button b = (Button) pane.lookupButton(bt);
            if (b != null) b.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 1px; -fx-cursor: hand;");
        }
    }

    private void showErrorDialog(Stage stage, String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        applyDarkTheme(alert);
        alert.showAndWait();
    }


    private boolean showPuzzleDialog(Stage stage) {
        Random random = new Random();
        int num1 = random.nextInt(100);
        int num2 = random.nextInt(100);

        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(stage);
        dialog.setTitle("Puzzle Time!");
        dialog.setHeaderText("Solve the puzzle to proceed");
        dialog.setContentText("What is " + num1 + " + " + num2 + " ?");

        applyDarkTheme(dialog);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String s = result.get().trim();

            if (s.isEmpty()) {
                showErrorDialog(stage, "Input required!", "Please enter a number.");
                return false;
            }

            try {
                int val = Integer.parseInt(s);
                if (val == num1 + num2) {
                    return true;
                } else {
                    showErrorDialog(stage, "Wrong answer!", "You cannot pass.");
                    return false;
                }
            } catch (NumberFormatException e) {
                showErrorDialog(stage, "Invalid input!", "Please enter a valid number.");
                return false;
            }
        }

        showErrorDialog(stage, "Input required!", "Please enter a number.");
        return false;
    }


    private void showKey(Stage stage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("A Mysterious Door!");
        dialog.setHeaderText("A locked door blocks your path.");
        dialog.setContentText("Enter the key to proceed:");

        ImageView imageView = new ImageView(new Image(getClass().getResource("/d.jfif").toExternalForm()));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        dialog.setGraphic(imageView);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("BRILLIANT!~");
        alert.setHeaderText("YOU FOUND A SPECIAL KEY!");
        alert.setContentText("Press OK to continue.");
        alert.initOwner(stage);

        Image img = new Image(getClass().getResource("/key.png").toExternalForm());
        ImageView iv = new ImageView(img);
        iv.setFitWidth(100);
        iv.setFitHeight(100);
        alert.setGraphic(iv);

        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        pane.getButtonTypes().setAll(ButtonType.OK);

        alert.setOnHidden(e -> stage.getScene().getRoot().requestFocus());
        alert.show();
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


