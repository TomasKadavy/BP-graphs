package cz.muni.csirt.kypo.logs.training;

import lombok.Data;

@Data
public class Command {
    private String cmd_type;
    private String ip;
    private String pool_id;
    private String cmd;
    private String username;
    private String host;
    private String timestamp_str;
    private String sandbox_id;
    private String hostname;
    private String wd;
}
