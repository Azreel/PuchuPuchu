import java.net.URL;
import javax.sound.sampled.*;

public class BGM extends Thread{
	private AudioInputStream ais;
	private Clip clip;
	private boolean isActive;
	
	// コンストラクタ
	BGM(URL url) {
		try {
			ais = AudioSystem.getAudioInputStream(url);
			AudioFormat af = ais.getFormat();
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			clip = (Clip)AudioSystem.getLine(dataLine);
			clip.open(ais);
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
				Thread.sleep(100);
			} catch(InterruptedException e) { }
		}
		clip.close();
	}
	
	// 停止
	public void Stop() {
		this.isActive = false;
	}
}