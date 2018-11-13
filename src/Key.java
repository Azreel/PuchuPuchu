import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnRight;
	public boolean TurnLeft;
	public boolean Enter;
	
	private int KeyData = 0; //送信用
	private int oldKey = 0;
	private Network nw;

	Key(Network parent){
		nw = parent;
	}
	
	// キーボードが押された時の処理(文字)
	public void keyTyped(KeyEvent e) {
	}

	// キーボードが押された時の処理
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キー
			Left = true;
			sendKeyData(1);
			break;
		case KeyEvent.VK_RIGHT:// →キー
			Right = true;
			sendKeyData(2);
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			Down = true;
			sendKeyData(3);
			break;
		case KeyEvent.VK_Z:// Zキー
			TurnLeft = true;
			sendKeyData(4);
			break;
		case KeyEvent.VK_X:// Xキー
			TurnRight = true;
			sendKeyData(5);
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
			Left = false;
			sendKeyData(-1);
			break;
		case KeyEvent.VK_RIGHT:// →キー
			Right = false;
			sendKeyData(-2);
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			Down = false;
			sendKeyData(-3);
			break;
		case KeyEvent.VK_Z:// Zキー
			TurnLeft = false;
			sendKeyData(-4);
			break;
		case KeyEvent.VK_X:// Xキー
			TurnRight = false;
			sendKeyData(-5);
			break;
		case KeyEvent.VK_ENTER:// Enterキー
			Enter = false;
			break;
		}
	}
	
	private void sendKeyData(int key) {
		nw.sendStatus(Integer.toString(key));
		if(key != oldKey) {
			
			oldKey = key;
		}
	}
}
