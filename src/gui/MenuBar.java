package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


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
	public MenuBar(JFrame frame) {
		super();
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
		autosave = new JCheckBoxMenuItem("Auto-save",true);
		options.add(autosave);
		music = new JCheckBoxMenuItem("Music",true);
		options.add(music);
		editButtons = new JCheckBoxMenuItem("Show Edit Buttons",true);
		options.add(editButtons);
		
		help = new JMenu("Help");
		add(help);
		info = new JMenuItem("Info");
		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "TV Series Toolkit\n\n Adam Zieliñski\n version 0.01");
				
			}
		});
		help.add(info);	
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
