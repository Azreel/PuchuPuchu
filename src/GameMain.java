import java.awt.*;
import java.util.Random;

import javax.swing.*;

public class GameMain extends Thread {
	final int ScreenW = 960;
	final int ScreenH = 640;
	final long MSPF = 1000 / 60; //MilliSecond Per Frame
	
	public static enum Status { GAME_TITLE, GAME_SOLO, GAME_DUO };
	public static final int PPSIZE = 200;
	public final JFrame frame = new JFrame();
	public boolean canStart = false;
	
	private Status gameStatus = Status.GAME_TITLE;
	private Network nw;
	private long loopDelay = MSPF;
	private Title title = null;
	private int[][] ppInit = new int[PPSIZE][2];
	private Field me = null;
	private Field rival = null;
	private String oldKey = "NULL";
	private boolean isFinish = false;
	
	// コンストラクタ
	GameMain(){
		MakeWindow();
	}

	// ウィンドウ生成
	private void MakeWindow() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 終了処理
		frame.setSize(ScreenW, ScreenH);
	    frame.setLocationRelativeTo(null);
		frame.setTitle("ぷちゅぷちゅ");
		frame.setResizable(false);// サイズ変更不可
		frame.setLayout(new GridLayout(1,2));
		frame.setVisible(true);
	}
	
	// スレッドのメイン
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
				} else {
					title.repaint();
				}
				break;
			case GAME_SOLO: // 1Pプレイ
				if(me == null && rival == null) {
					System.out.println("1Pモード");
					// サーバーを閉じる
					nw.Close();
					// タイトル除去
					frame.remove(title);
					title = null;
					// ぷちゅ生成
					makePuchu();
					// プレイヤーフィールド
					me = new Field(this, ppInit);
					// nullプレイヤーフィールド
					rival = new Field(this, null);
					// フレームに追加
					frame.add(me.draw);
					frame.add(rival.draw);
					frame.revalidate();
					me.draw.requestFocus();
				} else {
					// Fieldの画面描画関係
					me.update();
					me.draw.repaint();
					rival.draw.repaint();
				}
				break;
			case GAME_DUO: // 2Pプレイ
				if(me == null && rival == null) {
					System.out.println("2Pモード");
					// タイトル除去
					frame.remove(title);
					title = null;
					// ぷちゅ生成
					if(nw.programMode == Network.Mode.SERVER) nw.sentPuchu(makePuchu());
					else while(!canStart) { try { sleep(1); } catch(Exception e) {} }
					// プレイヤーフィールド
					me = new Field(this, ppInit);
					// ライバルプレイヤーフィールド
					rival = new Field(this, ppInit);
					// フレームに追加
					frame.add(me.draw);
					frame.add(rival.draw);
					frame.revalidate();
					me.draw.requestFocus();
					// ぷちゅ受信完了
					if(nw.programMode == Network.Mode.CLIENT) nw.sentStatus("START");
				} else {
					if(canStart) {
						// Fieldの画面描画関係
						me.update();
						me.draw.repaint();
						if(!oldKey.equals(me.key.KeyData) && !isFinish) { //ゲームが終了したら送信しない
							nw.sentStatus(me.key.KeyData);
							oldKey = me.key.KeyData;
						}
						rival.update();
						rival.draw.repaint();
					}
				}
				break;
			}
			loopDelay = MSPF - (System.currentTimeMillis() - start);
		}
	}
	
	// ゲーム画面の状態変更
	public void setStatus(Status next) {
		gameStatus = next;
	}
	
	// ゲーム終了の検知
	public void finishGame() {
		isFinish = true;
		if(gameStatus == Status.GAME_DUO) {
			nw.sentStatus("END");
			nw.Close();
		}
	}
	
	//初期ぷちゅペア生成
	private String[] makePuchu() {
		String[] ppList = new String[PPSIZE];
		Random rnd = new Random();
		for(int i = 0; i < PPSIZE; i++) {
			ppInit[i][0] = rnd.nextInt(6)+1;
			ppInit[i][1] = rnd.nextInt(6)+1;
			ppList[i] = Integer.toString(ppInit[i][0]) + "," + Integer.toString(ppInit[i][1]);
 		}
		return ppList;
	}
	
	// 初期ぷちゅペア生成(外部受信)
	public void makePuchuByServer(int[][] list) {
		if(list == null) {
			System.out.println("ぷちゅ生成エラー");
			System.exit(0);
		} else {
			ppInit = list;
		}
	}
	
	// クライアントの接続受け入れ
	public void rivalApply() {
		title.rivalApply();
	}
	
	// ライバルのキー入力を反映
	public void setRivalInput(String key) {
		switch(key) {
		case "":
			break;
		case "LEFTPRESS":
			rival.key.Left = true;
			break;
		case "RIGHTPRESS:":
			rival.key.Right = true;
			break;
		case "DOWNPRESS":
			rival.key.Down = true;
			break;
		case "ZPRESS":
			rival.key.TurnLeft = true;
			break;
		case "XPRESS":
			rival.key.TurnRight = true;
			break;
		case "LEFTRELEASE":
			rival.key.Left = false;
			break;
		case "RIGHTRELEASE":
			rival.key.Right = false;
			break;
		case "DOWNRELEASE":
			rival.key.Down = false;
			break;
		case "ZRELEASE":
			rival.key.TurnLeft = false;
			break;
		case "XRELEASE":
			rival.key.TurnRight = false;
			break;
		default:
			System.out.println("不正なキーデータ: "+key);
			break;
		}
	}
	
	// ライバルの負けを検知
	public void finishRival() {
		isFinish = true;
	}
	
	// プログラム実行本体
	public static void main(String[] args) {
		GameMain gm = new GameMain();
		gm.start();
	}
}
