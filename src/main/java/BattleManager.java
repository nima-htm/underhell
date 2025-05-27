import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

public class BattleManager extends Application {

    private Rectangle battleBox;
    private Rectangle playerHPBackground;
    private Pane dialogueBar;
    private Text dialogueText;
    private Path heart;
    private Image villainImg;
    private ImageView villainImage;
    private GameState currentState = GameState.PLAYER_CHOICE_OPTIONS;
    private Button t_option1, t_option2, t_option3;
    private Button heal;
    Player player = new Player("Mari", 100, 1);
    Button fightButton = new Button("FIGHT");
    Button itemButton = new Button("ITEM");
    Button talkButton = new Button("TALK");
    Alastor alastor = new Alastor(100);

    @Override
    public void start(Stage stage) {
        alastor.setPlayer(player);
        Item healpotion = new Item(player);
        Pane root = new Pane();
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root, 500, 450, Color.BLUE);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setMaximized(true);
        stage.setResizable(false);
        Text playerNameText = new Text(player.getName());
        playerNameText.setFill(Color.WHITE);
        playerNameText.getStyleClass().add("game-label");

        Text playerLevelText = new Text("Lv. " + player.getLevel());
        playerLevelText.setFill(Color.WHITE);
        playerLevelText.getStyleClass().add("game-label");

        battleBox = new Rectangle(500, 300);
        battleBox.setStroke(Color.WHITE);
        battleBox.setStrokeWidth(2);

        battleBox.xProperty().bind(scene.widthProperty().subtract(battleBox.widthProperty()).divide(4));
        battleBox.yProperty().bind(scene.heightProperty().subtract(battleBox.heightProperty()).divide(2).subtract(40));

