package cz.muni.csirt.kypo.logic;

import cz.muni.csirt.kypo.events.trainings.Event;
import cz.muni.csirt.kypo.logs.training.Command;

import java.util.List;

/**
 * For loading info from elastic
 */
public interface ElasticSearchLoader {

    /**
     * Loads all commands from all players from all levels
     * @return list of found commands
     */
    List<Command> loadCommands();

    /**
     * Loads all events from all players from all levels
     * @return list of events
     */
    List<Event> loadEvents();

    /**
     * Loads all commands from one specific user specified by id parameter
     * @param id integer to specify which user to select
     * @return list of commands from this user
     */
    List<Command> loadCommandsFromOneUser(Integer id);

    /**
     * Loads all events from one specific user specified by id parameter
     * @param id integer to specify which user to select
     * @return list of events from this user
     */
    List<Event> loadEventsFromOneUser(Integer id);

    /**
     * Loads all events from one specific level by level parameter
     * @param level Integer to say which level to choose
     * @return list of events from one level
     */
    List<Event> loadEventsFromOneLevel(Integer level);

    /**
     * Loads all bash commands from all levels
     * @return list of commands, bash commands
     */
    List<Command> loadBashCommands();

    /**
     * Loads all msf commands from all levels
     * @return list of commands, msf commands
     */
    List<Command> loadMsfCommands();
}
