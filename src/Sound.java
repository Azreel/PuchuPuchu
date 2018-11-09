import java.net.URL;
import javax.sound.sampled.*;

public class Sound {
	private boolean isLoop;
	private Clip clip;
	private FloatControl control = null;
	private boolean isClosed = false;
	private float range, gain = 1.0f;
	
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
			clip.loop(isLoop ? clip.LOOP_CONTINUOUSLY : 0);
			clip.open(ais);
			control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			range = control.getMaximum() - control.getMinimum();
		} catch (Exception e) {
			System.out.println("サウンド生成失敗");
		}
		
	}

	public void Play() {
		if(isClosed) {
			System.out.println("サウンドは閉じられています");
			return;
		}
		clip.start();
		while(clip.isActive()) {
			try {
				Thread.sleep(100);
				try { control.setValue(gain); } catch(Exception e) {}
			} catch(InterruptedException e) { }
		}
	}
	
	public void Stop() {
		if(!isClosed) clip.stop();
	}
	
	public void Close() {
		clip.close();
		isClosed = true;
	}
	
	public void setVolume(float vol) {
		if(control != null) gain = (range * vol) + control.getMinimum();
	}
}