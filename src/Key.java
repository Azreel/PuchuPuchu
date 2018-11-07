import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnRight;
	public boolean TurnLeft;
	
	GameMain gm;

	Key(GameMain parent) {
		gm = parent;
	}

	public void keyTyped(KeyEvent e) {// キーボードが押された時の処理(文字)
	}

	public void keyPressed(KeyEvent e) {// キーボードが押された時の処理
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キーが押されている間
			Left = true;
			break;
		case KeyEvent.VK_RIGHT:// →キーが押されている間
			Right = true;
			break;
		case KeyEvent.VK_DOWN:// ↓キーが押されている間
			Down = true;
			break;
		case KeyEvent.VK_Z:// Zキーが押されている間
			TurnLeft = true;
			break;
		case KeyEvent.VK_X:// Xキーが押されている間
			TurnRight = true;
			break;
		case KeyEvent.VK_Q:// Qキーが押されている間
			System.exit(0);
			break;
		}

	}

	public void keyReleased(KeyEvent e) {// キーボードから離された時の処理
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キーが押されている間
			Left = false;
			break;
		case KeyEvent.VK_RIGHT:// →キーが押されている間
			Right = false;
			break;
		case KeyEvent.VK_DOWN:// ↓キーが押されている間
			Down = false;
			break;
		case KeyEvent.VK_Z:// Zキーが押されている間
			TurnLeft = false;
			break;
		case KeyEvent.VK_X:// Xキーが押されている間
			TurnRight = false;
			break;
		}
	}
}
