package models;

import java.util.ArrayList;
import java.util.List;

public class TvSeries{

	public String FriendlyName;
	public String Name;
	public int[] Seasons;
	public int CurrentEpisode;
	public String ImgPath;
	public String MusicPath;
	public List<Link> Links;
	
	
	public TvSeries() {
		this("","",new int[]{},"","");
	}
	public TvSeries(String fn,String n, int[] s, String img,String music) {
		FriendlyName = fn;
		Name = n;
		Seasons = s;
		CurrentEpisode = 0;
		ImgPath = img;
		MusicPath = music;
		Links = new ArrayList<Link>();
		//Links.add(new Link());
	}
	public TvSeries(String fn,String n, int[] s,int current, String img,String music, List<Link> links) {
		this(fn,n,s,img,music);
		CurrentEpisode = current;
		Links = links;
	}
	public void Edit(String fn,String n, int[] s, String img,String music) {
		FriendlyName = fn;
		Name = n;
		Seasons = s;
		ImgPath = img;
		MusicPath = music;
	}
	
	public void AddLink(Link link) {
		Links.add(link);
	}
	public void RemoveLink(Link link) {
		Links.remove(link);
	}

	public int numberOfEpisodes() {
		int sum = 0;
		for(int item : Seasons) {
			sum+=item;
		}
		return sum;
	}
	public int fromSeasonEpisode(int s, int e) {
		int episode=0;
		if(s>=Seasons.length) {
			return -1;
		}
		for (int i =0; i<s; i++) {
			episode+=Seasons[i];
		}
		episode+=e;
		return episode;
	}
	public int[] fromEpisode(int e) {
		if(e>numberOfEpisodes()) {
			return new int[]{-1,-1,0} ;
		}

		int s;
		for(s=0;s<Seasons.length;s++) {
			if(e<=Seasons[s])break;
			e-=Seasons[s];
		}
		int finale = e==Seasons[s]?1:0;
		return new int[] {s,e,finale};
	}
	public String SeasonsToString() {
		StringBuilder sb = new StringBuilder();
		for(int item:Seasons) {
			sb.append(item + ", ");
		}
		return sb.toString();
	}
	public String getSEString(int ep) {
		int[] se= fromEpisode(ep);
		int s=se[0]+1;
		int e=se[1];
		return "s" + (s < 10 ? "0" : "") + s + "e" + (e < 10 ? "0" : "") + e;
	}
	public String getSEString() {
		return getSEString(CurrentEpisode);
	}
	public int getNumberOfSeasons() {
		return Seasons.length;
	}
	public String toString() {
		return FriendlyName;
	}
	
}
