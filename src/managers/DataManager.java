package managers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Link;
import models.TvSeries;


public class DataManager {

	ImportExportManager importExportManager;
	List<TvSeries> tvseries = new ArrayList<>();
	List<Link> searchEngines = new ArrayList<>();
	
	public DataManager() {
		importExportManager = new ImportExportManager();
	}
	
	public void importData(W window) {
		tvseries = importExportManager.ImportData();
		searchEngines = importExportManager.ImportSearchEngines();
		window.callback();
	}
	
	public void exportData() {
		importExportManager.ExportData(tvseries);
	}
	
	public BufferedImage loadImage(String imgPath) {
		return importExportManager.loadImage(imgPath);
	}
	
	public List<TvSeries> getTvSeries() {
		return tvseries;
	}
	public void setTvSeries(TvSeries[] arr) {
		tvseries = new ArrayList<>(Arrays.asList(arr));
		exportData();
	}
	public List<Link> getSearchEngines() {
		return searchEngines;
	}

	public void add(TvSeries series) {
		tvseries.add(series);
	}

	public void remove(TvSeries series) {
		tvseries.remove(series);
	}

}
