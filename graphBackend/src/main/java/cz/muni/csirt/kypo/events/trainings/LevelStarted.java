package cz.muni.csirt.kypo.events.trainings;

import lombok.Data;

@Data
public class LevelStarted extends Event{
    private String level_type;
    private String max_score;
    private String level_title;

    @Override
    public String getSpecialContent() {
        return this.getLevel_title();
    }

}
