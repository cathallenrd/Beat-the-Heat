package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import core.GameManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import objects.Board;
import objects.NaturalDisaster;
import objects.Player;
import objects.cards.EventCard;
import objects.cards.QuizCard;

//1. GameFX acts as primary user interface
public class GameFX extends Application {
    private static GameFX activeInstance;

    private GameManager gameManager; // temporary null, will set actual GameUI in start()
    private GameUI cliUI;
    private BoardUI boardUI;

    MediaPlayer mediaPlayer;
    MediaPlayer secondaryMediaPlayer;
    Button startButton;
    Button loadButton;
    Button rulesButton;
    Button settingsButton;
    Button exitButton;

    public static Label liveRoundLabel;
    public static Label liveTemperatureBar;
    private Label temperatureValueLabel;
    private Rectangle temperatureFill;
    private Rectangle temperatureTrack;
    private HBox boardTopBar;
    private VBox boardTempBox;
    private BorderPane boardRoot;
    private static final double TEMP_BAR_WIDTH = 46;
    private double tempBarHeight = 300;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        activeInstance = this;
        this.cliUI = new GameUI();
        this.gameManager = new GameManager(cliUI, this);

        stage.setTitle("Beat the Heat");

        playBackgroundAudio();

        VBox menuLayout = createMenuLayout();

        Scene mainMenu = new Scene(menuLayout);

        stage.setScene(mainMenu);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * 2. Audio management system
     * 
     * Conrtols all background music, sound effects and mute toggles
     * 
     */
    public void playBackgroundAudio()
    {
        var url1 = getClass().getResource("/audio/background2.mp4");

        if (url1 == null) {
            throw new RuntimeException("Primary audio file not found");
        }
        Media media = new Media(url1.toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.5);
        mediaPlayer.play();
    

        var url2 = getClass().getResource("/audio/background.mp3");

        if (url2 == null) {
            throw new RuntimeException("Secondary audio file not found");
        }
        Media media2 = new Media(url2.toExternalForm());
        secondaryMediaPlayer = new MediaPlayer(media2);
        secondaryMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        secondaryMediaPlayer.setVolume(0.5);
        secondaryMediaPlayer.play();
    }

    public void toggleBackgroundAudio()
    {
        boolean shouldMute = false;

        if (mediaPlayer != null) {
            shouldMute = !mediaPlayer.isMute();
        } else if (secondaryMediaPlayer != null) {
            shouldMute = !secondaryMediaPlayer.isMute();
        }

        if (mediaPlayer != null) {
            mediaPlayer.setMute(shouldMute);
        }
        if (secondaryMediaPlayer != null) {
            secondaryMediaPlayer.setMute(shouldMute);
        }
    }

    public void playDiceAudio()
    {
        var url = getClass().getResource("/audio/diceroll.mp3");

        if (url == null) {
            throw new RuntimeException("Audio file not found");
        }

        Media media = new Media(url.toExternalForm());
        MediaPlayer dicePlayer = new MediaPlayer(media);

        dicePlayer.setVolume(1);
        dicePlayer.setOnEndOfMedia(dicePlayer::dispose);
        dicePlayer.play();
    }

    public void playSpecialSquarePopupAudio()
    {
        var url = getClass().getResource("/audio/universfield-happy-message-ping-351298.mp3");

        if (url == null) {
            throw new RuntimeException("Special square audio file not found");
        }

        Media media = new Media(url.toExternalForm());
        MediaPlayer popupPlayer = new MediaPlayer(media);
        popupPlayer.setVolume(1);
        popupPlayer.setOnEndOfMedia(popupPlayer::dispose);
        popupPlayer.play();
    }

    public void playMoneyChangedAudio()
    {
        var url = getClass().getResource("/audio/chaChingAudio.mp3");

        if (url == null) {
            throw new RuntimeException("Money changed audio file not found");
        }

        Media media = new Media(url.toExternalForm());
        MediaPlayer moneyPlayer = new MediaPlayer(media);
        moneyPlayer.setVolume(1);
        moneyPlayer.setOnEndOfMedia(moneyPlayer::dispose);
        moneyPlayer.play();
    }

    public static void playMoneyChangedAudioIfAvailable()
    {
        if (activeInstance != null) {
            activeInstance.playMoneyChangedAudio();
        }
    }

    /**
     * 3. Main menu and Pre-game set-up
     * 
     * Generate the home screen, settings, rules and player registration UI
     */

    public VBox createMenuLayout() throws Exception{
    
        
        Image logo = new Image(getClass().getResource("/images/gameLogo.png").toExternalForm());
        ImageView logoView = new ImageView(logo);

        logoView.setFitWidth(300);
        logoView.setPreserveRatio(true);
        logoView.setStyle("-fx-alignment: center;");
        

        Label title = new Label("Beat the Heat");
        String titleColour = GlobalUI.darkMode ? "white" : "black";
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: " + titleColour + ";");
        GlobalUI.registerTitle(title);

        String backgroundFile = GlobalUI.darkMode ? "/images/darkBackground.png" : "/images/lightBackground.png";
        String backgroundImage = getClass().getResource(backgroundFile).toExternalForm();

        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);

        String backgroundColour = GlobalUI.darkMode ? "#2b2b2b" : "#c9e3a9";
        menuLayout.setStyle(
            "-fx-background-color: " + backgroundColour + ";" + "-fx-background-image: url('" + backgroundImage + "');" + "-fx-background-size: cover;" 
            + "-fx-background-position: center center;" + "-fx-background-repeat: no-repeat");
        menuLayout.setFillWidth(true);
        menuLayout.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GlobalUI.registerLayout(menuLayout);

        startButton = formatMenuButton("Start Game", "mediumseagreen");
        startButton.setOnAction(e -> startGame());

        loadButton = formatMenuButton("Load Game", "lightsteelblue");
        loadButton.setOnAction(e -> loadGameSave());

        rulesButton = formatMenuButton("Rules", "mediumpurple");
        rulesButton.setOnAction(e -> showRules());

        settingsButton = formatMenuButton("Settings", "gold");
        settingsButton.setOnAction(e -> openSettings());

