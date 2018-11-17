package models;

public class Link{
	public static final int URL = 1;
	public static final int File = 2;
	public static final int Folder = 2;

	public int[] Seasons;
	public String Address;
	public String Name;
	public int Type;
	
	public Link(int[] s,String a,String n,int t) {
		Seasons = s;
		Address = a;
		Name = n;
		Type = t;
	}
	public Link() {
		Seasons = new int[]{-1};
		Address = "";
		Name = "";
		Type = URL;
	}
	
	public void set(int[] s,String a,String n,int t) {
		Seasons = s;
		Address = a;
		Name = n;
		Type = t;
	}
	public boolean isSeason(int s) {
		for(int item:Seasons) {
			if(item==s||item==-1)return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return Name;
	}
}