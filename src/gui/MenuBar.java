package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import managers.DataManager;


public class MenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 622952725935997605L;
	JMenu app;
	JMenu options;
	JMenu help;
	
	JMenuItem exit;
	JCheckBoxMenuItem autosave;
	JCheckBoxMenuItem music;
	JCheckBoxMenuItem editButtons;
	JMenuItem info;
	
	JFrame frame;
	managers.W window;
	public MenuBar(JFrame frame, DataManager dataManager, managers.W window) {
		super();
		this.frame = frame;
		this.window = window;
		app = new JMenu("App");
		add(app);
		exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		app.add(exit);
		
		options = new JMenu("Options");
		add(options);
		JMenuItem seriesManager = new JMenuItem("Series Manager");
		options.add(seriesManager);
		seriesManagerDialog(seriesManager,dataManager);
		options.addSeparator();
		autosave = new JCheckBoxMenuItem("Auto-save",true);
		options.add(autosave);
		music = new JCheckBoxMenuItem("Music",true);
		options.add(music);
		editButtons = new JCheckBoxMenuItem("Show Edit Buttons",true);
		options.add(editButtons);
		
		help = new JMenu("Help");
		add(help);
		info = new JMenuItem("Info");
		infoDialog(info,frame);
		help.add(info);	
	}
	void seriesManagerDialog(JMenuItem seriesManager,DataManager dataManager) {
		seriesManager.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SeriesManagerDialog dialog = new SeriesManagerDialog(frame,dataManager);
				dialog.setVisible(true);
				if(dialog.state == dialog.OK)window.gotData();
			}
		});
	}
	void infoDialog(JMenuItem info,JFrame frame) {
		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "TV Series Toolkit\n\n Adam Zieliñski\n version 0.01");
				
			}
		});
	}
	
	public void setMusicListener(ActionListener l){
		music.addActionListener(l);
	}
	public void setEditButtonsListener(ActionListener l){
		editButtons.addActionListener(l);
	}
	public boolean isAutosave() {
		return autosave.isSelected();
	}
	public boolean isMusic() {
		return music.isSelected();
	}
	public boolean isEdit() {
		return editButtons.isSelected();
	}
}
