import javax.swing.*;
import java.awt.*;

public class Overlay extends JPanel{
	final int fadeSpeed = 60;
	static enum Mode {STOP, FADEIN, FADEOUT};
	Mode paintMode = Mode.STOP;
	float fadeAlpha;
	GameMain gm;
	
	Overlay(GameMain parent){
		gm = parent;
		this.setOpaque(false);
	}
	
	public void FadeIn() {
		paintMode = Mode.FADEIN;
		fadeAlpha = 0.0f;
	}
	
	public void FadeOut() {
		paintMode = Mode.FADEOUT;
		fadeAlpha = 1.0f;
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
            if(fadeAlpha >= 1.0f) {
            	paintMode = Mode.STOP;
            	gm.fadeEnd();
            }
        	break;
        case FADEOUT:
        	g2D.setColor(new Color(0,0,0,(int)((fadeAlpha >= 0.0f ? fadeAlpha : 0.0f)*255)));
            g2D.fillRect(0, 0, w, h);
            fadeAlpha -= 1.0f / fadeSpeed;
            if(fadeAlpha <= 0.0f) {
            	paintMode = Mode.STOP;
            	gm.fadeEnd();
            }
        	break;
        }
    }
}
