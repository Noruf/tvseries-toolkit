package managers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import models.Link;

public class LinkManager {
	private Desktop desktop;
	public LinkManager() {
    	desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	}
	
	private void openWebpage(URI uri) {
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	private void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}
	private void openFolder(String path,String se){
        File dirToOpen = null;
        try {
            dirToOpen = new File(path);
            File[] listOfFiles = dirToOpen.listFiles();
            Pattern P = Pattern.compile(Pattern.quote(se), Pattern.CASE_INSENSITIVE);
            for (int i = 0; i < listOfFiles.length; i++) {
              if (P.matcher(listOfFiles[i].getName()).find()) {
                desktop.open(new File(listOfFiles[i].getAbsolutePath()));
                return;
              }
            }
            desktop.open(dirToOpen);
        } catch (IllegalArgumentException | IOException iae) {
            System.out.println("File Not Found");
        }
    }
	
	public void openLink(Link link,String name,String se) {
		if(link.Type==Link.URL) {
			String addr = link.Address.replaceAll("###",name.replace(' ', '+') + "+" + se);
			addr = addr.replaceAll("\\$\\{se\\}",se);
			addr = addr.replaceAll("\\$\\{name\\}",name.replace(' ', '+'));
			try {
				openWebpage(new URL(addr));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(link.Type == Link.Folder) {
			openFolder(link.Address,se);
		}
		
	}
	
	
	
}
