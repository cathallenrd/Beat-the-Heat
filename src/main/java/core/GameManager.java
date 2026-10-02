package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import enums.Difficulty;
import objects.Board;
import objects.BoardSquare;
import objects.NaturalDisaster;
import objects.Player;
import objects.cards.Choice;
import objects.cards.EventCard;
import objects.cards.QuizCard;
import ui.GameFX;
import ui.GameUI;
import utils.GameConstants;

  /** GameManager acts as the central state controller for the application.
     * Manages:
     *  - Turn based cycle
     *  - tracks global variables
     *  - enforces rules via GameRulesEngine
     *  - bridge core logic with UI
     */
    
public class GameManager {
    
/** 
* 1. Global State and Variables
* Tracks the active game session, decks, players and environmental triggers
*/
    
    private boolean isGameActive;
    private int currentPlayerIndex;
    private float globalTemp;
    private int turnCounter;
    private List<Player> players;
    private Board board;
    private GameUI ui;
    private GameFX gameFX;
    private GameRulesEngine rulesEngine;
    private List<EventCard> eventDeck;
    private List<EventCard> eventDiscardPile;
    private List<QuizCard> quizDeck;
    private GameLoader loader;
    private boolean parisAgreementTriggered;
    private List<NaturalDisaster> minorNaturalDisasters;
    private List<NaturalDisaster> majorNaturalDisasters;
    private float tempAtRoundStart;
    private String activeSaveName = null; //name of current active file
    private int roundReached15 = -1;
    private int roundReached20 = -1;
    private int currentStepsRemaining = 0;


/** 
* 2. Initialisation and setup
* Constructor and methods to build the player nad link the GUI to the logic.
*/    

    public GameManager(GameUI ui, GameFX gfx)
    {
        this.ui = ui;
        this.gameFX = gfx;
        this.players = new ArrayList<>();
        this.board = new Board();
        this.rulesEngine = new GameRulesEngine();
        this.globalTemp = 0.0f;
        this.turnCounter = 1;
        this.parisAgreementTriggered = false;

        this.loader = new GameLoader();

        this.eventDeck = loader.loadEventCards();
        Collections.shuffle(this.eventDeck);

        this.eventDiscardPile = new ArrayList<>();

        this.quizDeck = loader.loadQuizCards();
        Collections.shuffle(this.quizDeck);

        this.minorNaturalDisasters = loader.loadMinorNaturalDisasters();
        Collections.shuffle(this.minorNaturalDisasters);

        this.majorNaturalDisasters = loader.loadMajorNaturalDisasters();
        Collections.shuffle(this.majorNaturalDisasters);
    }

    public boolean isDuplicateName(String playerName){
        for(Player p : players){
            if(p.getName().equalsIgnoreCase(playerName)){
                return true;
            }
        }
        return false;
    }

    public void displayMessage(String message) {
        if (ui != null) {
            ui.displayMessage(message);
        }
    }
    public boolean isGameActive() {
        return this.isGameActive;
    }

    //Finds Broke Player
    private Player getPlayerByName(String name) {
        for(Player p : players) {
            if(p.getName().equals(name)) return p;
        }
        return null;
    }

/**
* 3. Core Turn Lifecycle
* The primary gameplay loop: start turns, ending turns and handling landing.
*/

