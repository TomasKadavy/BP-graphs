package cz.muni.csirt.kypo.rest;

import cz.muni.csirt.kypo.logic.InfoLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/info", produces = MediaType.TEXT_PLAIN_VALUE)
public class InfoController {

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/players")
    public String getAllPlayers() {
        return InfoLoader.loadAllPlayersIds().toString();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/levels")
    public String getAllLevels() {
        return InfoLoader.loadAllLevels().toString();
    }

}
