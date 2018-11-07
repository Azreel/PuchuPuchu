import java.awt.*;
import javax.swing.*;

public class GameMain extends Thread {
	final int ScreenW = 960;
	final int ScreenH = 640;
	final long FPS = 1000 / 60;
	
	public static enum Status { GAME_TITLE, GAME_SOLO, GAME_DUO };
	public JFrame frame = new JFrame();
	
	private Status gameStatus = Status.GAME_TITLE;
	private Network nw;
	private long loopDelay = FPS;
	private Key meKey = null;
	private Key rivalKey = null;
	private Title title = null;
	private Field me = null;
	private Field rival = null;
	private Draw meDraw = null;
	private Draw rivalDraw = null;
	
	GameMain(){
		MakeWindow();
	}
	
	private void MakeWindow() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 終了処理
		frame.setSize(ScreenW, ScreenH);
	    frame.setLocationRelativeTo(null);
		frame.setTitle("ぷちゅぷちゅ");
		frame.setResizable(false);// サイズ変更不可
		frame.setLayout(new GridLayout(1,2));
		frame.setVisible(true);
		meKey = new Key(this);
		frame.addKeyListener(meKey);
	}
	
	public void run() {
		while(true) {
			// 60fps保つ
			try {
				if(loopDelay > 0) sleep(loopDelay);
			}catch(Exception e) {}
			// ゲーム全体の管理
			long start = System.currentTimeMillis();
			switch(gameStatus) {
			case GAME_TITLE: // タイトル
				if(title == null) {
					System.out.println("タイトル生成");
					// 前のプレイデータを消去
					me = null;
					rival = null;
					// ネットワーク開始
					nw = new Network(this);
					nw.start();
					// タイトルパネル追加
					title = new Title(this, nw);
					title.setPreferredSize(new Dimension(ScreenW, ScreenH));
					frame.add(title);
					frame.revalidate();
					frame.requestFocus();
				}
				break;
			case GAME_SOLO: // 1Pプレイ
				if(me == null && rival == null) {
					System.out.println("1Pモード");
					// タイトル除去
					frame.remove(title);
					title = null;
					// プレイヤーフィールド
					//me = new Field(meKey);
					me = new Field();
					meDraw = new Draw(me);
					meDraw.setPreferredSize(new Dimension(ScreenW/2, ScreenH));
					// nullプレイヤーフィールド
					rivalDraw = new Draw();
					rivalDraw.setPreferredSize(new Dimension(ScreenW/2, ScreenH));
					// フレームに追加
					frame.add(meDraw);
					frame.add(rivalDraw);
					frame.revalidate();
					frame.requestFocus();
				} else {
					// Fieldの画面描画関係
					meDraw.repaint();
				}
				break;
			case GAME_DUO: // 2Pプレイ
				if(me == null && rival == null) {
					System.out.println("2Pモード");
//					// タイトル除去
//					frame.remove(title);
//					title = null;
//					// プレイヤーフィールド
//					me = new Field(meKey);
//					meDraw = new Draw(me);
//					meDraw.setPreferredSize(new Dimension(ScreenW/2, ScreenH));
//					// ライバルプレイヤーフィールド
//					rivalKey = new Key(null);
//					rival = new Field(rivalKey);
//					rivalDraw = new Draw(rival);
//					rivalDraw.setPreferredSize(new Dimension(ScreenW/2, ScreenH));
//					// フレームに追加
//					frame.add(meDraw);
//					frame.add(rivalDraw);
//					frame.revalidate();
//					frame.requestFocus();
				} else {
					// Fieldの画面描画関係
					nw.sentMyInput("Left");
					String rival = nw.getRivalInput();
					if(rival.equals("END")) {
						JLabel label = new JLabel("勝ち");
					    JOptionPane.showMessageDialog(frame, label);
					    nw = null;
					    setStatus(GameMain.Status.GAME_TITLE);
					}else {
						System.out.println(nw.getRivalInput());
					}
				}
				break;
			}
			loopDelay = FPS - (System.currentTimeMillis() - start);
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
