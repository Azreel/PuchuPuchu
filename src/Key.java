import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnRight;
	public boolean TurnLeft;
	public String KeyData;

	// キーボードが押された時の処理(文字)
	public void keyTyped(KeyEvent e) {
	}

	// キーボードが押された時の処理
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キーが押されている
			Left = true;
			KeyData = "LEFTPRESS";
			break;
		case KeyEvent.VK_RIGHT:// →キーが押されている
			Right = true;
			KeyData = "RIGHTPRESS";
			break;
		case KeyEvent.VK_DOWN:// ↓キーが押されている
			Down = true;
			KeyData = "DOWNPRESS";
			break;
		case KeyEvent.VK_Z:// Zキーが押されている
			TurnLeft = true;
			KeyData = "ZPRESS";
			break;
		case KeyEvent.VK_X:// Xキーが押されている
			TurnRight = true;
			KeyData = "XPRESS";
			break;
		case KeyEvent.VK_Q:// Qキーが押されている
			System.exit(0);
			break;
		}
		System.out.println(KeyData);
	}

	// キーボードから離された時の処理
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キーが押されている
			Left = false;
			KeyData = "LEFTRELEASE";
			break;
		case KeyEvent.VK_RIGHT:// →キーが押されている
			Right = false;
			KeyData = "RIGHTRELEASE";
			break;
		case KeyEvent.VK_DOWN:// ↓キーが押されている
			Down = false;
			KeyData = "DOWNRELEASE";
			break;
		case KeyEvent.VK_Z:// Zキーが押されている
			TurnLeft = true;
			KeyData = "ZRELEASE";
			break;
		case KeyEvent.VK_X:// Xキーが押されている
			TurnRight = true;
			KeyData = "XRELEASE";
			break;
		}
		System.out.println(KeyData);
	}
}
