import java.awt.*;
import javax.swing.*;

public class GameMain extends Thread {
	final int ScreenW = 960;
	final int ScreenH = 640;
	
	public int gameStatus = 0;
	
	private JFrame frame = new JFrame();
	private long loopDelay = 16;
	private Field me = null;
	private Field rival = null;
	private Title title = null;
	
	public GameMain(){
		MakeWindow();
	}
	
	private void MakeWindow() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 終了処理
		frame.setSize(ScreenW, ScreenH);
	    frame.setLocationRelativeTo(null);
		frame.setTitle("ぷちゅぷちゅ");
		frame.setResizable(false);// サイズ変更不可
		frame.setLayout(new GridLayout());
		frame.setVisible(true);
	}
	
	public void run() {
		while(true) {
			// 60fps保つ
			try {
				if(loopDelay > 0) {
					sleep(loopDelay);
				}
			}catch(Exception e) {}
			// ゲーム全体の管理
			long start = System.currentTimeMillis();
			switch(gameStatus) {
			case 0: // タイトル
				if(title == null) {
					System.out.println("タイトル生成");
					me = null;
					rival = null;
					title = new Title(this);
					title.setPreferredSize(new Dimension(ScreenW, ScreenH));
					frame.add(title);
					frame.pack();
				}
				break;
			case 1: // 1Pプレイ
				if(me == null) {
					System.out.println("1Pモード");
					frame.remove(title);
					title = null;
					me = new Field();
				}
				break;
			case 2: // 2Pプレイ
				if(me == null && rival == null) {
					System.out.println("1Pモード");
					frame.remove(title);
					title = null;
					me = new Field();
					rival = new Field();
				}
				break;
			default: // エラー
				System.out.println("予期せぬゲームステータス");
				break;
			}
			loopDelay = 16 - (System.currentTimeMillis() - start);
		}
	}
	
	public static void main(String[] args) {
		GameMain gm = new GameMain();
		gm.start();
	}
}
