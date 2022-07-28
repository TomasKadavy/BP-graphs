package cz.muni.csirt.kypo.events.trainings;

import lombok.Data;

@Data
public class TrainingRunEnded extends Event {
    private String start_time;
    private String end_time;
}
