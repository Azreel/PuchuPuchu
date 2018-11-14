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
	private ArrayList<Integer> keyBuffer = new ArrayList<Integer>();
	//private boolean isUpdate = true;
	
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
					else while(!canStart) { nw.getRivalStatus(); }
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
					// BGM
					overlay.setBGM(getClass().getResource("gamemusic.wav"));
					// スタートアニメーション
					while(!canStart) { nw.getRivalStatus(); }
					me.draw.startReadyAnim();
					rival.draw.startReadyAnim();
				} else {
					// 相手のキー入力を取り出す
					if(nw.isConnect) nw.getRivalStatus();
					// 相手のキーを1つだけ取り出す
					if(nw.index == rivalIndex && !keyBuffer.isEmpty()) {
						setRivalKey(keyBuffer.get(0));
						keyBuffer.remove(0);
					}
					if(canStart) {
						// 状態更新
						int temp;
						// ライバル
						if(!isFinish && nw.index >= rivalIndex) {
							temp = rival.update();
							if(temp != -1) rivalIndex = temp;
						}
						// 自分
						if(!isFinish) {
							temp = me.update();
							if(temp != meIndex) {
								meIndex = temp;
								if(meIndex != -1) nw.sendPuchuIndex(meIndex);
							}
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
	public void setRivalInput(String key) {
		int keyCode;
		// キー情報の取り出し
		try {
			keyCode = Integer.parseInt(key);
			System.out.println(keyCode);
		} catch(Exception e) {
			System.out.println("不正なキーデータ: "+key);
			return;
		}
		// 全部一旦バッファに入れる
		keyBuffer.add(keyCode);
	}
	
	// ライバルのキーをセット
	private void setRivalKey(int keyCode) {
		boolean isPress;
		// 押したのか押されたのか
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
			System.out.println("不正なキーデータ: "+keyCode);
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
		// 入ってるキー情報を全て消費
		if(!keyBuffer.isEmpty()) {
			for(int code : keyBuffer) {
				setRivalKey(code);
				rival.update();
				rival.draw.repaint();
			}
			keyBuffer.clear();
		}
	}
	
	// ライバルの負けを検知
	public void finishRival() {
		while(!rival.lose_flag) {
			// 相手に高速で合わせる
			nw.getRivalStatus();
			rival.update();
			rival.draw.repaint();
		}
		isFinish = true;
		nw.Close();
	}
	
	// プログラム実行本体
	public static void main(String[] args) {
		GameMain gm = new GameMain();
		gm.start();
	}
}
