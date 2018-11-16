import java.net.URL;
import javax.sound.sampled.*;

public class BGM{
	private AudioInputStream ais;
	private Clip clip;
	private FloatControl control;
	
	// コンストラクタ
	BGM(URL url) {
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
	
	// 再生(スレッドのメイン)
	public void Play() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		control.setValue((float)Math.log10(0.8f) * 20);
	}
	
	// 停止
	public void Stop() {
		clip.stop();
		clip.close();
	}
	
	// 音量調整
	public void setVol(float vol) {
		if(vol <= 0.1f) vol = 0.0f;
		control.setValue((float)Math.log10(vol) * 20);
	}
}