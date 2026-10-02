package ui;

// =================== AI for testing ===============
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.GameManager;
import enums.SquareType;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import objects.Board;
import objects.BoardSquare;
import objects.Player;

/**
 * 1. BoardUI is responsible for rending the physical game board.
 *  - Maps abstract logical nodes to X and Y screen coordinates
 *  - handles player movement animation
 *  - renders crossroad arrows 
 *  - maintains live player stats HUD
 */
public class BoardUI {
    private static final double SQUARE_IMAGE_WIDTH = 34;
    private static final double SQUARE_IMAGE_HEIGHT = 34;
    
    private Pane boardContainer = new Pane();
    public VBox playerStats = new VBox(10);
    private Map<Integer, StackPane> visualSquares = new HashMap<>();
    private Map<Integer, SquareType> squareTypes = new HashMap<>();
    private final Map<SquareType, String> squareTypeImagePaths = createSquareTypeImagePaths();
    private final Map<Integer, String> squareIdImagePaths = createSquareIdImagePaths();
    private Map<Integer, double[]> boardCoordinates;
    private Map<Integer, Circle> playerMarkers = new HashMap<>();
    private GameManager gameManager;

    private Button diceBtn = new Button();
    private Text diceRollText;

    //Box calculations for grid layout
    private final double stepX = 500.0/9.0;
    private final double stepY = 50.0;

    /** 
     * 2. Core board initialisation and square rendering
     * Generates the base grid, assigns square colours and applies icons
     */
    public Pane buildBoard(Board boardLogic, GameManager gameManager) {
        this.gameManager = gameManager;
        this.boardCoordinates = getCoordinates();

        //Creates squares first (form base)
        for(int i = 0; i< boardLogic.getSquareCount(); i++) {
            SquareType type = boardLogic.getSquareType(i);
            StackPane visualSquare = createdVisualSquare(i, type, boardCoordinates.get(i));
            visualSquares.put(i, visualSquare);
            squareTypes.put(i, type);
            boardContainer.getChildren().add(visualSquare);
        }

        //Overlay arrows
        for(int i = 0; i < boardLogic.getSquareCount(); i++) {
            BoardSquare square = boardLogic.getSquare(i);
            if (square.getNextSquares().length > 1) {
                for(int nextId: square.getNextSquares()) {
                    drawArrow(boardCoordinates.get(i), boardCoordinates.get(nextId), nextId);
                }
            }
        }
    
        buildDiceButton();
        applyBoardTheme();
        
         return boardContainer;

         
    }
    
    //Build square visuals
    private StackPane createdVisualSquare(int id, SquareType type, double[] pos) {
        StackPane stack = new StackPane();
       
        // Rectangle size
        Rectangle rectangle = new Rectangle(stepX, stepY, squareTypeColor(type));
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);

        stack.getChildren().add(rectangle);

        ImageView squareImage = createSquareImage(id, type);
        if (squareImage != null) {
            stack.getChildren().add(squareImage);
        }

        //Start checkpoint
        if(id == 0) {
            Text text = new Text("Start");
            text.setFill(GlobalUI.darkMode ? Color.WHITE : Color.BLACK);
            text.setFont(Font.font("Arial",FontWeight.BOLD, 12));
            stack.getChildren().add(text);
        }

