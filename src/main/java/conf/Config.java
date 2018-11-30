package conf;

import entity.GroupManager;
import entity.ListSkylineGroup;
import entity.SetSkylineGroup;
import entity.TreeSkylineGroup;

public class Config {
    public static final GroupManager groupManager = new TreeSkylineGroup.Manager();
}
