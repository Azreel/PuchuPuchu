import java.awt.*;
import java.util.ArrayList;
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
	public int rivalIndex = 1;
	
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
	private int nowKey = -1;
	private long nowKeyTime = -1;
	private boolean isFirst = true;
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
						canStart = false;
						isFirst = true;
						nw.Close();
					}
					isFinish = false;
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
					//overlay.setBGM(getClass().getResource("Title.wav"));
					overlay.setBGM(getClass().getResource("Title2.wav"));
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
					// BGM
					overlay.setBGM(getClass().getResource("gamemusic.wav"));
					// スタートアニメーション
					me.draw.startReadyAnim();
				} else {
					// 状態更新
					//if(!isFinish) me.update();
					int temp = -1;
					if(!isFinish) temp = me.update();
					if(temp != meIndex) {
						meIndex = temp;
						if(meIndex != -1) System.out.println(meIndex);
					}
					// 画面描画
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
					else while(!canStart) { nw.getRivalStatus(false); }
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
					// 準備完了
					nw.sendStatus("START");
					while(!canStart) { nw.getRivalStatus(false); }
					// BGM
					overlay.setBGM(getClass().getResource("gamemusic.wav"));
					// スタートアニメーション
					me.draw.startReadyAnim();
					rival.draw.startReadyAnim();
				} else {
					if(nowKey != -1 || nw.index == rivalIndex) {
						isUpdate = true;
						if(nowKeyTime > MSPF) {
							if(loopDelay > 0) nowKeyTime -= loopDelay;
							setRivalInput(nowKey, true);
						} else {
							if(nowKeyTime > 0) try { sleep(nowKeyTime); } catch(Exception e) {}
							if(nowKey != -1) setRivalInput(nowKey, false);
							nowKey = -1;
							nowKeyTime = -1;
							if(nw.isConnect) nw.getRivalStatus(true);
						}
					}else if(nw.index < rivalIndex) {
						// なぜか自分のほうが早くなった場合
						System.out.println("ずれ");
						isUpdate = false;
						if(nw.isConnect) nw.getRivalStatus(false); // キー情報を捨ててしまう
					}
					if(canStart) {
						// 状態更新
						int temp;
						if(!isFinish && !me.lose_flag) {
							// 自分
							temp = me.update();
							if(temp != meIndex) {
								if(meIndex == -1 && temp != -1) nw.sendField(me.cell); //次のぷちゅが降り始めたらフィールド全体を送信
								meIndex = temp;
								if(meIndex != -1) nw.sendPuchuIndex(meIndex);
							}
						}
						if(!isFinish && !rival.lose_flag && isUpdate) {
							// ライバル
							temp = rival.update();
							if(temp != -1) rivalIndex = temp;
						}
						// 画面描画
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
			loopDelay = MSPF - (System.currentTimeMillis() - start);
		}
	}
	
	// フェードイン
	public void fadeIn(Status next) {
		nextStatus = next;
		isOverlay = true;
		overlay.FadeIn();
	}
	
	// フェードアウト(点滅します)
	public void fadeOut() {
		nextStatus = gameStatus;
		isOverlay = true;
		overlay.FadeOut();
	}
	
	// フェード系終了
	public void fadeEnd() {
		gameStatus = nextStatus;
		isOverlay = false;
	}
	
	// リザルトの表示
	public void resultDisp(int score) {
		isOverlay = true;
		overlay.Result(score);
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
	public void getRivalInput(String key) {
		String[] keyData = key.split(":");

		// キー情報の取り出し
		try {
			nowKey = Integer.parseInt(keyData[0]);
			nowKeyTime = Long.parseLong(keyData[1]);
			//System.out.println(key);
		} catch(Exception e) {
			System.out.println("不正なキーデータ: "+key);
			return;
		}
	}
	
	// ライバルのキーをセット
	private void setRivalInput(int key, boolean isPress) {
		switch(key) {
		case 1: // ←キー
			rival.key.Left = isPress;
			break;
		case 2: // →キー
			rival.key.Right = isPress;
			break;
		case 3: // ↓キー
			rival.key.Down = isPress;
			break;
		case 4: // Zキー
			rival.key.TurnLeft = isPress;
			break;
		case 5: // Xキー
			rival.key.TurnRight = isPress;
			break;
		default:
			System.out.println("不正なキーデータ: "+key);
			break;
		}
	}
	
	// ライバルのキーを一旦リセット
	public void resetRivalInput() {
		rival.key.Left = false;
		rival.key.Right = false;
		rival.key.Down = false;
		rival.key.TurnLeft = false;
		rival.key.TurnRight = false;
		nowKey = -1;
		nowKeyTime = -1;
	}
	
	// ライバルのフィールドを同期
	public void setRivalField(int[][] type) {
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 14; j++) {
				rival.cell[i][j].type = type[i][j];
			}
		}
	}
	
	// ライバルの負けを検知
	public void finishRival() {
		isFinish = true;
		nw.Close();
	}
	
	// プログラム実行本体
	public static void main(String[] args) {
		GameMain gm = new GameMain();
		gm.start();
	}
}
