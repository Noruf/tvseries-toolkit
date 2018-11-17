package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import managers.DataManager;
import models.TvSeries;

class SeriesManagerDialog extends JDialog
        implements ActionListener,
        PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1876781612105027706L;
	
	JOptionPane optionPane;
	
	final public int OK = 0;
	final public int CANCEL = 1;
	
	public int state = 1;
	
	DataManager dataManager;

	TvSeries[] listModel;
	JList<TvSeries> list;
	
    /**
     * Creates the reusable dialog.
     */
    public SeriesManagerDialog(Frame aFrame,DataManager dataManager) {
        super(aFrame,"Series Manager",true);
        this.dataManager = dataManager;
        List<TvSeries> tvseries =  dataManager.getTvSeries();
        listModel = (TvSeries[])tvseries.toArray(new TvSeries[tvseries.size()]);
        
        list = new JList<TvSeries>(listModel);
        JPanel panel1 = new JPanel();
        panel1.add(list);
        JButton moveUp = new JButton("Move up!");
        moveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				move(-1);
			}
		});
        JButton moveDown = new JButton("Move down!");
        moveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				move(1);
			}
		});
        JPanel buttons = new JPanel();
        buttons.add(moveUp);
        buttons.add(moveDown);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
        JPanel main = new JPanel();
        main.add(panel1);
        main.add(buttons);
		final JComponent[] inputs = new JComponent[] { main};
		String[] options = { "OK", "Cancel" };
		

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
        
        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);

        pack();
    }

    private void move(int rel) {
    	int pos = list.getSelectedIndex();
    	int target = Math.max(0, pos+rel);
    	target = Math.min(listModel.length-1, target);
    	swapElements(pos,target);
    	list.setSelectedIndex(target);
    	list.updateUI();
    }
    
    private void swapElements(int pos1, int pos2) {
    	TvSeries tmp = (TvSeries) listModel[pos1];
        listModel[pos1] = listModel[pos2];
        listModel[pos2] = tmp;
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
       
            if (result.toString().matches("OK")) {
            	dataManager.setTvSeries(listModel);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}


}