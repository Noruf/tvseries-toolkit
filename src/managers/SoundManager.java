package managers;

import java.io.File;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class SoundManager {

	Thread playerThread;

	public SoundManager() {

		playerThread = new Thread();
	}

	public void play(String songName) {
		if (songName.isEmpty())
			return;
		
		playerThread = new Thread() {
			BasicPlayer player = new BasicPlayer();
			boolean running=true;
			public void run() {
				String pathToMp3 = System.getProperty("user.dir") + "/data/" + songName;
				File file = new File(pathToMp3);
				try {
					player.open(file);

					player.play();
					player.setGain(0.1);
				} catch (BasicPlayerException e) {
					e.printStackTrace();
				}
				
				while(running){
					try {
		                Thread.sleep((200));
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
				}
				
				try {

					//8,6775ln(x) + 3,0035

					double val = player.getGainValue();
					double volume = Math.exp((val-3.0035)/8.6775);			
					while (volume > 0.005) {
						volume *= 0.8;
						Thread.sleep(100);
						player.setGain(volume);
					}
					player.stop();
				} catch (BasicPlayerException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}

			public void interrupt() {
				running=false;
			}

		};
		playerThread.start();

	}

	public void stop() {
		playerThread.interrupt();
	}


}
