package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import models.Link;
import models.TvSeries;

class LinkDialog extends JDialog
        implements ActionListener,
        PropertyChangeListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3396957182889860591L;

	TvSeries series;
	Link link;
	boolean edit;
	JTextField Name;
	JTextField Address;
	JComboBox<String> Type;
	JToggleButton[] Seasons;
	
	JOptionPane optionPane;
	
	final public int OK = 0;
	final public int DELETE = 1;
	final public int CANCEL = 2;
	
	public int state = 2;
	
    /**
     * Creates the reusable dialog.
     */
    public LinkDialog(Frame aFrame, TvSeries series,Link link,boolean edit) {
        super(aFrame, true);
        
        this.series = series;
		this.edit = edit;
		this.link=link;
		this.setTitle((edit?"Edit":"Add")+" link");
		JPanel panel1 = new JPanel();
		
		
		panel1.add(new JLabel("Season"));
		Seasons = new JToggleButton[series.getNumberOfSeasons()];
		for(int i=0;i<Seasons.length;i++) {
			Seasons[i]=new JToggleButton((i+1)+"",edit?link.isSeason(i):false);
			panel1.add(Seasons[i]);
		}
		
		
		JPanel panel2 = new JPanel();
		Name = new JTextField(link.Name, 20);
		Name.setComponentPopupMenu(new ContextMenu());
		panel2.add(new JLabel("Name"));
		panel2.add(Name);

		JPanel panel3 = new JPanel();
		Address = new JTextField(link.Address, 20);
		Address.setComponentPopupMenu(new ContextMenu());
		Address.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {}
			  public void removeUpdate(DocumentEvent e) {}
			  public void insertUpdate(DocumentEvent e) {
			    if(Name.getText().isEmpty()) {
			    	Pattern p = Pattern.compile("https?:\\/\\/(\\S*\\.\\w*)[/$]", Pattern.CASE_INSENSITIVE);
			    	Matcher m = p.matcher(Address.getText());
			    	if(m.find()&&m.group(1)!=null) {
			    		System.out.println(m.group(1));
			    		Name.setText(m.group(1));
			    	}
			    }
			  }
		});
		panel3.add(new JLabel("Address"));
		panel3.add(Address);

		JPanel panel4 = new JPanel();
		Type = new JComboBox<String>();
		Type.addItem("URL");
		Type.addItem("Folder");
		Type.setSelectedIndex(link.Type - 1);
		panel4.add(new JLabel("Type"));
		panel4.add(Type);

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		JButton folderChoose = new JButton("+");
		panel3.add(folderChoose);
		folderChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					Address.setText(fc.getSelectedFile().getPath());
				}
			}
		});

		final JComponent[] inputs = new JComponent[] { panel1, panel2, panel3, panel4 };

		String[] options = { "OK", "Delete", "Cancel" };
		//int result = JOptionPane.showOptionDialog(null, inputs, "Series", 0, 0, null, options, null);

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
            	Name.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        //textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        pack();
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

            //******MAIN LOGIC******
            
            if (result.toString().matches("OK")) {
            	
            	List<Integer> list = new ArrayList<Integer>();
            	for(int i=0;i<Seasons.length;i++) {
        			if(Seasons[i].isSelected()) list.add(i);
        		}
            	int[] s = new int[list.size()];
            	for(int i=0;i<s.length;i++) {
            		s[i]=list.get(i).intValue();
            	}
    			String a = Address.getText();
    			String n = Name.getText();
    			int t = Type.getSelectedIndex() + 1;
    			if (a.isEmpty() || n.isEmpty()) {
    				JOptionPane.showMessageDialog(null, "Pola Name oraz adress nie mog¹ byæ puste", "Uwaga!",
    						JOptionPane.WARNING_MESSAGE);
    				return;
    			} else if (s.length==0) {
    				JOptionPane.showMessageDialog(null, "Nie wybrano ¿adnego sezonu", "Uwaga!",
    						JOptionPane.WARNING_MESSAGE);
    				return;
    			} else {
    				link.set(s, a, n, t);
    				state=OK;
    				exit();
    			}
    		} else if (result.toString().matches("Delete")) {
    			if (!edit) {
    				JOptionPane.showMessageDialog(null, "Nie mo¿na usun¹æ nieistniej¹cego linku!!", "WTF!?",
    						JOptionPane.WARNING_MESSAGE);
    				return;
    			}
    			int sure = JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usun¹æ link: \"" + link.Name + "\"",
    					"Usuwanie linku", JOptionPane.OK_CANCEL_OPTION);
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