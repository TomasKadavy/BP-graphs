package cz.muni.csirt.kypo.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.csirt.kypo.events.trainings.Event;
import cz.muni.csirt.kypo.logic.ElasticSearchLoader;
import cz.muni.csirt.kypo.logic.ElasticSearchLoaderImpl;
import cz.muni.csirt.kypo.logs.training.Command;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

final class ElasticTest {

    private static ElasticSearchLoader elasticSearchLoader;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void initTest() {
        objectMapper = new ObjectMapper();
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder()
                        .connectedTo("localhost" + ":" + "9200")
                        .build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        elasticSearchLoader = new ElasticSearchLoaderImpl(client, objectMapper);
    }

    // For this test it is required to run elastic on localhost:9200
    @Test
    public void testLocalElastic() {
        try {
            ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200").build();
            RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
            var searchRequest = new SearchRequest("kypo*");
            var searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            List<Event> events = Arrays.stream(searchResponse.getHits().getHits()).map(hit -> {
                try {
                    return objectMapper.readValue(hit.getSourceAsString(), Event.class);
                } catch (JsonProcessingException e) {
                    System.out.println("Cannot process line");
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            assertThat(events).isNotEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEventsLoader() {
        List<Event> trainingEvents = elasticSearchLoader.loadEvents();
        assertThat(trainingEvents).isNotEmpty();
    }

    @Test
    public void testCommandsLoader() {
        List<Command> commands = elasticSearchLoader.loadCommands();
        assertThat(commands).isNotEmpty();
    }

    // Only works for this specific data set with sandbox_ids containing 3
    @Test
    public void testCommandsFromOneUser() {
        List<Command> commands = elasticSearchLoader.loadCommandsFromOneUser(3);
        assertThat(commands).isNotEmpty();
    }

    // Only works for this specific data set with sandbox_ids containing 3
    @Test
    public void testEventsFromOneUser() {
        List<Event> events = elasticSearchLoader.loadEventsFromOneUser(3);
        assertThat(events).isNotEmpty();
    }
}