package cz.muni.csirt.kypo.rest;

import cz.muni.csirt.kypo.logic.CSVCreator;
import cz.muni.csirt.kypo.logic.PM4PY;
import cz.muni.csirt.kypo.logic.ReinforceGraph;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping(value = "/graph", produces = "image/svg+xml")
public class GraphController {

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/normalGraph")
    public ResponseEntity<Resource> getSVG() throws IOException {
        PM4PY.createSVG("target/inputCSV.csv", "target/graph.svg");
        return getFile("target/graph.svg");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/events")
    public ResponseEntity<Resource> getEventsGraph() throws IOException {
        PM4PY.createSVG("target/events.csv", "target/events.svg");
        ReinforceGraph.createReinforcedGraph("target/events.csv", "target/events.svg", "target/events+.svg");
        return getFile("target/events+.svg");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/bash")
    public ResponseEntity<Resource> getBashGraph() throws IOException {
        PM4PY.createSVG("target/bash.csv", "target/bash.svg");
        ReinforceGraph.createReinforcedGraph("target/bash.csv", "target/bash.svg", "target/bash+.svg");
        return getFile("target/bash+.svg");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/msf")
    public ResponseEntity<Resource> getMsfGraph() throws IOException {
        PM4PY.createSVG("target/msf.csv", "target/msf.svg");
        ReinforceGraph.createReinforcedGraph("target/msf.csv", "target/msf.svg", "target/msf+.svg");
        return getFile("target/msf+.svg");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/reinforcedGraph")
    public ResponseEntity<Resource> getReinforcedSVG() throws IOException {
        PM4PY.createSVG("target/inputCSV.csv", "target/graph.svg");
        ReinforceGraph.createReinforcedGraph("target/inputCSV.csv", "target/graph.svg", "target/reinforcedGraph.svg");
        return getFile("target/reinforcedGraph.svg");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/playerGraph/{id}")
    public ResponseEntity<Resource> getGraphById(@PathVariable("id") Integer id)  throws IOException {
        File tempFile = new File("target/" + id.toString() + "+.svg");
        if (!tempFile.exists()) {
                CSVCreator.createSpecificPlayerCSV(id);
                PM4PY.createSVG("target/" + id.toString() + ".csv", "target/" + id.toString() + ".svg");
                ReinforceGraph.createReinforcedGraph("target/" + id.toString() + ".csv",
                    "target/" + id.toString() + ".svg",
                    "target/" + id.toString() + "+.svg");
        }
        return getFile("target/" + id.toString() + "+.svg");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/levelGraph/{level}")
    public ResponseEntity<Resource> getGraphByLevel(@PathVariable("level") Integer level)  throws IOException {
        File tempFile = new File("target/level-" + level.toString() + "+.svg");
        if (!tempFile.exists()) {
                CSVCreator.createSpecificLevelCSV(level);
                PM4PY.createSVG("target/level-" + level.toString() + ".csv", "target/level-" + level.toString() + ".svg");
                ReinforceGraph.createReinforcedGraph("target/level-" + level.toString() + ".csv",
                    "target/level-" + level.toString() + ".svg",
                    "target/level-" + level.toString() + "+.svg");
        }
        return getFile("target/level-" + level.toString() + "+.svg");
    }

    private ResponseEntity<Resource> getFile(String inputFile) throws IOException {
        Path path = new File(inputFile).toPath();
        FileSystemResource resource = new FileSystemResource(path);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                .body(resource);
    }
}