    public void startGame()
    {
        //To make sure any faulty choices cant throw errors
        boolean sessionInitialised = false;

        while(!sessionInitialised) {
        String[] startOptions = {"new game", "load game"};
        int choice = ui.promptPlayerChoice("select option", startOptions, false);

        if (choice == 1)
        {
            // LOAD GAME
            sessionInitialised = loadGame();
        }
        else if(choice == 0)
        {
            // NEW GAME
            players.clear();
            int numPlayers = ui.promptForPlayerCount();
            for (int i = 1; i <= numPlayers; i++)
            {
                String playerName;
                while(true){
                    playerName = ui.promptPlayerName(i);
                    //validation
                    if(playerName.isEmpty()){
                        ui.displayMessage("Name must not be empty! Enter a name:");
                        continue;
                    }
                    //thought should limit length for display later
                    if(playerName.length() > 10){
                        ui.displayMessage("Name must be 10 characters or less!\nChoose a shorter name");
                        continue;
                    }
                    if(isDuplicateName(playerName)){
                        ui.displayMessage("Name must be unique!\nChoose another name");
                        continue;
                    }
                    break;
                }
                players.add(new Player (playerName, "#FFFFFF"));
            }
            this.currentPlayerIndex = 0;
            System.out.println("player count: " + numPlayers); 
            sessionInitialised = true;
        }
        }   
        // NOTE
        // THIS IS GAMEPLAY LOOP AND HOW THE GAME STARTS !!!
        this.isGameActive = true;
        this.tempAtRoundStart = globalTemp; 
        
        Player firstPlayer = players.get(currentPlayerIndex);
        ui.displayMessage("Game start! It is " + firstPlayer.getName() + "'s turn. Roll the dice!");
    }

    //connect GUI players and logic
    public void setPlayersAndStart(List<Player> guiPlayers) {
        this.players = guiPlayers;
        this.currentPlayerIndex = 0;
        this.isGameActive = true;
        this.tempAtRoundStart = this.globalTemp;

        Player firstPlayer = players.get(currentPlayerIndex);
        displayMessage("Game start! It is " + firstPlayer.getName() + "'s turn. Roll the dice!");
    }

    public int rollDie()
    {
        int num = (int)((Math.random() * 6) + 1);
        return num;
    }

    /** 
     * Called at the end of every action adn checks win/loss conditions
     * Forces "Bailout Quiz" if players money reaches 0
     */
    public void endTurn()
    {
        String status = rulesEngine.checkWinLossConditions(players, globalTemp);

        while(status.startsWith("OFFER_LOAN_")) {
            String brokeName = status.replace("OFFER_LOAN_", "");
            Player brokePlayer = getPlayerByName(brokeName);

            if(brokePlayer != null) {
                ui.displayMessage("\n*** BANKRUPTCY WARNING -" + brokeName + " ***");
                ui.displayMessage("You have £0. You are being offered a ONE TIME loan.");

                //Error handling for corrupt files
                if(quizDeck.isEmpty()) {
                    ui.displayMessage("No quizzes left! The bank rejects your loan.");
                    brokePlayer.setEliminated(true);
                }
                else {
                    QuizCard card = quizDeck.remove(0);
                    ui.displayMessage("LOAN EXAM: " + card.getDifficulty() + " Difficulty");

                    boolean success = gameFX.showLoanQuiz(brokePlayer, card);

                    if(success) {
                        brokePlayer.setHasLoan(true);
                        brokePlayer.updateResources(100, 0);
                    }
                    else {
                        brokePlayer.setEliminated(true);
                    }
                    quizDeck.add(card);
                }
            }
            status = rulesEngine.checkWinLossConditions(players, globalTemp);
        }
        if (!status.equals("CONTINUE"))
            {
            if (status.equals("GAME_OVER_TEMP") || status.equals("GAME_OVER_ALL_ELIMINATED") || status.startsWith("PLAYER_WIN")) {
                isGameActive = false;
                displayFinalStatistics();
                return;
            } 
            else if (status.startsWith("PLAYER_ELIMINATED"))
            {
                String loser = status.replace("PLAYER_ELIMINATED_", "");
                ui.displayMessage("eliminated " + loser);
            }
        }

        if(gameFX != null) {
            javafx.application.Platform.runLater(() -> gameFX.refreshUI());
        }
    }
    
/** 
* 4. Game Rules and State Updates
* Modifies core metrics, handles banruptcy checks and triggers disasters
*/

    /** 
     * Adjusts global temperature and trakcs specific round when critical thresholds are surpassed
     */

