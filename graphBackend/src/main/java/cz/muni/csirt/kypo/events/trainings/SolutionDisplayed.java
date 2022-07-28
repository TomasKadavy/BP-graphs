package cz.muni.csirt.kypo.events.trainings;

import lombok.Data;

@Data
public class SolutionDisplayed extends Event {
    private String penalty_points;

    @Override
    public String getSpecialContent() {
        return this.getPenalty_points();
    }
}
