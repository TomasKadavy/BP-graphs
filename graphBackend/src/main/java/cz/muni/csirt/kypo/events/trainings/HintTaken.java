package cz.muni.csirt.kypo.events.trainings;

import lombok.Data;

@Data
public class HintTaken extends Event {
    private String hint_id;
    private String hint_penalty_points;
    private String hint_title;

    @Override
    public String getSpecialContent() {
        return this.getHint_title();
    }

    @Override
    public String getNameAppender() {
        return this.getHint_id();
    }
}
