import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class BattleManager extends Application {

    private Rectangle battleBox;
    private Rectangle villainHPBar;
    private Rectangle playerHPBackground;
    private Pane dialogueBar;
    private Text dialogueText;
    private Path heart;


    @Override
    public void start(Stage stage) {

        stage.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Game");
            alert.setHeaderText("Do you want to save before exiting?");
            alert.setContentText("Your progress will be saved.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // saveGame();
                    stage.close();
                }
            });
        });

        Pane root = new Pane();
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root, 600, 450, Color.BLUE);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setMaximized(true);
        stage.setResizable(false);


        battleBox = new Rectangle(300, 200);
        battleBox.setStroke(Color.WHITE);
        battleBox.setStrokeWidth(2);

        battleBox.setWidth(400);
        battleBox.setHeight(300);

        battleBox.xProperty().bind(scene.widthProperty().subtract(battleBox.widthProperty()).divide(2));
        battleBox.yProperty().bind(scene.heightProperty().subtract(battleBox.heightProperty()).divide(2).subtract(40));

        heart = createHeartShape(20, Color.RED);
        heart.setLayoutX(scene.getWidth() / 2);
        heart.setLayoutY(scene.getHeight() / 2);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!heart.isPressed()) { // if you want to control when to update
                heart.setLayoutX(newVal.doubleValue() / 2);
            }
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!heart.isPressed()) {
                heart.setLayoutY(newVal.doubleValue() / 2);
            }
        });

        Text villainName = new Text("VILLAIN");
        villainName.setFill(Color.WHITE);
        villainName.layoutXProperty().bind(scene.widthProperty().multiply(0.25)); // ~150/600
        villainName.layoutYProperty().bind(scene.heightProperty().multiply(0.255)); // ~115/450
        villainName.setStyle("-fx-font-size: 14; -fx-font-family: 'Courier New';");

        Rectangle playerHPBackground = new Rectangle();
        playerHPBackground.setFill(Color.DARKRED);
        playerHPBackground.widthProperty().bind(scene.widthProperty().multiply(0.25));
        playerHPBackground.setHeight(10);
        playerHPBackground.xProperty().bind(scene.widthProperty().subtract(playerHPBackground.widthProperty()).divide(2));
        playerHPBackground.yProperty().bind(Bindings.add(battleBox.yProperty(), battleBox.heightProperty()).add(40));

        Rectangle villainImage = new Rectangle(120, 80, Color.DARKGRAY);
        villainImage.layoutXProperty().bind(scene.widthProperty().subtract(villainImage.widthProperty()).divide(2));
        villainImage.layoutYProperty().bind(scene.heightProperty().multiply(0.1)); // ~30/450


        Button fightButton = new Button("FIGHT");
        Button itemButton = new Button("ITEM");
        Button talkButton = new Button("TALK");
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
        talkButton.setOnAction(e -> showDialogue("Why are you talking?   Are you courting death?!"));


        Rectangle dialogueBackground = new Rectangle();
        dialogueBackground.widthProperty().bind(scene.widthProperty().multiply(0.15));  // half the previous width
        dialogueBackground.heightProperty().bind(scene.heightProperty().multiply(0.2)); // double the previous height
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
                villainImage, villainName,
                battleBox, heart, playerHPBackground, dialogueBar,
                fightButton, itemButton, talkButton
        );

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> moveHeart(0, -7);
                case S -> moveHeart(0, 7);
                case A -> moveHeart(-7, 0);
                case D -> moveHeart(7, 0);
            }
        });

        stage.setTitle("Undertale Boss Fight - Step 3");
        stage.setScene(scene);
        stage.show();
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
        heart.setFill(color);
        heart.setStroke(Color.BLACK);
        heart.setStrokeWidth(1);
        return heart;
    }

    private void moveHeart(double dx, double dy) {
        double newX = heart.getLayoutX() + dx;
        double newY = heart.getLayoutY() + dy;
        double minX = battleBox.getX();
        double minY = battleBox.getY();
        double maxX = battleBox.getX() + battleBox.getWidth();
        double maxY = battleBox.getY() + battleBox.getHeight();
        double heartWidth = heart.getBoundsInLocal().getWidth();
        double heartHeight = heart.getBoundsInLocal().getHeight();
        if (newX >= minX && newX + heartWidth <= maxX) {
            heart.setLayoutX(newX);
        }
        if (newY >= minY && newY + heartHeight <= maxY) {
            heart.setLayoutY(newY);
        }
    }

    public static void main(String[] args) {
        launch(args);

    }
}
