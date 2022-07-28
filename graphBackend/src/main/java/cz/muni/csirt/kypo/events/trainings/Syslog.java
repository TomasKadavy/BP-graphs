package cz.muni.csirt.kypo.events.trainings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = { "fromhost-ip", "timegenerated" })
public class Syslog {
    private String from_ip;
    private String severity;
    private String procid;
    private String programname;
    private String host;
    private String sysLogHost;
    private String type;
    private String facility;
    @JsonProperty("@timestamp")
    private String timestamp;
    @JsonProperty("@version")
    private Long version;
}
