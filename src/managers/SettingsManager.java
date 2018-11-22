package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsManager {
	public static SettingsManager SettingsManager= new SettingsManager();
	
	boolean devEnv = System.getenv("eclipse42")!=null;
	
	Map<String, Object> settings;
	List<W> delegates;
	public SettingsManager() {
		settings = new HashMap<String, Object>();
		delegates = new ArrayList<W>();
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
		updateAll();
	}
	public void toggle(String key) {
		set(key,!getBoolean(key));
	}
	
	public void addCallback(W callback) {
		delegates.add(callback);
	}
	public void updateAll() {
		for(W w : delegates) {
			w.callback();
		}
	}
}
