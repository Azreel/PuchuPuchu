import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;

@SuppressWarnings("serial")
public class Overlay extends JPanel{
	private final int fadeSpeed = 60;
	private final Font btnFont = new Font(Font.DIALOG, Font.PLAIN, 24);
	private final Font labelFont = new Font(Font.DIALOG, Font.PLAIN, 40);
	
	private static enum Mode {STOP, FADEIN, RESULT, PAUSE};
	private Mode paintMode = Mode.STOP;
	private float fadeAlpha;
	private GameMain gm;
	private Sound bgm;
	private boolean isPlay = false;
	private Image resultImg, pauseImg;
	
	// コンストラクタ
	Overlay(GameMain parent){
		gm = parent;
		this.setLayout(null);
		this.setOpaque(false);
		try {
			resultImg = ImageIO.read(getClass().getResource("result.png"));
			pauseImg = ImageIO.read(getClass().getResource("pause.png"));
		} catch(Exception e) {}
	}
	
	// フェードイン
	public void FadeIn() {
		this.removeAll();
		paintMode = Mode.FADEIN;
		fadeAlpha = 0.0f;
	}
	
	// リザルト表示
	public void Result(int score) {
		paintMode = Mode.RESULT;
		DecimalFormat df = new DecimalFormat("0000000");
		//スコア
		JLabel scoreLabel = new JLabel("SCORE：" + df.format(score));
		scoreLabel.setBounds(310, 300, 500, 40);
		scoreLabel.setFont(labelFont);
		scoreLabel.setForeground(new Color(255,255,255));
        //リターンボタン
		JButton titleBtn = new JButton("タイトルに戻る");
		titleBtn.setBounds(350, 500, 260, 60);
		titleBtn.setFont(btnFont);
		titleBtn.addActionListener(event -> {
			paintMode = Mode.STOP;
			gm.fadeIn(GameMain.Status.GAME_TITLE);
			Sound pushSound = new Sound(getClass().getResource("selectmode.wav"));
			pushSound.play();
		});
		
		this.add(scoreLabel);
		this.add(titleBtn);
	}
	
	// ポーズ画面
	public void Pause() {
		this.removeAll();
		paintMode = Mode.PAUSE;
		JLabel noticeLabel = new JLabel("もう一度Pを押すと解除します");
		noticeLabel.setBounds(200, 300, 700, 40);
		noticeLabel.setFont(labelFont);
		noticeLabel.setForeground(new Color(255,255,255));
		
		this.add(noticeLabel);
		
	}
	
	// 画面消去
	public void Clear() {
		this.removeAll();
		paintMode = Mode.STOP;
	}
	
	// BGMの再生
	public void setBGM(URL path) {
		bgm = new Sound(path);
		bgm.loop();
		bgm.setVol(0.8f);
		isPlay = true;
	}
	
	// BGMの停止
	public void stopBGM() {
		if(isPlay) {
			bgm.stop();
			isPlay = false;
		}
	}
	
	// 描画
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();
        switch(paintMode) {
        case STOP:
        	break;
        case FADEIN:
        	g2D.setColor(new Color(0,0,0,(int)((fadeAlpha <= 1.0f ? fadeAlpha : 1.0f)*255)));
            g2D.fillRect(0, 0, w, h);
            fadeAlpha += 1.0f / fadeSpeed;
            bgm.setVol(0.8f - fadeAlpha*3.5f);
            if(fadeAlpha >= 1.0f) {
            	paintMode = Mode.STOP;
            	gm.fadeEnd();
            	if(isPlay) stopBGM();
            }
        	break;
        case RESULT:
        	g2D.setColor(new Color(0,0,0,120));
            g2D.fillRect(0, 0, w, h);
        	g2D.drawImage(resultImg, 280, 50, null);
        	break;
        case PAUSE:
        	g2D.setColor(new Color(0,0,0,120));
            g2D.fillRect(0, 0, w, h);
            g2D.drawImage(pauseImg, 280, 50, null);
            break;
        }
    }
}
