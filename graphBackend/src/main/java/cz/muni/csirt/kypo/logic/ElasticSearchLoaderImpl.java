package cz.muni.csirt.kypo.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.csirt.kypo.events.trainings.Event;
import cz.muni.csirt.kypo.logs.training.Command;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads info from elastic
 */
public class ElasticSearchLoaderImpl implements ElasticSearchLoader{

    public static final String INDEX_COMMANDS = "kypo.logs*";
    public static final String INDEX_EVENTS = "kypo.cz.muni.csirt.kypo.events*";
    public static final int MAX_SIZE = 10000;
    public static final String TIMESTAMP = "timestamp_str";

    private final RestHighLevelClient client;
    private final ObjectMapper mapper;

    public ElasticSearchLoaderImpl(RestHighLevelClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public List<Command> loadCommands() {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.sort(TIMESTAMP).size(MAX_SIZE);
        SearchRequest request = new SearchRequest(INDEX_COMMANDS);
        request.source(builder);
        try {
            return Arrays.stream(client.search(request, RequestOptions.DEFAULT).getHits().getHits()).map(hit -> {
                try {
                    return mapper.readValue(hit.getSourceAsString(), Command.class);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Failed to read this json:");
                    System.out.println(hit.getSourceAsString());
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Failed to access ElasticSearch");
            return null;
        }
    }

    @Override
    public List<Event> loadEvents() {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.sort(TIMESTAMP).size(MAX_SIZE);
        SearchRequest request = new SearchRequest(INDEX_EVENTS);
        request.source(builder);
        try {
            return Arrays.stream(client.search(request, RequestOptions.DEFAULT).getHits().getHits()).map(hit -> {
                try {
                    return mapper.readValue(hit.getSourceAsString(), Event.class);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Failed to read this json:");
                    System.out.println(hit.getSourceAsString());
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Failed to access ElasticSearch");
            return null;
        }
    }

    @Override
    public List<Command> loadCommandsFromOneUser(Integer id) {
        return loadCommands().stream().filter(
                (command -> command != null && command.getSandbox_id().equals(id.toString()))
        ).collect(Collectors.toList());
    }

    @Override
    public List<Event> loadEventsFromOneUser(Integer id) {
        return loadEvents().stream().filter(
                (event -> event != null && event.getSandbox_id().equals(id.toString()))
        ).collect(Collectors.toList());
    }

    @Override
    public List<Event> loadEventsFromOneLevel(Integer level) {
        return loadEvents().stream().filter(
                (event -> event != null && event.getLevel().equals(level.toString()))
        ).collect(Collectors.toList());
    }

    @Override
    public List<Command> loadBashCommands() {
        return loadCommands().stream().filter(
                (command -> command != null &&
                        command.getCmd_type().equals("bash-command") &&
                        !command.getTimestamp_str().equals("NONE"))
        ).collect(Collectors.toList());
    }

    @Override
    public List<Command> loadMsfCommands() {
        return loadCommands().stream().filter(
                (command -> command != null && command.getCmd_type().equals("msf-command"))
        ).collect(Collectors.toList());
    }
}
