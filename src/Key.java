import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {

	public boolean Left;
	public boolean Right;
	public boolean Down;
	public boolean TurnLeft;
	public boolean TurnRight;
	public boolean canKeyInput = false;
	
	private GameMain gm;

	Key(GameMain _gm){
		gm = _gm;
	}
	
	// キーボードが押された時の処理(文字)
	@Override
	public void keyTyped(KeyEvent e) {
	}

	// キーボードが押された時の処理(連続で入力あり)
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キー
			if(!Left && canKeyInput) {
				Left = true;
				gm.nw.sendStatus(1 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_RIGHT:// →キー
			if(!Right && canKeyInput) {
				Right = true;
				gm.nw.sendStatus(2 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			if(!Down && canKeyInput) {
				Down = true;
				gm.nw.sendStatus(3 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_Z:// Zキー
			if(!TurnLeft && canKeyInput) {
				TurnLeft = true;
				gm.nw.sendStatus(4 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_X:// Xキー
			if(!TurnRight && canKeyInput) {
				TurnRight = true;
				gm.nw.sendStatus(5 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_Q:// Qキー
			System.exit(0);
			break;
		}
	}

	// キーボードから離された時の処理
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:// ←キー
			if(Left && canKeyInput) {
				Left = false;
				gm.nw.sendStatus(-1 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_RIGHT:// →キー
			if(Right && canKeyInput) {
				Right = false;
				gm.nw.sendStatus(-2 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_DOWN:// ↓キー
			if(Down && canKeyInput) {
				Down = false;
				gm.nw.sendStatus(-3 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_Z:// Zキー
			if(TurnLeft && canKeyInput) {
				TurnLeft = false;
				gm.nw.sendStatus(-4 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_X:// Xキー
			if(TurnRight && canKeyInput) {
				TurnRight = false;
				gm.nw.sendStatus(-5 + ":" + gm.frameCount);
			}
			break;
		case KeyEvent.VK_P:// Pキー
			gm.switchPause();
			break;
		}
	}
}
