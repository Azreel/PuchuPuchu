import java.net.URL;
import javax.sound.sampled.*;

public class Sound extends Thread{
	private boolean isLoop;
	private AudioInputStream ais;
	private Clip clip;
	private FloatControl control = null;
	private float range, gain = 1.0f;
	private boolean isActive;
	
	// コンストラクタ(ループなし)
	Sound(URL url) {
		this(url,false);
	}
	
	// コンストラクタ(ループ指定あり)
	Sound(URL url,boolean isLoop) {
		this.isLoop = isLoop;
		load(url);
	}
	
	// ファイル読み込み
	private void load(URL url) {
		try {
			ais = AudioSystem.getAudioInputStream(url);
			AudioFormat af = ais.getFormat();
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			clip = (Clip)AudioSystem.getLine(dataLine);
			clip.loop(isLoop ? clip.LOOP_CONTINUOUSLY : 0);
		} catch (Exception e) {
			System.out.println("サウンド生成失敗");
		}
		
	}
	
	// 再生(スレッドのメイン)
	public void run() {
		isActive = true;
		try {
			clip.open(ais);
		} catch (Exception e) {
			System.out.println("サウンド再生失敗");
			return;
		}
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
	
	// 停止
	public void Stop() {
		this.isActive = false;
	}
	
	// ボリューム調整(うまく動かない)
	public void setVolume(float vol) {
		if(isActive) gain = (range * vol) + control.getMinimum();
	}
}