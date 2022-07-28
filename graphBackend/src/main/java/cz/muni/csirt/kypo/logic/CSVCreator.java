package cz.muni.csirt.kypo.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.csirt.kypo.events.trainings.Event;
import cz.muni.csirt.kypo.logs.training.Command;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Class for creating csv that is then consumed by MP4PY
 */
public final class CSVCreator {
    // Your ip address is needed to connect to database in another docker
    public static final String IP_ADDRESS = "host.docker.internal";

    public static final String CSV_FILE = "target/inputCSV.csv";

    // To make aggregated nodes, write in this specific file the names of that commands.
    // Each one on the separate line.
    public static final String AGGREGATIONS_FILE = "target/aggregations.txt";


    /**
     * From AGGREGATIONS_FILE loads regexes to be used for creating nodes from commands with params
     * @return a list of regexes
     */
    private static List<String> loadAggregations() {
        List<String> aggregations = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AGGREGATIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                aggregations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aggregations;
    }

    /**
     * Create a new loader with correct configuration
     * @return ElasticSearchLoaderImpl that is configured
     */
    private static ElasticSearchLoaderImpl createLoaderImpl() {
   		ObjectMapper mapper = new ObjectMapper();
		ClientConfiguration clientConfiguration =
				ClientConfiguration.builder()
						.connectedTo(IP_ADDRESS + ":" + "9200")
						.build();
		RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
		return new ElasticSearchLoaderImpl(client, mapper);
    }

    /**
     * Writes csv file for reinforced graph page in frontend
     */
    public static void createsCSV() {
        ElasticSearchLoaderImpl impl = createLoaderImpl();
        createCSVHeader("target/inputCSV.csv");
        createCSVHeader("target/events.csv");
        createCSVHeader("target/bash.csv");
        createCSVHeader("target/msf.csv");

        List<Event> events = impl.loadEvents();
        writeEvents(events, "target/inputCSV.csv");
        writeEvents(events, "target/events.csv");
        writeEvents(events, "target/bash.csv");
        writeEvents(events, "target/msf.csv");

        List<Command> bash = impl.loadBashCommands();
        writeCommands(bash, "target/inputCSV.csv", loadAggregations());
        writeCommands(bash, "target/bash.csv", loadAggregations());

        List<Command> msf = impl.loadMsfCommands();
        writeCommands(msf, "target/inputCSV.csv", loadAggregations());
        writeCommands(msf, "target/msf.csv", loadAggregations());

    }


    /**
     * Creates a new csv file (id.csv) for one specific user selected with id
     * @param id Integer to select a sandbox
     */
    public static void createSpecificPlayerCSV(Integer id) {
        ElasticSearchLoaderImpl impl = createLoaderImpl();
        List<Command> commands = impl.loadCommandsFromOneUser(id);
        List<Event> events = impl.loadEventsFromOneUser(id);
        if (commands == null) {
            return;
        }
        String filePath = "target/" + id.toString() + ".csv";
        createCSVHeader(filePath);
        writeEvents(events, filePath);
        writeCommands(commands, filePath, loadAggregations());
    }

