import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BattleManager extends Application {

    private Rectangle battleBox;
    private Circle heart;
    private Rectangle villainHPBar;
    private Rectangle villainHPBackground;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        // باکس مبارزه
        battleBox = new Rectangle(150, 150, 300, 200);
        battleBox.setStroke(Color.WHITE);
        battleBox.setFill(Color.color(0, 0, 0, 0.3));

        // قلب بازیکن
        heart = new Circle(10, Color.RED);
        heart.setLayoutX(300);
        heart.setLayoutY(250);

        // نوار HP ویلن
        villainHPBackground = new Rectangle(150, 120, 300, 10);
        villainHPBackground.setFill(Color.DARKRED);

        villainHPBar = new Rectangle(150, 120, 300, 10);
        villainHPBar.setFill(Color.LIMEGREEN);

        Text villainName = new Text("VILLAIN");
        villainName.setFill(Color.WHITE);
        villainName.setLayoutX(150);
        villainName.setLayoutY(115);
        villainName.setStyle("-fx-font-size: 14; -fx-font-family: 'Courier New';");

        // تصویر ویلن (فعلاً مستطیل به جای تصویر)
        Rectangle villainImage = new Rectangle(120, 80, Color.DARKGRAY);
        villainImage.setLayoutX(240);
        villainImage.setLayoutY(30);

        // دکمه‌ها
        Button fightButton = new Button("FIGHT");
        Button itemButton = new Button("ITEM");
        Button talkButton = new Button("TALK");

        fightButton.setLayoutX(150);
        itemButton.setLayoutX(270);
        talkButton.setLayoutX(390);

        fightButton.setLayoutY(370);
        itemButton.setLayoutY(370);
        talkButton.setLayoutY(370);

        styleButton(fightButton);
        styleButton(itemButton);
        styleButton(talkButton);

        root.getChildren().addAll(
                villainImage, villainName,
                villainHPBackground, villainHPBar,
                battleBox, heart,
                fightButton, itemButton, talkButton
        );

        Scene scene = new Scene(root, 600, 450, Color.BLACK);

        // حرکت قلب
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> moveHeart(0, -5);
                case S -> moveHeart(0, 5);
                case A -> moveHeart(-5, 0);
                case D -> moveHeart(5, 0);
            }
        });


        stage.setTitle("Undertale Boss Fight - Step 3");
        stage.setScene(scene);
        stage.show();
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 14;" +
                        "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;"
        );
    }

    private void moveHeart(double dx, double dy) {
        double newX = heart.getLayoutX() + dx;
        double newY = heart.getLayoutY() + dy;

        if (newX >= battleBox.getX() + heart.getRadius() &&
                newX <= battleBox.getX() + battleBox.getWidth() - heart.getRadius()) {
            heart.setLayoutX(newX);
        }

        if (newY >= battleBox.getY() + heart.getRadius() &&
                newY <= battleBox.getY() + battleBox.getHeight() - heart.getRadius()) {
            heart.setLayoutY(newY);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
