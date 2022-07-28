package cz.muni.csirt.kypo.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ControllerTest {
    @Autowired
    private InfoController infoController;

    @Autowired
    private GraphController graphController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
        assertThat(infoController).isNotNull();
        assertThat(graphController).isNotNull();
    }

    @Test
    public void returnListOfPlayers() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/players/all", String.class))
                .isNotNull();
    }

    @Test
    public void returnSVG() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/graph/normalGraph", String.class))
                .isNotNull();
    }

}