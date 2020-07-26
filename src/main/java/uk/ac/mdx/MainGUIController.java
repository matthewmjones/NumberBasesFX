package uk.ac.mdx;

import javafx.animation.PauseTransition;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Controller class for the main window
 */
public class MainGUIController implements Initializable {

    private final static String [] DIGITS = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "A", "B", "C", "D",
            "E", "F"};

    private final static String [] BASES = {"2 (Binary)", "3", "4", "5", "6",
            "7", "8 (Octal)", "9", "10 (Decimal)", "11", "12", "13",
            "14", "15", "16 (Hexadecimal)"};

    private final static int PRECISION = 10;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button convertBtn;

    @FXML
    private TextField outputTextField;

    @FXML
    private Button copyOutputBtn;

    @FXML
    private ChoiceBox<String> baseChoiceBox;

    private Map<String, Long> basesHM = new HashMap<>();

    private long currentBase;

    private Stage popup = new Stage();

    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent clipboardContent = new ClipboardContent();

    @FXML
    private void convertNumber() {
        String inputNumberAsString = inputTextField.getText();

        if (!inputNumberAsString.matches("^-?\\d*.?\\d+$")) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Input error");
            a.setContentText("I'm having trouble understanding your input - please check it.");
            a.show();
            return;
        }

        long sign = inputNumberAsString.startsWith("-") ? -1 : 1;

        String [] parsedInput = inputNumberAsString.split("\\.");

        if ("".equals(parsedInput[0])) {parsedInput[0] = "0";}
        if ("-".equals(parsedInput[0])) {parsedInput[0] = "-0";}

        long integerPart = sign * Long.valueOf(parsedInput[0]);
        double fractionalPart =
                parsedInput.length > 1 ? Double.valueOf("0." + parsedInput[1]) : 0;

        List<String> outputNumberList = new ArrayList<>();
        if (sign == -1) outputNumberList.add(0, "-");

        // offset is the position in outputNumberList at which the digits will be added
        // sign = -1, offset = 1; sign = 1, offset = 0
        int offset = (int) (1 - sign) / 2;

        while (integerPart > 0) {
            long remainder = integerPart % currentBase;
            integerPart = integerPart / currentBase;

            outputNumberList.add(offset, DIGITS[(int) remainder]);
        }

        if (fractionalPart > 0) {

            outputNumberList.add(".");

            int numberOfDigits = 0;
            while (numberOfDigits < PRECISION & fractionalPart > 0) {

                int currentDigit = (int) (fractionalPart * (double) currentBase);

                outputNumberList.add(DIGITS[currentDigit]);

                fractionalPart = fractionalPart * (double) currentBase - (double) currentDigit;
                numberOfDigits++;
            }
        }

        StringBuilder outputString = new StringBuilder();
        outputNumberList.forEach(s -> outputString.append(s));
        outputTextField.setText(outputString.toString());
    }

    @FXML
    private void copyToClipboard() {
        String s = outputTextField.getText();
        clipboardContent.putString(s);
        clipboard.setContent(clipboardContent);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        popup.setX(10);
        popup.setY(20);
        popup.show();
        delay.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int i = 2;
        for (String s : BASES) {
            basesHM.put(s, (long) i);
            i++;
        }

        baseChoiceBox.getItems().addAll(BASES);
        baseChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changeBase);
        baseChoiceBox.getSelectionModel().selectFirst();

        popup.setResizable(false);
        popup.setAlwaysOnTop(true);
        popup.initStyle(StageStyle.UNDECORATED);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("popup.fxml"));
        try {
            Scene popupScene = new Scene(fxmlLoader.load());
            popup.setScene(popupScene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void changeBase(Observable observable, String oldvalue, String newValue) {
        currentBase = basesHM.get(newValue);
    }
}
