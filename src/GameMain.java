import java.awt.*;
import javax.swing.*;

public class GameMain extends Thread {
	final int ScreenW = 960;
	final int ScreenH = 640;
	
	public static enum Status { GAME_TITLE, GAME_SOLO, GAME_DUO };
	public JFrame frame = new JFrame();
	
	private Status gameStatus = Status.GAME_TITLE;
	private Network nw;
	private long loopDelay = 16;
	private Field me = null;
	private Field rival = null;
	private Title title = null;
	
	GameMain(){
		MakeWindow();
		nw = new Network(this);
		nw.start();
	}
	
	private void MakeWindow() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 終了処理
		frame.setSize(ScreenW, ScreenH);
	    frame.setLocationRelativeTo(null);
		frame.setTitle("ぷちゅぷちゅ");
		frame.setResizable(false);// サイズ変更不可
		frame.setLayout(new GridLayout(1,2));
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
			case GAME_TITLE: // タイトル
				if(title == null) {
					System.out.println("タイトル生成");
					me = null;
					rival = null;
					title = new Title(this, nw);
					title.setPreferredSize(new Dimension(ScreenW, ScreenH));
					frame.add(title);
					frame.pack();
				}
				break;
			case GAME_SOLO: // 1Pプレイ
				if(me == null) {
					System.out.println("1Pモード");
					frame.remove(title);
					title = null;
					me = new Field();
					//me.setPreferredSize(new Dimension(ScreenW/2, ScreenH));
				} else {
					// Fieldの画面描画関係
				}
				break;
			case GAME_DUO: // 2Pプレイ
				if(me == null && rival == null) {
					System.out.println("2Pモード");
					frame.remove(title);
					title = null;
					me = new Field();
					rival = new Field();
					//me.setPreferredSize(new Dimension(ScreenW/2, ScreenH));
					//rival.setPreferredSize(new Dimension(ScreenW/2, ScreenH));
				} else {
					// Fieldの画面描画関係
				}
				break;
			default: // エラー
				System.out.println("予期せぬゲームステータス");
				break;
			}
			loopDelay = 16 - (System.currentTimeMillis() - start);
		}
	}
	
	public void setStatus(Status next) {
		gameStatus = next;
	}
	
	public void rivalApply() {
		title.rivalApply();
	}
	
	public static void main(String[] args) {
		GameMain gm = new GameMain();
		gm.start();
	}
}
