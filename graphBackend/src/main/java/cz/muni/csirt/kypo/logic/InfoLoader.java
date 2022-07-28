package cz.muni.csirt.kypo.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for loading additional information about the graphs
 */
public final class InfoLoader {
    public static final String FILE_CSV = "target/events.csv";

    /**
     * Loads all players Ids to a list
     * @return list of all players Ids
     */
    public static List<String> loadAllPlayersIds() {
        List<String> allPlayersIds = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String sandboxId = line.split(",")[3];
                if (sandboxId.equals("sandbox_id")) {
                    continue;
                }
                if (!allPlayersIds.contains(sandboxId)) {
                    allPlayersIds.add(sandboxId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allPlayersIds;
    }

    /**
     * Returns count of all players
     * @return Integer of count of all players
     */
    public static Integer countOfPlayers() {
        return loadAllPlayersIds().size();
    }

    /**
     * Returns a list of all levels
     * @return List of String with levels
     */
    public static List<String> loadAllLevels() {
        List<String> allLevels = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String levelID = line.split(",")[6];
                if (levelID.equals("level")) {
                    continue;
                }
                if (levelID.equals("-")) {
                    continue;
                }
                if (!allLevels.contains(levelID)) {
                    allLevels.add(levelID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allLevels;
    }
}
