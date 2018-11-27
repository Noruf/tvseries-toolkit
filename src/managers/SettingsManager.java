package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsManager {
	public static SettingsManager SettingsManager = new SettingsManager();
	
	Map<String, Object> settings;
	List<W> delegates;
	ImportExportManager dataManager;
	public SettingsManager() {
		dataManager = ImportExportManager.ImportExportManager;
		settings = dataManager.ImportSettings();
		delegates = new ArrayList<W>();
	}
	
	public Boolean getBoolean(String key) {
		Object val = settings.get(key);
		return Boolean.valueOf(val!=null?val.toString():"true");
	}

	public int getInteger(String key) {
		return (Integer) settings.get(key);
	}
	public void setVal(String key,Object value) {
		settings.put(key, value);
	}
	
	public void set(String key,Object value) {
		settings.put(key, value);
		updateAll();
		dataManager.ExportData(settings);
	}
	public void toggle(String key) {
		set(key,!getBoolean(key));
	}
	
	public void addCallback(W callback) {
		delegates.add(callback);
		callback.callback();
	}
	public void updateAll() {
		for(W w : delegates) {
			w.callback();
		}
	}
}