    /**
     * Creates a new csv file (level-number.csv) for one specific level selected with level param
     * @param level to specify which level to choose
     */
    public static void createSpecificLevelCSV(Integer level) {
        ElasticSearchLoaderImpl impl = createLoaderImpl();
        List<Event> events = impl.loadEventsFromOneLevel(level);
        String filePath = "target/level-" + level.toString() + ".csv";
        createCSVHeader(filePath);
        writeEvents(events, filePath);
        try {
            List<Command> commands = writeCommandsByTime(level, impl);
            writeCommands(commands, filePath, loadAggregations());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns a list of commands to be added to a csv file for specific level
     * It is done this way because command does not have a level field
     * @param level Integer to say which level to select commands
     * @param impl ElasticSearchLoaderImpl
     * @return a list of commands
     * @throws ParseException when parsing fails for date
     */
    private static List<Command> writeCommandsByTime(Integer level, ElasticSearchLoaderImpl impl) throws ParseException {
        List<Command> result = new LinkedList<>();
        List<String> players = InfoLoader.loadAllPlayersIds();
        Map<String, List<String>> timesByPlayers = new HashMap<>();
        for (String player : players) {
            timesByPlayers.put(player, startEndTimes(player, level));
        }
        for (Map.Entry<String, List<String>> entry : timesByPlayers.entrySet()) {
            if (entry.getValue().get(0) == null || entry.getValue().get(1) == null) {
                continue;
            }
            var startFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            var endFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date start = startFormat.parse(entry.getValue().get(0));
            Date end = endFormat.parse(entry.getValue().get(1));
            List<Command> playerCommands = impl.loadCommandsFromOneUser(Integer.parseInt(entry.getKey()));
            List<Command> playerLevelCommands = new LinkedList<>();
            for (Command playerCommand : playerCommands) {
                var currentStartFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                var currentEndFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date currentStart = currentStartFormat.parse(playerCommand.getTimestamp_str());
                Date currentEnd = currentEndFormat.parse(playerCommand.getTimestamp_str());
                if (currentStart.after(start) && currentEnd.before(end)) {
                    playerLevelCommands.add(playerCommand);
                }
            }
            result.addAll(playerLevelCommands);
        }
        return result;
    }

    /**
     * For a specific player and level return their start and end time
     * @param player to select
     * @param level to select
     * @return a list of start and end time
     */
    private static List<String> startEndTimes(String player, Integer level) {
        String startTime = null;
        String endTime = null;
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(",")[3].equals(player) &&
                        line.split(",")[4].startsWith("LevelStarted") &&
                        line.split(",")[6].equals(level.toString())) {
                    startTime = line.split(",")[1];
                }
                if (line.split(",")[3].equals(player) &&
                        line.split(",")[4].startsWith("LevelCompleted") &&
                        line.split(",")[6].equals(level.toString())) {
                    endTime = line.split(",")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LinkedList<>((Arrays.asList(startTime, endTime)));
    }

    /**
     * Creates a correct csv header
     * @param filePath String with path to new file
     */
    public static void createCSVHeader(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("hostname,timestamp_str,ip,sandbox_id,cmd_name,cmd_arguments,level,wd,cmd_type,username");
            writer.write(System.lineSeparator());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes all commands from a list to the csv file
     * @param commands list of commands to write
     * @param filePath String with a path to file
     * @param regexes list of regexes to apply to discovery process
     */
    public static void writeCommands(List<Command> commands, String filePath, List<String> regexes) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (Command command : commands) {
                if (command == null) {
                    continue;
                }

                if (command.getSandbox_id().equals("3")) {
                    continue;
                }
                // only user`s commands (not server`s and game`s)
                if (!command.getHostname().equals("attacker")) {
                    continue;
                }
                // disable new line character for Linux and comma for csv file
                command.setCmd(command.getCmd().replace("\\n", "(newline)"));
                command.setCmd(command.getCmd().replace(",", "(comma)"));
                // if Command does not have an argument
                String commandName = command.getCmd().split(" ", 2)[0];
                String argument = command.getCmd().split(" ", 2).length == 2 ? command.getCmd().split(" ", 2)[1] : "NONE";
                // if user specified regexes to create special commands nodes
                for (String regex : regexes) {
                    if (command.getCmd().startsWith(regex)) {
                        commandName = regex;
                        argument = command.getCmd().substring(regex.length()).length() != 0 ? command.getCmd().substring(regex.length()) : "NONE";
                        break;
                    }
                }
                // to not have super long names for nodes
                if (commandName.length() > 60) {
                    commandName = commandName.substring(0, 57) + "...";
                }
                if (command.getTimestamp_str().equals("NONE") || command.getSandbox_id().isEmpty()) {
                    continue;
                }
                String lineToAppend =
                        command.getHostname() + "," +
                        command.getTimestamp_str() + "," +
                        command.getIp() + "," +
                        command.getSandbox_id() + "," +
                        commandName + "," +
                        argument + "," +
                        "-" + "," +
                        command.getWd() + "," +
                        command.getCmd_type() + "," +
                        command.getUsername();

                if (lineToAppend.split(",").length != 10) {
                    continue;
                }
                writer.append(lineToAppend);
                writer.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes all commands from a list to a csv file
     * @param events list of events to write to csv file
     * @param filePath String with a path to file
     */
    public static void writeEvents(List<Event> events, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (Event event : events) {
                if (event == null) {
                    continue;
                }
                if (event.getClass().getName().split("\\.")[6].equals("TrainingRunResumed")) {
                    continue;
                }

                if (event.getSandbox_id().equals("3")) {
                    continue;
                }
                // Change the trainingRunStarted event timestamp so it is correctly first all the time
                if (event.getClass().getName().equals("cz.muni.csirt.kypo.events.trainings.TrainingRunStarted")) {
                    event.getSyslog().setTimestamp("2000-01-0T00:00:01.999Z");
                }

                // Change the time of level completed to one millisecond prior so it is correctly first before level started
                // Change the time of correct flag submitted event to one millisecond prior so it is correctly first before level completed
                if (event.getClass().getName().equals("cz.muni.csirt.kypo.events.trainings.LevelCompleted") ||
                        event.getClass().getName().equals("cz.muni.csirt.kypo.events.trainings.CorrectFlagSubmitted")) {
                    String time = event.getSyslog().getTimestamp();
                    int milliseconds = Integer.parseInt(time.split("\\.")[1].substring(0, 3));
                    int oneLess;
                    if (event.getClass().getName().equals("cz.muni.csirt.kypo.events.trainings.LevelCompleted")) {
                        oneLess = milliseconds - 1;
                    } else {
                        oneLess = milliseconds - 2;
                    }

                    if (oneLess < 10 && oneLess >= 0) {
                        event.getSyslog().setTimestamp(time.split("\\.")[0] + ".00" + oneLess + "Z");
                    } else if (oneLess < 100 && oneLess >= 0) {
                        event.getSyslog().setTimestamp(time.split("\\.")[0] + ".0" + oneLess + "Z");
                    } else if (oneLess < 1000 && oneLess >= 0) {
                        event.getSyslog().setTimestamp(time.split("\\.")[0] + "." + oneLess + "Z");
                    }
                }

                // Append to name more info to make more structured graphs
                String nameAppender = "";
                String eventType = event.getClass().getName().split("\\.")[6];
                switch (eventType) {
                    case "LevelStarted":
                    case "SolutionDisplayed":
                    case "WrongFlagSubmitted":
                    case "CorrectFlagSubmitted":
                    case "LevelCompleted":
                        nameAppender = " | " + "level " + event.getLevel();
                        break;
                    case "HintTaken":
                        nameAppender = " | " + "level " + event.getLevel() + " id " + event.getNameAppender();

                }
                String nodeName = eventType + nameAppender;
                // to not have super long names for nodes
                if (nodeName.length() > 60) {
                    nodeName = nodeName.substring(0, 57) + "...";
                }

                String lineToAppend =
                        "attacker1" + "," +
                        event.getSyslog().getTimestamp() + "," +
                        event.getHost() + "," +
                        event.getSandbox_id() + "," +
                        nodeName + "," +
                        event.getSpecialContent() + "," +
                        event.getLevel() + "," +
                        event.getPlayerLogin() + "," +
                        "event" + "," +
                        event.getUser_ref_id();
                if (lineToAppend.split(",").length != 10) {
                    continue;
                }

                writer.append(lineToAppend);
                writer.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
