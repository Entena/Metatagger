/**
 * 
 */
package gui;

import gui.plugin.LearningPlugin;

import java.awt.Container;
import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * @author pulsence
 *
 */
public class LoadedPluginDialog extends JDialog  {
    
    ArrayList<LearningPlugin> loadedPlugins;
    LearningPlugin currentSelected;

    /**
     * 
     */
    public LoadedPluginDialog( ArrayList<LearningPlugin> loadedPlugins,
                               LearningPlugin currentSelected) {
        super(new Frame(), "Loaded PLugins", false); 
        this.loadedPlugins = loadedPlugins;
        this.currentSelected = currentSelected;
        
        Container pane = this.getContentPane();
        
        DefaultListModel<String> pluginNames = new DefaultListModel<String>();
        for(LearningPlugin plugin : loadedPlugins){
            pluginNames.addElement(plugin.getName());
        }
        
        JList<String> pluginList = new JList<String>(pluginNames);
        pluginList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pane.add(pluginList);
    }
    
    public LearningPlugin getSelected(){
        return null;
    }

}
