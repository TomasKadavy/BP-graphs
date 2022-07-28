package cz.muni.csirt.kypo.events.trainings;

import lombok.Data;

@Data
public class CorrectFlagSubmitted extends Event {
    private String host;
    private String flag_content;

    @Override
    public String getSpecialContent() {
        return this.getFlag_content();
    }
}
