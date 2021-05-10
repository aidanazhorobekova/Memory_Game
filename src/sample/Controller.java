package sample;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Controller {

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox checkEasy;

    @FXML
    private CheckBox checkMedium;

    @FXML
    private CheckBox checkHard;

    @FXML
    private Button startButton;

    @FXML
    private Label warning;

    String level = null;
    int widthAndHeight;
    static int rowsAndColumns;
    int pairs;
    int quantity = 2;
    int count = 2;
    Tile selected = null;


    @FXML
    void onStart(ActionEvent event) {
        if (nameField.getText().isEmpty()) {
            warning.setOpacity(1);
        } else {
            if (checkHard.isSelected()) {
                checkMedium.setSelected(false);
                checkEasy.setSelected(false);
                level = "hard";
            } else if (checkMedium.isSelected()) {
                checkEasy.setSelected(false);
                checkHard.setSelected(false);
                level = "medium";
            } else if (checkEasy.isSelected()) {
                checkHard.setSelected(false);
                checkMedium.setSelected(false);
                level = "easy";
            }

            Stage stage = new Stage();
            stage.setTitle("Memory Game");
            stage.setMaximized(true);
            if (level.equals("hard")) {
                quantity = 4;
                rowsAndColumns = 8;
                widthAndHeight = 900;
            } else if (level.equals("medium")) {
                rowsAndColumns = 6;
                widthAndHeight = 700;
            } else {
                rowsAndColumns = 4;
                widthAndHeight = 500;
            }

            pairs = rowsAndColumns * rowsAndColumns / quantity;

            stage.setWidth(widthAndHeight);
            stage.setHeight(widthAndHeight);
            stage.setScene(new Scene(createContent()));
            stage.show();

        }
    }


    public Parent createContent() {
        Pane pane = new Pane();
        int size = 50 * rowsAndColumns;
        int layoutX = (1400 - size) / 2;
        int layoutY = (700 - size) / 2;

        pane.setLayoutX(layoutX);
        pane.setLayoutY(layoutY);

        char c = 'A';
        List<Tile> array = new ArrayList<>();

        for (int i = 0; i < pairs; i++) {
            for (int j = 0; j < quantity; j++) {
                array.add(new Tile(Character.toString(c)));
            }
            c++;
        }

        Collections.shuffle(array);

        for (int i = 0; i < array.size(); i++) {
            Tile tile = array.get(i);
            tile.setTranslateX(50 * (i % rowsAndColumns));
            tile.setTranslateY(50 * (i / rowsAndColumns));
            pane.getChildren().add(tile);
        }

        return pane;
    }

    public class Tile extends StackPane {
        Text letter = new Text();

        public Tile(String value) {
            Rectangle squares = new Rectangle(50, 50);
            squares.setFill(null);
            squares.setStroke(Color.BLACK);

            letter.setText(value);
            letter.setFont(Font.font(30));

            setAlignment(Pos.CENTER);
            getChildren().addAll(squares, letter);

            letter.setOpacity(0);
            setOnMouseClicked(this::HandleMouseClick);
        }

        public void HandleMouseClick(MouseEvent event) {
            if (isOpen() || count == 0) {
                return;
            }

            if (selected == null) {
                selected = this;
                open(() -> {});
                count --;
            } else {
                open(() -> {
                    if (isSame(selected)) {
                        selected.found();
                        this.found();
                    } else {
                        this.close();
                        selected.close();
                    }
                    selected = null;
                    count = 2;
                });
            }
        }


        void open(Runnable action) {
            FadeTransition fd = new FadeTransition(Duration.seconds(0.25),letter);
            fd.setToValue(1);
            fd.setOnFinished(e -> action.run());
            fd.play();
        }


        void close() {
            FadeTransition fd = new FadeTransition(Duration.seconds(0.75), letter);
            fd.setToValue(0);
            fd.play();
        }


        boolean isSame(Tile other) {
            return letter.getText().equals(other.letter.getText());
        }


        void found() {
            letter.setFill(Color.GREEN);
        }


        boolean isOpen() {
            return letter.getOpacity() == 1;
        }
    }

}
