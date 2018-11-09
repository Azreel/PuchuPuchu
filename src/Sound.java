import java.net.URL;
import javax.sound.sampled.*;

public class Sound extends Thread{
	private boolean isLoop;
	private Clip clip;
	private FloatControl control = null;
	private float range, gain = 1.0f;
	private boolean isActive;
	
	Sound(URL url) {
		this(url,false);
	}
	
	Sound(URL url,boolean isLoop) {
		this.isLoop = isLoop;
		load(url);
	}
	
	private void load(URL url) {
		AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(url);
			AudioFormat af = ais.getFormat();
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			clip = (Clip)AudioSystem.getLine(dataLine);
			clip.open(ais);
			clip.loop(isLoop ? clip.LOOP_CONTINUOUSLY : 0);
		} catch (Exception e) {
			System.out.println("サウンド生成失敗");
		}
		
	}

	public void run() {
		isActive = true;
		clip.start();
		control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		range = control.getMaximum() - control.getMinimum();
		while(clip.isActive() && this.isActive) {
			try {
				Thread.sleep(100);
				control.setValue(gain);
			} catch(InterruptedException e) { }
		}
		clip.stop();
	}
	
	public void Stop() {
		this.isActive = false;
	}
	
	
	public void setVolume(float vol) {
		if(isActive) gain = (range * vol) + control.getMinimum();
	}
}