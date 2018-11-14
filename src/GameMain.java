import java.awt.*;
import java.net.URL;
import java.util.Random;

import javax.swing.*;

public class GameMain extends Thread {
	final int ScreenW = 960;
	final int ScreenH = 640;
	final long MSPF = 1000 / 60; //MilliSecond Per Frame
	
	public static enum Status { GAME_TITLE, GAME_SOLO, GAME_DUO };
	public Network nw;
	public static final int PPSIZE = 200;
	public final JFrame frame = new JFrame();
	public boolean canStart = false;
	public int rivalIndex = 0;
	
	private Status gameStatus = Status.GAME_TITLE;
	private Status nextStatus;
	private long loopDelay = MSPF;
	private Overlay overlay;
	private boolean isOverlay = false;
	private Title title = null;
	private int[][] ppInit = new int[PPSIZE][2];
	private Field me = null;
	private Field rival = null;
	private int meIndex = 0;
	private boolean isFinish = false;
	private boolean isUpdate = true;
	
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
		frame.setLayout(null);
		frame.setVisible(true);
		overlay = new Overlay(this);
		overlay.setPreferredSize(new Dimension(ScreenW, ScreenH));
		overlay.setBounds(0, 0, ScreenW, ScreenH);
	}
	
	// スレッドのメイン
	public void run() {
		boolean isPaint = true;
		
		while(true) {
			// 60fps保つ
			try {
				if(loopDelay > 0) sleep(loopDelay);
			}catch(Exception e) {}
			isPaint = !isOverlay;
			// ゲーム全体の管理
			long start = System.currentTimeMillis();
			switch(gameStatus) {
			case GAME_TITLE: // タイトル
				if(title == null) {
					System.out.println("タイトル生成");
					// 前のプレイデータを消去
					if(me != null || rival != null) {
						frame.remove(me.draw);
						frame.remove(rival.draw);
						me = null;
						rival = null;
					}
					// ネットワーク開始
					nw = new Network(this);
					nw.start();
					// タイトルパネル追加
					title = new Title(this, nw);
					title.setPreferredSize(new Dimension(ScreenW, ScreenH));
					title.setBounds(0, 0, ScreenW, ScreenH);
					frame.add(overlay);
					frame.add(title);
					frame.revalidate();
					//BGM
					overlay.setBGM(getClass().getResource("Title.wav"));
					//setBGM(getClass().getResource("Title2.wav"));
				} else {
					if(isPaint) title.repaint();
					overlay.repaint();
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
					me.draw.setBounds(0, 0, ScreenW/2, ScreenH);
					// nullプレイヤーフィールド
					rival = new Field(this, null);
					rival.draw.setBounds(ScreenW/2, 0, ScreenW/2, ScreenH);
					// フレームに追加
					frame.add(me.draw);
					frame.add(rival.draw);
					frame.revalidate();
					me.draw.requestFocus();
					isPaint = false;
					//BGM
					overlay.setBGM(getClass().getResource("gamemusic.wav"));
				} else {
					// Fieldの画面描画関係
					me.update();
					if(isPaint) {
						me.draw.repaint();
						rival.draw.repaint();
					} else {
						overlay.repaint();
					}
				}
				break;
			case GAME_DUO: // 2Pプレイ
				if(me == null && rival == null) {
					System.out.println("2Pモード");
					// タイトル除去
					frame.remove(title);
					title = null;
					// ぷちゅ生成
					if(nw.programMode == Network.Mode.SERVER) nw.sendPuchu(makePuchu());
					else while(!canStart) { try { sleep(1); } catch(Exception e) {} }
					// プレイヤーフィールド
					me = new Field(this, ppInit);
					me.draw.setBounds(0, 0, ScreenW/2, ScreenH);
					// ライバルプレイヤーフィールド
					rival = new Field(this, ppInit);
					rival.draw.setBounds(ScreenW/2, 0, ScreenW/2, ScreenH);
					// フレームに追加
					frame.add(me.draw);
					frame.add(rival.draw);
					frame.revalidate();
					me.draw.requestFocus();
					// ぷちゅ受信完了
					if(nw.programMode == Network.Mode.CLIENT) nw.sendStatus("START");
					//BGM
					overlay.setBGM(getClass().getResource("gamemusic.wav"));
				} else {
					if(canStart) {
						int temp = -1;
						// Fieldの画面描画関係
						temp = me.update();
						if(temp != -1) {
							meIndex = temp;
							nw.sendPuchuIndex(meIndex);
						}
						if(isUpdate) {
							temp = rival.update();
							if(temp != -1) rivalIndex = temp;
						} else {
							isUpdate = true;
						}
						//if(isFinish) nw.Close();
						if(isPaint) {
							me.draw.repaint();
							rival.draw.repaint();
						} else {
							overlay.repaint();
						}
					}
				}
				break;
			}
			nw.flushBuffer();
			loopDelay = MSPF - (System.currentTimeMillis() - start);
		}
	}
	
	// ゲーム画面の状態変更
	public void setStatus(Status next) {
		gameStatus = next;
	}
	
	// フェードイン
	public void fadeIn(Status next) {
		nextStatus = next;
		overlay.FadeIn();
		isOverlay = true;
	}
	
	// フェードアウト
	public void fadeOut() {
		nextStatus = gameStatus;
		overlay.FadeOut();
		isOverlay = true;
	}
	
	// フェード系終了
	public void fadeEnd() {
		gameStatus = nextStatus;
		isOverlay = false;
	}
	
	// ゲーム終了の検知
	public void finishGame() {
		isFinish = true;
		if(gameStatus == Status.GAME_DUO) {
			nw.sendStatus("END");
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
		int keyCode;
		boolean isPress;
		try {
			keyCode = Integer.parseInt(key);
		} catch(Exception e) {
			System.out.println("不正なキーデータ: "+key);
			return;
		}
		
		if(keyCode > 0) {
			isPress = true;
		} else {
			isPress = false;
			keyCode *= -1;
		}
		
		switch(keyCode) {
		case 1:
			rival.key.Left = isPress;
			break;
		case 2:
			rival.key.Right = isPress;
			break;
		case 3:
			rival.key.Down = isPress;
			break;
		case 4:
			rival.key.TurnLeft = isPress;
			break;
		case 5:
			rival.key.TurnRight = isPress;
			break;
		default:
			System.out.println("不正なキーデータ: "+key);
			break;
		}
		
		int temp = rival.update();
		if(temp != -1) rivalIndex = temp;
		isUpdate = false;
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
