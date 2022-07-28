package cz.muni.csirt.kypo.rest;

import cz.muni.csirt.kypo.logic.CSVCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphBackendApplication.class, args);
		CSVCreator.createsCSV();
	}

}
