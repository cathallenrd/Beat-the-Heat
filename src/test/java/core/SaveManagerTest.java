//import static org.junit.jupiter.api.Assertions.*;

// import java.io.File;
// import java.util.ArrayList;
// import java.util.List;

// import org.junit.jupiter.api.Test;

// import objects.Player;
// import core.SaveManager;

// public class SaveManagerTest {

//     @Test
//     void saveGameCreatesNewSave() {
//         List<Player> players = new ArrayList<>();
//         players.add(new Player("Aoife"));

//         String path = "test_saves/test_save.json";

//         boolean result = SaveManager.saveGame(path, players, 1.5f, 5, 0, -1, -1);

//         assertTrue(result);
//         assertTrue(new File(path).exists());
//     }

//     @Test
//     void loadGameUsesSaveData() {
//         List<Player> players = new ArrayList<>();
//         players.add(new Player("Aoife"));

//         String path = "test_saves/load_test.json";

//         SaveManager.saveGame(path, players, 2.0f, 10, 0, 3, 5);

//         SaveManager.GameStateData data = SaveManager.loadGame(path);

//         assertNotNull(data);
//         assertEquals(2.0f, data.globalTemp);
//         assertEquals(10, data.turnCounter);
//         assertEquals(1, data.players.size());
//         assertEquals("Aoife", data.players.get(0).getName());
//     }

//     @Test
//     void deleteSaveRemovesSaveFile() {
//         List<Player> players = new ArrayList<>();
//         players.add(new Player("Aoife"));

//         String path = "test_saves/delete_test.json";

//         SaveManager.saveGame(path, players, 1, 1, 0, -1, -1);

//         boolean deleted = SaveManager.deleteSave(path);

//         assertTrue(deleted);
//         assertFalse(new File(path).exists());
//     }

//     @Test
//     void deleteSaveReturnsFalseIfFileMissing() {
//         boolean deleted = SaveManager.deleteSave("test_saves/does_not_exist.json");

//         assertFalse(deleted);
//     }
// }