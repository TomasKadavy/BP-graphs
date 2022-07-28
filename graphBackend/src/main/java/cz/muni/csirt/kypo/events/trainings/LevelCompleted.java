package cz.muni.csirt.kypo.events.trainings;

import lombok.Data;

@Data
public class LevelCompleted extends Event {
    private String level_type;

    @Override
    public String getSpecialContent() {
        return this.getLevel_type();
    }
}
