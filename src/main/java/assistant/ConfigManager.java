package assistant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

public class ConfigManager {
	public static final String FILENAME = "assistant.yml";
	private Yaml yaml = new Yaml();
	private static Map<String, Object> configStore = new LinkedHashMap<String, Object>();
	boolean hasUnsavedChanges;
	
	public ConfigManager() {
		initDefaults(); 
		load();
	}
 
	public void initDefaults() {
	}
	
	public void set(String key, Object val) {
		configStore.put(key, val);
		hasUnsavedChanges = true;
	}
	
	public Object get(String key) {
		return configStore.get(key);
	}
	
	public void load() {
	    FileInputStream fin = null;
		try {
			File file = new File(FILENAME);
			if(!file.exists()) {
				file.createNewFile();
				hasUnsavedChanges = true;
				save();
			} else {
    			fin = new FileInputStream(file);
    			LinkedHashMap map = (LinkedHashMap)yaml.load(fin);
    			if(map != null) {
    				configStore.putAll(map);
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    if(fin != null)
                try { fin.close(); } catch (IOException e) {}
		}
	}
	
	public boolean save() {
		if(!hasUnsavedChanges) return true;
		
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(new File(FILENAME));
			//stream.write(yaml.dumpAsMap(configStore).getBytes());
			for(Entry<String, Object> entry : configStore.entrySet()) {
			    Object val = entry.getValue() != null ? entry.getValue() : "";
				fout.write((entry.getKey() + ": " + val + "\r\n").getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(fout != null)
				try { fout.close(); } catch (IOException e) {}
		}
		hasUnsavedChanges = false;
		return true;
	}
                                                                                                                  
}
