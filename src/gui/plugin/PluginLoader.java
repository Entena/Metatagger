package gui.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    
    @SuppressWarnings("unchecked")
    public static ArrayList<LearningPlugin> loadPlugin(String filePath) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{

        ArrayList<LearningPlugin> plugins = new ArrayList<LearningPlugin>();
        
        URLClassLoader loader = new URLClassLoader(new URL[] {new URL(filePath)});
        JarFile jarFile = new JarFile(filePath);
        Enumeration<JarEntry> allEntries = jarFile.entries();
        while (allEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) allEntries.nextElement();
            String name = entry.getName();
            if(name.endsWith(".class")){
                Class clazz = loader.loadClass(name.replace(".class", ""));
                if(clazz.equals(LearningPlugin.class))
                    plugins.add((LearningPlugin) clazz.newInstance());
            }
        }
        
        loader.close();
        jarFile.close();
        
        return plugins;
    }
    
    public static ArrayList<LearningPlugin> loadDefaultPlugins(){
        ArrayList<LearningPlugin> plugins = new ArrayList<LearningPlugin>();
        
        // The first plugin added to this list is the default plugin used
        plugins.add(new BetterRandom());
        plugins.add(new RandomSongPlugin());
        plugins.add(new SaltNPepperPlugin());
        return plugins;
    }
}