    public void updateGlobalTemp(float amount)
    {
        this.globalTemp += amount;

        //1.5 degrees hit
        if(this.globalTemp >= GameConstants.DISASTER_THRESHOLD_MINOR) {
            if(roundReached15 == -1) roundReached15 = turnCounter;
        }else {
            //reset if temp drops below 1.5
            roundReached15 = -1;
        }

        //2 degrees hit
        if(this.globalTemp >= GameConstants.DISASTER_THRESHOLD_MAJOR) {
            if(roundReached20 == -1) roundReached20 = turnCounter;
        }else {
            //reset if temp drops below 2
            roundReached20 = -1;
        }

        if(gameFX != null) {
            javafx.application.Platform.runLater(() -> gameFX.refreshUI());
        }

    }

    public boolean validateChoice(Choice c, Player p)
    {
        return true;
    }

     public boolean canAffordAChoice(Player p, List<Choice> choices){
        for(Choice c : choices){
            if(p.getMoney() >= c.getCost()){
                return true;
            }
        }
        return false;
    }

/** 
* 5. Player Rankings and Game Over
* Sorts players based on sustainability and money, and calculate final outcomes.
*/

    public List<Player> rankPlayers(){
        List<Player> rankedPlayers = new ArrayList<>(players);
        int n = rankedPlayers.size();
        //big up bubble sort
        for(int i = 0; i < n - 1; i ++){
            for(int j = 0; j < n - i - 1; j ++){
                Player current = rankedPlayers.get(j);
                Player next = rankedPlayers.get(j + 1);

                if (current.getMoney() < 0 && next.getMoney() >= 0) {
                    rankedPlayers.set(j, next);
                    rankedPlayers.set(j + 1, current);
                } else if (current.getMoney() >= 0 && next.getMoney() < 0) {
                    continue;

                } else {
                    if (current.getSustainabilityScore() < next.getSustainabilityScore()) {
                        rankedPlayers.set(j, next);
                        rankedPlayers.set(j + 1, current);
                    } else if (current.getSustainabilityScore() == next.getSustainabilityScore()) {
                        if (current.getMoney() < next.getMoney()) {
                        rankedPlayers.set(j, next);
                        rankedPlayers.set(j + 1, current);
                        }
                    }
                }
            }
        }
        return rankedPlayers;
    }

    public void displayRanking(){
        List<Player> ranked = rankPlayers();
        ui.displayMessage("Rankings:\n");
        for(int i = 0; i < ranked.size(); i ++){
            Player p = ranked.get(i);
            ui.displayMessage((i+1) + ". " + p.getName() + " Sustainability: " + p.getSustainabilityScore() + " Money: " + p.getMoney() + "\n");
        }
    }

    public void displayFinalStatistics()
    {
        ui.displayMessage("game over");
        ui.displayMessage("final global temp: " + String.format("%.2f", globalTemp) + "°C");
        ui.displayMessage("total rounds played: " + (turnCounter - 1));
        String outcomeMsg = "Game Over";
        if (globalTemp > 2.5f)
        {
            ui.displayMessage("outcome: >2.5 degree everyone dies");
        }
        else
        {
            List<Player> ranked = rankPlayers();
            if (!ranked.isEmpty() && ranked.get(0).getSustainabilityScore() >= 100)
            {
                ui.displayMessage("outcome: sustainability victory");
                ui.displayMessage("winner: " + ranked.get(0).getName());
            }
            else
            {
                ui.displayMessage("outcome: last player standing");
            }
        }

        ui.displayMessage("\n final player standings: ");
        displayRanking();

        if(gameFX != null) {
            List<Player> finalRankings = rankPlayers();
            String finalOutcome = outcomeMsg;
            javafx.application.Platform.runLater(() -> gameFX.showGameOverLeaderboard(finalRankings, finalOutcome));
        }
    }

/** 
* 6. Interactive Square Handlers
* Manages the logic for drawing and answering Quiz Cards and Event Cards
*/

    public Difficulty selectDifficulty(){
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = ui.promptPlayerChoice("Select difficulty: ", options, true);

        if(choice == -99) return null;

        return Difficulty.values()[choice];
    }

