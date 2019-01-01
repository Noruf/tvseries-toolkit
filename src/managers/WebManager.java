package managers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.TvSeries;

public class WebManager {
	public static WebManager WebManager = new WebManager();
	
	
	public WebManager() {
		
		
	}
	
	public TvSeries scrapWebsite(String url) {
		Pattern p = Pattern.compile("https?:\\/\\/(\\S*\\.\\w*)[/$]", Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(url);
    	if(!m.find()||m.group(1)==null) {
    		return null;
    	}
    	String website = m.group(1);
    	switch(website) {
    		case "fili.cc": return scrapFilicc(url);
    		case "www.imdb.com": return scrapIMDB(url);
    		default: return null;
    	}
	}
	
	
	private TvSeries scrapFilicc(String url) {
		try {
			Document document = Jsoup.connect(url).get();
			Elements el = document.select("ul[data-season-num]");
			int[] seasons = new int[el.size()];
			for(int i = 0; i < seasons.length; i++) {
				seasons[i] = el.get(i).childNodeSize();
			}
			for (int s : seasons) {
				System.out.println(s);
			}
			String title = document.select(".title").text();
			String poster = "https://fili.cc" + document.select("#poster").attr("src");
			return new TvSeries("", title, seasons, poster, "");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private TvSeries scrapIMDB(String url) {
		url = url.replaceFirst("\\?.*", "");
		try {
			Document document = Jsoup.connect(url).get();
			String title = document.select(".title_wrapper > h1").text();
			String poster = document.select(".poster > a:nth-child(1) > img:nth-child(1)").attr("src");
			Elements el = document.select(".seasons-and-year-nav > div:nth-child(4) > a");
			int[] seasons = new int[el.size()];
			for(int i = 0; i < seasons.length; i++) {
				String href = "https://www.imdb.com" + el.get(el.size()-i-1).attr("href");
				Document seasonPage = Jsoup.connect(href).get();
				Elements s = seasonPage.select(".list.detail > div");
				seasons[i] = s.size();
			}
			return new TvSeries("", title, seasons, poster, "");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
