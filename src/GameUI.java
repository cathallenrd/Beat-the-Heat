package ui;

import java.util.Scanner;

import utils.GameConstants;

/** 
 * Handles all console based input and output
 */

/** 
 * 1. Class Definition and initialisation
 */

public class GameUI {
    private Scanner scanner;

    public GameUI() {
        this.scanner = new Scanner(System.in);
    }

    public int promptForPlayerCount()
    {
        while (true)
        {
            System.out.print("enter num of players(2-4): ");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input.trim());
                if (choice >= GameConstants.MIN_PLAYER_COUNT && choice <= GameConstants.MAX_PLAYER_COUNT) {
                    return choice;
                }
            } catch (NumberFormatException e) {

            }

            System.out.print("invalid input\n");
        }
    }

    public int promptPlayerChoice(String description, String[] options, boolean allowMenu) {
        System.out.println("\n" + description);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        
        while (true) {
            String promptSuffix = allowMenu ? "or 'm' for menu" : "";  //Validation for menu
            System.out.print("Select an option (1-" + options.length + ")" + promptSuffix + ": ");
            String input = scanner.nextLine().trim().toLowerCase();

            //-99 used as a specific flag to tell Gamemanager to interupt flow and route back to main menu
            if (allowMenu && input.equals("m")) return -99;

            try{
                int choice = Integer.parseInt(input.trim());

                if(choice >= 1 && choice <= options.length) {
                    return choice - 1;
                }
            }
            catch (NumberFormatException e){
                //catches the error
                //The loop will automatically restart and print invalid input
            }
            String errorSuffix = allowMenu ? " or 'm'" : "";
            System.out.println("Invalid input. Please enter a number between 1 and " + options.length + errorSuffix);
        }
    }

    public int promptForDirection(int squareID) {
        System.out.println("\n--- CROSSROADS at Square " + squareID + " ---");
        System.out.println("1. Continue Main Path");
        System.out.println("2. Take the Branch (Riskier?)");
        
        while (true) {
            System.out.print("Choose direction (1 or 2): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("m")) return -99;

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 2) return choice -1;
            }
            catch (NumberFormatException e) {

            }
            
            System.out.println("Invalid input. Please enter 1, 2 or 'm'.");
        }
    }

    /** 
     * 2. Message and Rules display
     * Simple void methods for printing game states and istruction to the console
     */

    public void displayRules(){
        System.out.println("    Rules   \nFirst player to reach 100 sustainability wins!\n\nAnyone who runs out of money loses!\nIf global temperature reaches +2.5 degrees, everyone loses!");
    }

     public void displayMessage(String message) {
        System.out.println(message);
    }

    public void setGameTemperature(float temp){

    }

    /** 
     * 3. Input Prompts and validation
     * Contains all while-loops that request input, parse strings to integer and catch errors
     */
    
    public String promptPlayerName(int playerNumber){
        System.out.println("Enter name for Player " + playerNumber + ": ");
        String playerName = scanner.nextLine().trim();
        return playerName;
    }


    public String promptForString(String message) {
        System.out.print(message + ": ");
        return scanner.nextLine().trim();
    }
}
