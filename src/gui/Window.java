package gui;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


import managers.ImportExportManager;
import managers.LinkManager;
import managers.SoundManager;
import models.Link;
import models.TvSeries;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;


public class Window implements Runnable {

	JFrame frame;

	JComboBox<TvSeries> seriesChoice;
	JTextField episodeNumber;
	JLabel seasonEpisode;
	JLabel picLabel;
	JComboBox<String> source;
	JLabel statusLabel;

	List<TvSeries> tvseries;
	List<Link> searchEngines;

	ImportExportManager dataManager;
	SoundManager soundManager;
	LinkManager linkManager;
	
	MenuBar menuBar;

//	int currentSeason = 0;
	int[] currentSE = { 0, 0 };

	public static void main(String[] args) {

		
		EventQueue.invokeLater(new Window());
	}

	boolean devEnv = System.getenv("eclipse42")!=null;
	
	@Override
	public void run() {
		
		dataManager = new ImportExportManager();

		linkManager = new LinkManager();

		tvseries = dataManager.ImportData();
		searchEngines = dataManager.ImportSearchEngines();
		// tvseries = new ArrayList<TvSeries>();
		frame = new JFrame("CSI Miami Toolkit");
		Container contentPane = frame.getContentPane();
	
		
		
		setUIFont(new javax.swing.plaf.FontUIResource("", Font.BOLD, 25));
		UIManager.put("Menu.font", new javax.swing.plaf.FontUIResource("", Font.BOLD, 15));
		//UIManager.put("MenuItem.font", new javax.swing.plaf.FontUIResource("", Font.BOLD, 10));
		menuBar = new MenuBar(frame);
		menuBar.setMusicListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSound(seriesChoice.getSelectedIndex());
			}
		});
		
		frame.setJMenuBar(menuBar);
		
		menuBar.music.setSelected(!devEnv);
		InputStream stream = this.getClass().getResourceAsStream("/icon.png");
		//ImageIcon img = new ImageIcon(this.getClass().getResource("../icon.png"));
		BufferedImage myPicture;
		
		try {
			if(stream!=null) {
			myPicture = ImageIO.read(stream);
			ImageIcon img = new ImageIcon(myPicture);
			frame.setIconImage(img.getImage());
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		frame.setLocation(new Point(300, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();

		contentPane.add(mainPanel);
		contentPane.setLayout(new GridBagLayout());

		JPanel choosePanel = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		seriesChoice = new JComboBox<TvSeries>();
		updateSeriesChooser();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		seriesChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == 2 || e.getItemSelectable().toString().contains("invalid"))
					return;
				int selection = seriesChoice.getSelectedIndex();
				updateGUI();
				updateSound(selection);
			}
		});
		choosePanel.add(seriesChoice);

		JButton editSeries = new JButton("@");
		editSeries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSeriesDialog(true);

			}
		});
		choosePanel.add(editSeries);

		JButton addSeries = new JButton("+");
		addSeries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSeriesDialog(false);

			}
		});
		choosePanel.add(addSeries);
		contentPane.add(choosePanel, gbc);

		// gbc = new GridBagConstraints();

		episodeNumber = new JTextField(4);
		((AbstractDocument) episodeNumber.getDocument()).setDocumentFilter(new NumberOnlyFilter());
		episodeNumber.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				// Gives notification that a portion of the document has been removed.
				if (episodeNumber.getText().isEmpty())
					return;
				int ep = Integer.valueOf("0" + episodeNumber.getText());
				updateLabel(ep);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// Gives notification that there was an insert into the document.
				int ep = Integer.valueOf(episodeNumber.getText());
				updateLabel(ep);
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}
		});
		episodeNumber.setComponentPopupMenu(new ContextMenu());
		// gbc.ipadx = 100;
		// JOptionPane.showMessageDialog(null, episodeNumber);
		// JTextField episodeNumber = new JTextField(20);
		JPanel epPanel = new JPanel(); // Create new panel
		// epPanel.setLayout(new BoxLayout(epPanel, BoxLayout.X_AXIS));
		epPanel.add(new JLabel("Episode: ")); // Add components to it
		epPanel.add(episodeNumber);
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				episodeNumber.setText(Integer.toString(Integer.valueOf("0" + episodeNumber.getText()) - 1));
			}
		});
		epPanel.add(minus);
		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				episodeNumber.setText(Integer.toString(Integer.valueOf("0" + episodeNumber.getText()) + 1));
			}
		});
		epPanel.add(plus);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		contentPane.add(epPanel, gbc);

		// gbc = new GridBagConstraints();
		JPanel sePanel = new JPanel();
		seasonEpisode = new JLabel("s01e01");
		sePanel.add(seasonEpisode);
		gbc.gridx = 0;
		gbc.gridy = 2;
		contentPane.add(sePanel, gbc);

		// gbc = new GridBagConstraints();
		JPanel sourcePanel = new JPanel();
		source = new JComboBox<String>();
		sourcePanel.add(new JLabel("Source"));
		sourcePanel.add(source);
		JButton editSource = new JButton("@");
		editSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showLinkDialog(true);
			}
		});
		sourcePanel.add(editSource);
		JButton addSource = new JButton("+");
		addSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showLinkDialog(false);
			}
		});

		sourcePanel.add(addSource);

		gbc.gridx = 0;
		gbc.gridy = 3;
		contentPane.add(sourcePanel, gbc);

		JPanel goPanel = new JPanel();
		JButton saveButton = new JButton("Save!");
		goPanel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int ep = Integer.parseInt("0" + episodeNumber.getText());
				TvSeries series = tvseries.get(seriesChoice.getSelectedIndex());
				series.CurrentEpisode = ep;
				dataManager.ExportData(tvseries);
				statusLabel.setText("Saved! Episode: " + ep + ", " + seasonEpisode.getText());
			}
		});

		JButton goButton = new JButton("Go!");
		goPanel.add(goButton);
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				soundManager.stop();

				int ep = Integer.parseInt("0" + episodeNumber.getText());
				TvSeries series = tvseries.get(seriesChoice.getSelectedIndex());
				if(menuBar.isAutosave())series.CurrentEpisode = ep;
				statusLabel.setText("Wait...");
				Thread one = new Thread() {
					public void run() {
						
						linkManager.openLink(getSelectedLink(), series.Name, series.getSEString(ep));

						if(menuBar.isAutosave())dataManager.ExportData(tvseries);
						statusLabel.setText("Saved! Link Opened.");
					}
				};
				one.start();
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 4;
		contentPane.add(goPanel, gbc);


		picLabel = new JLabel(new ImageIcon(new BufferedImage(300, 450, BufferedImage.TYPE_3BYTE_BGR)));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridheight = 5;
		gbc.insets = new Insets(10, 10, 10, 10);
		contentPane.add(picLabel, gbc);

		// frame.setUndecorated(true);
		// frame.setOpacity(0.40f);

		// create the status bar panel and shove it down the bottom of the frame
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel = new JLabel(" ");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(-5, -5, -5, -5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(statusPanel, gbc);

		frame.setResizable(false);
		frame.pack();

		frame.setLocationByPlatform(true);
		frame.setVisible(true);

		soundManager = new SoundManager();
		if (!tvseries.isEmpty()) {
			updateGUI();
			updateLabel(tvseries.get(0).CurrentEpisode);
			updateSound(0);
		}
		menuBar.setEditButtonsListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean show = menuBar.isEdit();
				editSeries.setVisible(show);
				addSeries.setVisible(show);
				editSource.setVisible(show);
				addSource.setVisible(show);
				frame.pack();
			}
		});
	}

	private void updateSeriesChooser() {
		seriesChoice.removeAllItems();
		for (TvSeries item : tvseries) {
			seriesChoice.addItem(item);
		}
		seriesChoice.setMaximumRowCount(seriesChoice.getModel().getSize());
	}

	private void updateSeriesChooser(int selection) {
		updateSeriesChooser();
		seriesChoice.setSelectedIndex(selection);
		updateGUI();
		updateSound(selection);

	}

	private void updateSources(TvSeries series, int s) {
		source.removeAllItems();
		for (Link item : series.Links) {
			if (item.isSeason(s)) {
				source.addItem(item.Name);
			}
		}
		for (Link item : searchEngines) {
			source.addItem(item.Name);
		}

	}

	private void updateLabel(int ep) {
		TvSeries series = (TvSeries)seriesChoice.getSelectedItem();
		int[] se = series.fromEpisode(ep);
		int s = se[0] + 1;
		int e = se[1];
		String sestring = "s" + (s < 10 ? "0" : "") + s + "e" + (e < 10 ? "0" : "") + e;
		sestring+=se[2]==1?" season finale":"";
		seasonEpisode.setText(sestring);
		if (currentSE[0] != se[0]) {
			updateSources(series, se[0]);
		}
		currentSE = se;
	}
	
	private void setPoster(ImageIcon image, TvSeries series) {
		if(series == getSelectedSeries()&&image!=null) {
			picLabel.setIcon(image);
			frame.pack();
			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
		}
	}

	protected void updateGUI() {
		TvSeries series = getSelectedSeries();
		int ep = series.CurrentEpisode;
		episodeNumber.setText(Integer.toString(ep));
		updateSources(series, currentSE[0]);
		String imgPath = series.ImgPath.isEmpty() ? "empty.png" : series.ImgPath;
		statusLabel.setText(series.Name + ", ep:"+ep+", "+series.getSEString());
		Thread one = new Thread() {
			public void run() {

				BufferedImage myPicture = dataManager.loadImage(imgPath);
				ImageIcon image = new ImageIcon(myPicture.getScaledInstance(-1, 450, Image.SCALE_AREA_AVERAGING));
				setPoster(image,series);
					
//					picLabel.setIcon(new ImageIcon(myPicture.getScaledInstance(-1, 450, Image.SCALE_AREA_AVERAGING)));
//					frame.pack();
//					frame.getContentPane().revalidate();
//					frame.getContentPane().repaint();

			}
		};
		one.start();
	}

	private void updateSound(int i) {
		if (i < 0)
			return;
		TvSeries series = tvseries.get(i);
		soundManager.stop();

		if(menuBar.isMusic())soundManager.play(series.MusicPath);

	}
	
	

	private Link getSelectedLink() {
		TvSeries series = tvseries.get(seriesChoice.getSelectedIndex());
		for (Link item : series.Links) {
			if (item.Name.equalsIgnoreCase(source.getSelectedItem().toString())
					&& (item.isSeason(currentSE[0]))) {
				return item;
			}
		}
		for (Link item : searchEngines) {
			if (item.Name.equalsIgnoreCase(source.getSelectedItem().toString())) {
				return item;
			}
		}
		return null;
	}

	private TvSeries getSelectedSeries() {
		return (TvSeries)seriesChoice.getSelectedItem();
	}
	
	
	
	private void showSeriesDialog(boolean edit) {
		TvSeries series = edit ? getSelectedSeries() : new TvSeries();
		SeriesDialog sd = new SeriesDialog(frame,series,edit);
		sd.setVisible(true);	
		int choice = sd.state;
		if(choice==sd.OK) {
			if(!edit)tvseries.add(series);
			updateSeriesChooser(tvseries.indexOf(series));
		}
		else if(choice==sd.DELETE) {
			tvseries.remove(series);
			updateSeriesChooser();
			updateGUI();
			updateSound(0);
		}else {
			return;
		}
		dataManager.ExportData(tvseries);
	}

	private void showLinkDialog(boolean edit) {
		TvSeries series = getSelectedSeries();
		Link link = edit?getSelectedLink():new Link();
		
		if (edit&&searchEngines.contains(link)) {
			JOptionPane.showMessageDialog(null, "You cannot edit search engines!", "Warning!",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		LinkDialog ld = new LinkDialog(frame,series,link,edit);
		ld.setVisible(true);	
		int choice = ld.state;
		if(choice==ld.OK) {
			if(!edit)series.AddLink(link);
			updateLabel(Integer.parseInt("0" + episodeNumber.getText()));
		}
		else if(choice==ld.DELETE) {
			series.RemoveLink(link);
			updateLabel(Integer.parseInt("0" + episodeNumber.getText()));
		}else {
			return;
		}
		updateSources(series, currentSE[0]);
		dataManager.ExportData(tvseries);
		
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
			
		}
	}

	
	public static class NumberOnlyFilter extends DocumentFilter {

		public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
				throws BadLocationException {
			StringBuilder sb = new StringBuilder();
			sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
			sb.insert(offset, text);
			if (!containsOnlyNumbers(sb.toString()))
				return;
			fb.insertString(offset, text, attr);
		}

		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr)
				throws BadLocationException {
			StringBuilder sb = new StringBuilder();
			sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
			sb.replace(offset, offset + length, text);
			if (!containsOnlyNumbers(sb.toString()))
				return;
			fb.replace(offset, length, text, attr);
		}

		/**
		 * This method checks if a String contains only numbers
		 */
		public boolean containsOnlyNumbers(String text) {
			Pattern pattern = Pattern.compile("^[0-9]*$");
			Matcher matcher = pattern.matcher(text);
			boolean isMatch = matcher.matches();
			return isMatch;
		}

	}
}


