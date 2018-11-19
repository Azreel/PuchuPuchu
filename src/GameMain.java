import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class GameMain extends Thread {	
	public static enum Status { GAME_TITLE, GAME_SOLO, GAME_DUO };
	public static final int PPSIZE = 200;
	public final JFrame frame = new JFrame();
	public final Font dialogFont = new Font(Font.DIALOG, Font.PLAIN, 16);
	public Network nw;
	public String rivalIP = "0.0.0.0";
	public boolean canStart = false;
	public int rivalIndex = 1;
	public long frameCount;
	public int[][] nextRivalField;
	public int score, fallenObs, unfallenObs;
	
	private final int ScreenW = 960;
	private final int ScreenH = 640;
	private final long MSPF = 1000 / 60; //MilliSecond Per Frame
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
	private boolean isMeFinish = false;
	private boolean isRivalFinish = false;
	private int nowKey = 0;
	private long nowKeyTime = -1;
	private boolean isUpdate = true;
	private boolean haveField = false;
	private int stopCount;
	private boolean isPause = false;
	private Sound pauseSE;
	
	// コンストラクタ
	GameMain(){
		// ウィンドウの初期化
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 終了処理
		frame.setSize(ScreenW, ScreenH);
	    frame.setLocationRelativeTo(null);
		frame.setTitle("ぷちゅぷちゅ");
		frame.setResizable(false);// サイズ変更不可
		frame.setLayout(null);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setVisible(true);
		// 一番上のパネル
		overlay = new Overlay(this);
		overlay.setPreferredSize(new Dimension(ScreenW, ScreenH));
		overlay.setBounds(0, 0, ScreenW, ScreenH);
		frame.add(overlay);
	}
	
	// スレッドのメイン
	@Override
	public void run() {
		while(true) {
			// 60fpsを保つ
			try {
				if(loopDelay > MSPF) sleep(MSPF);
				else if(loopDelay > 0) sleep(loopDelay);
			}catch(Exception e) {}
			// ゲーム全体の管理
			long start = System.currentTimeMillis();
			switch(gameStatus) {
			case GAME_TITLE: // タイトル
				if(title == null) {
					initTitle();
				} else {
					updateTitle();
				}
				break;
			case GAME_SOLO: // 1Pプレイ
				if(me == null && rival == null) {
					initSolo();
				} else {
					updateSolo();
				}
				break;
			case GAME_DUO: // 2Pプレイ
				if(me == null && rival == null) {
					initDuo();
				} else {
					updateDuo();
				}
				break;
			}
			loopDelay = MSPF - (System.currentTimeMillis() - start);
		}
	}
	
	//---メインループ関係---
	// タイトルの初期化処理
	private void initTitle() {
		//System.out.println("タイトル生成");
		// 前のプレイデータを消去
		if(me != null || rival != null) {
			frame.remove(me.draw);
			frame.remove(rival.draw);
			me = null;
			rival = null;
			canStart = false;
			rivalIndex = 1;
			meIndex = 0;
			isMeFinish = false;
			isRivalFinish = false;
			nowKey = 0;
			nowKeyTime = -1;
			isUpdate = true;
			haveField = false;
			nw.Close();
			nw = null;
		}
		// ネットワーク開始
		nw = new Network(this);
		nw.start();
		// タイトルパネル追加
		title = new Title(this, nw);
		title.setPreferredSize(new Dimension(ScreenW, ScreenH));
		title.setBounds(0, 0, ScreenW, ScreenH);
		frame.add(title);
		frame.revalidate();
		//BGM
		overlay.setBGM(getClass().getResource("Title2.wav"));
	}
	
	// タイトルの更新処理
	private void updateTitle() {
		if(!isOverlay) title.repaint();
		else overlay.repaint();
	}
	
	// ソロプレイ時の初期化処理
	private void initSolo() {
		//System.out.println("1Pモード");
		// サーバーを閉じる
		nw.Close();
		// タイトル除去
		frame.remove(title);
		title = null;
		// ぷちゅ生成
		makePuchu();
		// プレイヤーフィールド
		me = new Field(this, ppInit, true);
		me.draw.setBounds(0, 0, ScreenW/2, ScreenH);
		// nullプレイヤーフィールド
		rival = new Field(this, null, false);
		rival.draw.setBounds(ScreenW/2, 0, ScreenW/2, ScreenH);
		// フレームに追加
		frame.add(me.draw);
		frame.add(rival.draw);
		frame.revalidate();
		me.draw.requestFocus();
		// BGM
		overlay.setBGM(getClass().getResource("gamemusic.wav"));
		// ポーズ用サウンド
		pauseSE = new Sound(getClass().getResource("selectmode.wav"));
		// スタートアニメーション
		me.draw.startReadyAnim();
	}
	
	// ソロプレイ時の更新処理
	private void updateSolo() {
		// 状態更新
		if(!isMeFinish && !isPause) me.update();
		// 画面描画
		if(!isOverlay) {
			me.draw.repaint();
			rival.draw.repaint();
		} else {
			overlay.repaint();
		}
	}
	
	// 対戦プレイ時の初期化処理
	private void initDuo() {
		long waitStart;
		
		//System.out.println("2Pモード");
		// タイトル除去
		frame.remove(title);
		title = null;
		// ぷちゅ生成
		if(nw.programMode == Network.Mode.SERVER) {
			nw.sendPuchuList(makePuchu());
		} else {
			waitStart = System.currentTimeMillis();
			while(!canStart) {
				nw.getRivalStatus();
				try { sleep(100); } catch (Exception e) {}
				// 1分経ってもデータが来なければ切断と判定する
				if(System.currentTimeMillis() - waitStart > 60 * 1000) {
					JLabel label = new JLabel("接続が切断されました");
					label.setFont(dialogFont);
				    JOptionPane.showMessageDialog(frame, label);
				    gameStatus = Status.GAME_TITLE;
				    return;
				}
			}
		}
		// プレイヤーフィールド
		me = new Field(this, ppInit, true);
		me.draw.setBounds(0, 0, ScreenW/2, ScreenH);
		// ライバルプレイヤーフィールド
		rival = new Field(this, ppInit, false);
		rival.draw.setBounds(ScreenW/2, 0, ScreenW/2, ScreenH);
		// 準備完了
		nw.sendStatus("START");
		waitStart = System.currentTimeMillis();
		while(!canStart) {
			nw.getRivalStatus();
			try { sleep(100); } catch (Exception e) {}
			// 1分経ってもデータが来なければ切断と判定する
			if(System.currentTimeMillis() - waitStart > 60 * 1000) {
				JLabel label = new JLabel("接続が切断されました");
				label.setFont(dialogFont);
			    JOptionPane.showMessageDialog(frame, label);
			    gameStatus = Status.GAME_TITLE;
			    return;
			}
		}
		// フレームに追加
		frame.add(me.draw);
		frame.add(rival.draw);
		frame.revalidate();
		me.draw.requestFocus();
		// BGM
		overlay.setBGM(getClass().getResource("gamemusic.wav"));
		// スタートアニメーション
		me.draw.startReadyAnim();
		rival.draw.startReadyAnim();
		frameCount = 0;
		stopCount = 0;
	}
	
	// 対戦プレイ時の更新処理
	private void updateDuo() {
		int temp;
		
		// スタート同期確認
		if(canStart) {
			// ネットワーク
			if(nw.index >= rivalIndex) { //一致してるとき or 遅い場合
				isUpdate = true;
				stopCount = 0;
				// 次のデータを取り出し
				// フィールドデータを持っている場合、同期待ちをする
				if(nw.isConnect && !haveField && nw.getRivalStatus()) {
					//フィールドデータを取り出したら次のインデックスも取り出す
					nw.getRivalStatus();
					haveField = true;
				}
			} else { //自分のほうが早い
				isUpdate = false;
				stopCount++;
				// 強制切断検出
				if(stopCount >= 500 && !isRivalFinish) disConnect();
				// 次のデータを取得
				// 相手よりも早いため即座にフィールドを反映する
				if(nw.isConnect && nw.getRivalStatus()) {
					//フィールドデータを取り出したら次のインデックスも取り出す
					nw.getRivalStatus();
					setRivalField();
				}
			}
			// 自分の状態更新
			if(!isMeFinish) {
				temp = me.update();
				if(temp != meIndex && temp != -1) {
					meIndex = temp;
					me.key.canKeyInput = false;
					nw.sendField(me.cell, me.score, rival.fallen_obs, rival.unfallen_obs); //次のぷちゅになったらフィールド全体を送信
					nw.sendPuchuIndex(meIndex);
					resetInput(me.key); //長押し解除
					me.key.canKeyInput = true;
				}
				if(me.now != null) nw.sendPuchu(me.now);
			}
			// ライバルの状態更新
			if(!isRivalFinish && isUpdate) {
				temp = rival.update();
				if(temp != rivalIndex && temp != -1) {
					rivalIndex = temp;
					if(haveField) setRivalField();
					resetInput(rival.key); //長押し解除
				}
			}
			frameCount++;
			// 画面描画
			if(!isOverlay) {
				me.draw.repaint();
				rival.draw.repaint();
			} else {
				overlay.repaint();
			}
		}
	}
	
	//---Field関係---
	// 初期ぷちゅペア生成
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
	
	// ゲームオーバーの検知
	public void finishGame(boolean isMe) {
		isMeFinish = true;
		isRivalFinish = true;
		if(gameStatus == Status.GAME_DUO) {
			if(isMe) {
				rival.win();
				nw.sendStatus("END");
			} else {
				me.win();
			}
			nw.Close();
		}
	}
	
	// おじゃまを逆のフィールドに送信
	public void sendObs(int count, boolean isMe) {
		if(isMe) rival.receive_obs(count);
		else me.receive_obs(count);
	}
	
	//---Overlay関係---
	// フェードイン
	public void fadeIn(Status next) {
		nextStatus = next;
		isOverlay = true;
		overlay.FadeIn();
	}
	
	// フェードイン終了
	public void fadeEnd() {
		gameStatus = nextStatus;
		isOverlay = false;
	}
	
	// リザルトの表示
	public void resultDisp(int score, boolean isMe) {
		if(isMe) {
			isOverlay = true;
			overlay.Result(score);
		}
	}
	
	// ポーズ画面
	public void switchPause() {
		// 1人モードのみ動作
		if(gameStatus == Status.GAME_SOLO) {
			if(isPause) {
				pauseSE.play();
				isOverlay = false;
				isPause = false;
				overlay.Clear();
			} else {
				pauseSE.play();
				isOverlay = true;
				isPause = true;
				overlay.Pause();
			}
		}
	}
	
	//---NetWork関係---
	// クライアントの接続受け入れ
	public void rivalApply() {
		title.rivalApply();
	}
	
	// 初期ぷちゅペア生成(外部受信)
	public void makePuchuByServer(int[][] list) {
		if(list == null) {
			//System.out.println("ぷちゅ生成エラー");
			System.exit(0);
		} else {
			ppInit = list;
		}
	}
	
	// ライバルのキー入力を取得
	public void getRivalInput(String key) {
		String[] keyData = key.split(":");

		// キー情報の取り出し
		try {
			nowKey = Integer.parseInt(keyData[0]);
			nowKeyTime = Long.parseLong(keyData[1]);
		} catch(Exception e) {
			//System.out.println("不正なキーデータ: "+key);
			return;
		}
	}
	
	// ライバルのキーを反映
	private void setRivalInput(int key) {
		boolean isPress = true;
		
		if(key < 0) {
			isPress = false;
			key *= -1;
		}
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
			//System.out.println("不正なキーデータ: "+key);
			break;
		}
	}
	
	// ライバルのnowの座標を反映
	public void setNowPuchuPos(String pos) {
		if(rival.now == null) return;
		String[] parse = pos.split(":");
		rival.now.form = Integer.parseInt(parse[0]);
		rival.now.puchu1.x = rival.now.puchu1.draw_x = Integer.parseInt(parse[1]);
		rival.now.puchu1.y = rival.now.puchu1.draw_y = Integer.parseInt(parse[2]);
		rival.now.puchu2.x = rival.now.puchu2.draw_x = Integer.parseInt(parse[3]);
		rival.now.puchu2.y = rival.now.puchu2.draw_y = Integer.parseInt(parse[4]);
	}
	
	// キーを一旦リセット
	public void resetInput(Key key) {
		key.Left = false;
		key.Right = false;
		key.Down = false;
		key.TurnLeft = false;
		key.TurnRight = false;
	}
	
	// ライバルのフィールドを同期
	public void setRivalField() {
		rival.score = score;
		me.fallen_obs = fallenObs;
		me.unfallen_obs = unfallenObs;
		me.draw.setObsNum(fallenObs+unfallenObs);
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 14; j++) {
				rival.cell[i][j].setPuchu(nextRivalField[i][j], i, j);
			}
		}
		haveField = false;
	}
	
	// ライバルの負けを検知
	public void finishRival() {
		nw.Close();
	}
	
	// ライバルの切断を検知
	public void disConnect() {
		me.win();
		rival.defeat();
		isMeFinish = true;
		isRivalFinish = true;
		nw.Close();
	}
	
	//--------------------
	//   プログラム実行本体
	//--------------------
	public static void main(String[] args) {
		GameMain gm = new GameMain();
		gm.start();
	}
}
