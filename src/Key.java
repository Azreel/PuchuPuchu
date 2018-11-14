import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnLeft;
	public boolean TurnRight;
	public boolean Enter;
	
	private Network nw;
	private long leftTime;
	private long rightTime;
	private long downTime;
	private long turnLeftTime;
	private long turnRightTime;

	Key(Network _nw){
		nw = _nw;
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
				leftTime = System.currentTimeMillis();
			}
			break;
		case KeyEvent.VK_RIGHT:// →キー
			if(!Right) {
				Right = true;
				rightTime = System.currentTimeMillis();
			}
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			if(!Down) {
				Down = true;
				downTime = System.currentTimeMillis();
			}
			break;
		case KeyEvent.VK_Z:// Zキー
			if(!TurnLeft) {
				TurnLeft = true;
				turnLeftTime = System.currentTimeMillis();
			}
			break;
		case KeyEvent.VK_X:// Xキー
			if(!TurnRight) {
				TurnRight = true;
				turnRightTime = System.currentTimeMillis();
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
				leftTime = System.currentTimeMillis() - leftTime;
				sendKeyData(1, leftTime);
			}
			break;
		case KeyEvent.VK_RIGHT:// →キー
			if(Right) {
				Right = false;
				rightTime = System.currentTimeMillis() - rightTime;
				sendKeyData(2, rightTime);
			}
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			if(Down) {
				Down = false;
				downTime = System.currentTimeMillis() - downTime;
				sendKeyData(3, downTime);
			}
			break;
		case KeyEvent.VK_Z:// Zキー
			if(TurnLeft) {
				TurnLeft = false;
				turnLeftTime = System.currentTimeMillis() - turnLeftTime;
				sendKeyData(4, turnLeftTime);
			}
			break;
		case KeyEvent.VK_X:// Xキー
			if(TurnRight) {
				TurnRight = false;
				turnRightTime = System.currentTimeMillis() - turnRightTime;
				sendKeyData(5, turnRightTime);
			}
			break;
		case KeyEvent.VK_ENTER:// Enterキー
			Enter = false;
			break;
		}
	}
	
	private void sendKeyData(int key, long time) {
		nw.sendStatus(key + ":" + time);
	}
}
