package cardgame.classes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Music {
	
	
	/**Zum Abspielen von Hintergrundmusik als Daemon.
	 * Tracks muessen .wav Dateien sein und muessen im Folder "audio" liegen.
	 * @param list Liste aller Tracks, ohne "audio/" und ohne ".wav", nur der Name.
	 * @param ordered wenn true, dann in angegebener Reihenfolge, sonst zufaellig.
	 */
	public static synchronized void music(List<String> list, boolean ordered){
		List<String> trackList = new ArrayList<>();
		for(String t: list){
			trackList.add("audio/"+t+".wav");
		}
		
		Thread musicDaemon = new Thread(new Runnable(){

			@Override
			public void run() {
				int counter = 0;
				int old = -1;
				while(true){
					Clip clip;
					try {
						clip = AudioSystem.getClip();
						AudioInputStream inputStream;
						
						if(ordered){
							inputStream = AudioSystem.getAudioInputStream(new File(trackList.get(counter)));
							counter = (counter+1) % trackList.size();
						}else{
							int choosen = new Random().nextInt(trackList.size());
							if(old == choosen){
								choosen = (choosen+1) % trackList.size();
							}
							inputStream = AudioSystem.getAudioInputStream(new File(trackList.get(choosen)));
							old = choosen;
						}
						clip.open(inputStream);
						clip.loop(0);
						
						Thread.sleep(clip.getMicrosecondLength()/1000);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		});
		musicDaemon.setDaemon(true);
		musicDaemon.start();
	}
	
	/**Fueht einen SoundEffekt aus.
	 * Enthaelt kein looping, aber hoehere Lautstaerke.
	 * @param soundEffect Name der .wav Datei, die ausgefuehrt werden soll.
	 */
	public static synchronized void soundEffect(String soundEffect){
		Thread soundEffectThread = new Thread(new Runnable(){

			@Override
			public void run() {
				Clip clip;
				try {
					clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("audio/"+soundEffect+".wav"));
					clip.open(inputStream);
					FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					gainControl.setValue(6.0f);
					clip.loop(0);
					Thread.sleep(clip.getMicrosecondLength()/1000);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
	soundEffectThread.start();	
	}
}
