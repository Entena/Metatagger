/**
 * 
 */
package gui;

import gui.plugin.LearningPlugin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListModel;
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
        pane.setPreferredSize(new Dimension(128, 64));
        
        DefaultListModel<String> pluginNames = new DefaultListModel<String>();
        for(LearningPlugin plugin : loadedPlugins){
            pluginNames.addElement(plugin.getName());
        }
        
        JList<String> pluginList = new JList<String>(pluginNames);
        pluginList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pluginList.addMouseListener(new Listener());
        
        
        pane.add(pluginList);
        pack();
    }
    
    public LearningPlugin getSelected(){
        return currentSelected;
    }
    
    class Listener implements MouseListener {
        
        @Override
        public void mouseReleased(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void mousePressed(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void mouseExited(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void mouseEntered(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void mouseClicked(MouseEvent e) {
            JList<String> list = (JList<String>) e.getSource();
            if(e.getClickCount() == 2){
                int index = list.locationToIndex(e.getPoint());
                ListModel<String> dlm = list.getModel();
                String item = dlm.getElementAt(index);
                list.ensureIndexIsVisible(index);
                
                for(LearningPlugin plugin : loadedPlugins){
                    if(plugin.getName().equals(item)){
                        currentSelected = plugin;
                    }
                }
            }
        }
    }

}
