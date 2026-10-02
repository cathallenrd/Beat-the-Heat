package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** 
 * GlobalUI acts as the central styling and theme engine for the application
 */

public class GlobalUI{

    /** 
 * 1. Global Variables and Registry Lists
 * Stores font scales, mode states and references to all active UI elements
 */

    public static int buttonFontSize = 15;
    public static int titleFontSize = 50;
    public static int textFontSize = 15;

    public static int enlargedButtonFontSize = 25;
    public static int enlargedTitleFontSize = 60;
    public static int enlargedTextFontSize = 25;

    // Standardised button styling
    private static final String BUTTON_SHAPE_STYLE = "-fx-background-radius: 12; -fx-border-color: rgba(0,0,0,0.15); -fx-border-width: 2; -fx-border-radius: 12;";

    public static boolean darkMode = false;

    public static boolean fontEnlarged = false;

    private static List<Label> titlesList = new ArrayList<>();
    private static List<Label> textList = new ArrayList<>();

    //note that buttons and layouts must be registered with name and background colour, css colour names are fine as well as hex codes etc.
    private static Map<Button, String> buttonsMap = new HashMap<>();
    private static Map<javafx.scene.layout.Pane, String> layouts = new HashMap<>();

    /** 
     * 2. Component Registration
     * Methods to add UI elements to the global lists so their themes can be tracked.
     */
    
    public static void register(Label text) {
        //checks text isn't already in list
        if (!textList.contains(text)) {
            textList.add(text);

            //applies correct colour depending on dark/light mode
            String textColour = darkMode ? "white" : "black";
            text.setStyle("-fx-text-fill: " + textColour + ";");

            //applies correct font size
            applyTextFont(text);
        }
    }

    public static void registerTitle(Label title) {
        //checks text isn't already in list
        if (!titlesList.contains(title)) {
            titlesList.add(title);

            //applies correct colour depending on dark/light mode
            String textColour = darkMode ? "white" : "black";
            String existingStyle = title.getStyle().replaceAll("-fx-text-fill:[^;]+;?", "");
            title.setStyle(existingStyle + "-fx-text-fill: " + textColour + ";");

            //applies correct font size
            applyTitleFont(title);
        }
    }

    public static void registerLayout(javafx.scene.layout.Pane layout) {
        //checks layout isn't already in HashMap
        if (!layouts.containsKey(layout)) {
            layouts.put(layout, layout.getStyle()); // store original background for use in toggling mode
            applyTheme(layout);
        }
    }

