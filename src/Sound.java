import java.net.URL;
import javax.sound.sampled.*;

public class Sound{
	private AudioInputStream ais;
	private Clip clip;
	private FloatControl control;
	
	// コンストラクタ
	Sound(URL url) {
		try {
			ais = AudioSystem.getAudioInputStream(url);
			AudioFormat af = ais.getFormat();
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			clip = (Clip)AudioSystem.getLine(dataLine);
			clip.open(ais);
			control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (Exception e) {
			System.out.println("BGM生成失敗");
		}
	}
	
	// 再生
	public void play() {
		// 前回の状態を一旦リセット
		stop();
		clip.start();
	}
	
	// ループ再生
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	// 停止
	public void stop() {
		clip.stop();
		clip.flush();
		clip.setFramePosition(0);
	}
	
	// 音量調整
	public void setVol(float vol) {
		if(vol <= 0.1f) vol = 0.0f;
		control.setValue((float)Math.log10(vol) * 20);
	}
}