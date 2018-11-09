import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnRight;
	public boolean TurnLeft;
	public String KeyData;

	public void keyTyped(KeyEvent e) {// キーボードが押された時の処理(文字)
	}

	public void keyPressed(KeyEvent e) {// キーボードが押された時の処理
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キーが押されている間
			Left = true;
			KeyData = "LEFTPRESS";
			break;
		case KeyEvent.VK_RIGHT:// →キーが押されている間
			Right = true;
			KeyData = "RIGHTPRESS";
			break;
		case KeyEvent.VK_DOWN:// ↓キーが押されている間
			Down = true;
			KeyData = "DOWNPRESS";
			break;
		case KeyEvent.VK_Z:// Zキーが押されている間
			TurnLeft = true;
			KeyData = "ZPRESS";
			break;
		case KeyEvent.VK_X:// Xキーが押されている間
			TurnRight = true;
			KeyData = "XPRESS";
			break;
		case KeyEvent.VK_Q:// Qキーが押されている間
			System.exit(0);
			break;
		}
		System.out.println(KeyData);
	}

	public void keyReleased(KeyEvent e) {// キーボードから離された時の処理
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キーが押されている間
			Left = false;
			KeyData = "LEFTRELEASE";
			break;
		case KeyEvent.VK_RIGHT:// →キーが押されている間
			Right = false;
			KeyData = "RIGHTRELEASE";
			break;
		case KeyEvent.VK_DOWN:// ↓キーが押されている間
			Down = false;
			KeyData = "DOWNRELEASE";
			break;
		case KeyEvent.VK_Z:// Zキーが押されている間
			TurnLeft = true;
			KeyData = "ZRELEASE";
			break;
		case KeyEvent.VK_X:// Xキーが押されている間
			TurnRight = true;
			KeyData = "XRELEASE";
			break;
		}
		System.out.println(KeyData);
	}
}
