package cz.muni.csirt.kypo.events.trainings;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(LevelStarted.class), @JsonSubTypes.Type(CorrectFlagSubmitted.class), @JsonSubTypes.Type(HintTaken.class),
    @JsonSubTypes.Type(LevelCompleted.class), @JsonSubTypes.Type(SolutionDisplayed.class), @JsonSubTypes.Type(TrainingRunEnded.class),
    @JsonSubTypes.Type(TrainingRunStarted.class), @JsonSubTypes.Type(WrongFlagSubmitted.class)})
public class Event {
    private String sandbox_id;
    private String training_instance_id;
    private String training_definition_id;
    private String training_run_id;
    private String total_assessment_level_score;
    private String actual_score_in_level;
    private String total_game_level_score;
    private String level;
    private String pool_id;
    private String timestamp;
    private String type;
    private String game_time;
    private Syslog syslog;
    private String fullNameWithoutTitles;
    private String fullName;
    private String playerLogin;
    private String user_ref_id;
    private String levelTitle;
    private String port;
    private String host;

    /**
     * For each event returns a special information to be displayed on hover at final graph
     * @return String with information to be displayed
     */
    public String getSpecialContent() {
        return "None";
    }

    /**
     * For event name to append more info if needed
     * @return String with information to appended
     */
    public String getNameAppender() {
        return "None";
    }
}
