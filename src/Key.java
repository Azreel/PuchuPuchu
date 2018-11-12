import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnRight;
	public boolean TurnLeft;
	public boolean Enter;
	public int KeyData = 0; //送信用

	// キーボードが押された時の処理(文字)
	public void keyTyped(KeyEvent e) {
	}

	// キーボードが押された時の処理
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キー
			Left = true;
			KeyData = 1;
			break;
		case KeyEvent.VK_RIGHT:// →キー
			Right = true;
			KeyData = 2;
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			Down = true;
			KeyData = 3;
			break;
		case KeyEvent.VK_Z:// Zキー
			TurnLeft = true;
			KeyData = 4;
			break;
		case KeyEvent.VK_X:// Xキー
			TurnRight = true;
			KeyData = 5;
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
			KeyData = -1;
			break;
		case KeyEvent.VK_RIGHT:// →キー
			Right = false;
			KeyData = -2;
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			Down = false;
			KeyData = -3;
			break;
		case KeyEvent.VK_Z:// Zキー
			TurnLeft = false;
			KeyData = -4;
			break;
		case KeyEvent.VK_X:// Xキー
			TurnRight = false;
			KeyData = -5;
			break;
		case KeyEvent.VK_ENTER:// Enterキー
			Enter = false;
			break;
		}
	}
}
