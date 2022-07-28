package cz.muni.csirt.kypo.events.trainings;

import lombok.Data;

@Data
public class WrongFlagSubmitted extends Event {
    private String flag_content;
    private String count;

    @Override
    public String getSpecialContent() {
        return this.getFlag_content();
    }
}
