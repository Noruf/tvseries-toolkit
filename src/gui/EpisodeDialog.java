package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import managers.ImportExportManager;
import models.Link;
import models.TvSeries;

class EpisodeDialog extends JDialog
        implements PropertyChangeListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3396957182889860591L;

	TvSeries series;
	
	
	JToggleButton[] Seasons;
	JToggleButton[] Episodes;
	
	JOptionPane optionPane;
	
	final public int OK = 0;
	final public int CANCEL = 1;
	
	public int state = 2;
	public int season = -1;
	public int episode = -1;
	
	JPanel episodePanel;
	
	JPanel mainPanel = new JPanel();



    /**
     * Creates the reusable dialog.
     */
    public EpisodeDialog(Frame aFrame, TvSeries series) {
        super(aFrame, true);
        
        this.series = series;
		this.setTitle("Choose Episode");
		
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.setLayout(new GridBagLayout());
		
		
		
		mainPanel.add(new JLabel("Season"),gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		JPanel seasonPanel = new JPanel();
		BoxLayout bl = new BoxLayout(seasonPanel,BoxLayout.LINE_AXIS);
		
		ButtonGroup group = new ButtonGroup(); 
		Seasons = new JToggleButton[series.getNumberOfSeasons()];
		for(int i=0;i<Seasons.length;i++) {
			Seasons[i]=new JToggleButton((i+1)+"",false);
			seasonPanel.add(Seasons[i]);
			group.add(Seasons[i]);
			Seasons[i].setPreferredSize(new Dimension(62, 43));
			Seasons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					showEpisodeButtons();
				}
			});
		}
		mainPanel.add(seasonPanel,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPanel.add(new JLabel("Episode"),gbc);
		episodePanel = new JPanel();
		gbc.gridx = 1;
		mainPanel.add(episodePanel,gbc);
		
		episodePanel.setLayout(new BoxLayout(episodePanel,BoxLayout.Y_AXIS));
		
		final JComponent[] inputs = new JComponent[] { mainPanel};

		String[] options = { "OK",  "Cancel" };
		
        //Create the JOptionPane.
        optionPane = new JOptionPane(inputs,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //Register an event handler that puts the text into the option pane.
        //textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        pack();
    }

    protected void showEpisodeButtons() {
		int index = 0;
    	for(int i=0;i<Seasons.length;i++) {
			if(Seasons[i].isSelected()) {
				index = i;
				break;
			}
		}
    	episodePanel.removeAll();
    	Episodes = new JToggleButton[series.Seasons[index]];
    	ButtonGroup group = new ButtonGroup(); 
    	JPanel panel = null;
    	int w =series.Seasons[index]<100?62:76;
    	for(int i=0;i<series.Seasons[index];i++) {
    		if(i%10==0) {
    			if(panel!=null) episodePanel.add(panel);
    			panel = new JPanel();
			}
    		Episodes[i]=new JToggleButton((i+1)+"",false);
    		panel.add(Episodes[i]);
			group.add(Episodes[i]);
		
			Episodes[i].setPreferredSize(new Dimension(w, 43));
		}
    	episodePanel.add(panel);
    	pack();
	}

    
    /**
     * This method reacts to state changes in the option pane.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop)
                || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
        	
            Object result = optionPane.getValue();
            System.out.println(result);
            if (result == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            //******MAIN LOGIC******
            
            if (result.toString().matches("OK")) {
            	
            	season = -1;
            	for(int i=0;i<Seasons.length;i++) {
        			if(Seasons[i].isSelected()) {
        				season = i;
        				break;
        			}
        		}
            	if(season<0) {
            		JOptionPane.showMessageDialog(null, "Season not chosen!", "Warning!",
    						JOptionPane.WARNING_MESSAGE);
            		return;
            	}
            	episode = -1;
            	for(int i=0;i<Episodes.length;i++) {
        			if(Episodes[i].isSelected()) {
        				episode = i+1;
        				break;
        			}
        		}
            	if(episode<0) {
            		JOptionPane.showMessageDialog(null, "Episode not chosen!", "Warning!",
    						JOptionPane.WARNING_MESSAGE);
            		return;
            	}
    			state=OK;
    			exit();

    		} else {
    			System.out.println("User canceled / closed the dialog, result = " + result);
    			state=CANCEL;
    			exit();
    		}
        }
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void exit() {
        dispose();
    }


}