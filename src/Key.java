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
		case KeyEvent.VK_LEFT:// ←キー
			Left = true;
			setKeyData("LEFTPRESS");
			break;
		case KeyEvent.VK_RIGHT:// →キー
			Right = true;
			setKeyData("RIGHTPRESS");
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			Down = true;
			setKeyData("DOWNPRESS");
			break;
		case KeyEvent.VK_Z:// Zキー
			TurnLeft = true;
			setKeyData("ZPRESS");
			break;
		case KeyEvent.VK_X:// Xキー
			TurnRight = true;
			setKeyData("XPRESS");
			break;
		case KeyEvent.VK_Q:// Qキー
			System.exit(0);
			break;
		}
	}

	// キーボードから離された時の処理
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キー
			Left = false;
			setKeyData("LEFTRELEASE");
			break;
		case KeyEvent.VK_RIGHT:// →キー
			Right = false;
			setKeyData("RIGHTRELEASE");
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			Down = false;
			setKeyData("DOWNRELEASE");
			break;
		case KeyEvent.VK_Z:// Zキー
			TurnLeft = true;
			setKeyData("ZRELEASE");
			break;
		case KeyEvent.VK_X:// Xキー
			TurnRight = true;
			setKeyData("XRELEASE");
			break;
		}
	}
	
	// 送信用キー情報のセット
	private void setKeyData(String key) {
		if(!KeyData.equals(key)) KeyData = key;
		System.out.println(KeyData);
	}
}