    //i deleted the method i wrote that was here because idt it was necessary but i have it chilling on a local copy if we need it for some reason
    public void removeQuizCard(QuizCard qCard){
        this.quizDeck.remove(qCard);
        this.quizDeck.add(qCard);
    }
    public void handleQuizSquare(Player player)
    {
        if (quizDeck.isEmpty()) return;

        //changed this from quizDeck.remove(0) so that it doesn't just remove a random ass difficulty card
        QuizCard card = null;
        while (card == null) {
            Difficulty difficultyChoice = selectDifficulty();

            if(difficultyChoice == null) {
                handlePauseMenu();
                handleQuizSquare(player);
                return;
            }
            
            for(QuizCard quizCard : quizDeck){
                if(quizCard.getDifficulty() == difficultyChoice){
                    card = quizCard;
                    break;
                }
            }

            if(card == null){
                ui.displayMessage("No " + difficultyChoice + " cards available");
                if (quizDeck.isEmpty()) return; 
            }
        }

        quizDeck.remove(card);
        
        ui.displayMessage("difficulty " + card.getDifficulty());

        int choiceIndex = ui.promptPlayerChoice(card.getQuestion(), card.getOptions(), false);
        if (choiceIndex == -99) {
            handlePauseMenu();
            handleQuizSquare(player);
            return;
        }
        if (card.checkAnswer(choiceIndex))
        {
            int reward = card.getRewardAmount();
            ui.displayMessage("correct, reward: " + reward);
            ui.displayMessage(card.getExplanation());
            player.updateResources(reward, 0);
        }
        else
        {
            ui.displayMessage("incorrect -" + card.getPenaltyAmount());
            ui.displayMessage(card.getExplanation());
            player.updateResources(-card.getPenaltyAmount(), 0);
        }

        quizDeck.add(card);
    }

    // FUNCTION USED IN NEW JAVAFX 'GameFX.java'
    public List<QuizCard> getQuizDeck()
    {
        return this.quizDeck;
    }

    public List<EventCard> getEventDeck()
    {
        return this.eventDeck;
    }

/** 
* 7. Save and Load System
* Interfaces with the SaveManager to write/read JSON state data to the directory.
*/

    public void saveGame()
    {
        String saveName = ui.promptForString("Enter a name for your save file (no spaces)");

        if(saveName.isEmpty()) {
            ui.displayMessage("Save cancelled: Name cannot be empty.");
            return;
        }

        //Make sure any illegal file characters are replaced with _
        saveName = saveName.replaceAll("[^a-zA-Z0-9-_]", "_");
        String path = "resources/" + saveName + ".json";

        //Check for existing file for overwriting
        java.io.File file = new java.io.File(path);
        if(file.exists()) {
            String[] options = {"Overwrite", "Cancel"};
            int confirm = ui.promptPlayerChoice("File " + saveName +" already exists, would you like to overwrite it?", options, false);
            if(confirm != 0) {
                ui.displayMessage("Save cancelled");
                return;
            }
        }
        if(SaveManager.saveGame(path, players, globalTemp, turnCounter, currentPlayerIndex, roundReached15, roundReached20)) {
        this.activeSaveName = saveName;
        ui.displayMessage("Progress saved successfully as -" + saveName + "-");
        }else {
        ui.displayMessage("Error: Could not save progress");
        }
    }

    public boolean loadGame()
    {
        String directory = "resources/";
        String[] availableSaves = SaveManager.listSaveFiles(directory);

        if(availableSaves.length == 0) {
            ui.displayMessage("No save files found.");
            return false;
        }

        int choice = ui.promptPlayerChoice("Select a save to load", availableSaves, true);

        if(choice == -99) return false;

        String selectedPath = directory + availableSaves[choice] + ".json";
        SaveManager.GameStateData loadedData = SaveManager.loadGame(selectedPath);

        if (loadedData != null)
        {
            this.globalTemp = loadedData.globalTemp;
            this.turnCounter = loadedData.turnCounter;
            this.currentPlayerIndex = loadedData.currentPlayerIndex;
            this.players = loadedData.players;
            this.roundReached15 = loadedData.roundReached15;
            this.roundReached20 = loadedData.roundReached20;
            this.activeSaveName = availableSaves[choice];
            ui.displayMessage("loaded save file -" + availableSaves[choice] + "-");
            return true;
        }
        else
        {
            ui.displayMessage("failed to load save file");
            return false;
        }
    }

