import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;

public class Overlay extends JPanel{
	final int fadeSpeed = 60;
	final Toolkit tk = Toolkit.getDefaultToolkit();
	final Font btnFont = new Font(Font.DIALOG, Font.PLAIN, 24);
	final Font labelFont = new Font(Font.DIALOG, Font.PLAIN, 40);
	
	static enum Mode {STOP, FADEIN, FADEOUT, PAUSE, RESULT};
	Mode paintMode = Mode.STOP;
	float fadeAlpha;
	GameMain gm;
	BGM bgm;
	boolean isPlay = false;
	Image resultImg;
	
	Overlay(GameMain parent){
		gm = parent;
		this.setOpaque(false);
	}
	
	public void FadeIn() {
		this.removeAll();
		paintMode = Mode.FADEIN;
		fadeAlpha = 0.0f;
	}
	
	public void FadeOut() {
		this.removeAll();
		paintMode = Mode.FADEOUT;
		fadeAlpha = 1.0f;
	}
	
	public void Result(int score) {
		paintMode = Mode.RESULT;
		resultImg = tk.createImage(getClass().getResource("result.png"));
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
			gm.fadeIn(GameMain.Status.GAME_TITLE);
		});
		
		this.add(scoreLabel);
		this.add(titleBtn);
	}
	
	public void setBGM(URL path) {
		bgm = new BGM(path);
		bgm.start();
		isPlay = true;
	}
	
	public void stopBGM() {
		if(isPlay) {
			bgm.Stop();
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
        case FADEOUT:
        	g2D.setColor(new Color(0,0,0,(int)((fadeAlpha >= 0.0f ? fadeAlpha : 0.0f)*255)));
            g2D.fillRect(0, 0, w, h);
            fadeAlpha -= 1.0f / fadeSpeed;
            if(fadeAlpha <= 0.0f) {
            	paintMode = Mode.STOP;
            	if(isPlay) gm.fadeEnd();
            }
        	break;
        case RESULT:
        	g2D.setColor(new Color(0,0,0,120));
            g2D.fillRect(0, 0, w, h);
        	g2D.drawImage(resultImg, 280, 50, this);
        	break;
        }
    }
}
