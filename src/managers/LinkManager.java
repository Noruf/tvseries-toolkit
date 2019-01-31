package managers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.PrimitiveIterator.OfDouble;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import models.Link;
import models.TvSeries;

public class LinkManager {
	public static LinkManager LinkManager = new LinkManager();
	
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
	
	
	private void openFolder(String path,TvSeries series,int ep){
        File dirToOpen = null;
        try {
            dirToOpen = new File(path);
            File[] listOfFiles = dirToOpen.listFiles();
            Pattern P = Pattern.compile(series.getSEString(ep,true), Pattern.CASE_INSENSITIVE);
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
	
	public void openLink(Link link,TvSeries series, int ep) {
		if(link.Type==Link.URL) {
			String addr = link.getAddress(series.Name, series.getSEString(ep));
			try {
				openWebpage(new URL(addr));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(link.Type == Link.Folder) {
			openFolder(link.Address,series,ep);
		}	
	}
	public String analyzeLink(Link link, TvSeries series, int e) {
		StringBuilder sb = new StringBuilder();
		sb.append(link.Name);
		sb.append("\n");
		sb.append(link.Address);
		sb.append("\nSeasons: ");
		
		int[] seasons = new int[link.Seasons.length];
		System.arraycopy( link.Seasons, 0, seasons, 0, link.Seasons.length );
		for(int i=0; i < seasons.length; i++) {
			seasons[i] += 1;
		}
		String[] seasonsString = Arrays.stream(seasons).mapToObj(String::valueOf).toArray(String[]::new);
		sb.append(String.join(", ", seasonsString));
		
		if(link.Type == Link.Folder) {
			
			sb.append("\n\nLast avaible unseen episode: ");
			File dir = new File(link.Address);
			File[] listOfFiles = dir.listFiles();
			boolean found = false;
			for(int i = e; i <= series.numberOfEpisodes(); i++) {
				 found = false;
				 Pattern P = Pattern.compile(Pattern.quote(series.getSEString(i)), Pattern.CASE_INSENSITIVE);
				 for (File file : listOfFiles) { 
					 if (P.matcher(file.getName()).find()) {
						 found = true;
						 break;
					 }
				 }
				 if(!found) {
					 sb.append(i!=e?series.getSEString(i-1):"none");
					 break;
				 }
			 }
			if(found)sb.append(series.getSEString(e));
		}
		return sb.toString();
	}
	
	
	
}
