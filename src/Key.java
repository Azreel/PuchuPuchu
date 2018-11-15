import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnLeft;
	public boolean TurnRight;
	public boolean Enter;
	
	private GameMain gm;
	private long leftTime;
	private long rightTime;
	private long downTime;
	private long turnLeftTime;
	private long turnRightTime;

	Key(GameMain _gm){
		gm = _gm;
	}
	
	// キーボードが押された時の処理(文字)
	public void keyTyped(KeyEvent e) {
	}

	// キーボードが押された時の処理(連続で入力あり)
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キー
			if(!Left) {
				Left = true;
				gm.nw.sendStatus(1 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_RIGHT:// →キー
			if(!Right) {
				Right = true;
				gm.nw.sendStatus(2 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			if(!Down) {
				Down = true;
				gm.nw.sendStatus(3 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_Z:// Zキー
			if(!TurnLeft) {
				TurnLeft = true;
				gm.nw.sendStatus(4 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_X:// Xキー
			if(!TurnRight) {
				TurnRight = true;
				gm.nw.sendStatus(5 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_Q:// Qキー
			System.exit(0);
			break;
		case KeyEvent.VK_ENTER:// Enterキー
			Enter = true;
			break;
		}
	}

	// キーボードから離された時の処理
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キー
			if(Left) {
				Left = false;
				gm.nw.sendStatus(-1 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_RIGHT:// →キー
			if(Right) {
				Right = false;
				gm.nw.sendStatus(-2 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			if(Down) {
				Down = false;
				gm.nw.sendStatus(-3 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_Z:// Zキー
			if(TurnLeft) {
				TurnLeft = false;
				gm.nw.sendStatus(-4 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_X:// Xキー
			if(TurnRight) {
				TurnRight = false;
				gm.nw.sendStatus(-5 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_ENTER:// Enterキー
			Enter = false;
			break;
		}
	}
	
}