        stack.setLayoutX(pos[0] - (stepX / 2));
        stack.setLayoutY(pos[1] - (stepY / 2));
        return stack;
    }

    private Map<SquareType, String> createSquareTypeImagePaths() {
        Map<SquareType, String> imagePaths = new HashMap<>();

        // Shared overlay icons for board square types.
        imagePaths.put(SquareType.QUIZ, "/images/questionMark.png");
        imagePaths.put(SquareType.RESOURCE, "/images/poundSign.png");
        imagePaths.put(SquareType.EVENT, "/images/exclamationPoint.png");
        imagePaths.put(SquareType.CHECKPOINT, "/images/white_flag.png");


        return imagePaths;
    }

    private Map<Integer, String> createSquareIdImagePaths() {
        Map<Integer, String> imagePaths = new HashMap<>();
        return imagePaths;
    }

    private ImageView createSquareImage(int squareId, SquareType type) {
        String imagePath = squareIdImagePaths.get(squareId);
        if (imagePath == null) {
            imagePath = squareTypeImagePaths.get(type);
        }

        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        try {
            java.net.URL resource = getClass().getResource(imagePath);
            if (resource == null) {
                System.out.println("Square image not found: " + imagePath);
                return null;
            }

            ImageView imageView = new ImageView(new Image(resource.toExternalForm()));
            imageView.setFitWidth(SQUARE_IMAGE_WIDTH);
            imageView.setFitHeight(SQUARE_IMAGE_HEIGHT);
            imageView.setPreserveRatio(true);
            imageView.setMouseTransparent(true);
            return imageView;
        } catch (Exception ex) {
            System.out.println("Unable to load square image: " + imagePath);
            return null;
        }
    }

    //Assign color to square types
    private Color squareTypeColor(SquareType type) {
        if (type == SquareType.BLANK && GlobalUI.darkMode) {
            return Color.web("#4A4A4A");
        }
        return switch (type) {
            case EVENT -> Color.web("#f3750d");
            case QUIZ -> Color.web("#f414b8");
            case RESOURCE -> Color.web("#32CD32");
            case CHECKPOINT -> Color.web("#4819ae");
            default -> Color.web("#ecf0f1");
        };
    }

    /** 
     * 3. Crossroad arrows and interaction Logic
     * Draws directional arrows using trigonometry and handles mouse click routing
     */

    //Arrows for main or branch choice
    private void drawArrow(double[] start, double[] end, int targetNodeId) {
        if(start == null || end == null) return;

        double diffX = end[0] - start[0];
        double diffY = end[1] - start[1];

        //Calculates the angle between the start and end nodes to properly rotate the arrow
        double angle = Math.atan2(diffY,diffX);

        //Calculate tail offsets and head offsets to prevent arrow from overlapping square UI elements
        double startX = start[0] + diffX * 0.3;
        double startY = start[1] + diffY * 0.3;

        //arrow head
        double tipX = start[0] + diffX * 0.8;
        double tipY = start[1] + diffY * 0.8;

        double size = 18;

        //prevent overlay
        double hideOffset = size * 0.5;

        double lineEndX = tipX - hideOffset * Math.cos(angle);
        double lineEndY = tipY - hideOffset * Math.sin(angle);

        //Create line
        Line line = new Line(startX, startY, lineEndX, lineEndY);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(5);
        line.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);

        //Triangle
        Polygon tip = new Polygon();
        tip.getPoints().addAll(new Double[] {
            tipX, tipY,
            tipX - size * Math.cos(angle - Math.PI/6), tipY - size * Math.sin(angle - Math.PI/6),
            tipX - size * Math.cos(angle + Math.PI/6), tipY - size * Math.sin(angle + Math.PI/6)
        });
        tip.setFill(Color.BLACK);

        DropShadow glowArrow = new DropShadow();
        glowArrow.setColor(Color.YELLOW);
        glowArrow.setRadius(15);
        glowArrow.setSpread(0.6);

        Group arrowGroup = new Group(line, tip);

        arrowGroup.setOnMouseEntered(e -> {
            int startingSquareId = -1;
        for(Map.Entry<Integer, double[]> entry : boardCoordinates.entrySet()) {
            if(entry.getValue() == start) {
                startingSquareId = entry.getKey();
                break;
            }
        }
            if(gameManager.getStepsRemaining() > 0 && gameManager.getCurrentPlayer().getCurrentPosition() == startingSquareId) {
                arrowGroup.setEffect(glowArrow);
                arrowGroup.setCursor(Cursor.HAND);
            }
        });

        arrowGroup.setOnMouseExited(e -> {
            arrowGroup.setEffect(null);
            arrowGroup.setCursor(Cursor.DEFAULT);
        });

        arrowGroup.setOnMouseClicked(e -> {

            Player currentPlayer = gameManager.getCurrentPlayer();

            //Identify arrows square
            int startingSquareId = -1;
            for(Map.Entry<Integer, double[]> entry : boardCoordinates.entrySet()) {
                if(entry.getValue() == start) {
                    startingSquareId = entry.getKey();
                    break;
                }
            }
            //make sure arrow cant be clicked without need
            if(gameManager.getStepsRemaining() == 0 || currentPlayer.getCurrentPosition() != startingSquareId){
                return;
            }
            // Update player position
            int currentIndex = gameManager.getCurrentPlayerIndex();

            //Manual first move
            currentPlayer.setPosition(targetNodeId);

            //reduce steps remaining
            gameManager.useOneStep();

            //Check if walk can be automatic
            List<Integer> remainingPath = new ArrayList<>();
            remainingPath.add(targetNodeId);
            remainingPath.addAll(gameManager.continuePath(currentPlayer));

            //Animate step
            playerPath(currentIndex, remainingPath);

            //continue turn
            System.out.println("Choice made. " + gameManager.getStepsRemaining() + " steps remaining");
        });
        boardContainer.getChildren().add(arrowGroup);
    }
    /** 
     * 4. Board Cordinates Mapping
     * Hardcoded X/Y mapping for all logical nodes on grid
     */
    private Map<Integer, double[]> getCoordinates() {
        Map<Integer, double[]> coords = new HashMap<>();

        //Main Path
        for(int i= 0; i<= 6; i++) {
            coords.put(i, new double[]{20 + ((6 - i) * stepX), 470});
        }

        for(int i= 7; i<= 13; i++) {
            coords.put(i, new double[]{20, 420 -((i - 7) * 50)});
        }
        //used so that map stays in certain corner and doesnt get decentred
        coords.put(14, new double[]{20, 70});

        for(int i= 15; i<= 22; i++) {
            coords.put(i, new double[]{20 + ((i - 14) * stepX), 70});
        }

        for(int i= 23; i<= 31; i++) {
            coords.put(i, new double[]{520, 70 + ((i - 23) * 50)});
        }

        coords.put(32, new double[]{20 + (8 * stepX), 470});
        coords.put(33, new double[]{20 + (7 * stepX), 470});

        //set columns so branches can be matched to main route
        double col1 = 20 + (stepX * 1);
        double col2 = 20 + (stepX * 2);
        double col3 = 20 + (stepX * 3);
        double col4 = 20 + (stepX * 4);
        double col5 = 20 + (stepX * 5);
        double col7 = 20 + (stepX * 7);

        //Branchs 1
        coords.put(34, new double[]{col3, 420});
        coords.put(35, new double[]{col3, 370});
        coords.put(36, new double[]{col3, 320});
        coords.put(37, new double[]{col3, 270});
        coords.put(38, new double[]{col3, 220});
        coords.put(39, new double[]{col2, 220});
        coords.put(40, new double[]{col1, 220});

        //Branch 2
        for(int i= 41; i<= 47; i++) {
            coords.put(i, new double[]{col7, 120 + ((i - 41) * 50)});
        }

        //Branch 3
        coords.put(48, new double[]{col4, 220});
        coords.put(49, new double[]{col5, 220});
        coords.put(50, new double[]{col5, 170});
        coords.put(51, new double[]{col5, 120});
        
        return coords;
    }

    /** 
     * 5. Player movement and animation
     * Handles marker creation, ovlap prevention and gliding logic
     */
    public void initialisePlayerMarker(List<Player> players) {
        for(int i = 0; i < players.size(); i++) {
            Player p = players.get(i);

            //Create players marker with players color choice
            Circle marker = new Circle(8, Color.web(p.getColorString()));
            marker.setStroke(Color.BLACK);
            marker.setStrokeWidth(2);

            playerMarkers.put(i, marker);
            boardContainer.getChildren().add(marker);

            //starting position
            updatePlayerPosition(i, p.getCurrentPosition());
        }
    }

    public void updatePlayerPosition(int playerIndex, int nodeId) {
        if(!boardCoordinates.containsKey(nodeId) || !playerMarkers.containsKey(playerIndex)) return;

        //get player coordinates
        double[] coords = boardCoordinates.get(nodeId);
        Circle marker = playerMarkers.get(playerIndex);

        //Stop overlap by offsetting player markers to 4 corners of the square
        double overLapX = 0;
        double overLapY = 0;

        //Player position on board square
        switch (playerIndex) {
            case 0 -> {overLapX = -10; overLapY = -10;} //Top left
            case 1 -> {overLapX = 10; overLapY = -10;} //Top right
            case 2 -> {overLapX = -10; overLapY = 10;} //Bottom-Left
            case 3 -> {overLapX = 10; overLapY = 10;} //Bottom-Right
        }

        //Apply coordinates
        double targetX = coords[0] + overLapX;
        double targetY = coords[1] + overLapY;

        marker.toFront();

        //Gliding animation
        Timeline glide = new Timeline(
            new KeyFrame(Duration.millis(500), 
            new KeyValue(marker.layoutXProperty(), targetX), 
            new KeyValue(marker.layoutYProperty(), targetY)
        )
        );

        glide.play();

    }

    /**
     * 6. Dice UI and Roll Animation
     * Generates the interactive dice button, handles roll animation and triggers paths
     */

    private void buildDiceButton() {

        //Create button
        Image diceImg = new Image(getClass().getResource("/images/Dice.png").toExternalForm());
        ImageView diceView = new ImageView(diceImg);

        diceView.setFitWidth(60);
        diceView.setFitHeight(60);
        diceView.setPreserveRatio(true);

        diceBtn = new Button();
        diceBtn.setGraphic(diceView);

        diceBtn.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10;");
        diceBtn.setPrefSize(80, 80);

        //Coordinates
        diceBtn.setLayoutX(258.8);
        diceBtn.setLayoutY(320);

        //Create rolling text
        diceRollText = new Text();
        diceRollText.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        diceRollText.setFill(GlobalUI.darkMode ? Color.WHITE : Color.web("#100302"));
        diceRollText.setLayoutX(258.8);
        diceRollText.setLayoutY(300);
        diceRollText.setVisible(false);

        //click
        diceBtn.setOnAction(e -> {

            if(!gameManager.isGameActive()){
            System.out.print("The game is over!");
            diceBtn.setDisable(true);
            return;
        }
            diceBtn.setDisable(true);

            //call gamemanager method
            int roll = gameManager.rollDie();

            diceRollText.setText(String.valueOf(roll));
            diceRollText.setVisible(true);
            diceRollText.toFront();

            //Animation
            ScaleTransition pop = new ScaleTransition(Duration.millis(500), diceRollText);
            pop.setFromX(0.5); pop.setFromY(0.5);
            pop.setToX(2.5); pop.setToY(2.5);
            pop.setAutoReverse(true);
            pop.setCycleCount(2);

            //audio
            GameFX gfx = new GameFX();
            gfx.playDiceAudio();

            pop.setOnFinished(ev -> {
                diceRollText.setVisible(false);
                
                //Get current player
                Player currentPlayer = gameManager.getPlayers().get(gameManager.getCurrentPlayerIndex());
                //Generate path
                List<Integer> movementPath = gameManager.recordPath(currentPlayer, roll);
                //Trigger animation
                playerPath(gameManager.getCurrentPlayerIndex(), movementPath);
            });
            pop.play();
        });
        boardContainer.getChildren().addAll(diceBtn, diceRollText);
    }

    /** 
     * 7. Player HUD and Theme Management
     * Renders live player statistics, elimination states anddark/light mode styles.
     */
    
    public VBox drawPlayerStats(){
        this.playerStats.getChildren().clear();

        //setup container
        this.playerStats.setStyle("-fx-background-color: transparent;");
        this.playerStats.setSpacing(20);
        this.playerStats.setPadding(new Insets(30,20,20,20));

        for (Player p : gameManager.getPlayers()) {
            int playerIndex = gameManager.getPlayers().indexOf(p);
            Circle marker = playerMarkers.get(playerIndex);
            if(marker != null) marker.setVisible(!p.isEliminated());

            VBox card = new VBox(8);
            card.setPrefWidth(getPlayerCardWidth());
            //Check if player is alive      
            if (p.isEliminated()) {
                String eliminatedCardColour = GlobalUI.darkMode ? "#555555" : "#dddddd";
                String eliminatedBorderColour = GlobalUI.darkMode ? "#AAAAAA" : "#888888";
                String eliminatedNameColour = GlobalUI.darkMode ? "#E0E0E0" : "#555555";
                 card.setStyle(
                "-fx-background-color: " + eliminatedCardColour + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: " + eliminatedBorderColour + ";" +
                "-fx-border-width: 3;" +
                "-fx-padding: 15;" +
                "-fx-opacity: 0.6;"
            );
            

            Label nameLbl = new Label("💀" + p.getName());
            nameLbl.setStyle("-fx-font-size: " + getPlayerNameFontSize() + "px; -fx-font-weight: bold; -fx-text-fill:" + eliminatedNameColour + "; -fx-strikethrough: true;");

            Label statusLbl = new Label("BANKRUPT");
            statusLbl.setStyle("-fx-font-size: " + getPlayerInfoFontSize() + "px; -fx-font-weight: bold; -fx-text-fill: red;");
            card.getChildren().addAll(nameLbl, statusLbl);   
        }else {
            //Current player border
            boolean isCurrent = (gameManager.getPlayers().indexOf(p) == gameManager.getCurrentPlayerIndex());
            String borderWeight = isCurrent ? "6" : "3";
            String effect = isCurrent ? "-fx-effect: dropshadow(three-pass-box, #FFD700, 15, 0.5, 0, 0);" : "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 4);";
            String cardBackground = GlobalUI.darkMode ? "#3A3A3A" : "#FFFDF7";
            String infoTextColour = GlobalUI.darkMode ? "#F2F2F2" : "#333333";
            
            //Check if player is alive
            //Card style
            card.setStyle(
                "-fx-background-color: " + cardBackground + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: " + p.getColorString() + ";" +
                "-fx-border-width: " + borderWeight + ";" +
                "-fx-padding: 15;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 10, 0, 0, 4);" +
                effect
            );

            Label nameLbl = new Label(p.getName());
            nameLbl.setStyle("-fx-font-size: " + getPlayerNameFontSize() + "px; -fx-font-weight: bold; -fx-text-fill: " + p.getColorString() + ";");

            Label moneyLbl = new Label("💵 Money £" + p.getMoney());
            moneyLbl.setStyle("-fx-font-size: " + getPlayerInfoFontSize() + "px; -fx-font-weight: bold; -fx-text-fill: " + infoTextColour + ";");

            Label sustainableLbl = new Label("🌿 Sustainability: " + p.getSustainabilityScore());
            sustainableLbl.setStyle("-fx-font-size: " + getPlayerInfoFontSize() + "px; -fx-font-weight: bold; -fx-text-fill: " + infoTextColour + ";");

            card.getChildren().addAll(nameLbl, moneyLbl, sustainableLbl);
        }
         
        this.playerStats.getChildren().add(card);

        }
        return this.playerStats;
    }

    private double getPlayerCardWidth() {
        return GlobalUI.fontEnlarged ? 255 : 220;
    }

    private int getPlayerNameFontSize() {
        return GlobalUI.fontEnlarged ? 24 : 20;
    }

    private int getPlayerInfoFontSize() {
        return GlobalUI.fontEnlarged ? 18 : 15;
    }

    public void applyBoardTheme() {
        for (Map.Entry<Integer, StackPane> entry : visualSquares.entrySet()) {
            StackPane square = entry.getValue();
            if (!square.getChildren().isEmpty() && square.getChildren().get(0) instanceof Rectangle rectangle) {
                rectangle.setFill(squareTypeColor(squareTypes.get(entry.getKey())));
            }

            for (int i = 1; i < square.getChildren().size(); i++) {
                if (square.getChildren().get(i) instanceof Text text) {
                    text.setFill(GlobalUI.darkMode ? Color.WHITE : Color.BLACK);
                }
            }
        }

        if (diceBtn != null) {
            String diceBackground = GlobalUI.darkMode ? "#4A4A4A" : "white";
            String diceBorder = GlobalUI.darkMode ? "#DDDDDD" : "black";
            diceBtn.setStyle(
                "-fx-background-color: " + diceBackground + ";" +
                "-fx-border-color: " + diceBorder + ";" +
                "-fx-border-width: 3;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 16px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
            );
        }

        if (diceRollText != null) {
            diceRollText.setFill(GlobalUI.darkMode ? Color.WHITE : Color.web("#100302"));
        }

        if (gameManager != null && gameManager.getPlayers() != null && !gameManager.getPlayers().isEmpty()) {
            drawPlayerStats();
        }
    }



    //Path that player marker follows for gliding animation
    public void playerPath(int playerIndex, List<Integer> path) {
        if(path == null || path.isEmpty()) return;

        //First square
        int nextSquare = path.remove(0);

        //Get next square coordinates
        double[] coords = boardCoordinates.get(nextSquare);
        Circle marker = playerMarkers.get(playerIndex);

        //Keep grid logic
        double overLapX = (playerIndex == 0 || playerIndex == 2) ? -10 : 10;
        double overLapY = (playerIndex == 0 || playerIndex == 1) ? -10 : 10;

        double targetX = coords[0] + overLapX;
        double targetY = coords[1] + overLapY;

        marker.toFront();

        //Glide for this step
         Timeline glide = new Timeline(
            new KeyFrame(Duration.millis(300), 
            new KeyValue(marker.layoutXProperty(), targetX), 
            new KeyValue(marker.layoutYProperty(), targetY)

            
        )
        );

        //check if any more steps needed
        glide.setOnFinished( e -> {
            if(!path.isEmpty()) {
               playerPath(playerIndex, path); 
            }else {
                if(gameManager.getStepsRemaining() > 0) {
                    System.out.println("Waiting for crossroad choice...");
                    gameManager.displayMessage("Crossroad! Click an arrow to choose");

                }else {
                    System.out.println("All moves completed");
                    if(diceBtn != null) {
                        diceBtn.setDisable(false);
                    }
                    javafx.application.Platform.runLater(() -> {
                        gameManager.handlingLanding();
                    });
                }
            }
        });

        glide.play();
    }

}
