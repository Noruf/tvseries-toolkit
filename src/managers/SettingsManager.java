package managers;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
	public static SettingsManager SettingsManager= new SettingsManager();
	
	boolean devEnv = System.getenv("eclipse42")!=null;
	
	Map<String, Object> settings;
	public SettingsManager() {
		settings = new HashMap<String, Object>();
		set("autosave",true);
		set("music",!devEnv);
		set("editButtons",true);
	}
	
	public Boolean getBoolean(String key) {
		return (Boolean) settings.get(key);
	}

	public int getInteger(String key) {
		return (Integer) settings.get(key);
	}
	
	public void set(String key,Object value) {
		settings.put(key, value);
	}
	public void toggle(String key) {
		set(key,!getBoolean(key));
	}
}