        heart = createHeartShape(20, Color.RED);
        heart.setLayoutX(scene.getWidth() / 2);
        heart.setLayoutY(scene.getHeight() / 2);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!heart.isPressed()) {
                heart.setLayoutX(newVal.doubleValue() / 2);
            }
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!heart.isPressed()) {
                heart.setLayoutY(newVal.doubleValue() / 2);
            }
        });


        Rectangle playerHPBackground = new Rectangle();
        playerHPBackground.setFill(Color.GREEN);
        playerHPBackground.widthProperty().bind(
                player.getHp().divide(100.0).multiply(scene.widthProperty().multiply(0.25))
        );
        playerHPBackground.setHeight(10);
        playerHPBackground.xProperty().bind(scene.widthProperty().subtract(playerHPBackground.widthProperty()).divide(2));
        playerHPBackground.yProperty().bind(Bindings.add(battleBox.yProperty(), battleBox.heightProperty()).add(40));

        Rectangle playerHPFrame = new Rectangle();
        playerHPFrame.setWidth(320);
        playerHPFrame.setHeight(10);
        playerHPFrame.setStroke(Color.WHITE);
        playerHPFrame.setFill(Color.TRANSPARENT);
        playerHPFrame.setStrokeWidth(2);
        playerHPFrame.xProperty().bind(scene.widthProperty().subtract(playerHPFrame.getWidth()).divide(2));
        playerHPFrame.yProperty().bind(Bindings.add(battleBox.yProperty(), battleBox.heightProperty()).add(40));

        playerNameText.setFill(Color.WHITE);
        playerNameText.getStyleClass().add("player-info");
        playerNameText.yProperty().bind(playerHPFrame.yProperty().add(10)); // align vertically
        playerNameText.xProperty().bind(playerHPFrame.xProperty().subtract(60)); // left side with spacing
        playerLevelText.setFill(Color.WHITE);
        playerLevelText.getStyleClass().add("player-info");
        playerLevelText.yProperty().bind(playerHPFrame.yProperty().add(10)); // align vertically
        playerLevelText.xProperty().bind(playerHPFrame.xProperty().add(playerHPFrame.widthProperty()).add(20)); // right side with spacing


        villainImg = new Image(getClass().getResource("/villain.png").toExternalForm());
        villainImage = new ImageView(villainImg);
        villainImage.setFitWidth(300);
        villainImage.setFitHeight(300);
        villainImage.layoutXProperty().bind(scene.widthProperty().subtract(villainImage.fitWidthProperty()).subtract(120));
        villainImage.layoutYProperty().bind(scene.heightProperty().multiply(0.15));
        alastor.setRoot(root);
        alastor.setHeart(heart);

        fightButton.setPrefWidth(120);
        fightButton.setPrefHeight(50);
        itemButton.setPrefWidth(120);
        itemButton.setPrefHeight(50);
        talkButton.setPrefWidth(120);
        talkButton.setPrefHeight(50);


        fightButton.layoutXProperty().bind(scene.widthProperty().multiply(0.33).subtract(fightButton.widthProperty().divide(2)));
        itemButton.layoutXProperty().bind(scene.widthProperty().multiply(0.5).subtract(itemButton.widthProperty().divide(2)));
        talkButton.layoutXProperty().bind(scene.widthProperty().multiply(0.67).subtract(talkButton.widthProperty().divide(2)));

        fightButton.layoutYProperty().bind(Bindings.add(playerHPBackground.yProperty(), 90));
        itemButton.layoutYProperty().bind(fightButton.layoutYProperty());
        talkButton.layoutYProperty().bind(fightButton.layoutYProperty());

        fightButton.getStyleClass().add("game-button");
        itemButton.getStyleClass().add("game-button");
        talkButton.getStyleClass().add("game-button");

        itemButton.setOnAction(e -> {
            if (currentState == GameState.ENEMY_TURN) return; // Only block during enemy turn
            currentState = GameState.PLAYER_CHOICE_ITEM;
            options_visibility(fightButton, talkButton, itemButton, false); // keep all visible
            item_options_visibility(true);
            talk_options_visibility(false); // just in case
        });


        talkButton.setOnAction(e -> {
            if (currentState == GameState.ENEMY_TURN) return;
            currentState = GameState.PLAYER_CHOICE_TALK;
            options_visibility(fightButton, talkButton, itemButton, false);
            talk_options_visibility(true);
            item_options_visibility(false);
        });


        t_option1 = createTalkOption("Plead", scene, 0);
        t_option2 = createTalkOption("Insult", scene, 1);
        t_option3 = createTalkOption("Stay Silent", scene, 2);
        heal = createTalkOption("Heal", scene, 1);
        heal.setOnAction(e -> {
            healpotion.hpUp();
            handlePlayerChoice3("now it's my turn");
        });
        t_option1.setOnAction(e -> handlePlayerChoice("You plead. The villain chuckles."));
        t_option2.setOnAction(e -> handlePlayerChoice("You insult the villain. Its eyes glow red."));
        t_option3.setOnAction(e -> handlePlayerChoice("You stay silent. The air grows heavy."));


        Rectangle dialogueBackground = new Rectangle();
        dialogueBackground.widthProperty().bind(scene.widthProperty().multiply(0.15));
        dialogueBackground.heightProperty().bind(scene.heightProperty().multiply(0.2));
        dialogueBackground.setFill(Color.rgb(0, 0, 0, 0.8));
        dialogueBackground.setArcWidth(20);
        dialogueBackground.setArcHeight(50);
        dialogueBackground.setStroke(Color.WHITE);
        dialogueBackground.setStrokeWidth(2);

        dialogueText = new Text();
        dialogueText.setFill(Color.WHITE);
        dialogueText.getStyleClass().add("game-dialogue-text");
        dialogueText.wrappingWidthProperty().bind(dialogueBackground.widthProperty().subtract(20));


        dialogueBar = new StackPane(dialogueBackground, dialogueText);
        dialogueBar.layoutXProperty().bind(scene.widthProperty().subtract(dialogueBackground.widthProperty()).subtract(400));
        dialogueBar.layoutYProperty().bind(scene.heightProperty().multiply(0.1));
        dialogueBar.setVisible(false);

        root.getChildren().addAll(
                villainImage,
                battleBox, heart, playerHPBackground, dialogueBar, playerHPFrame,
                fightButton, itemButton, talkButton, heal,playerNameText, playerLevelText,
                t_option1, t_option2, t_option3
        );

        final Set<KeyCode> activeKeys = new HashSet<>();
        scene.setOnKeyPressed(event -> activeKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> activeKeys.remove(event.getCode()));

        AnimationTimer movement = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double x = heart.getTranslateX();
                double y = heart.getTranslateY();
                final double speed = 10;
                if (activeKeys.contains(KeyCode.A)) {
                    moveHeart((-1) * speed, 0);
                }
                if (activeKeys.contains(KeyCode.D)) {
                    moveHeart(speed, 0);
                }
                if (activeKeys.contains(KeyCode.W)) {
                    moveHeart(0, (-1) * speed);
                }
                if (activeKeys.contains(KeyCode.S)) {
                    moveHeart(0, speed);
                }
            }
        };
        movement.start();

        stage.setTitle("Underhell Boss Fight");
        stage.setScene(scene);
        stage.show();
        stage.show();

        Platform.runLater(() -> {
            Bounds bounds = battleBox.localToScene(battleBox.getBoundsInLocal());
            double centerX = (bounds.getMinX() + bounds.getMaxX()) / 2;
            double centerY = (bounds.getMinY() + bounds.getMaxY()) / 2;
            heart.setLayoutX(centerX);
            heart.setLayoutY(centerY);
        });

    }

    private void showDialogue(String message) {
        dialogueText.setText(message);
        dialogueBar.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> dialogueBar.setVisible(false));
        pause.play();
    }

    private Path createHeartShape(double size, Color color) {
        Path heart = new Path();

        heart.getElements().addAll(
                new MoveTo(size / 2, size / 5),

                new CubicCurveTo(size / 2, 0, 0, 0, 0, size / 3),
                new CubicCurveTo(0, size / 2, size / 2, size * 0.8, size / 2, size),

                new CubicCurveTo(size / 2, size * 0.8, size, size / 2, size, size / 3),
                new CubicCurveTo(size, 0, size / 2, 0, size / 2, size / 5)
        );
        heart.setScaleX(1.5);
        heart.setScaleY(1.5);
        heart.setFill(color);
        heart.setStroke(Color.BLACK);
        heart.setStrokeWidth(1);
        return heart;
    }

    private void moveHeart(double dx, double dy) {
        Bounds bounds = battleBox.localToScene(battleBox.getBoundsInLocal());

        double newX = heart.getLayoutX() + dx;
        double newY = heart.getLayoutY() + dy;
        double heartWidth = heart.getBoundsInLocal().getWidth();
        double heartHeight = heart.getBoundsInLocal().getHeight();

        if (newX >= bounds.getMinX() && newX + heartWidth <= bounds.getMaxX()) {
            heart.setLayoutX(newX);
        }
        if (newY >= bounds.getMinY() && newY + heartHeight <= bounds.getMaxY()) {
            heart.setLayoutY(newY);
        }
    }

    private Button createTalkOption(String text, Scene scene, int index) {
        Button btn = new Button(text);
        btn.setPrefSize(200, 40);
        btn.getStyleClass().add("talk-option");
        btn.setVisible(false);
        btn.layoutXProperty().bind(battleBox.xProperty().add(
                battleBox.widthProperty().subtract(btn.prefWidthProperty()).divide(2)
        ));
        btn.layoutYProperty().bind(battleBox.yProperty().add(30 + index * 50));
        return btn;
    }

    private void handlePlayerChoice(String message) {
        talk_options_visibility(false);
        showDialogue(message);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(ev -> {
            currentState = GameState.ENEMY_TURN;

            Timeline spearAttack = new Timeline();
            for (int i = 0; i < 20; i++) {
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1), e -> alastor.throwSpear());
                spearAttack.getKeyFrames().add(keyFrame);
            }

            spearAttack.setOnFinished(e -> {
                currentState = GameState.PLAYER_CHOICE_OPTIONS;
                options_visibility(fightButton, talkButton, itemButton, true);
            });

            spearAttack.play();
        });
        pause.play();
    }

    private void talk_options_visibility(boolean isVisible) {
        t_option1.setVisible(isVisible);
        t_option2.setVisible(isVisible);
        t_option3.setVisible(isVisible);
    }

    private void item_options_visibility(boolean isVisible) {
        heal.setVisible(isVisible);
    }

    private void options_visibility(Button f, Button t, Button i, boolean isVisible) {
        f.setVisible(isVisible);
        t.setVisible(isVisible);
        i.setVisible(isVisible);
    }

    private void handlePlayerChoice2() {
        item_options_visibility(false);
        currentState = GameState.PLAYER_CHOICE_OPTIONS;
        options_visibility(fightButton, talkButton, itemButton, true);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(ev -> dialogueBar.setVisible(false));
        pause.play();
    }
    private void handlePlayerChoice3(String message) {
        item_options_visibility(false);
        showDialogue(message);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(ev -> {
            currentState = GameState.ENEMY_TURN;

            Timeline spearAttack = new Timeline();
            for (int i = 0; i < 20; i++) {
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1), e -> alastor.throwSpear());
                spearAttack.getKeyFrames().add(keyFrame);
            }

            spearAttack.setOnFinished(e -> {
                currentState = GameState.PLAYER_CHOICE_OPTIONS;
                options_visibility(fightButton, talkButton, itemButton, true);
            });

            spearAttack.play();
        });
        pause.play();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
