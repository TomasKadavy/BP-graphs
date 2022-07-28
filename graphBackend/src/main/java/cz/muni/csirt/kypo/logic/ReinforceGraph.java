package cz.muni.csirt.kypo.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Reinforce the graph for more information
 */
public final class ReinforceGraph {
    /**
     * Append Line to file
     * @param line to be appended
     * @param svgFileOutput file to write the line
     */
    private static void addLine(String line, String svgFileOutput) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(svgFileOutput, true));
            writer.append(line);
            writer.append(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reinforces the graph with additional info
     * @param csvFile with additional data file
     * @param svgFileOutput write the more info here
     */
    public static void createReinforcedGraph(String csvFile, String normalSvg, String svgFileOutput) {
        emptyFile(svgFileOutput);
        try (BufferedReader br = new BufferedReader(new FileReader(normalSvg))) {
            String line;
            String title = null;
            boolean isNode = true;
            while ((line = br.readLine()) != null) {
                addLine(line, svgFileOutput);

                if (line.startsWith("<g id=\"node")) {
                    isNode = true;
                }
                if (line.startsWith("<g id=\"edge")) {
                    isNode = false;
                }

                if (line.startsWith("<title>") && !isNode) {
                    title = line.substring(7, line.length() - 8);
                }

                if (line.startsWith("<text")) {
                    if (isNode) { // Reinforcing node
                        // Add ids of users going via this node
                        String nameInLine = line.split("<")[1].split(">")[1];
                        String[] commandNames = nameInLine.split(" ");
                        StringBuilder commandName = new StringBuilder(commandNames[0]);
                        for (int i = 1; i < commandNames.length - 1; i++) {
                            commandName.append(" ").append(commandNames[i]);
                        }
                        List<String> commandSandboxesIds = commandSandboxes(commandName.toString(), csvFile);
                        addLine("<title>" + commandSandboxesIds + "</title>", svgFileOutput);
                        // Add theirs commands arguments
                        addLine("<title>" + argumentSandboxes(commandSandboxesIds, commandName.toString(), csvFile) + "</title>", svgFileOutput);
                    } else {
                        addLine("<title>" + reinforceEdge(title, csvFile).toString() + "</title>", svgFileOutput);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reinforces SCG with arguments for nodes
     * @param commandSandboxesIds list of all ids of all players
     * @param commandName current node = current command
     * @param csvFile file to load additional data
     * @return a String with each player and his arguments used
     */
    private static String argumentSandboxes(List<String> commandSandboxesIds, String commandName, String csvFile) {
        List<List<String>> allCommandsIds = new LinkedList<>();
        for (String commandSandboxesId : commandSandboxesIds) {
            List<String> allCommandsId = new LinkedList<>();
            allCommandsId.add(commandSandboxesId);
            allCommandsIds.add(allCommandsId);
        }
        StringBuilder result = new StringBuilder("[");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(",")[4].equals(commandName)) {
                    String argument = line.split(",")[5];
                    String sandboxId = line.split(",")[3];
                    for (List<String> allCommandsId : allCommandsIds) {
                        if (allCommandsId.get(0).equals(sandboxId)) {
                            allCommandsId.add(svgEncoding(argument));
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (List<String> allCommandsId : allCommandsIds) {
            for (String argument : allCommandsId) {
                result.append(argument).append(",");
            }
            result.append("|");
        }
        return result.append("]").toString();
    }

    /**
     * Removes special characters for SVG
     * @param argument line to have special characters deleted
     * @return a line without special characters
     */
    private static String svgEncoding(String argument) {
        argument = argument.replace('&', 'a');
        argument = argument.replace('\'', 'p');
        argument = argument.replace('\"', 'q');
        argument = argument.replace('<', 'l');
        argument = argument.replace('>', 'g');
        return argument;
    }

    /**
     * Returns only a number of a node as a string (deletes &#45; before and after)
     * @param title of the node
     * @return numerical title
     */
    private static String getNodeTitle(String title) {
        if (title.startsWith("&#45;")) {
            if (title.endsWith("&#45")) {
                return title.substring(5, title.length() - 4);
            }
            return title.substring(5);
        }
        if (title.endsWith("&#45")) {
            return title.substring(0, title.length() - 4);
        }
        return title;
    }

    /**
     * For each node create a list of all Players going through it
     * @param text Node name
     * @return List of all players that used this command
     */
    private static List<String> commandSandboxes(String text, String csvFile) {
        List<String> result = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(",")[4].equals(text)) {
                    String sandbox_id = line.split(",")[3];
                    if (!result.contains(sandbox_id)) {
                        result.add(sandbox_id);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Reinforces one edge with all players that went via this edge
     * @param title of this edge
     * @param fileOutput filepath to csv file to read additional information
     */
    private static List<String> reinforceEdge(String title, String fileOutput) {
        if (title == null) {
            return new LinkedList<>();
        }
        List<String> result = new LinkedList<>();
        String startingNode = getNodeTitle(getEdgeNode(title, 0));
        String endingNode = getNodeTitle(getEdgeNode(title, 1));
        try (BufferedReader br = new BufferedReader(new FileReader(fileOutput))) {
            String line;
            List<String> candidates = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                String id = line.split(",")[3];
                String command = line.split(",")[4];
                if (command.startsWith(startingNode)) {
                    if (!candidates.contains(id)) {
                        candidates.add(id);
                        continue;
                    }
                }

                if (command.startsWith(endingNode)) {
                    if (candidates.contains(id)) {
                        if (!result.contains(id)) {
                            result.add(id);
                            candidates.remove(id);
                        }
                    }
                }

                if (candidates.contains(id)) {
                    candidates.remove(id);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * From two lists of players in string create a new String with an intersection
     * @param startingNodeUsers string of list of node users
     * @param endingNodeUsers string of list of node users
     * @return String of intersection
     */
    private static String usersIntersection(String startingNodeUsers, String endingNodeUsers) {
        if (startingNodeUsers == null || endingNodeUsers == null) {
            return "";
        }
        StringBuilder result = new StringBuilder("<title>[");
        String[] starting = startingNodeUsers.substring(1, startingNodeUsers.length() - 1).split(",");
        String[] ending = endingNodeUsers.substring(1, endingNodeUsers.length() - 1).split(",");

        for (String startUser : starting) {
            for (String endUser : ending) {
                if (startUser.trim().equals(endUser.trim())) {
                    result.append(startUser.trim()).append(",");
                    break;
                }
            }
        }
        return result.append("]</title>").toString();
    }

    /**
     * Returns the title of starting node or ending node
     * @param title the title of edge
     * @param number 0 - starting node, 1 - ending node
     * @return the title of starting node (0) or ending node (1)
     */
    private static String getEdgeNode(String title, int number) {
        String[] titleSplit = title.split(";&gt;");
        return titleSplit[number];
    }

    /**
     * Creates an empty file
     */
    private static void emptyFile(String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