    public static void register(Button button, String colour) {
        //checks button isn't already in HashMap
        if (!buttonsMap.containsKey(button)) {
            buttonsMap.put(button, colour);

            //darkens background colour depending on light/dark mode settings
            String background = darkMode ? darkenColour(colour, 0.7) : colour;
            //applies text colour depending on light/dark mode settings
            String textColour = darkMode ? "white" : "black";

            button.setStyle("-fx-background-color: " + background + ";" + "-fx-text-fill: " + textColour + ";" + BUTTON_SHAPE_STYLE);
            //applies font size to button
            applyButtonFont(button);
        }

        /** 
         * Button Animation and Hover States
         * Binds event listeners to scale the button and darken background dynamically based on mouse click.
         */

        //darken on hover
        button.setOnMouseEntered(e -> {
            //gets colour button starts at
            String hoverBase = darkMode ? darkenColour(colour, 0.7) : colour;
            //works out colour button darkens to when hovered over
            String hoverColour = darkenColour(hoverBase, 0.9);

            //applies background colour and text colour to button when hovered over
            button.setStyle("-fx-background-color: " + hoverColour + ";" + "-fx-text-fill: " + (darkMode ? "white" : "black") + ";" + BUTTON_SHAPE_STYLE);
            applyButtonFont(button);
            button.setScaleX(1.05);
            button.setScaleY(1.05);});

        //lighten again
        button.setOnMouseExited(e -> {
            //gets original background colour
            String normalColour = darkMode ? darkenColour(colour, 0.7) : colour;
            //applies original background colour when cursor leaves button
            button.setStyle("-fx-background-color: " + normalColour + ";" + "-fx-text-fill: " + (darkMode ? "white" : "black") + ";" + BUTTON_SHAPE_STYLE);
            applyButtonFont(button);
            button.setScaleX(1.0);
            button.setScaleY(1.0);});

        //darken on click
        button.setOnMousePressed(e -> {
            //gets original colour
            String clickedBase = darkMode ? darkenColour(colour, 0.7) : colour;
            //works out darkened colour
            String clickedColour = darkenColour(clickedBase, 0.8);
            //applies colour change when clicked
            button.setStyle("-fx-background-color: " + clickedColour + ";" + "-fx-text-fill: " + (darkMode ? "white" : "black") + ";" + BUTTON_SHAPE_STYLE);
            applyButtonFont(button);});

        //lighten again
        button.setOnMouseReleased(e -> {
            //gets original colour
            String clickedBase = darkMode ? darkenColour(colour, 0.7) : colour;
            //gets darkened colour
            String clickedColour = darkenColour(clickedBase, 0.9);
            //applies colour change when mouse released
            button.setStyle("-fx-background-color: " + clickedColour + ";" + "-fx-text-fill: " + (darkMode ? "white" : "black") + ";" + BUTTON_SHAPE_STYLE);
            applyButtonFont(button);});
    }

    /** 
     * 3. Theme application and colour math
     * Internal methods to apply CSS styles, font size and RGB darkening algorithms.
     */

    //apply font/background changes
    public static void applyButtonFont(Button button) {
        //gets font size depending on if enlarged or not
        int size = fontEnlarged ? enlargedButtonFontSize : buttonFontSize;
        String style = button.getStyle();
        button.setStyle(style + "-fx-font-size: " + size + "px; -fx-font-weight: bold;");
    }

    public static void applyTextFont(Label text) {
        //gets font size depending on if enlarged or not
        int size = fontEnlarged ? enlargedTextFontSize : textFontSize;
        String style = text.getStyle();
        text.setStyle(style + "-fx-font-size: " + size + "px; -fx-font-weight: bold;");
    }

    public static void applyTitleFont(Label title) {
        //gets font size depending on if enlarged or not
        int size = fontEnlarged ? enlargedTitleFontSize : titleFontSize;
        String style = title.getStyle();
        title.setStyle(style + "-fx-font-size: " + size + "px; -fx-font-weight: bold;");
    }

    public static void applyTheme(javafx.scene.layout.Pane layout) {
        //sets background to dark grey if dark mode enabled, or original backrgound colour if light mode enabled
        String lightStyle = layouts.get(layout);
        if (darkMode) {
            layout.setStyle(lightStyle + "; -fx-background-color: #2B2B2B;");
        }else{
            layout.setStyle(layouts.get(layout));
        }
    }

    public static String darkenColour(String colour, double factor) {

        Color color = Color.web(colour);
        //multiplies RGB values by factor value to darken them
        Color darker = new Color(color.getRed() * factor, color.getGreen() * factor, color.getBlue() * factor, 1);

    
    //returns new RGB value for darkened colour
    return String.format("#%02X%02X%02X", (int)(darker.getRed() * 255), (int)(darker.getGreen() * 255), (int)(darker.getBlue() * 255));
    }


    /** 
     * 4. Global Toggles (Modes and Fonts)
     * Loops through all registered components and updates their CSS to reflect new application state
     */
    
    public static void toggleFontSize(){
        fontEnlarged = !fontEnlarged;
        //calls apply font functions so button font size is updated everytime font is toggled
        for (Button button : buttonsMap.keySet()) {
            applyButtonFont(button);
        }
        for(Label text : textList){
            applyTextFont(text);
        }
        for (Label title : titlesList) {
            applyTitleFont(title);
        }
    }

