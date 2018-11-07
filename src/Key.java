import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	static public boolean Left;
	static public boolean Right;
	static public boolean Down;
	static public boolean TurnRight;
	static public boolean TurnLeft;
	static public boolean Up;
	static public int GameStage;

	Key() {
		System.out.println("keyconst");
	}

	public void keyTyped(KeyEvent e) {// キーボードが押された時の処理(文字)
	}

	public void keyPressed(KeyEvent e) {// キーボードが押された時の処理

		switch (e.getKeyCode()) {
		case 37:// ←キーが押されている間
			Left = true;
			break;
		case 39:// →キーが押されている間
			Right = true;
			break;
		case 38:// ↑キーが押されている間
			TurnLeft = true;
			break;
		case 40:// ↓キーが押されている間
			TurnRight = true;
			break;
		case 32:// spaceキーが押されている間
			Down = true;
			break;
		case 1:
			Up = true;
			break;
		case KeyEvent.VK_ENTER:
			if (GameStage == 0 || GameStage == 3)
				GameStage = 1;
			if (GameStage == 2)
				System.exit(0);
			break;
		case 16:// shiftキーが押されている間
			if (GameStage == 1)
				GameStage = 3;
			if (GameStage == 4)
				GameStage = 1;
			break;
		case 81:// Qキーが押されている間
			GameStage = 2;
			break;
		}

	}

	public void keyReleased(KeyEvent e) {// キーボードから離された時の処理

		switch (e.getKeyCode()) {
		case 37:// ←キーが離されたとき
			Left = false;
			break;
		case 39:// →キーが離されたとき
			Right = false;
			break;
		case 38:// ↑キーが離されたとき
			TurnLeft = false;
			break;
		case 40:// ↓キーが離されたとき
			TurnRight = false;
			break;
		case 32:// spaceキーが離されたとき
			Down = false;
			break;
		case 16:
			if (GameStage == 3)
				GameStage = 4;
			break;
		}
	}
}