    //handles delete save

    private void handleDeleteSave() {
        String directory = "resources/";
        String[] availableSaves = SaveManager.listSaveFiles(directory);

        if(availableSaves.length == 0) {
            ui.displayMessage("No save files found to delete");
            return;
        }
        int choice = ui.promptPlayerChoice("Select a save to delete (ITS PERMANENT)", availableSaves, true);
        if (choice == -99) return; //exits choice if you choose m 

        // Double checks choice
        String selectedName = availableSaves[choice];
        String[] confirmOptions = {"Delete it", "Don't Delete"};
        int confirm = ui.promptPlayerChoice("You 100% sure???, You will permanently delete " + selectedName, confirmOptions, false);

        if(confirm == 0) {
            String path = directory + selectedName + ".json";
            if(SaveManager.deleteSave(path)) {
                ui.displayMessage("Deleted save file: " + selectedName);

                //clears current active file name (protects quick save)
                if(selectedName.equals(activeSaveName)) {
                    activeSaveName = null;
                }
            } else {
                ui.displayMessage("ERROR, File could not be deleted");
            }
            } else {
                ui.displayMessage("Deletion cancelled");
            }
        }
    
    public void quickSave() {
        if(activeSaveName == null) {
            saveGame();
            return;
        }

        String path = "resources/" + activeSaveName + ".json";
        if(SaveManager.saveGame(path, players, globalTemp, turnCounter, currentPlayerIndex, roundReached15, roundReached20)) {
            ui.displayMessage("Quick Saved to: " + activeSaveName);
        }
    }

    public boolean hasActiveSave() {
        return this.activeSaveName != null;
    }

    public boolean saveGameAs(String saveName) {
        saveName = saveName.replaceAll("[^a-zA-Z0-9-_]", "_");
        String path = "resources/" + saveName + ".json";

        if(SaveManager.saveGame(path, players, globalTemp, turnCounter, currentPlayerIndex, roundReached15, roundReached20)){
        this.activeSaveName = saveName;
        return true;
        }
        return false;
    }

    public void autoSave() {
        String name = (activeSaveName != null) ? activeSaveName : "autosave";
        String path = "resources/" + name + ".json";

        if(SaveManager.saveGame(path, players, globalTemp, turnCounter, currentPlayerIndex, roundReached15, roundReached20)) {
            ui.displayMessage("...[Autosaved to " +name + "]...");
        }
    }

    public String[] getSaveFiles() {
        return SaveManager.listSaveFiles("resources/");
    }

    public boolean loadGameFiles(String saveName) {

        String path = "resources/" + saveName + ".json";
        SaveManager.GameStateData loadedData = SaveManager.loadGame(path);

        if (loadedData != null)
        {
            this.globalTemp = loadedData.globalTemp;
            this.turnCounter = loadedData.turnCounter;
            this.currentPlayerIndex = loadedData.currentPlayerIndex;
            this.players = loadedData.players;
            this.roundReached15 = loadedData.roundReached15;
            this.roundReached20 = loadedData.roundReached20;
            this.activeSaveName = saveName;
            this.isGameActive = true;
            this.tempAtRoundStart = this.globalTemp;
            return true;
        }
        else
        {
            return false;
        }
    }

    public int getTurnCounter() {
        return this.turnCounter;
    } 

    //Pause menu
    private void handlePauseMenu() {
        String[] options = {"Quick Save","Save Game","Delete Save","Go Back to Game", "Quit Game"};
        int choice = ui.promptPlayerChoice("--- PAUSE MENU ---", options, false);

        switch (choice) {
            case 0:
                quickSave();
                break;
            case 1:
                saveGame();
                break;
            case 2:
                handleDeleteSave();
                break;
            case 4:
                ui.displayMessage("Quit Game... Goodbye!!!");
                System.exit(0);
                break;
            default:
                break;
        }
    }

/** 
* 8. Movement and Pathing Logic
* Calculates the node by node path the player marker takes, halting at crossroads.
*/

