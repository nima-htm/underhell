import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class UndyneStage extends Application {
    Media bgMusic = new Media(getClass().getResource("/sounds/bg_music_pre.mp3").toExternalForm());
    Media outro = new Media(getClass().getResource("/sounds/outro.mp3").toExternalForm());

    Random random = new Random();
    private Rectangle battleBox;
    private Rectangle playerHPBackground;
    private Pane dialogueBar;
    private Text dialogueText;
    private Path heart;
    private Image villainImg;
    private ImageView villainImage;
    private Image villainImg_hurt;
    private ImageView villainImage_hurt;
    private GameState currentState = GameState.PLAYER_CHOICE_OPTIONS;
    private Button t_option1, t_option2, t_option3;
    private Button heal, BoostATK;
    private AnimationTimer movement;

    Player player = GameSession.get().getPlayer();

    Button fightButton = new Button("FIGHT");
        Button itemButton = new Button("ITEM");
        Button talkButton = new Button("TALK");
        Undyne undyne = new Undyne(10);
        Item potion =GameSession.get().getItems();
        Label hpLabel = new Label("");
        Label atkLabel = new Label("");
        private map currentMapApp;
        private Scene mapScene;
        private Stage primaryStage;

    public UndyneStage(map mapApp, Scene mapScene, Stage stage) {
        this.currentMapApp = mapApp;
        this.mapScene = mapScene;
        this.primaryStage = stage;
    }

        AudioClip btnClicked = new AudioClip(getClass().getResource("/sounds/select-sound.mp3").toExternalForm());
        AudioClip ItemClicked = new AudioClip(getClass().getResource("/sounds/item.mp3").toExternalForm());
        AudioClip slashEnemy = new AudioClip(getClass().getResource("/sounds/slash.mp3").toExternalForm());

        @Override
        public void start(Stage stage) {
            undyne.setPlayer(player);

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
        Text playerAtk = new Text("Atk Boosted");
        playerAtk.setFill(Color.BLUE);
        playerAtk.getStyleClass().add("game-label");

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
        playerAtk.yProperty().bind(playerHPFrame.yProperty().add(10));
        playerAtk.xProperty().bind(playerHPFrame.xProperty().add(playerHPFrame.widthProperty()).add(70));
        playerAtk.setVisible(false);

        villainImg = new Image(getClass().getResource("/Undyne.gif").toExternalForm());
        villainImage = new ImageView(villainImg);
        villainImage.setFitWidth(300);
        villainImage.setFitHeight(300);
        villainImage.layoutXProperty().bind(scene.widthProperty().subtract(villainImage.fitWidthProperty()).subtract(220));
        villainImage.layoutYProperty().bind(scene.heightProperty().multiply(0.15));

        villainImg_hurt = new Image(getClass().getResource("/Undyne.png").toExternalForm());
        villainImage_hurt = new ImageView(villainImg_hurt);
        villainImage_hurt.setFitWidth(300);
        villainImage_hurt.setFitHeight(300);
        villainImage_hurt.layoutXProperty().bind(scene.widthProperty().subtract(villainImage_hurt.fitWidthProperty()).subtract(220));
        villainImage_hurt.layoutYProperty().bind(scene.heightProperty().multiply(0.15));
        villainImage_hurt.setVisible(false);

            undyne.setRoot(root);
            undyne.setHeart(heart);

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
            btnClicked.play();
            options_visibility(fightButton, talkButton, itemButton, false);
            heart.setVisible(false);

            ArrayList<Integer> damages;
            if (potion.getAtkInUse() == 1) {
                playerAtk.setVisible(false);
                damages = player.getDamages();
                potion.setAtkInUse(0);
            } else {
                player.setDamage(5, 10);
                damages = player.getDamages();
            }
            int[] villainHP = {undyne.getHp()};
            final Pane[] bossFightPane = new Pane[1];

            playTransition(battleBox, bossFightPane[0], () -> {
                ((Pane) fightButton.getScene().getRoot()).getChildren().add(bossFightPane[0]);
                bossFightPane[0].requestFocus();
            });

            bossFightPane[0] = BossFight(() -> {
                undyne.setHp(villainHP[0]);
                if (undyne.getHp() <= 0) {
                    GameFinished();
                    return;
                }
                currentState = GameState.ENEMY_TURN;
                showDialogue("You dare to stand against me?!\nWretched human ",6);
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(e2 -> {
                    handlePlayerChoiceTwo(battleBox, root, player, "\nReturn to where you came from!");
                });
                pause.play();

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
                villainImage.setVisible(true);
                villainImage_hurt.setVisible(false);
            }, villainHP, damages);

        });


        itemButton.setOnAction(e -> {
            if (currentState == GameState.ENEMY_TURN) return;
            currentState = GameState.PLAYER_CHOICE_ITEM;
            btnClicked.play();
            options_visibility(fightButton, talkButton, itemButton, false);
            heart.setVisible(false);
            item_options_visibility(true);
            talk_options_visibility(false);
            hpLabel.setVisible(true);
            atkLabel.setVisible(true);
            BoostATK.setVisible(true);
        });


        talkButton.setOnAction(e -> {
            if (currentState == GameState.ENEMY_TURN) return;
            currentState = GameState.PLAYER_CHOICE_TALK;
            btnClicked.play();
            options_visibility(fightButton, talkButton, itemButton, false);
            heart.setVisible(false);
            talk_options_visibility(true);
            item_options_visibility(false);
            hpLabel.setVisible(false);
            atkLabel.setVisible(false);
        });

        t_option1 = createTalkOption("Plead", scene, 0);
        t_option2 = createTalkOption("Insult", scene, 1);
        t_option3 = createTalkOption("Stay Silent", scene, 2);
        heal = createTalkOption("Heal", scene, 1);
        hpLabel = createLable(potion.getHealCount().get() + "", scene, 1, hpLabel);
        BoostATK = createTalkOption(" BoostATK", scene, 2);
        atkLabel = createLable(potion.getAtkCount().get() + "", scene, 2, atkLabel);
        BoostATK.setOnAction(e -> {
            if (potion.getHealCount().get() > 0) {
                playerAtk.setVisible(true);
                ItemClicked.play();
                potion.setAtkInUse(1);
                potion.atkuse();
                atkLabel.setText(potion.getAtkCount().get() + "");
                potion.atkUp(15, 20);
                handlePlayerChoiceTwo(battleBox, root, player, "Heh\nNO difference");
            }
            atkLabel.setText(potion.getAtkCount().get() + "");
            heart.setVisible(true);
        });
        heal.setOnAction(e -> {
            if (potion.getHealCount().get() > 0 && player.getHp().get() < 100 && player.getHp().get() > 0) {
                potion.healuse();
                hpLabel.setText(potion.getHealCount().get() + "");
                potion.hpUp();
                ItemClicked.play();
                handlePlayerChoiceTwo(battleBox, root, player, "Do you want to run away m$$8r ?\nHow stupid!!\nYou will not meet the BOSS !");
            }
            hpLabel.setText(potion.getHealCount().get() + "");
        });
        t_option1.setOnAction(e -> {
            ItemClicked.play();
            talk_options_visibility(false);
            options_visibility(fightButton, talkButton, itemButton, false);
            showDialogue("WEAK! Do better!", 2);
            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(e2 -> {
                handlePlayerChoiceTwo(battleBox, root, player, "HAHAHAAA");
            });
            pause.play();

        });

        t_option2.setOnAction(e -> {
            ItemClicked.play();
            showDialogue("Lowly peasent", 2);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e2 -> {
                handlePlayerChoiceTwo(battleBox, root, player, "Know your place!  ");
            });
            pause.play();
        });
        t_option3.setOnAction(e -> {
            ItemClicked.play();
            showDialogue("You stay silent. The air grows heavy.", 2);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e2 -> {
                handlePlayerChoiceTwo(battleBox, root, player, "Hmph\nDon't waste my time");
            });
            pause.play();

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
        dialogueBar.layoutXProperty().bind(scene.widthProperty().subtract(dialogueBackground.widthProperty()).subtract(550));
        dialogueBar.layoutYProperty().bind(scene.heightProperty().multiply(0.1));
        dialogueBar.setVisible(false);

        root.getChildren().addAll(
                villainImage, villainImage_hurt,
                battleBox, heart, playerHPBackground, dialogueBar, playerHPFrame, hpLabel,playerAtk,
                fightButton, itemButton, talkButton, heal, playerNameText, playerLevelText, playerHp, BoostATK, atkLabel,
                t_option1, t_option2, t_option3
        );
        GameBeginningMethods();

        final Set<KeyCode> activeKeys = new HashSet<>();
        scene.setOnKeyPressed(event -> {
            if (activeKeys.add(event.getCode())) {
                if (event.getCode() == KeyCode.SHIFT) {
                    handlePlayerChoiceOne();

                }
            }
        });

        scene.setOnKeyReleased(event -> activeKeys.remove(event.getCode()));

        movement = new AnimationTimer() {
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

        private void showDialogue(String message, int duration) {
        dialogueText.setText(message);
        dialogueBar.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(duration));
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

        private Label createLable(String labelText, Scene scene, int index, Label l) {

        Label label = new Label(labelText);
        label.setVisible(false);
        label.setPrefSize(30, 5);
        label.getStyleClass().add("circular-label");
        label.layoutXProperty().bind(battleBox.xProperty().add(
                battleBox.widthProperty().subtract(label.prefWidthProperty()).divide(2)).subtract(55));
        label.layoutYProperty().bind(battleBox.yProperty().add(40 + index * 50));
        l.textProperty().bind(potion.getHealCount().asString());

        return label;


    }


        private void talk_options_visibility(boolean isVisible) {
        t_option1.setVisible(isVisible);
        t_option2.setVisible(isVisible);
        t_option3.setVisible(isVisible);
    }

        private void item_options_visibility(boolean isVisible) {
        heal.setVisible(isVisible);
        BoostATK.setVisible(isVisible);


    }

        private void options_visibility(Button f, Button t, Button i, boolean isVisible) {
        f.setVisible(isVisible);
        t.setVisible(isVisible);
        i.setVisible(isVisible);
        heart.setVisible(isVisible);
    }

        private void handlePlayerChoiceOne() {
        options_visibility(fightButton, talkButton, itemButton, true);
        talk_options_visibility(false);
        item_options_visibility(false);
        hpLabel.setVisible(false);
        atkLabel.setVisible(false);
        heart.setVisible(true);
             battleBox.setFocusTraversable(true);
             battleBox.requestFocus();
              javafx.application.Platform.runLater(() -> battleBox.requestFocus());

    }

        private void handlePlayerChoiceTwo(Rectangle r, Pane p, Player P, String s) {
        hpLabel.setVisible(false);
        atkLabel.setVisible(false);
        talk_options_visibility(false);
        options_visibility(fightButton, talkButton, itemButton, false);
        showDialogue(s, 4);
        item_options_visibility(false);
        heart.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(ev -> {
            currentState = GameState.ENEMY_TURN;
//            int choice = random.nextInt(0);
            int choice=0;
            int sd = switch (choice) {
                case 0 -> {
                    undyne.multiSpiralWaterAttack(r, heart, () -> {
                        currentState = GameState.PLAYER_CHOICE_OPTIONS;
                        options_visibility(fightButton, talkButton, itemButton, true);
                        heart.setVisible(true);

                    });
                    yield 100;
                }
                default -> 0;
            };

            PauseTransition resume = new PauseTransition(Duration.seconds(sd + 1));
            resume.setOnFinished(e -> {
                currentState = GameState.PLAYER_CHOICE_OPTIONS;
                options_visibility(fightButton, talkButton, itemButton, false);
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
                slashEnemy.play();
                villainImage.setVisible(false);
                villainImage_hurt.setVisible(true);

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
        mediaPlayer.setVolume(0.3);
        options_visibility(fightButton, talkButton, itemButton, false);

        String[] dialogues = {
                "As Undyne, the great guard of Pathway,",
                "There is no way you defeat me !"
        };

        double delayBetween = 2.5;

        for (int i = 0; i < dialogues.length; i++) {
            String line = dialogues[i];
            PauseTransition pause = new PauseTransition(Duration.seconds(i * delayBetween));
            pause.setOnFinished(e -> showDialogue(line, 5));
            pause.play();
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(5));

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
        MediaPlayer outroMediaPlayer = new MediaPlayer(outro);


    private void GameFinished() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::GameFinished);
            return;
        }

        currentState = GameState.GAME_FINISHED;

        options_visibility(fightButton, talkButton, itemButton, false);
        talk_options_visibility(false);
        item_options_visibility(false);
        hpLabel.setVisible(false);
        atkLabel.setVisible(false);
        heart.setVisible(false);

        if (movement != null) movement.stop();
        if (mediaPlayer != null) mediaPlayer.stop();
        try { if (outroMediaPlayer != null) outroMediaPlayer.stop(); } catch (Exception ignored) {}

        Pane root = (Pane) fightButton.getScene().getRoot();
        if (root == null) {
            primaryStage.setScene(mapScene);
            mapScene.getRoot().requestFocus();

            Platform.runLater(() -> currentMapApp.removeBossDoorWithFade());
            return;
        }

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), root);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            primaryStage.setScene(mapScene);
            primaryStage.setFullScreen(false);
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
            primaryStage.centerOnScreen();
            mapScene.getRoot().requestFocus();


            Platform.runLater(() -> currentMapApp.removeBossDoorWithFade());
        });
        fade.play();
    }




    private void gameOver(Stage stage) {
        StackPane gameOverRoot = new StackPane();
        gameOverRoot.setStyle("-fx-background-color: black;");

        Image alastorImage = new Image(getClass().getResource("/villain.png").toExternalForm());
        ImageView alastorView = new ImageView(alastorImage);
        alastorView.setFitWidth(300);
        alastorView.setFitHeight(300);

        Rectangle dialogueBackground = new Rectangle(300, 100);
        dialogueBackground.setFill(Color.rgb(0, 0, 0, 0.8));
        dialogueBackground.setArcWidth(20);
        dialogueBackground.setArcHeight(50);
        dialogueBackground.setStroke(Color.WHITE);
        dialogueBackground.setStrokeWidth(2);

        Text dialogueText = new Text("you lost");
        dialogueText.setFill(Color.WHITE);
        dialogueText.setStyle("-fx-font-size: 20px;");
        dialogueText.setWrappingWidth(280);

        StackPane dialogueBar = new StackPane(dialogueBackground, dialogueText);
        dialogueBar.setTranslateY(180);

        gameOverRoot.getChildren().addAll(alastorView, dialogueBar);

        Scene gameOverScene = new Scene(gameOverRoot, 800, 600);
        stage.setScene(gameOverScene);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreenExitHint(" ");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.centerOnScreen();

        mediaPlayer.stop();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }

        public static void main(String[] args) {
        launch(args);
    }
}

