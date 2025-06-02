import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BattleManager extends Application {
    Media bgMusic = new Media(getClass().getResource("/sounds/bg_music.m4a").toExternalForm());
    Random random = new Random();
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
    Player player = new Player("Maria", 100, 1);
    Button fightButton = new Button("FIGHT");
    Button itemButton = new Button("ITEM");
    Button talkButton = new Button("TALK");
    Alastor alastor = new Alastor(100);
    Item atkUp = new Item(player);
    Label hpLabel = new Label(atkUp.getHealCount().get()+"");


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
        Text playerHp = new Text();
        playerHp.textProperty().bind(Bindings.concat(
                " HP:", player.getHp().asString()));

        playerHp.setFill(Color.WHITE);
        playerHp.getStyleClass().add("game-label");
        Text playerLevelText = new Text("Lv. " + player.getLevel());
        playerLevelText.setFill(Color.WHITE);
        playerLevelText.getStyleClass().add("game-label");

        battleBox = new Rectangle(500, 300);
        battleBox.setStroke(Color.WHITE);
        battleBox.setStrokeWidth(3);

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


        playerNameText.yProperty().bind(playerHPFrame.yProperty().add(10));
        playerNameText.xProperty().bind(playerHPFrame.xProperty().subtract(120));
        playerHp.yProperty().bind(playerHPFrame.yProperty().add(10));
        playerHp.xProperty().bind(playerHPFrame.xProperty().subtract(70));

        playerLevelText.yProperty().bind(playerHPFrame.yProperty().add(10));
        playerLevelText.xProperty().bind(playerHPFrame.xProperty().add(playerHPFrame.widthProperty()).add(20));


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

        fightButton.setOnAction(e -> {
            if (currentState != GameState.PLAYER_CHOICE_OPTIONS) return;
            currentState = GameState.PLAYER_CHOICE_FIGHT;
            options_visibility(fightButton, talkButton, itemButton, false);
            heart.setVisible(false);

            ArrayList<Integer> damages = Damages();
            int[] villainHP = {alastor.getHp()};
            final Pane[] bossFightPane = new Pane[1];

            playTransition(battleBox, bossFightPane[0], () -> {
                ((Pane) fightButton.getScene().getRoot()).getChildren().add(bossFightPane[0]);
                bossFightPane[0].requestFocus();
            });

            bossFightPane[0] = BossFight(() -> {
                alastor.setHp(villainHP[0]);
                currentState = GameState.ENEMY_TURN;
                handlePlayerChoiceTwo(battleBox, root, player, "Ahh, that hurts, you gotta pay for that!");
                ((Pane) fightButton.getScene().getRoot()).getChildren().remove(bossFightPane[0]);

                battleBox.setOpacity(1);
                battleBox.setScaleX(1);
                battleBox.setScaleY(1);
                battleBox.requestFocus();
                Bounds bounds = battleBox.localToScene(battleBox.getBoundsInLocal());
                double centerX = (bounds.getMinX() + bounds.getMaxX()) / 2;
                double centerY = (bounds.getMinY() + bounds.getMaxY()) / 2;
                heart.setLayoutX(centerX);
                heart.setLayoutY(centerY);
                heart.setVisible(true);
            }, villainHP, damages);

        });


        itemButton.setOnAction(e -> {
            if (currentState == GameState.ENEMY_TURN) return;
            currentState = GameState.PLAYER_CHOICE_ITEM;
            options_visibility(fightButton, talkButton, itemButton, false);
            heart.setVisible(false);
            item_options_visibility(true);
            talk_options_visibility(false);
        });


        talkButton.setOnAction(e -> {
            if (currentState == GameState.ENEMY_TURN) return;
            currentState = GameState.PLAYER_CHOICE_TALK;
            options_visibility(fightButton, talkButton, itemButton, false);
            heart.setVisible(false);
            talk_options_visibility(true);
            item_options_visibility(false);
        });

        t_option1 = createTalkOption("Plead", scene, 0);
        t_option2 = createTalkOption("Insult", scene, 1);
        t_option3 = createTalkOption("Stay Silent", scene, 2);
        heal = createTalkOption("Heal", scene, 1);

        heal.setOnAction(e -> {
            healpotion.hpUp();
            handlePlayerChoiceTwo(battleBox, root, player, "Useless~");
            createLabel(hpLabel,scene,1);


        });
        t_option1.setOnAction(e -> {

            handlePlayerChoiceTwo(battleBox, root, player, "You plead. The villain chuckles.");
        });
        t_option2.setOnAction(e -> {
            handlePlayerChoiceTwo(battleBox, root, player, "You insult the villain. Its eyes glow red.");
        });
        t_option3.setOnAction(e -> {
            handlePlayerChoiceTwo(battleBox, root, player, "You stay silent. The air grows heavy.");
        });

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                handlePlayerChoiceOne();
            }
        });

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
                battleBox, heart, playerHPBackground, dialogueBar, playerHPFrame,hpLabel,
                fightButton, itemButton, talkButton, heal, playerNameText, playerLevelText, playerHp,
                t_option1, t_option2, t_option3
        );
     //  GameBeginningMethods();


        final Set<KeyCode> activeKeys = new HashSet<>();
        scene.setOnKeyPressed(event -> {
            if (activeKeys.add(event.getCode())) {
                if (event.getCode() == KeyCode.SHIFT) {
                    System.out.println("shift pressed");
                    handlePlayerChoiceOne();
                }
            }
        });

        scene.setOnKeyReleased(event -> activeKeys.remove(event.getCode()));

        AnimationTimer movement = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double x = heart.getTranslateX();
                double y = heart.getTranslateY();
                final double speed = 1;
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
        player.getHp().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                gameOver(stage);
            }
        });

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

        PauseTransition pause = new PauseTransition(Duration.seconds(10));
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
    private Label createLabel(Label l, Scene scene, int index) {
        l.setPrefSize(200, 40);
        l.getStyleClass().add("talk-option");
      //  l.setVisible(false);
        l.layoutXProperty().bind(battleBox.xProperty().add(
                battleBox.widthProperty().subtract(l.prefWidthProperty()).divide(2)
        ));
        l.layoutYProperty().bind(battleBox.yProperty().add(30 + index * 50));
        return l;
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
private void handlePlayerChoiceOne(){
    options_visibility(fightButton, talkButton, itemButton, true);
    talk_options_visibility(false);
    item_options_visibility(false);
    heart.setVisible(true);
}
    private void handlePlayerChoiceTwo(Rectangle r, Pane p, Player P, String s) {
        talk_options_visibility(false);
        options_visibility(fightButton, talkButton, itemButton, false);
        showDialogue(s);
        item_options_visibility(false);
        heart.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(ev -> {
            currentState = GameState.ENEMY_TURN;
            int choice = random.nextInt(2);
            int sd = switch (choice) {
                case 0 -> {
                    alastor.throwSpearAll();
                    yield 10;
                }
                case 1 -> {
                    alastor.Laser(r, p, P);
                    yield 19;
                }
                default -> 1;
            };


            PauseTransition resume = new PauseTransition(Duration.seconds(sd + 1));
            resume.setOnFinished(e -> {
                currentState = GameState.PLAYER_CHOICE_OPTIONS;
                options_visibility(fightButton, talkButton, itemButton, true);
                heart.setVisible(false);
            });
            resume.play();
        });
        pause.play();
    }

    public Pane BossFight(Runnable onFinishCallback, int[] Hp, ArrayList<Integer> Damages) {
        Pane root = new Pane();

        final int RECT_WIDTH = 600;
        final int RECT_HEIGHT = 150;
        final double lineSpeed = 1.5;

        Line movingLine = new Line(0, 7, 0, RECT_HEIGHT - 7);
        movingLine.setStroke(Color.WHITE);
        movingLine.setStrokeWidth(5);
        boolean[] isPaused = {false};
        boolean[] enterPressed = {false};
        boolean[] fightActive = {true};
        Pane fight = new Pane();
        fight.setLayoutX(145);
        fight.setLayoutY(200);
        Image fightImage = new Image("/Battle Background.jpg");
        ImageView imageView = new ImageView(fightImage);
        imageView.setFitWidth(RECT_WIDTH);
        imageView.setFitHeight(RECT_HEIGHT);
        imageView.setPreserveRatio(false);
        Rectangle clip = new Rectangle(0, 0, RECT_WIDTH, RECT_HEIGHT);
        imageView.setClip(clip);
        Rectangle border = new Rectangle(0, 0, RECT_WIDTH, RECT_HEIGHT);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);
        fight.getChildren().addAll(imageView, border, movingLine);
        root.getChildren().addAll(fight);
        final int HP_BAR_WIDTH = 200;
        final int HP_BAR_HEIGHT = 20;
        final int HP_BAR_X = 350;
        final int HP_BAR_Y = 155;
        Rectangle hpBarBackground = new Rectangle(HP_BAR_X, HP_BAR_Y, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        hpBarBackground.setFill(Color.RED);
        Rectangle hpBarForeground = new Rectangle(HP_BAR_X, HP_BAR_Y, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        hpBarForeground.setFill(Color.YELLOW);
        double maxHp = 100.0;
        double currentHpPercent = Hp[0] / maxHp;
        hpBarForeground.setWidth(HP_BAR_WIDTH * currentHpPercent);
        root.getChildren().addAll(hpBarBackground, hpBarForeground);
        AnimationTimer moveTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused[0] && fightActive[0]) {
                    double x = movingLine.getStartX();
                    x += lineSpeed;
                    if (x >= RECT_WIDTH) {
                        x = RECT_WIDTH;
                    }

                    movingLine.setStartX(x);
                    movingLine.setEndX(x);

                    if (!enterPressed[0] && (x >= RECT_WIDTH)) {
                        fightActive[0] = false;
                        movingLine.setOpacity(0);
                        this.stop();
                        onFinishCallback.run();
                    }
                }
            }
        };

        root.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE && !enterPressed[0] && fightActive[0]) {
                enterPressed[0] = true;
                double x = movingLine.getStartX();
                double center = RECT_WIDTH / 2.0;
                double distance = Math.abs(x - center);

                if (distance < 20)
                    Hp[0] -= Damages.get(1);
                else
                    Hp[0] -= Damages.get(0);

                double updatedHpPercent = Hp[0] / maxHp;
                hpBarForeground.setWidth(HP_BAR_WIDTH * updatedHpPercent);

                isPaused[0] = true;

                AnimationTimer blinkTimer = new AnimationTimer() {
                    long startTime = System.nanoTime();
                    boolean isBright = true;

                    @Override
                    public void handle(long now) {
                        long elapsedMs = (now - startTime) / 1_000_000;
                        if (elapsedMs > 2500) {
                            stop();
                            isPaused[0] = false;
                            movingLine.setStroke(Color.WHITE);
                            movingLine.setOpacity(1.0);
                            movingLine.setStartX(0);
                            movingLine.setEndX(0);
                            fightActive[0] = false;
                            onFinishCallback.run();
                        }

                        if (isBright) {
                            movingLine.setStroke(Color.WHITE);
                            movingLine.setOpacity(1.0);
                        } else {
                            movingLine.setStroke(Color.RED);
                            movingLine.setOpacity(0.3);
                        }
                        isBright = !isBright;
                    }
                };
                blinkTimer.start();
            }
        });

        moveTimer.start();

        root.setFocusTraversable(true);
        return root;
    }

    public ArrayList<Integer> Damages() {
        ArrayList<Integer> damage = new ArrayList<>();
        int dmg = 5;
        damage.add(5);
        damage.add(10);
        return damage;
    }

    private void playTransition(Node from, Node to, Runnable onCollapseFinished) {
        Timeline collapse = new Timeline(
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(from.opacityProperty(), 0),
                        new KeyValue(from.scaleXProperty(), 0),
                        new KeyValue(from.scaleYProperty(), 0)
                )
        );

        collapse.setOnFinished(e -> {
            onCollapseFinished.run();

            Timeline appear = new Timeline(
                    new KeyFrame(Duration.seconds(0.5),
                            new KeyValue(to.opacityProperty(), 1),
                            new KeyValue(to.scaleXProperty(), 1),
                            new KeyValue(to.scaleYProperty(), 1)
                    )
            );
            appear.play();
        });

        collapse.play();
    }

    MediaPlayer mediaPlayer = new MediaPlayer(bgMusic);

    public void GameBeginningMethods() {
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.seek(Duration.seconds(3));
            mediaPlayer.play();
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.seconds(3));
            mediaPlayer.play();
        });
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.8);
        options_visibility(fightButton, talkButton, itemButton, false);

        String[] dialogues = {
                "Welcome to M Y H E L L <<<<<<<:::::: ",
                "You're gonna Die soon",
                "Or... Maybe we can make a deal...",
                "A trade based on your \nS O U L <: "
        };

        double delayBetween = 2.5;

        for (int i = 0; i < dialogues.length; i++) {
            String line = dialogues[i];
            PauseTransition pause = new PauseTransition(Duration.seconds(i * delayBetween));
            pause.setOnFinished(e -> showDialogue(line));
            pause.play();
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(10));

        pause.setOnFinished(ev -> {
            PauseTransition resume = new PauseTransition(Duration.seconds(0)); // Adjust as needed
            resume.setOnFinished(e -> {
                currentState = GameState.PLAYER_CHOICE_OPTIONS;
                options_visibility(fightButton, talkButton, itemButton, true);
            });
            resume.play();
        });
        pause.play();
    }

    private void gameOver(Stage stage) {

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");
        StackPane gameOverRoot = new StackPane(gameOverLabel);
        gameOverRoot.setStyle("-fx-background-color: black;");
        shakeStage(stage);
        mediaPlayer.pause();
        AudioClip sound = new AudioClip(getClass().getResource("/sounds/jumpscare.mp3").toExternalForm());
        sound.play();
        sound.play();

        Scene gameOverScene = new Scene(gameOverRoot, 800, 600);
        stage.setScene(gameOverScene);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreenExitHint(" ");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.centerOnScreen();


        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> Platform.exit());
        delay.play();
    }

    private void shakeStage(Stage stage) {
        final int shakeDistance = 60;
        final int shakeCycle = 60;
        final int intervalMs = 60;
        double originalX = stage.getX();
        double originalY = stage.getY();

        Timeline timeline = new Timeline();

        for (int i = 0; i < shakeCycle; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * intervalMs), event -> {
                double offsetX = (Math.random() - 0.5) * 2 * shakeDistance;
                double offsetY = (Math.random() - 0.5) * 2 * shakeDistance;
                stage.setX(originalX + offsetX);
                stage.setY(originalY + offsetY);
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}