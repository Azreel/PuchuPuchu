import java.net.URL;
import javax.sound.sampled.*;

public class BGM extends Thread{
	private AudioInputStream ais;
	private Clip clip;
	private FloatControl control;
	private boolean isActive;
	private float volume = 0.8f;
	
	// コンストラクタ
	BGM(URL url) {
		try {
			ais = AudioSystem.getAudioInputStream(url);
			AudioFormat af = ais.getFormat();
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			clip = (Clip)AudioSystem.getLine(dataLine);
			clip.open(ais);
			control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue((float)Math.log10(volume) * 20);
			isActive = true;
		} catch (Exception e) {
			System.out.println("BGM生成失敗");
		}
	}
	
	// 再生(スレッドのメイン)
	public void run() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		while(this.isActive) {
			try {
				Thread.sleep(1);
				control.setValue((float)Math.log10(volume) * 20);
			} catch(InterruptedException e) { }
		}
		clip.close();
	}
	
	// 停止
	public void Stop() {
		this.isActive = false;
	}
	
	// 音量調整
	public void setVol(float vol) {
		if(vol <= 0.1f) vol = 0.0f;
		volume = vol;
	}
}