        exitButton = formatMenuButton("Exit", "indianred");
        exitButton.setOnAction(e -> confirmExit());

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);
        buttonBox.setMaxHeight(300);
        buttonBox.getChildren().addAll(startButton, loadButton, rulesButton, settingsButton, exitButton);

        String buttonBoxBackground = GlobalUI.darkMode ? "#2b2b2b" : "#3a7d47";
        String buttonBoxBorder = GlobalUI.darkMode ? "#1a1a1a" : "#2a5333";
        buttonBox.setStyle("-fx-background-color: " + buttonBoxBackground + ";" + "-fx-border-color: " + buttonBoxBorder + ";" + "-fx-border-width: 3px;" + "-fx-padding: 20;" +
                "-fx-background-radius: 10;" + "-fx-border-radius: 10;");

        menuLayout.getChildren().addAll(logoView, title, buttonBox);

        return menuLayout;

    }

    public Button formatMenuButton(String name, String colour) {
        Button button = new Button(name);
        int size = GlobalUI.buttonFontSize;
        button.setStyle("-fx-background-color: " + colour + ";" + "-fx-font-size:" + size + "px;" + "-fx-font-weight: bold;");

        button.setWrapText(true);
        button.setAlignment(Pos.CENTER);
        button.setMinWidth(350);
        button.setPrefWidth(350);
        button.setMaxWidth(450);
        button.setPrefHeight(70);

        GlobalUI.register(button, colour);

        return button;
    }

    // Same as function above, but this function requires the buttons to be bigger to fit large questions/answers
    public Button formatQuizMenuButton(String name, String colour)
    {
        Button button = new Button(name);
        int size = GlobalUI.buttonFontSize;
        button.setStyle("-fx-background-color: " + colour + ";" + "-fx-font-size:" + size + "px;" + "-fx-font-weight: bold;");

        button.setWrapText(true);
        button.setAlignment(Pos.CENTER);

        button.setMinWidth(350);
        button.setPrefWidth(350);
        button.setMaxWidth(450);
        button.setMinHeight(70);

        GlobalUI.register(button, colour);

        return button;
    }

    // Start game logic and progression
    public void startGame() {
        Stage playerSetupStage = new Stage();
        playerSetupStage.setTitle("New Game");
        playerSetupStage.initModality(Modality.APPLICATION_MODAL);

        String bgColor = GlobalUI.darkMode ? "#2b2b2b" : "#FFFDF7";
        String textColor = GlobalUI.darkMode ? "white" : "#2c3e50";
        String inputBg = GlobalUI.darkMode ? "#3d3d3d" : "white";
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: " + bgColor +  "; -fx-border-color: #3a7d47; -fx-border-width: 8;" +
            "-fx-background-radius: 20; -fx-border-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 20, 0, 0, 0);");

        Label headerLabel = new Label("Setup Game");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #3a7d47; -fx-padding: 8 30; -fx-background-radius: 10;");

        Label playerCountLabel = new Label("How many players?");
        playerCountLabel.setStyle("-fx-font-size: 16px; -fx-alignment: center; -fx-background-radius: 10; -fx-text-fill: " + textColor + ";");

        TextField playerCountField = new TextField();
        playerCountField.setPromptText("2-4");
        playerCountField.setMaxWidth(100);
        playerCountField.setStyle("-fx-font-size: 16px; -fx-alignment: center; -fx-background-radius: 10;" + 
                                    "-fx-control-inner-background: " + inputBg + "; -fx-text-fill: " + textColor + ";");
        
        Button nextButton = formatMenuButton("Continue", "mediumseagreen");
        nextButton.setPrefHeight(50);
        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-underline: true; -fx-font-weight: bold; -fx-cursor: hand;");

        backButton.setOnAction(e -> playerSetupStage.close());

        layout.getChildren().addAll(headerLabel, playerCountLabel, playerCountField, nextButton, backButton);
        
        GlobalUI.registerLayout(layout);
        nextButton.setOnAction(e -> {
            try {
                int playerCount = Integer.parseInt(playerCountField.getText());

                if (playerCount < 2 || playerCount > 4)
                {
                    String errorMessage = "Number of players must be between 2 and 4!";
                    Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
                    alert.showAndWait();
                    return;
                }

                // Input Validation for Player count
                if (playerCount < 2 || playerCount > 4) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The number of players must be between 2 and 4.");
                    alert.showAndWait();
                    return; // Stop further execution
                }

                showPlayerNameInputs(playerCount);
                playerSetupStage.close();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a number between (2-4)");
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(layout, 450, 450);
        playerSetupStage.setScene(scene);
        playerSetupStage.show();
    }

    /**
     * 4. Save and Load Management
     * 
     * Interface with GameManager to load, rename, delete and save JSON files
     */
    public void loadGameSave() {
        Stage loadStage = new Stage();
        loadStage.setTitle("Save Manager");
        loadStage.initModality(Modality.APPLICATION_MODAL);

        String bgColor = GlobalUI.darkMode ? "#2b2b2b" : "#FFFDF7";
        String textColor = GlobalUI.darkMode ? "white" : "#2c3e50";
        String listBg = GlobalUI.darkMode ? "#3d3d3d" : "white";

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: " + bgColor +  "; -fx-border-color: #4682B4; -fx-border-width: 8;" + 
                        "-fx-background-radius: 20; -fx-border-radius: 12;" + 
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 20, 0, 0, 0);");

        Label title = new Label("Load Game");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; " + 
                        "-fx-background-color: #4682B4; -fx-padding: 8 30; -fx-background-radius: 10;");

        //Scrollable list
        ListView<String> saveListView = new ListView<>();
        saveListView.setPrefHeight(200);
        saveListView.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #bdc3c7; -fx-control-inner-background: " + listBg + ";" + "-fx-text-fill:" + textColor + ";");
        updateSaveList(saveListView);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        //buttons

        Button loadBtn = new Button("Load Game");
        loadBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");

        Button renameBtn = new Button("Rename");
        renameBtn.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button cancelBtn = new Button("Back");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-underline: true; -fx-font-weight: bold; -fx-cursor: hand;");
        //Load
        loadBtn.setOnAction(e -> {
            String selected = saveListView.getSelectionModel().getSelectedItem();
            if(selected != null) {
                if(gameManager.loadGameFiles(selected)) {
                    loadStage.close();
                    showBoardScene(gameManager.getPlayers());
                }else {
                    new Alert(Alert.AlertType.ERROR, "Failed to load").showAndWait();
                }
            }
        });

        //Rename
        renameBtn.setOnAction(e -> {
            String selected = saveListView.getSelectionModel().getSelectedItem();
            if(selected != null) {
                TextInputDialog renamedDialog = new TextInputDialog(selected);
                renamedDialog.setTitle("Rename Save");
                renamedDialog.setHeaderText("Rename your save file");
                renamedDialog.setContentText("New Name:");

                renamedDialog.showAndWait().ifPresent(newName -> {
                    String cleanName = newName.replaceAll("[^a-zA-Z0-9-_]", "_");
                    if(!cleanName.isEmpty() && !cleanName.equals(selected)) {
                        java.io.File oldFile = new java.io.File("resources/" + selected + ".json");
                        java.io.File newFile = new java.io.File("resources/" + cleanName + ".json");

                        if(!newFile.exists() && oldFile.renameTo(newFile)) {
                            updateSaveList(saveListView);
                        }else {
                            new Alert(Alert.AlertType.ERROR, "Rename failed");
                        }
                    }

            });
            }
        });

        //Delete
        deleteBtn.setOnAction(e -> {
            String selected = saveListView.getSelectionModel().getSelectedItem();
            if(selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete '" + selected + "' permanently?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if(response == ButtonType.YES) {
                        java.io.File file = new java.io.File(("resources/" + selected + ".json"));
                        if(file.delete()) {
                            updateSaveList(saveListView);
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Could not delete file").show();
                        }
                    }
                });
            }
        });

        cancelBtn.setOnAction(e -> {
            Stage currentStage = (Stage) cancelBtn.getScene().getWindow();
            currentStage.close();
        });

        //Load game container
        buttonBox.getChildren().addAll(loadBtn, renameBtn, deleteBtn);
        layout.getChildren().addAll(title, saveListView, buttonBox, cancelBtn);
        Scene scene = new Scene(layout, 500, 500);
        loadStage.setScene(scene);
        loadStage.showAndWait();
    }

    private void updateSaveList(ListView<String> listView) {
        String[] saves = gameManager.getSaveFiles();
        listView.getItems().clear();
        if(saves != null && saves.length >0){
            listView.getItems().addAll(saves);
        }
    }

    public void showRules() {

        Stage rulesStage = new Stage();
        rulesStage.setTitle("Rules");
        rulesStage.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #FFFDF7;");
        GlobalUI.registerLayout(layout);

        Label title = new Label("Rules");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: 900; -fx-text-fill: #2c3e50;");
        GlobalUI.registerTitle(title);

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(10, 20, 10, 10));
        contentBox.setStyle("-fx-background-color: transparent;");

        addRuleSection(contentBox, "Getting started", "Enter the number of players and select a colour.\n" +
                "When it's your turn, click on the dice to roll.\n" +
                "When you reach crossroads, click on an arrow to choose your path (some paths are riskier than others!)");

        addRuleSection(contentBox, "Winning and Losing", "Each player has an individual money and sustainability score" +
                "The first player to reach a sustainability score of 100 wins!" +
                "If you become bankrupt, you have one chance to answer a quiz question correclty for a loan");
        
        addRuleSection(contentBox, "Board Squares", "Resource squares grant you extra money" +
                "If you land on a quiz card, select your difficult and answer the question (wrong answers will deduct money!)" +
                "Event cards give you multiple options which will affect your money and sustainability socre" +
                "\n\n");
        
        addRuleSection(contentBox, "The environment", "Be careful: Global temperate will rise due to your game choices" +
                "This triggers natural disasters, affecting every player");


        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setPrefHeight(400);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 10;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Button closeButton = formatMenuButton("Close", "green");
        closeButton.setOnAction(e -> rulesStage.close());

        layout.getChildren().addAll(title, scrollPane, closeButton);
        Scene scene = new Scene(layout, 700, 600);
        rulesStage.setScene(scene);
        rulesStage.showAndWait();
    }

    //Creates consistent body for Rules
    private void addRuleSection(VBox container, String header, String body) {
        Label head = new Label(header);
        head.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 5 0 0 0;");

        Label text = new Label(body);
        text.setWrapText(true);
        text.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555; -fx-line-spacing: 5px;");
        text.setMaxWidth(600);

        container.getChildren().addAll(head, text);
    }

    public void openSettings() {
        Stage settingsStage = new Stage();

        settingsStage.setTitle("Settings");
        settingsStage.initModality(Modality.APPLICATION_MODAL);

        Button toggleFont = formatMenuButton("Toggle Font", "mediumseagreen");
        toggleFont.setOnAction(e -> {
            GlobalUI.toggleFontSize();
            refreshUI();
            resizeSettingsWindow(settingsStage);
        });

        Button toggleMode = formatMenuButton("Light / Dark Mode", "lightsteelblue");
        toggleMode.setOnAction(e -> {
            GlobalUI.toggleMode();
            refreshUI();

            //check if board has been created
            if(boardUI == null || boardRoot == null){
                try{
                    Stage stage = (Stage) startButton.getScene().getWindow();
                    VBox menuRoot = createMenuLayout();
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    Scene menuScene = new Scene(menuRoot, screenBounds.getWidth(), screenBounds.getHeight());
                    stage.setScene(menuScene);
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        Button toggleAudio = formatMenuButton("Audio On / Off", "mediumpurple");
        toggleAudio.setOnAction(e -> toggleBackgroundAudio());


        Button backButton = formatMenuButton("Back", "gold");
        backButton.setOnAction(e -> settingsStage.close());

        //all this code until scene creation is an exercise in trying to fix bug with settings title moving
        VBox settingsLayout = new VBox(20);
        Label title = new Label("Settings");
        GlobalUI.registerTitle(title);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(toggleFont, toggleMode, toggleAudio, backButton);

        settingsLayout.setAlignment(Pos.TOP_CENTER);
        settingsLayout.setPadding(new Insets(40, 20, 20, 20));
        settingsLayout.setStyle("-fx-background-color: #FFFDF7;");
        GlobalUI.registerLayout(settingsLayout);

        settingsLayout.getChildren().addAll(title, buttonBox);

        Scene settingsScene = new Scene(settingsLayout, getSettingsWindowWidth(), getSettingsWindowHeight());

        settingsStage.setScene(settingsScene);
        settingsStage.setResizable(false);
        resizeSettingsWindow(settingsStage);
        settingsStage.showAndWait();
    }

    private double getSettingsWindowWidth() {
        return GlobalUI.fontEnlarged ? 620 : 500;
    }

    private double getSettingsWindowHeight() {
        return GlobalUI.fontEnlarged ? 500 : 400;
    }

    private void resizeSettingsWindow(Stage settingsStage) {
        settingsStage.setWidth(getSettingsWindowWidth());
        settingsStage.setHeight(getSettingsWindowHeight());
        settingsStage.centerOnScreen();
    }

    public void confirmExit() {
        Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);

        confirmExit.setTitle("Exit Game");
        confirmExit.setHeaderText("Are you sure you want to exit?");
        confirmExit.setContentText("Unsaved progress may be lost!");

        ButtonType yes = new ButtonType("Yes", javafx.scene.control.ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", javafx.scene.control.ButtonBar.ButtonData.NO);

        confirmExit.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> result = confirmExit.showAndWait();

        if (result.isPresent() && result.get() == yes) {
            System.exit(0);
        }
    }

    public void showPlayerNameInputs(int playerCount) {
        Stage nameStage = new Stage();
        nameStage.setTitle("Enter Player Names");
        nameStage.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #FFFDF7; -fx-border-color: #3a7d47; -fx-border-width: 8;" +
            "-fx-background-radius: 20; -fx-border-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 20, 0, 0, 0);");

        Label instruction = new Label("Enter each player's name (max 15 chars):");
        instruction.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #3a7d47; -fx-padding: 8 30; -fx-background-radius: 10;");

        VBox playerRowsContainer = new VBox(15);
        playerRowsContainer.setAlignment(Pos.CENTER);

        // Colours to choose from
        Color[] colours = new Color[] { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
        Map<Integer, Color> playerColours = new HashMap<>(); // stores colour per player
        Map<Color, Boolean> colourTaken = new HashMap<>(); // tracks taken colours
        for (Color c : colours) colourTaken.put(c, false);

        // Creates text fields with colour selectors
        TextField[] nameFields = new TextField[playerCount];

        for (int i = 0; i < playerCount; i++) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER);

            nameFields[i] = new TextField();
            nameFields[i].setPromptText("Player " + (i + 1) + " name");
            nameFields[i].setMaxWidth(200);
            nameFields[i].setStyle("-fx-font-size: 14px; -fx-background-radius: 5");

            ToggleGroup colourGroup = new ToggleGroup();
            HBox colourBox = new HBox(8);

            for (int j = 0; j < colours.length; j++) {
                ToggleButton colourBtn = new ToggleButton();
                colourBtn.setToggleGroup(colourGroup);
                Color btnColour = colours[j];

                // Adds visible coloured circles for the buttons
                Region circle = new Region();
                circle.setPrefSize(20, 20);
                circle.setStyle("-fx-background-color: " + toHex(btnColour) + "; " +
                        "-fx-background-radius: 9px; -fx-border-color: #2c3e50; -fx-border-radius: 9px;");
                colourBtn.setGraphic(circle);

                final int playerIndex = i;

                colourBtn.setOnAction(ev -> {
                    if(!colourBtn.isSelected()) {
                        Color oldColor = playerColours.remove(playerIndex);
                        if(oldColor != null) colourTaken.put(oldColor, false);

                        nameFields[playerIndex].setStyle("-fx-control-inner-background: white; -fx-background-radius: 5;");    
                    
                    }else {
                        if(colourTaken.get(btnColour)) {
                            colourBtn.setSelected(false);
                            return;
                        }

                 Color oldColour = playerColours.get(playerIndex);
                if(oldColour != null) colourTaken.put(oldColour, false);

                //creates a lighter pastel versioon of color to ensure player name is readable
                Color lightColor = btnColour.deriveColor(0, 0.3, 1.5, 1.0);

                playerColours.put(playerIndex, btnColour);
                colourTaken.put(btnColour, true);

                nameFields[playerIndex].setStyle("-fx-control-inner-background: " + toHex(lightColor) + "; -fx-background-radius: 5;");
                    }

                     updatedColourAvailability(playerRowsContainer, colours, colourTaken);

                });

                colourBox.getChildren().add(colourBtn);
            }
            row.getChildren().addAll(nameFields[i], colourBox);
            playerRowsContainer.getChildren().add(row);

        }

        Button startButton = formatMenuButton("Start Game", "mediumseagreen");
        startButton.setPrefHeight(50);

        startButton.setOnAction(e -> {
            List<String> playerNames = new ArrayList<>();

            for (int i = 0; i < playerCount; i++) {
                String name = nameFields[i].getText().trim();

                if (name.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Player " + (i + 1) + " must enter a name.");
                    alert.showAndWait();
                    return;
                }

                if (name.length() > 15) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Player " + (i + 1) + "'s name must be 15 characters or less.");
                    alert.showAndWait();
                    return;
                }

                if (playerNames.contains(name)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Player " + (i + 1) + " has a duplicate name. Each player must have a unique name.");
                    alert.showAndWait();
                    return;
                }

                if (!playerColours.containsKey(i)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Player " + (i + 1) + " (\"" + name + "\") must select a colour.");
                    alert.showAndWait();
                    return;
                }

                playerNames.add(name);
            }

            //Player save logic
            List<Player> newPlayers = new ArrayList<>();
            for(int i = 0; i <playerNames.size(); i++) {
                String hex = toHex(playerColours.get(i));
                Player p = new Player(playerNames.get(i), hex);
                newPlayers.add(p);
            }

            nameStage.close();

            gameManager.setPlayersAndStart(newPlayers);

            showBoardScene(newPlayers);
        });
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-underline: true; -fx-font-weight: bold; -fx-cursor: hand;");
        backButton.setOnAction(e -> {
            nameStage.close();
            startGame();
        });

        layout.getChildren().addAll(instruction, playerRowsContainer, startButton, backButton);

        Scene scene = new Scene(layout, 550, 200 + (playerCount * 60));
        nameStage.setScene(scene);
        nameStage.showAndWait();
    }

    //Refresh buttons when someone picks a colour
    private void updatedColourAvailability(VBox container, Color[] colours, Map<Color, Boolean> colourTaken) {
        for (Node rowNode : container.getChildren()) {
            HBox row = (HBox) rowNode;
            HBox colourBox = (HBox) row.getChildren().get(1);
            for( int i = 0; i < colourBox.getChildren().size(); i++) {
                ToggleButton tb = (ToggleButton) colourBox. getChildren().get(i);
                tb.setDisable(colourTaken.get(colours[i]) && !tb.isSelected());
            }
        }
    }

    // Helper method for getting the colour for showPlayerNameInputs()
    private String toHex(Color colour) {
        return String.format("#%02X%02X%02X", (int) (colour.getRed() * 255), (int) (colour.getGreen() * 255), (int) (colour.getBlue() * 255));
    }

    /** 
     * 5. Interative squares and popups
     * 
     * Handles the modal windows for Quiz cards, Events, Resources and Disasters
     */
    public void quizCardMenu() {

        Stage quizStage = new Stage();
        quizStage.initModality(Modality.APPLICATION_MODAL);
        quizStage.setTitle("Quiz Card");
        playSpecialSquarePopupAudio();

        Image questionMark = new Image(getClass().getResourceAsStream("/images/questionMark.png"));
        ImageView questionMarkView = new ImageView(questionMark);

        questionMarkView.setFitWidth(150);
        questionMarkView.setPreserveRatio(true);

        VBox quizMenu = new VBox(30);
        quizMenu.setAlignment(Pos.CENTER);
        quizMenu.setPadding((new Insets(40)));
        quizMenu.setStyle("-fx-background-color: #c66abf;" + "-fx-border-color: #81347b;" + "-fx-border-width: 8;" +"-fx-background-radius: 20;" +
            "-fx-border-radius: 12;" + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 25, 0, 0, 0);");

        Label headerLabel = new Label("Quiz Square");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #81347b; -fx-padding: 10 40 10 40; -fx-background-radius: 10;");

        GlobalUI.registerLayout(quizMenu);

        Button easyButton = formatMenuButton("Easy", "#62CF3A");
        easyButton.setOnAction(e -> quizCardAction(quizMenu, "easy"));

        Button mediumButton = formatMenuButton("Medium", "#3AB6CF");
        mediumButton.setOnAction(e -> quizCardAction(quizMenu, "medium"));

        Button hardButton = formatMenuButton("Hard", "#E6452E");
        hardButton.setOnAction(e -> quizCardAction(quizMenu, "hard"));

        quizMenu.getChildren().addAll(headerLabel, questionMarkView, easyButton, mediumButton, hardButton);

        Scene scene = new Scene(quizMenu, 900, 550);
        quizStage.setScene(scene);
        quizStage.showAndWait();

    }

    public void quizCardAction(VBox quizMenu, String difficulty)
    {
        quizMenu.getChildren().clear();

        List<QuizCard> deck = gameManager.getQuizDeck();
        QuizCard selectedCard = null;

        Image questionMark = new Image(getClass().getResourceAsStream("/images/questionMark.png"));
        ImageView questionMarkView = new ImageView(questionMark);

        questionMarkView.setFitWidth(150);
        questionMarkView.setPreserveRatio(true);

        for (QuizCard card : deck)
        {
            if (card.getDifficulty().toString().equalsIgnoreCase(difficulty))
            {
                selectedCard = card;
                break;
            }
        }

        if (selectedCard == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Error loading quiz. Returning to game.");
            alert.showAndWait();

            Stage currentStage = (Stage) quizMenu.getScene().getWindow();
            currentStage.close();
            return;
        }

        Label questionText = GlobalUI.createText(selectedCard.getQuestion());
        questionText.setAlignment(Pos.CENTER);

        VBox optionsContainer = new VBox(15);
        optionsContainer.setAlignment(Pos.CENTER);

        String[] options = selectedCard.getOptions();
        for (int i = 0; i < options.length; i++) {
            final int index = i;
            QuizCard finalCard = selectedCard;

            Button optionButton = formatQuizMenuButton(options[i], "#81347b");
            optionButton.setOnAction(e -> checkQuizAnswer(quizMenu, finalCard, index));

            optionsContainer.getChildren().add(optionButton);
        }

        quizMenu.getChildren().addAll(questionMarkView, questionText, optionsContainer);

    }
    
    int eventCardCounter = 2;//track current event card
    public  void handleEventSqr(Player player)
    {
        List<EventCard> deck = gameManager.getEventDeck();
        EventCard selectedCard = deck.get(eventCardCounter);

        Stage eventStage = new Stage();
        eventStage.initModality(Modality.APPLICATION_MODAL);
        eventStage.setTitle("Event Card");
        playSpecialSquarePopupAudio();

         if (selectedCard == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Error loading event card. Returning to game.");
            alert.showAndWait();

            eventStage.close();
            return;
        }

        Image eventImage = new Image(getClass().getResourceAsStream("/images/exclamationPoint.png"));
        ImageView eventView = new ImageView(eventImage);

        eventView.setFitWidth(50);
        eventView.setPreserveRatio(true);

        VBox eventCard = new VBox(30);
        eventCard.setAlignment(Pos.CENTER);
        eventCard.setPadding((new Insets(40)));

        eventCard.setStyle("-fx-background-color: #fdb479;" + "-fx-border-color: #9e4904;" +
            "-fx-border-width: 8;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 25, 0, 0, 0);");

        Label headerLabel = new Label("Event Square");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #e64900; -fx-padding: 10 40 10 40; -fx-background-radius: 10;");

        Button Option1Button = formatMenuButton(selectedCard.getChoices().get(0).getDescription(), "#f3750d");
        Option1Button.setOnAction(e -> handleEventChoice(eventCard, player, selectedCard, 1));

        Button Option2Button = formatMenuButton(selectedCard.getChoices().get(1).getDescription(), "#f3750d");
        Option2Button.setOnAction(e -> handleEventChoice(eventCard, player, selectedCard, 2));

        Label descriptionLabel = new Label(selectedCard.getDetails());
        descriptionLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-text-alignment: center;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        descriptionLabel.setMaxWidth(750);
        descriptionLabel.setMinHeight(Region.USE_PREF_SIZE);
        descriptionLabel.setAlignment(Pos.CENTER);


        eventCard.getChildren().addAll(headerLabel, eventView, descriptionLabel, Option1Button, Option2Button);
        Scene scene = new Scene(eventCard, 900, 650);
        eventStage.setScene(scene);
        eventStage.showAndWait();

    }

    public void handleEventChoice(VBox eventCard, Player player, EventCard selectedCard, int choice)
    {
        //need to add check for when player cannot afford either choice

        eventCard.getChildren().clear();
        eventCard.setAlignment(Pos.CENTER);

        var selectedChoice = selectedCard.getChoices().get(choice - 1);
        String description = selectedChoice.getDescription();
        int cost = selectedChoice.getCost();
        int moneyChange = selectedChoice.getMoneyChange();
        int sustainabilityChange = selectedChoice.getSustainabilityChange();
        float tempChange = selectedChoice.getGlobalTempChange();

        Label headerLabel = new Label("Event Square");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #e64900; -fx-padding: 10 40 10 40; -fx-background-radius: 10;");

        Label titleLabel = new Label("Event Card");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        GlobalUI.registerTitle(titleLabel);

        Label result = GlobalUI.createText("You Chose:  " + description + "\nMoney Change: £" + moneyChange + "\nSustainability Change: " + sustainabilityChange + "\nGlobal Temperature Change: " + tempChange + "°C");

        result.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-text-alignment: center;");
        result.setWrapText(true);
        result.setMaxWidth(600);
        result.setAlignment(Pos.CENTER);

        if (eventCardCounter < gameManager.getEventDeck().size() - 1) {
            eventCardCounter++;
        } else {
            eventCardCounter = 0; // reset to first card
        }

        VBox feedback = new VBox(20);
        feedback.setAlignment(Pos.CENTER);
        feedback.getChildren().add(result);

        Button continueButton = formatMenuButton("Continue", "#f3750d");
            continueButton.setOnAction(e -> {
            player.updateResources(moneyChange, sustainabilityChange);
            gameManager.updateGlobalTemp(tempChange);
            refreshUI();
            Stage currentStage = (Stage) eventCard.getScene().getWindow();
            currentStage.close();
        });
        // Spacer to push content to vertical centre
        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        javafx.scene.layout.VBox.setVgrow(topSpacer, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.VBox.setVgrow(bottomSpacer, javafx.scene.layout.Priority.ALWAYS);
        eventCard.getChildren().addAll(topSpacer, headerLabel, titleLabel, feedback, continueButton, bottomSpacer);
        GlobalUI.registerLayout(eventCard);

    }


    public  void handleCheckpointSqr(Player player, int reward)
    {
        Stage cpStage = new Stage();
        cpStage.initModality(Modality.APPLICATION_MODAL);
        cpStage.setTitle("Checkpoint");
        playSpecialSquarePopupAudio();

        Label title = new Label("Checkpoint Square");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #ffffff");
        GlobalUI.registerTitle(title);

        Image checkpointImage = new Image(getClass().getResourceAsStream("/images/poundSign.png"));
        ImageView checkpointView = new ImageView(checkpointImage);

        checkpointView.setFitWidth(50);
        checkpointView.setPreserveRatio(true);

        VBox cpCard = new VBox(30);
        cpCard.setAlignment(Pos.CENTER);
        cpCard.setPadding((new Insets(40)));

        cpCard.setStyle(
            "-fx-background-color: #782ecc;" + "-fx-border-color: #531e84;" + "-fx-border-width: 8;" + "-fx-background-radius: 20;" +
            "-fx-border-radius: 12;" + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 25, 0, 0, 0);"
        );

        Label headerLabel = new Label("Checkpoint Square");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #5d1e84; -fx-padding: 10 40 10 40; -fx-background-radius: 10;");

         Label titleLabel = new Label("Checkpoint reached!");
        titleLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: 900; -fx-text-fill: white; " +
            "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 3, 3, 1, 1);");

        Label descriptionLabel = new Label("Congratulations! You landed on a checkpoint!");
        descriptionLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-text-alignment: center;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        descriptionLabel.setMaxWidth(750);
        descriptionLabel.setMinHeight(Region.USE_PREF_SIZE);
        descriptionLabel.setAlignment(Pos.CENTER);

        Label rewardLabel = new Label("Reward +£" + reward);
        rewardLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #ffeb3b; " +
            "-fx-padding: 15; -fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-width: 2 0 2 0;");

        Button continueButton = formatMenuButton("Continue", "#a480cd");
        continueButton.setOnAction(e -> {
            //contiue to next turn
            refreshUI();
            Stage currentStage = (Stage) cpCard.getScene().getWindow();
            currentStage.close();
        });

        cpCard.getChildren().addAll(headerLabel, titleLabel, checkpointView, descriptionLabel, rewardLabel, continueButton);
        Scene scene = new Scene(cpCard, 900, 650);
        cpStage.setScene(scene);
        cpStage.showAndWait();

    }

    public  void handleResourceSqr(Player player, int reward)
    {
        Stage resStage = new Stage();
        resStage.initModality(Modality.APPLICATION_MODAL);
        resStage.setTitle("Resource Square");
        playSpecialSquarePopupAudio();

        Image resourceImage = new Image(getClass().getResourceAsStream("/images/poundSign.png"));
        ImageView resourceView = new ImageView(resourceImage);

        resourceView.setFitWidth(50);
        resourceView.setPreserveRatio(true);

        Label title = new Label("Resource Square");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        GlobalUI.registerTitle(title);

        VBox resCard = new VBox(30);
        resCard.setAlignment(Pos.CENTER);
        resCard.setPadding((new Insets(40)));

        resCard.setStyle(
            "-fx-background-color: #2ecc71;" + "-fx-border-color: #1e8449;" + "-fx-border-width: 8;" +"-fx-background-radius: 20;" +
            "-fx-border-radius: 12;" + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 25, 0, 0, 0);"
        );

        Label headerLabel = new Label("Resource Square");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #1e8449; -fx-padding: 10 40 10 40; -fx-background-radius: 10;");

        Label titleLabel = new Label("Resources secured!");
        titleLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: 900; -fx-text-fill: white; " +
            "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 3, 3, 1, 1);");

        Label descriptionLabel = new Label("You have been gifted some money!");
        descriptionLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-text-alignment: center;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        descriptionLabel.setMaxWidth(750);
        descriptionLabel.setMinHeight(Region.USE_PREF_SIZE);
        descriptionLabel.setAlignment(Pos.CENTER);

        Label rewardLabel = new Label("Reward +£" + reward);
        rewardLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #ffeb3b; " +
            "-fx-padding: 15; -fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-width: 2 0 2 0;");


        Button continueButton = formatMenuButton("Claim reward", "#1e8449");
        continueButton.setStyle(continueButton.getStyle() + "-fx-text-fill: #27ae60; -fx-font-size: 20px;");
        continueButton.setOnAction(e -> {
            //contiue to next turn
            refreshUI();
            Stage currentStage = (Stage) resCard.getScene().getWindow();
            currentStage.close();
        });

        resCard.getChildren().addAll(headerLabel, titleLabel, resourceView, descriptionLabel, rewardLabel, continueButton);
        Scene scene = new Scene(resCard, 850, 600);
        resStage.setScene(scene);
        resStage.showAndWait();

    }


    public void checkQuizAnswer(VBox quizMenu, QuizCard selectedCard, int chosenIndex)
    {
        System.out.println(0);
        quizMenu.getChildren().clear();
        String result;
        String colour;
        Label action;

        Player currentPlayer = gameManager.getCurrentPlayer();

        final int moneyChange;

        if (selectedCard.getCorrectIndex() == chosenIndex)
        {
            result = "Correct!";
            colour = "#62CF3A";
            action = GlobalUI.createText("Reward: +£" + selectedCard.getRewardAmount());
            action.setStyle(action.getStyle() + "-fx-text-fill: white");
            moneyChange = selectedCard.getRewardAmount();
        }
        else
        {
            result = "Incorrect";
            colour = "#E6452E";
            action = GlobalUI.createText("Penalty: -£" + selectedCard.getPenaltyAmount());
            action.setStyle(action.getStyle() + "-fx-text-fill: white");
            moneyChange = -selectedCard.getPenaltyAmount();

        }

        // NOTE: Update player logic here!
        // // e.g., gameManager.getCurrentPlayer().updateResources(selectedCard.getRewardAmount(), 0);
        Label headerLabel = new Label("Quiz Square");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
            "-fx-background-color: #81347b; -fx-padding: 10 40 10 40; -fx-background-radius: 10;");
        
        System.out.println(6);
        Label resultTitle = GlobalUI.createTitle(result);
        resultTitle.setStyle(resultTitle.getStyle() + "-fx-text-fill: " + colour + ";"); ///
        GlobalUI.registerTitle(resultTitle);

        System.out.println(7);

        VBox feedback = new VBox(10);
        feedback.setAlignment(Pos.CENTER);

        feedback.getChildren().add(action);

        Button continueButton = formatMenuButton("Continue", "#81347b");
        continueButton.setOnAction(e -> {
            //contiue to next turn
            gameManager.removeQuizCard(selectedCard);
            currentPlayer.updateResources(moneyChange, 0);
            refreshUI();
            Stage currentStage = (Stage) quizMenu.getScene().getWindow();
            currentStage.close();
        });
        Label explanation = GlobalUI.createText(selectedCard.getExplanation());
        explanation.setStyle(explanation.getStyle() + "-fx-text-fill: white");
        explanation.setWrapText(true);
        explanation.setMaxWidth(600);
        explanation.setAlignment(Pos.CENTER);
        quizMenu.getChildren().addAll(headerLabel, resultTitle, feedback, continueButton,explanation);
    }

    public void handleQuizSquare(Player player) {
        quizCardMenu();
    }

    public void setBackgroundImage(Region layout, String backgroundPath){


        var backgroundUrl = getClass().getResource(backgroundPath);
        if (backgroundUrl != null){
            Image backgroundImage = new Image(backgroundUrl.toExternalForm());
            BackgroundSize size = new BackgroundSize(100, 100, true, true, false, true);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
            
            layout.setBackground(new Background(background));
        }
    }

    public void setBackgroundImage(VBox layout, String backgroundPath){


        var backgroundUrl = getClass().getResource(backgroundPath);
        if (backgroundUrl != null){
            Image backgroundImage = new Image(backgroundUrl.toExternalForm());
            BackgroundSize size = new BackgroundSize(100, 100, true, true, false, true);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
            
            layout.setBackground(new Background(background));
        }
    }

    /**
     * 6. Main Game board rendering
     * 
     * Transitions to the active game state, building the grid, HUD and temp Bar.
     */

    private void showBoardScene(List<Player> players) {
        this.boardUI = new BoardUI();
        Board boardLogic = new Board();
        //Build board
        Pane boardPane = boardUI.buildBoard(boardLogic, gameManager);
        VBox leftPanel = boardUI.drawPlayerStats();

        //Add players
        boardUI.initialisePlayerMarker(players);

        //Set up container
        Group scalableGroup = new Group(boardPane);
        BorderPane root = new BorderPane(scalableGroup, null, null, null, leftPanel);
        boardRoot = root;

        setBackgroundImage(root, "/images/lightBackground.png");

        //HUD bar
        HBox topBar = new HBox(100);
        topBar.setAlignment(Pos.CENTER);
        boardTopBar = topBar;

        //Bar size & style
        topBar.setMaxSize(600, 45);
        topBar.setStyle(
            "-fx-background-color: #FFFDF7;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 0 3 3 3;" +
            "-fx-background-radius: 0 0 15 15;" +
            "-fx-border-radius: 0 0 15 15;"
        );

        //Home button
        Button homeBtn = new Button("Home");
        homeBtn.setStyle("-fx-background-color: indianred; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        var homeUrl = getClass().getResource("/images/homepage.png");
        //Validation incase image not found
        if(homeUrl != null) {
            ImageView homeIcon = new ImageView(new Image(homeUrl.toExternalForm()));
            homeIcon.setFitWidth(30);
            homeIcon.setFitHeight(30);
            homeIcon.setPreserveRatio(true);
            homeBtn.setGraphic(homeIcon);
            homeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
            homeBtn.setText("");
        }else {
            System.out.print("Home.png not found");
        }
        homeBtn.setOnAction(e -> confirmHome());

        //Round
        liveRoundLabel = new Label("Round " + gameManager.getTurnCounter());
        liveRoundLabel.setStyle("-fx-text-fill: black; -fx-font-size: " + getBoardHudLabelFontSize() + "px; -fx-font-weight: bold;");

        //Settings button
        Button settingsBtn = new Button("Settings");
        settingsBtn.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-font-weight: bold; -fx-cursor: hand;");

        var settingsUrl = getClass().getResource("/images/settings.png");
        if(settingsUrl != null) {
            ImageView settingsIcon = new ImageView(new Image(settingsUrl.toExternalForm()));
            settingsIcon.setFitHeight(30);
            settingsIcon.setFitWidth(30);
            settingsIcon.setPreserveRatio(true);
            settingsBtn.setGraphic(settingsIcon);
            settingsBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
            settingsBtn.setText("");
        }else{
            System.out.print("settings.png not found");
        }
        
        settingsBtn.setOnAction(e -> openSettings());

        //Save button
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #3AB6CF; -fx-text-fill: black; -fx-font-weight: bold; -fx-cursor: hand;");
        var saveUrl = getClass().getResource("/images/save.png");
        if(saveUrl != null) {
            ImageView saveIcon = new ImageView(new Image(saveUrl.toExternalForm()));
            saveIcon.setFitHeight(30);
            saveIcon.setFitWidth(30);
            saveIcon.setPreserveRatio(true);
            saveBtn.setGraphic(saveIcon);
            saveBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
            saveBtn.setText("");
        }else {
            System.out.println("save.png not found");
        }

        saveBtn.setOnAction(e -> handleQuickSave());

        topBar.getChildren().addAll(homeBtn,liveRoundLabel, saveBtn, settingsBtn);
        BorderPane.setAlignment(topBar, Pos.CENTER);
        root.setTop(topBar);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        tempBarHeight = screenBounds.getHeight() * 0.55;
        double tubeWidth = TEMP_BAR_WIDTH;

        double currentTemp = gameManager.getGlobalTemp();
        double fillRatio = Math.min(Math.max(currentTemp / 2.5, 0.0), 1.0);

        temperatureTrack = new Rectangle(tubeWidth, tempBarHeight);
        temperatureTrack.setFill(Color.web("#EDEDED"));
        temperatureTrack.setArcWidth(tubeWidth);
        temperatureTrack.setArcHeight(tubeWidth);
        temperatureTrack.setStroke(Color.web("#AAAAAA"));
        temperatureTrack.setStrokeWidth(2);

        temperatureFill = new Rectangle();
        temperatureFill.setWidth(tubeWidth);
        temperatureFill.setHeight(Math.max(fillRatio > 0 ? tubeWidth : 0, tempBarHeight * fillRatio));
        temperatureFill.setFill(Color.color(fillRatio, 1 - fillRatio, 0));
        temperatureFill.setArcWidth(tubeWidth);
        temperatureFill.setArcHeight(tubeWidth);

        StackPane tubeStack = new StackPane();
        tubeStack.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(temperatureTrack, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(temperatureFill, Pos.BOTTOM_CENTER);
        tubeStack.getChildren().addAll(temperatureTrack, temperatureFill);

        // Labels
        String tempTextColour = GlobalUI.darkMode ? "white" : "black";

        Label tempValueLabel = new Label(String.format("%.1f°C", currentTemp));
        tempValueLabel.setStyle("-fx-font-size: " + getTemperatureValueFontSize() + "px; -fx-font-weight: 900; -fx-text-fill: " + tempTextColour + ";");
        this.temperatureValueLabel = tempValueLabel;

        Label liveTempLabel = new Label("Global Temp");
        liveTempLabel.setStyle("-fx-font-size: " + getBoardHudTitleFontSize() + "px; -fx-font-weight: 900; -fx-text-fill: " + tempTextColour + ";");
        liveTempLabel.setWrapText(true);
        liveTempLabel.setAlignment(Pos.CENTER);
        liveTemperatureBar = liveTempLabel;

        double panelHeight = tempBarHeight + 130;
        VBox tempBox = new VBox(10, liveTempLabel, tempValueLabel, tubeStack);
        tempBox.setAlignment(Pos.CENTER);
        double tempPanelWidth = getTemperaturePanelWidth();
        tempBox.setPrefWidth(tempPanelWidth);
        tempBox.setMinWidth(tempPanelWidth);
        tempBox.setMaxWidth(tempPanelWidth);
        tempBox.setPrefHeight(panelHeight);
        tempBox.setMaxHeight(panelHeight);
        tempBox.setStyle(
                "-fx-background-color: rgba(255,253,247,0.85);" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(0,0,0,0.1);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-padding: 18 15 18 15;"
        );
        boardTempBox = tempBox;

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            double scale = newVal.doubleValue() / 1050;
            scalableGroup.setScaleX(scale);
            scalableGroup.setScaleY(scale);
        });

        BorderPane.setAlignment(topBar, Pos.TOP_CENTER);
        root.setTop(topBar);
        root.setRight(tempBox);
        BorderPane.setAlignment(tempBox, Pos.CENTER);
        BorderPane.setMargin(tempBox, new Insets(0, 35, 0, 10));

        //Switch to game board
        StackPane background = new StackPane();
        setBackgroundImage(background, "/images/lightBackground.png");
        background.getChildren().add(root);
        Scene gameScene = new Scene(background, screenBounds.getWidth(), screenBounds.getHeight());
        gameScene.setFill(Color.TRANSPARENT);

        Stage currentStage = (Stage) startButton.getScene().getWindow();
        currentStage.setScene(gameScene);
        currentStage.setMaximized(false);
        currentStage.setMaximized(true);
        //applyBoardScreenTheme();
        updateTemperatureBar(gameManager.getGlobalTemp());
    } 

    private void updateTemperatureBar(double currentTemp) {
        if (temperatureFill == null || temperatureValueLabel == null) {
            return;
        }

        double fillRatio = Math.min(Math.max(currentTemp / 2.5, 0.0), 1.0);

        temperatureFill.setHeight(Math.max(fillRatio > 0 ? TEMP_BAR_WIDTH : 0, tempBarHeight * fillRatio));
        temperatureFill.setFill(Color.color(fillRatio, 1 - fillRatio, 0));

        String tempTextColour = GlobalUI.darkMode ? "white" : "black";
        temperatureValueLabel.setText(String.format("%.1f°C", currentTemp));
        temperatureValueLabel.setStyle("-fx-font-size: " + getTemperatureValueFontSize() + "px; -fx-font-weight: 900; -fx-text-fill: " + tempTextColour + ";");
    }

    public void applyBackgroundTheme(Region layout) {
        if (layout != null) {
            layout.setStyle("");
            if(GlobalUI.darkMode){
                setBackgroundImage(layout,"/images/darkBackground.png");

            }else{
                setBackgroundImage(layout, "/images/lightBackground.png");
            }
        }
        
        if (boardTopBar != null) {
            String topBarBackground = GlobalUI.darkMode ? "#3A3A3A" : "#FFFDF7";
            String topBarBorder = GlobalUI.darkMode ? "#DDDDDD" : "black";
            boardTopBar.setStyle(
                "-fx-background-color: " + topBarBackground + ";" +
                "-fx-border-color: " + topBarBorder + ";" +
                "-fx-border-width: 0 3 3 3;" +
                "-fx-background-radius: 0 0 15 15;" +
                "-fx-border-radius: 0 0 15 15;"
            );
        }
        if (boardTempBox != null) {
            double tempPanelWidth = getTemperaturePanelWidth();
            boardTempBox.setPrefWidth(tempPanelWidth);
            boardTempBox.setMinWidth(tempPanelWidth);
            boardTempBox.setMaxWidth(tempPanelWidth);
            boardTempBox.setStyle(
                "-fx-background-color: " + (GlobalUI.darkMode ? "rgba(40,40,40,0.88)" : "rgba(255,253,247,0.85)") + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + (GlobalUI.darkMode ? "rgba(255,255,255,0.1)" : "rgba(0,0,0,0.1)") + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-padding: 18 15 18 15;"
            );
        }
        if (temperatureTrack != null) {
            temperatureTrack.setFill(Color.web(GlobalUI.darkMode ? "#444444" : "#EDEDED"));
            temperatureTrack.setStroke(Color.web(GlobalUI.darkMode ? "#666666" : "#AAAAAA"));
        }
        if (liveRoundLabel != null) {
            liveRoundLabel.setStyle("-fx-text-fill: " + (GlobalUI.darkMode ? "white" : "black") + "; -fx-font-size: " + getBoardHudLabelFontSize() + "px; -fx-font-weight: bold;");
        }
        if (temperatureValueLabel != null) {
            String tempTextColour = GlobalUI.darkMode ? "white" : "black";
            temperatureValueLabel.setStyle("-fx-font-size: " + getTemperatureValueFontSize() + "px; -fx-font-weight: 900; -fx-text-fill: " + tempTextColour + ";");
        }
        if (liveTemperatureBar != null) {
            String tempTextColour = GlobalUI.darkMode ? "white" : "black";
            liveTemperatureBar.setStyle("-fx-font-size: " + getBoardHudTitleFontSize() + "px; -fx-font-weight: 900; -fx-text-fill: " + tempTextColour + ";");
        }
    }

    private int getBoardHudLabelFontSize() {
        return GlobalUI.fontEnlarged ? 24 : 18;
    }

    private int getBoardHudTitleFontSize() {
        return GlobalUI.fontEnlarged ? 22 : 18;
    }

    private int getTemperatureValueFontSize() {
        return GlobalUI.fontEnlarged ? 30 : 24;
    }

    private double getTemperaturePanelWidth() {
        return GlobalUI.fontEnlarged ? 190 : 160;
    }

    public void handleQuickSave() {
        if(gameManager.hasActiveSave()) {
            gameManager.quickSave();
            Alert success = new Alert(Alert.AlertType.INFORMATION, "Quick save successful");
            success.show();
        }else {
            String saveName = promptSaveName();
            if(saveName != null) {
                if(gameManager.saveGameAs(saveName)) {
                    Alert succes = new Alert(Alert.AlertType.INFORMATION, "Game saved as " + saveName);
                    succes.show();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Failed to save");
                    error.show();
                }
            }
        }
    }
    public void confirmHome() {
        Alert confirmHome = new Alert(Alert.AlertType.CONFIRMATION);
        confirmHome.setTitle("Return to Main Menu");
        confirmHome.setHeaderText("Do you want to save your game before leaving?");
        confirmHome.setContentText("Any unsaved progress will be lost.");

        ButtonType btnSaveAndLeave = new ButtonType("Save and Leave");
        ButtonType btnLeaveWithoutSaving = new ButtonType("Leave without Saving");
        ButtonType btnCancel = new ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmHome.getButtonTypes().setAll(btnSaveAndLeave, btnLeaveWithoutSaving, btnCancel);

        Optional<ButtonType> result = confirmHome.showAndWait();

        if(result.isPresent()) {
            if(result.get() == btnSaveAndLeave) {

                if(gameManager.hasActiveSave()) {
                    gameManager.quickSave();
                    goToMainMenu();
                }else{
                    String saveName = promptSaveName();
                    if(saveName != null) {
                        gameManager.saveGameAs((saveName));
                        goToMainMenu();
                    }
                }
            } else if (result.get() == btnLeaveWithoutSaving) {
                goToMainMenu();
            }
        }
    }

    private void goToMainMenu() {
        try {
            //get current menu and create layout
            Stage stage = (Stage) liveRoundLabel.getScene().getWindow();

            VBox menuRoot = createMenuLayout();
            
            //Get screen bounds and create screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene menuScene = new Scene(menuRoot, screenBounds.getWidth(), screenBounds.getHeight());

            stage.setScene(menuScene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            System.out.print("problem has occured");
        }
    }

    private String promptSaveName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your save file");
        dialog.setContentText("Name (no spaces): ");

        Optional<String> result = dialog .showAndWait();
        if(result.isPresent() && !result.get().trim().isEmpty()) {
            return result.get().trim();
        }
        return null;
    }

    public void handleNaturalDisasters(NaturalDisaster disaster, int moneyChange){

        Stage disasterStage = new Stage();
        disasterStage.initModality(Modality.APPLICATION_MODAL);

        disasterStage.setTitle("Climate Alert");

        VBox disasterCard = new VBox(25);
        disasterCard.setAlignment(Pos.CENTER);
        disasterCard.setPadding(new Insets(40));

        disasterCard.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-border-color: #7a1a10;" +
            "-fx-border-width: 8;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius:12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 25, 0, 0, 0);"
        );

        Label warningLabel = new Label("Climate Warning");
        warningLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " + "-fx-background-color: #7a1a10; -fx-padding: 10 40 10 40; -fx-background-radius: 10;");
        
        Label title = new Label(disaster.getName().toUpperCase());
        title.setStyle("-fx-font-size: 52px; -fx-font-weight: 900; -fx-text-fill: white; " + "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 3, 3, 1, 1);");
        
        Label getDescriptionLabel = new Label(disaster.getDescription());
        getDescriptionLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-text-alignment: center;");
        getDescriptionLabel.setWrapText(true);
        getDescriptionLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        getDescriptionLabel.setMaxWidth(750);
        getDescriptionLabel.setMinHeight(Region.USE_PREF_SIZE);
        getDescriptionLabel.setAlignment(Pos.CENTER);

        Label penalty = new Label("All players Lose: £" + moneyChange);
        penalty.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #ffffff; " + "-fx-padding: 15; -fx-border-width: 2 0 2 0;");
        
        Button continueButton = formatMenuButton("Continue", "white");
        continueButton.setStyle(continueButton.getStyle() + "-fx-text-fill: #e74c3c; -fx-font-size: 20px;");
        continueButton.setOnAction(e -> disasterStage.close());

        disasterCard.getChildren().addAll(warningLabel, title, getDescriptionLabel, penalty, continueButton);

        GlobalUI.registerLayout(disasterCard);

        Scene scene = new Scene(disasterCard, 850, 600);
        disasterStage.setScene(scene);
        disasterStage.showAndWait();

    }
    
    public void refreshUI() {
        if(boardUI != null) {
            boardUI.drawPlayerStats();
            boardUI.applyBoardTheme();
        }
        if (gameManager != null) {
            applyBackgroundTheme(boardRoot);
            updateTemperatureBar(gameManager.getGlobalTemp());
        }
    }

    /**
     * 7. Game Over and application Utilities
     * 
     * Handles the final leaderboard calculations, exiting and returning home
     */

    public void showGameOverLeaderboard(List<Player> rankedPlayers, String outcomeMsg) {
        Stage leaderStage = new Stage();
        leaderStage.initModality(Modality.APPLICATION_MODAL);
        leaderStage.setTitle("Game Over = Leaderboard");

        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #FFFDF7");
        GlobalUI.registerLayout(layout);

        Label title = new Label("Game Over!");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        GlobalUI.registerTitle(title);

        Label overLbl = new Label(outcomeMsg);
        overLbl.setStyle("-fx-font-size: 20px; -fx-font-style: italic; -fx-text-fill: #555555");
        GlobalUI.register(overLbl);

        VBox leaderboard = new VBox(15);
        leaderboard.setAlignment(Pos.CENTER);

        //Build each players position
        for(int i = 0; i < rankedPlayers.size(); i++) {
            Player p = rankedPlayers.get(i);
            HBox playerRow = new HBox(20);
            playerRow.setAlignment(Pos.CENTER_LEFT);
            playerRow.setPadding(new Insets(15));

            String medalColour = switch(i) {
                case 0 -> "#FFD700";
                case 1 -> "C0C0C0";
                case 2 -> "#CD7F32";
                default -> "EEEEEE";
            };

            playerRow.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + p.getColorString() + ";" +
                "-fx-border-width: 4;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(three-pass-box, " + medalColour + ", 15, 0.5, 0, 0);"
            );
            playerRow.setMaxWidth(450);

            Label rank = new Label("#" + (i + 1));
            rank.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + medalColour + ";");

            VBox stats = new VBox(5);
            Label name = new Label(p.getName());
            name.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + p.getColorString() + ";");

            Label score = new Label("🌿 Sustainability: " + p.getSustainabilityScore() + "   |  💵 Money £" + p.getMoney());
            score.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            stats.getChildren().addAll(name, score);
            playerRow.getChildren().addAll(rank, stats);
            leaderboard.getChildren().add(playerRow);

        }

        Button homeBtn = formatMenuButton("Return Home", "red");
        homeBtn.setOnAction(e -> {
            leaderStage.close();
            goToMainMenu();
        });

        layout.getChildren().addAll(title, overLbl, leaderboard, homeBtn);

        Scene scene = new Scene(layout, 700, 750);
        leaderStage.setScene(scene);
        leaderStage.show();
    }

    public boolean showLoanQuiz(Player player, QuizCard card) {
        Stage loanStage = new Stage();
        loanStage.initModality((Modality.APPLICATION_MODAL));
        loanStage.setTitle("Bailout Opportunity");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FFFDF7; -fx-border-color: gold; -fx-border-width: 5; -fx-padding: 30;");
        Label header = new Label("Bankrupty incoming!");
        header.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label question = new Label(card.getQuestion());
        question.setWrapText(true);

        final boolean[] passed = {false};

        for(int i = 0; i < card.getOptions().length; i++) {
            final int index = i;
            Button btn = formatQuizMenuButton(card.getOptions()[i], "lightsteelblue");
            btn.setOnAction( e -> {
                passed[0] = card.checkAnswer(index);
                loanStage.close();
            });
            layout.getChildren().add(btn);
        }
        layout.getChildren().add(0, header);
        layout.getChildren().add(1, question);
        loanStage.setScene(new Scene(layout, 550, 500));
        loanStage.showAndWait();
        return passed[0];
    }
}
