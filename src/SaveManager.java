package core;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import objects.Player;

/** 
 * SaveManager handles the data persistence layer for the application.
 * Uses a JSON engine to read and write GameStateData to local files
 */
public class SaveManager {

/** 
 * 1. Save File Bluprint
 * Defines the exact structure and variables required to reconstruct a saved game
 */
    public static class GameStateData
    {
        public float globalTemp;
        public int turnCounter;
        public int currentPlayerIndex;
        public List<Player> players;
        public int roundReached15;
        public int roundReached20;
    }

/** 
 * 2. State Encoding Engine
 * Manually translates active game variables into a structured JSON for storage
 */

    /** Encodes current game into a stuctured JSON string and writes it to disk 
     */

    public static boolean saveGame(String filePath, List<Player> players, float globalTemp, int turnCounter, int currentPlayerIndex, int round15, int round20)
    {
        //Ensures directory actually exists 
        java.io.File file = new java.io.File(filePath);
        java.io.File parentDir = file.getParentFile();
        if(parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); //Creates folder
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"globalTemp\": ").append(globalTemp).append(",\n");
        json.append("  \"turnCounter\": ").append(turnCounter).append(",\n");
        json.append("  \"currentPlayerIndex\": ").append(currentPlayerIndex).append(",\n");
        json.append(" \"roundReached15\": ").append(round15).append(",\n");
        json.append(" \"roundReached20\": ").append(round20).append(",\n");
        json.append("  \"players\": [\n");

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            json.append("    { ");
            json.append("\"name\": \"").append(p.getName()).append("\", ");
            json.append("\"color\": \"").append(p.getColorString()).append("\", ");
            json.append("\"money\": ").append(p.getMoney()).append(", ");
            json.append("\"sustainabilityScore\": ").append(p.getSustainabilityScore()).append(", ");
            json.append("\"currentPosition\": ").append(p.getCurrentPosition()).append(", ");
            json.append("\"isEliminated\": ").append(p.isEliminated()).append(", ");
            json.append("\"hasLoan\": ").append(p.hasLoan());
            json.append(" }");
            
            if (i < players.size() - 1)
                {
                    json.append(",");
                }
            json.append("\n");
        }
        json.append("  ]\n");
        json.append("}");

        try (FileWriter writer = new FileWriter(filePath))
        {
            writer.write(json.toString());
            System.out.println("saved to " + filePath);
            return true;
        }
        catch (IOException e)
        {
            System.err.println("error saving game " + e.getMessage());
            return false;
        }
    }

/** 
 * 3. Directory Controller
 * Manages local file scanning, listing available saves and permanent deletion.
 */

    //Used for reading and cleaning up multiple files (for loading system)
    public static String[] listSaveFiles(String directoryPath) {
        java.io.File folder = new java.io.File(directoryPath);
        if (!folder.exists()) {
            return new String[0];
        }

        java.io.File[] allFiles = folder.listFiles();
        if(allFiles == null) {
            return new String[0];
        }

        //temporary list
        List<String> saveNames = new ArrayList<>();

        for(int i = 0; i < allFiles.length; i++) {
            java.io.File f = allFiles[i];
            String fileName = f.getName().toLowerCase();

            if(f.isFile() && fileName.endsWith(".json")) {
                saveNames.add(f.getName().replace(".json",""));
            }
        }
        return saveNames.toArray(new String[0]);
    }

    //deletes specific save file (only if found)
    public static boolean deleteSave(String filePath) {
        java.io.File file = new java.io.File(filePath);

        if(file.exists()) {
            return file.delete();
        }
        return false;

    }

/** 
 * 4. State Restoration Logic
 * Parses flat JSON strings and insert them back into active Java game objects.
 */
    /** 
     * Parse's local JSON save file back into GameStateData object.
     */

    public static GameStateData loadGame(String filePath) {
        GameStateData data = new GameStateData();
        data.players = new ArrayList<>();

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            
            content = content.replace("\n", "").replace("\r", "");

            data.globalTemp = Float.parseFloat(extractValue(content, "\"globalTemp\":").trim());
            data.turnCounter = Integer.parseInt(extractValue(content, "\"turnCounter\":").trim());
            data.currentPlayerIndex = Integer.parseInt(extractValue(content, "\"currentPlayerIndex\":").trim());
            data.roundReached15 = Integer.parseInt(extractValue(content, "\"roundReached15\":").trim());
            data.roundReached20 = Integer.parseInt(extractValue(content, "\"roundReached20\":").trim());

            int arrayStart = content.indexOf("[");
            int arrayEnd = content.lastIndexOf("]");

            if (arrayStart != -1 && arrayEnd != -1)
            {
                String playersArray = content.substring(arrayStart + 1, arrayEnd).trim();
                
                if (!playersArray.isEmpty())
                {
                    String[] playerObjects = playersArray.split("\\}\\s*,\\s*\\{");

                    for (String pObj : playerObjects)
                    {
                        pObj = pObj.replace("{", "").replace("}", "");
                        
                        String name = extractValue(pObj, "\"name\":").replace("\"", "").trim();
                        String colorStr = extractValue(pObj, "\"color\":").replace("\"", "").trim();
                        int money = Integer.parseInt(extractValue(pObj, "\"money\":").trim());
                        int sust = Integer.parseInt(extractValue(pObj, "\"sustainabilityScore\":").trim());
                        int pos = Integer.parseInt(extractValue(pObj, "\"currentPosition\":").trim());
                        boolean elim = Boolean.parseBoolean(extractValue(pObj, "\"isEliminated\":").trim());
                        boolean loan = Boolean.parseBoolean(extractValue(pObj, "\"hasLoan\":").trim());

                        data.players.add(new Player(name, colorStr, money, sust, pos, elim, loan));

                        
                    }
                }
            }
            
            return data;

        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }

    /** 
     * Method for JSON parser
     */
    private static String extractValue(String source, String key)
    {
        int start = source.indexOf(key);

        if (start == -1) return "0";

        start += key.length();

        int end = source.indexOf(",", start);
        if (end == -1)
        {
            end = source.indexOf("}", start);
        }
        if (end == -1)
        {
            end = source.length();
        }
        return source.substring(start, end);
    }
}