    public static void toggleMode() {
        darkMode = !darkMode;
        //calls applyTheme() method for every registered layout every time dark/light mode is toggled
        for (javafx.scene.layout.Pane layout : layouts.keySet()) {
            applyTheme(layout);
        }

        //applies dark background and white text when dark mode applied and reverts when light mode applied for buttons
        for (Button button : buttonsMap.keySet()) {
            String baseColour = buttonsMap.get(button);

            String background = darkMode ? darkenColour(baseColour, 0.7): baseColour;
            String textColour = darkMode ? "white" : "black";

            button.setStyle("-fx-background-color: " + background + ";" + "-fx-text-fill: " + textColour + ";" + BUTTON_SHAPE_STYLE);

            applyButtonFont(button);

        }

        //applies white text when dark mode applied and reverts when light mode applied
        String textColour = darkMode ? "white" : "black";
        
        //applies appropriate font settings to all registered non-title labels
        for (Label text : textList) {
            int size = fontEnlarged ? enlargedTextFontSize : textFontSize;
            text.setStyle("-fx-text-fill: " + textColour + ";");
            applyTextFont(text);
        }

        //applies appropriate font settings to all registered title labels
        for (Label title : titlesList) {
            String existingStyle = title.getStyle().replaceAll("-fx-text-fill:[^;]+;?", "");
            title.setStyle(existingStyle + "-fx-text-fill: " + textColour + ";");
            applyTitleFont(title);
        }
    }

    

    public static void toggleAudio(MediaPlayer mediaPlayer)
    {
        //enables/disables audio when button pressed
        if (mediaPlayer.isMute())
        {
            mediaPlayer.setMute(false);
        }
        else
        {
            mediaPlayer.setMute(true);
        }
    }

    //used to create generic text labels
    public static Label createText(String labelText) {
        Label text = new Label(labelText);
        register(text);
        return text;
    }

    //used to create generic title labels
    public static Label createTitle(String titleText) {
        Label title = new Label(titleText);
        registerTitle(title);
        return title;
    }

    /** 
     * 5. Generic Pop-up Generators and utilities
     * Overloaded methods to quickly build model dialouges and clear the registry
     */
    
    //used to create pop-ups containing text only
    public static void createPopUp(String popUpText){

        //creates new window
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        Label message = new Label(popUpText);
        message.setWrapText(true);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> stage.close());

        //VBox for layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(message, closeButton);

        Scene scene = new Scene(layout, 350, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    //used to create pop-ups containing title and text
    public static void createPopUp(String popUpTitle, String popUpText){

        Stage stage = new Stage();
        stage.setTitle(popUpTitle);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label message = new Label(popUpText);
        message.setWrapText(true);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> stage.close());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(message, closeButton);

        Scene scene = new Scene(layout, 350, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }

    //used to create pop-ups containing title, text and a button
    public static void createPopUp(String popUpTitle, String popUpText, Button popUpOption){

        Stage stage = new Stage();
        stage.setTitle(popUpTitle);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label message = new Label(popUpText);
        message.setWrapText(true);

        popUpOption.setOnAction(e -> stage.close());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(message, popUpOption);

        Scene scene = new Scene(layout, 350, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }

    //used to create pop-ups containing title, text and two buttons
    public static void createPopUp(String popUpTitle, String popUpText, Button popUpOptionOne, Button popUpOptionTwo){

        Stage stage = new Stage();
        stage.setTitle(popUpTitle);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label message = new Label(popUpText);
        message.setWrapText(true);

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        popUpOptionOne.setOnAction(e -> stage.close());
        popUpOptionTwo.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(popUpOptionOne, popUpOptionTwo);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(message, buttonBox);

        Scene scene = new Scene(layout, 350, 200);
        stage.setScene(scene);

        stage.showAndWait();
    }

     public static void clearRegistry() {
        titlesList.clear();
        textList.clear();
        buttonsMap.clear();
        layouts.clear();
    }
}