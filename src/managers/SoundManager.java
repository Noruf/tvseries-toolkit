package managers;

import java.io.File;
import java.util.Objects;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class SoundManager {
	public static SoundManager SoundManager = new SoundManager();
	public ImportExportManager ImportExportManager;
	

	BasicPlayer player;
	String currentSong;
	public SoundManager() {
		player = new BasicPlayer();
		currentSong = "";
		ImportExportManager = ImportExportManager.ImportExportManager;
	}
	

	public void play(String songName) {
		if (!shouldPlay(songName))
			return;
		stop();
		currentSong = songName;
		if(songName.isEmpty())
			return;
		player = new BasicPlayer();
		String pathToMp3 = ImportExportManager.workingDirectory + "/data/" + songName;
		File file = new File(pathToMp3);
		try {;
			player.open(file);
			player.play();
			player.setGain(0.1);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}

	}
	private boolean shouldPlay(String songName) {
		return !Objects.equals(currentSong, songName) ||
				player.getStatus() == BasicPlayer.STOPPED;
	}
	
	public void stop() {
		BasicPlayer player = this.player;
		(new Thread() {
			public void run() {
				try {
					double val = player.getGainValue();
					double volume = Math.exp((val - 3.0035) / 8.6775);
					while (volume > 0.005) {
						volume *= 0.8;
						sleep(100);
						player.setGain(volume);
					}
					player.stop();
				} catch (BasicPlayerException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

}
