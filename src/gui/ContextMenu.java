package gui;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

public class ContextMenu extends JPopupMenu{

	JMenuItem cut;
	JMenuItem copy;
	JMenuItem paste;
	
	public ContextMenu() {
		super();
		cut = new JMenuItem(new DefaultEditorKit.CutAction());
		copy = new JMenuItem(new DefaultEditorKit.CopyAction());
		paste = new JMenuItem(new DefaultEditorKit.PasteAction());
		add(cut);
		add(copy);
		add(paste);

	}

	
	

}
