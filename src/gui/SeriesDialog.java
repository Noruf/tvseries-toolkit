package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import managers.ImportExportManager;
import managers.WebManager;
import models.TvSeries;

class SeriesDialog extends JDialog
        implements ActionListener,
        PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1876781612105027706L;
	TvSeries series;
	boolean edit;
	JTextField FriendlyName;
	JTextField Name;
	JTextField seasons;
	JTextField img;
	JTextField music;
	JTextField url;
	
	JOptionPane optionPane;
	
	final public int OK = 0;
	final public int DELETE = 1;
	final public int CANCEL = 2;
	
	public int state = 2;
	
	ImportExportManager dataManager;

	final private int textFieldWidth = 20;

    /**
     * Creates the reusable dialog.
     */
    public SeriesDialog(Frame aFrame, TvSeries series, boolean edit) {
        super(aFrame, (edit?"Edit":"Add")+" series",true);
        this.dataManager = ImportExportManager.ImportExportManager;
        this.series = series;
        this.edit = edit;

        GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		JPanel mainPanel = new JPanel(new GridBagLayout());
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
        
		FriendlyName = new JTextField(series.FriendlyName, textFieldWidth);
		FriendlyName.setComponentPopupMenu(new ContextMenu());
		mainPanel.add(new JLabel("Displayed name "),gbc);
		gbc.gridx = 1;
		
		mainPanel.add(FriendlyName,gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		Name = new JTextField(series.Name, textFieldWidth);
		Name.setComponentPopupMenu(new ContextMenu());
		mainPanel.add(new JLabel("Title "),gbc);
		gbc.gridx = 1;
		mainPanel.add(Name,gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		seasons = new JTextField(series.SeasonsToString(), textFieldWidth);
		seasons.setComponentPopupMenu(new ContextMenu());
		mainPanel.add(new JLabel("Seasons "),gbc);
		gbc.gridx = 1;
		mainPanel.add(seasons,gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		img = new JTextField(series.ImgPath, textFieldWidth);
		img.setComponentPopupMenu(new ContextMenu());
		mainPanel.add(new JLabel("Image Path/URL "),gbc);
		gbc.gridx = 1;
		mainPanel.add(img,gbc);


		JFileChooser fc = new JFileChooser();
		JButton imgplus = new JButton("+");
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(imgplus,gbc);
		gbc.fill = GridBagConstraints.NONE;
		imgplus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif",
						"jpeg");
				fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(imgplus);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					img.setText(file.getAbsolutePath());
				}
			}
		});
		
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		music = new JTextField(series.MusicPath, textFieldWidth);
		music.setComponentPopupMenu(new ContextMenu());
		mainPanel.add(new JLabel("Music Path/URL "),gbc);
		gbc.gridx = 1;
		mainPanel.add(music,gbc);
		
		JButton musicplus = new JButton("+");
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(musicplus,gbc);
		gbc.fill = GridBagConstraints.NONE;
		musicplus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Sound Files", "mp3", "ogg", "wav");
				fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(musicplus);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					music.setText(file.getAbsolutePath());
				}
			}
		});
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		JTextField url = new JTextField("", textFieldWidth);
		url.setComponentPopupMenu(new ContextMenu());
		url.setToolTipText("<html>Supported sites:<br>fili.cc<br>imdb.com<html>");
		mainPanel.add(new JLabel("URL "),gbc);
		gbc.gridx = 1;
		mainPanel.add(url,gbc);
		JButton checkUrl = new JButton(">>");
		checkUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkUrl(url.getText());
			}
		});
		gbc.gridx = 2;
		mainPanel.add(checkUrl,gbc);

		
		
		final JComponent[] inputs = new JComponent[] { mainPanel };
		String[] options = { "OK", "Delete", "Cancel" };
		

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

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                FriendlyName.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        //textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        
        
        pack();
    }

    protected void checkUrl(String url) {
		WebManager wm = WebManager.WebManager;
		TvSeries series = wm.scrapWebsite(url);
		if(series==null) {
			JOptionPane.showMessageDialog(this, "Error occured");
			return;
		}
		if(FriendlyName.getText().isEmpty())FriendlyName.setText(series.Name);
		Name.setText(series.Name);
		int[] s = new int[series.Seasons.length];
		System.arraycopy( series.Seasons, 0, s, 0, series.Seasons.length );
		String[] seasonsString = Arrays.stream(s).mapToObj(String::valueOf).toArray(String[]::new);
		seasons.setText(String.join(", ", seasonsString));
		img.setText(series.ImgPath);
	}

	/**
     * This method handles events for the text field.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //optionPane.setValue(btnString1);
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
//            JOptionPane.showMessageDialog(this,
//                  "Sorry, \"" + "dsa" + "\" "
//                  + "isn't a valid response.\n"
//                  + "Please enter "
//                   + ".",
//                  "Try again",
//                  JOptionPane.ERROR_MESSAGE);
            
            if (result.toString().matches("OK")) {
    			if (FriendlyName.getText().isEmpty() || Name.getText().isEmpty() || seasons.getText().isEmpty()) {
    				JOptionPane.showMessageDialog(null, "Pola Friendly Name, Name oraz seasons nie mog¹ byæ puste",
    						"Uwaga!", JOptionPane.WARNING_MESSAGE);
    			} else {
    				Pattern p = Pattern.compile("\\d+");
    				Matcher m = p.matcher(seasons.getText());
    				List<String> temp = new ArrayList<String>();
    				while (m.find()) {
    					temp.add(m.group());
    					System.out.println(m.group());
    				}
    				if (temp.isEmpty()) {
    					JOptionPane.showMessageDialog(null, "Wpisz w polu seasons co najmniej jedn¹ liczbê!", "Uwaga!",
    							JOptionPane.WARNING_MESSAGE);
    					return;
    				}
    				int[] s = new int[temp.size()];
    				for (int i = 0; i < s.length; i++) {
    					s[i] = Integer.parseInt(temp.get(i));
    				}
    				String imgFile = img.getText().compareTo(series.ImgPath) != 0
    						? dataManager.CopyFile(img.getText(), Name.getText())
    						: series.ImgPath;
    				String musicFile = music.getText().compareTo(series.MusicPath) != 0
    						? dataManager.CopyFile(music.getText(), Name.getText())
    						: series.MusicPath;

    				series.Edit(FriendlyName.getText(), Name.getText(), s, imgFile, musicFile);
    				state=OK;
    				exit();
    			}
    		} else if (result.toString().matches("Delete")) {
    			if (!edit) {
    				JOptionPane.showMessageDialog(null, "Nie mo¿na usun¹æ nieistniej¹cego serialu!", "Uwaga!",
    						JOptionPane.WARNING_MESSAGE);
    				return;
    			}
    			int sure = JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usun¹æ serial: \"" + series.Name + "\"",
    					"Usuwanie serialu", JOptionPane.OK_CANCEL_OPTION);
    			if (sure != JOptionPane.OK_OPTION)
    				return;
    			state=DELETE;
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