    //For gliding animation, records path player takes 
    public List<Integer> recordPath(Player player, int roll) {
        //Store roll
        this.currentStepsRemaining = roll; 
        return continuePath(player);
    }

    public List<Integer> continuePath(Player player) {
        List<Integer> path = new ArrayList<>();

         while(currentStepsRemaining > 0) {
            BoardSquare currentSquare = board.getSquare(player.getCurrentPosition());
            int[] nextOptions = currentSquare.getNextSquares();

            if(nextOptions.length > 1) {
                break;
            }

            int nextId = nextOptions[0];
            player.setPosition(nextId);
            path.add(nextId);
            currentStepsRemaining--;
        }
        return path;
    }

    //Player informtion for BoardUI
    public List<Player> getPlayers() {
        return this.players;
    }
    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void useOneStep() {
        this.currentStepsRemaining--;
    }

    public int getStepsRemaining() {
        return this.currentStepsRemaining;
    }

    /** 
     * Advances the turn pointer. If pointer loops back to 0, a full round has complete.
     * Triggers disaster checks, UI round updates and automated background check every 5 rounds
     */

    public void startNextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        
        if (currentPlayerIndex == 0) {
            turnCounter++;

            checkAndTriggerDisaster();

            ui.displayMessage("\n Round: " + (turnCounter - 1));
            ui.displayMessage("\n Global Temp: " + String.format("%.2f", globalTemp) + "°C");
            tempAtRoundStart = globalTemp; 
            
            if(GameFX.liveRoundLabel != null) {
                GameFX.liveRoundLabel.setText("Round " + (turnCounter - 1));
            }
            
            if((turnCounter - 1) % 5 == 0)
            {
                autoSave();
                displayRanking();
            }

            if (!parisAgreementTriggered && globalTemp >= GameConstants.PARIS_AGREEMENT_TRIGGER)
            {
                ui.displayMessage("paris agreement alert");
                parisAgreementTriggered = true;
            }
                    
            
        }
        Player next = players.get(currentPlayerIndex);
        if(next.isEliminated()) {
            startNextTurn();
            return;
        }
        ui.displayMessage("It is now " + next.getName() + "'s turn");
 
    }

    //Triggers Disaster
    private void checkAndTriggerDisaster() {
        String tier = rulesEngine.shouldTriggerDisaster(globalTemp, turnCounter-1, roundReached15, roundReached20);

            if(!tier.equals("NONE")) {
                NaturalDisaster currentDisaster = null;

                //Pick disaster and cycle to bottom
                if(tier.equals("MAJOR") && !majorNaturalDisasters.isEmpty()) {
                    currentDisaster = majorNaturalDisasters.remove(0);
                    majorNaturalDisasters.add(currentDisaster);
                }else if (tier.equals("MINOR") && !minorNaturalDisasters.isEmpty()) {
                    currentDisaster = minorNaturalDisasters.remove(0);
                    minorNaturalDisasters.add(currentDisaster);
                }

                if(currentDisaster != null) {
                    int displayAmount = Math.abs(currentDisaster.getMoneyChange());
                    gameFX.handleNaturalDisasters(currentDisaster, displayAmount);
                    currentDisaster.triggerEffect((players));
                    endTurn();
                    javafx.application.Platform.runLater(() -> gameFX.refreshUI());
                }
            }
    }

    //Called by BoardUi when marker stops sliding
    public void handlingLanding() {
        Player current = players.get(currentPlayerIndex);

        ui.displayMessage(current.getName() + "landed on square " + current.getCurrentPosition());

        //trigger square action
        BoardSquare finalSquare = board.getSquare((current.getCurrentPosition()));
        finalSquare.triggerAction(current, this.gameFX);
        

        endTurn();
        if(isGameActive) {
            startNextTurn();
        }else {
            javafx.application.Platform.runLater(() -> gameFX.refreshUI());
        }
    }
    public float getGlobalTemp(){
        return globalTemp;
    }

}

