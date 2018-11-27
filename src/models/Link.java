package models;

import java.util.regex.Pattern;

public class Link{
	public static final int URL = 1;
	public static final int File = 2;
	public static final int Folder = 2;

	public int[] Seasons;
	public String Address;
	public String Name;
	public int Type;
	private boolean parameterSE = true;
	
	public Link(int[] s,String a,String n,int t,boolean SE) {
		set(s,a,n,t);
		parameterSE = SE;
	}
	public Link(int[] s,String a,String n,int t) {
		set(s,a,n,t);
	}
	public Link() {
		this(new int[]{-1},"","",URL);
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
	public String getAddress(String seriesName, String se) {
		String series = seriesName.replace(' ', '+');
		String addr = Address.replaceAll("###",series + "+" + se)
			.replaceAll("\\$\\{se\\}",se)
			.replaceAll("\\$\\{name\\}",series);
		if(parameterSE)addr = setParameter("se", se, addr);
		System.out.println(addr);
		return addr;
	}
	private String setParameter(String param,String newval, String address) {
		String[] Addr = address.split("\\?");
		String path = Addr[0];
		String search = Addr.length>1 ? "?"+Addr[1] : "";
		Pattern P = Pattern.compile("([?;&])" + param + "[^&;]*[;&]?");
		String query = P.matcher(search).replaceAll("$1");
        return path+(query.length() > 2 ? query + "&" : "?") + (!newval.isEmpty() ? param + "=" + newval : "");
    }
	
	
}