class Undyne extends Villain {
    private Player p;
    AudioClip dmgtaken = new AudioClip(getClass().getResource("/sounds/damage-taken.mp3").toExternalForm());

    public void multiSpiralWaterAttack(Rectangle battleBox, Path heart, Runnable onFinish) {
        Pane root = getRoot();

        final int NUM_WAVES = 10;
        final int NUM_DROPS = 48;
        final double ANGLE_GAP = Math.PI / 24;
        final double ROTATION_SPEED = 2.0;
        final double RADIUS_SPEED = 60;
        final double START_RADIUS = 50;
        final double WAVE_INTERVAL = 1.5;

        final boolean[] isInvincible = {false};
        final int[] activeWaves = {0};

        for (int wave = 0; wave < NUM_WAVES; wave++) {
            int waveIndex = wave;
            activeWaves[0]++;
            PauseTransition waveDelay = new PauseTransition(Duration.seconds(wave * WAVE_INTERVAL));
            waveDelay.setOnFinished(event -> {


                List<Path> drops = new ArrayList<>();
                List<Double> baseAngles = new ArrayList<>();

                double[] offsets = {-100, 0, 100};
                double startOffsetX = offsets[waveIndex % offsets.length];


                double originX = heart.getLayoutX() + startOffsetX;
                double originY = heart.getLayoutY();

                for (int i = 0; i < NUM_DROPS; i++) {
                    double angle = i * ANGLE_GAP;
                    baseAngles.add(angle);

                    Path drop = new Path();
                    drop.getElements().addAll(
                            new MoveTo(0, 0),
                            new QuadCurveTo(5, -10, 0, -18),
                            new QuadCurveTo(-5, -10, 0, 0)
                    );
                    drop.setFill(Color.WHITE);
                    drop.setStroke(Color.WHITE);
                    root.getChildren().add(drop);
                    drops.add(drop);
                }

                AnimationTimer timer = new AnimationTimer() {
                    long last = 0;
                    double timeElapsed = 0;

                    @Override
                    public void handle(long now) {
                        if (last == 0) {
                            last = now;
                            return;
                        }
                        double dt = (now - last) / 1e9;
                        last = now;
                        timeElapsed += dt;

                        double radiusNow = START_RADIUS + timeElapsed * RADIUS_SPEED;

                        for (int i = 0; i < drops.size(); i++) {
                            double angle = baseAngles.get(i) + timeElapsed * ROTATION_SPEED;
                            double x = originX + radiusNow * Math.cos(angle);
                            double y = originY + radiusNow * Math.sin(angle);

                            Path drop = drops.get(i);
                            drop.setLayoutX(x);
                            drop.setLayoutY(y);

                            if (!isInvincible[0] &&
                                    drop.getBoundsInParent().intersects(heart.getBoundsInParent())) {

                                Shape intersection = Shape.intersect(drop, heart);
                                if (intersection.getBoundsInLocal().getWidth() != -1) {
                                    p.getdmg(8);
                                    dmgtaken.play();
                                    isInvincible[0] = true;

                                    heart.setOpacity(0.5);
                                    DropShadow flash = new DropShadow();
                                    flash.setColor(Color.RED);
                                    flash.setRadius(8);
                                    heart.setEffect(flash);

                                    PauseTransition removeFlash = new PauseTransition(Duration.millis(200));
                                    removeFlash.setOnFinished(e -> heart.setEffect(null));
                                    removeFlash.play();
                                    PauseTransition restore = new PauseTransition(Duration.seconds(1));
                                    restore.setOnFinished(e -> heart.setOpacity(1));
                                    restore.play();

                                    PauseTransition invincibility = new PauseTransition(Duration.seconds(1));
                                    invincibility.setOnFinished(e -> isInvincible[0] = false);
                                    invincibility.play();

                                    root.getChildren().remove(drop);
                                    drops.remove(i);
                                    baseAngles.remove(i);
                                    i--;
                                    continue;
                                }
                            }

                            Bounds bounds = drop.localToScene(drop.getBoundsInLocal());
                            if (!battleBox.localToScene(battleBox.getBoundsInLocal()).contains(bounds)) {
                                root.getChildren().remove(drop);
                                drops.remove(i);
                                baseAngles.remove(i);
                                i--;
                            }
                        }

                        if (timeElapsed > 6 || drops.isEmpty()) {
                            for (Path drop : drops) root.getChildren().remove(drop);
                            stop();
                            activeWaves[0]--;

                            if (activeWaves[0] == 0 && onFinish != null) {
                                onFinish.run();
                            }
                        }
                    }
                };
                timer.start();
            });
            waveDelay.play();
        }
    }

    public Undyne(int hp) {
        super(hp);
    }

    public void setPlayer(Player p) {
        this.p = p;
    }



    @Override
    public int getHp() {
        return super.getHp();
    }

    @Override
    public void setRoot(Pane root) {
        super.setRoot(root);
    }

    @Override
    public void setHeart(Path heart) {
        super.setHeart(heart);
    }

    @Override
    public void setHp(int hp) {
        super.setHp(hp);
    }
}

