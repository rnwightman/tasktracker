package tasktracker.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Gui extends JPanel {
    private static String currentTaskString = "Current Task: ";
    
    protected JLabel currentTaskLabel;
    protected JTextField currentTaskTextPane;
    
    public Gui() {
        super();
        
        // add components
        this.currentTaskLabel = new JLabel(currentTaskString);
        this.currentTaskTextPane = new JTextField();
        this.currentTaskTextPane.setColumns(20);
        
        add(currentTaskLabel);
        add(currentTaskTextPane);
    }
    
    public static void createAndShowGui() {
        // Create and set up the window
        JFrame frame = new JFrame("Task Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add contents to the window
        frame.add(new Gui());
        
        // Display the window
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGui();
            }
        });
    }
}