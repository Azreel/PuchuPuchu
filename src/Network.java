import java.io.*;
import java.net.*;

public class Network extends Thread {
	final int Port = 28385;
	public static enum Mode { SERVER, CLIENT };
	
	public Mode programMode = Mode.SERVER;
	public boolean isConnect = false;
	public int index = 1; //操作中のぷちゅ
	
	GameMain gm;
	boolean isAlive = true;
	ServerSocket ss;
	Socket sc;
	BufferedReader br;
	PrintWriter pw;
	//int sendCount = 0;
	//int recvCount = 0;
	
	// コンストラクタ
	Network(GameMain parent){
		gm = parent;
		try {
			ss = new ServerSocket(Port);
		}
		catch(Exception e) {
			System.out.println("サーバー作成失敗");
			programMode = Mode.CLIENT;
		}
	}
	
	// 接続待ち(スレッドのメイン)
	public void run() {
		while(!isConnect && isAlive) {
			try {
				switch(programMode){
				case SERVER:
					if(sc == null) {
						sc = ss.accept();
						// 受信バッファ
			            br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			            // 送信バッファ
			            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sc.getOutputStream())));
					} else {
						// 接続確認
						if(!isConnect && br.readLine().equals("PING")) {
							pw.println("PONG");
							pw.flush();
							isConnect = true;
							gm.rivalApply();
						}
					}
					break;
				case CLIENT:
					break;
				}
			}catch(Exception e) {
				System.out.println("nw run: "+e);
			}
			try { sleep(10); } catch(Exception e) {}
		}
	}
	
	// サーバーを閉じる
	public void Close() {
		try{
			if(!isConnect && programMode == Mode.SERVER) ss.close();
			else sc.close(); 
		} catch(Exception e) {}
		isAlive = false;
		isConnect = false;
		System.out.println("接続解除");
	}
	
	// 自分のIPアドレスを取得
	public String getIPaddr() {
		try {
			String addr = InetAddress.getLocalHost().getHostAddress();
			if(addr.equals("127.0.0.1")) throw new UnknownHostException();
			return "自分のIPアドレス: " + addr;
		} catch(UnknownHostException e) {
			Close();
			return "ネットワークに接続されていません";
		}
	}
	
	// サーバーに接続
	public boolean Connect(String addr) {
		// PING送信
		try {
			// サーバーを閉じる
			if(ss != null) ss.close();
			programMode = Mode.CLIENT;
			sc = new Socket(addr, Port);
            br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sc.getOutputStream())));
            // サーバーからの返事があるまで30秒待機
            long start = System.currentTimeMillis();
            pw.println("PING");
            pw.flush();
            while(true) {
            	if(System.currentTimeMillis() - start >= 30 * 1000) throw new SocketException();
            	if(br.readLine().equals("PONG")) break;
            	sleep(1);
            }
            isConnect = true;
            return true;
        }
        catch(Exception e){
        	System.out.println("nw connect: "+e);
        	try {
	        	if(pw != null) pw.close();
	        	if(br != null) br.close();
	        	if(sc != null) sc.close();
        	} catch(Exception ex) {}
        	// 失敗したらサーバーに戻る
        	sc = null;
        	try {
        		ss = new ServerSocket(Port);
        		programMode = Mode.SERVER;
        		System.out.println("サーバー再生成");
        	} catch(Exception ex) {
        		System.out.println("サーバー作成失敗");
    			programMode = Mode.CLIENT;
        	}
        	isConnect = false;
            return false;
        }
	}
	
	// 相手のステータス取得
	public void getRivalStatus(boolean key) {
		String input;
		try {
			if(br.ready()) input = br.readLine();
			else return;
			System.out.println(input);
		}catch(Exception e) {
			System.out.println("nw get: "+e);
			input = "END";
		}
		// コマンドによって分岐
		switch(input) {
		// 受信データなし
		case "null":
			break;
		// 相手が初期ぷちゅペアリストを受信完了すると送られてくる
		case "START":
			gm.canStart = true;
			break;
		// 相手が負けた場合に送られてくる
		case "END":
			gm.canStart = true; // 無理やりスタート
			gm.finishRival();
			Close();
			break;
		// 初期ぷちゅペア受信開始
		case "MAKESTART":
			gm.makePuchuByServer(getPuchuList());
			gm.canStart = true;
			break;
		// 操作対象が変わると送られてくる
		case "NEXT":
			getPuchuIndex();
			//gm.resetRivalInput();
			break;
		// フィールド全体の同期
		case "FIELDSTART":
			getRivalField();
			break;
		// キー入力
		default:
			if(key) gm.getRivalInput(input);
			break;
		}
	}
	
	// 初期ぷちゅペアリスト受信
	private int[][] getPuchuList(){
		String pair;
		int[][] pairList = new int[GameMain.PPSIZE][2];
		int index = 0;
		try {
			pair = br.readLine();
		} catch (Exception e) {
			System.out.println("nw get: " + e);
			return null;
		}
		while(!pair.equals("MAKEEND")) {
			if(pair.equals("null")) return null;
			String[] set = pair.split(",");
			pairList[index][0] = Integer.parseInt(set[0]);
			pairList[index][1] = Integer.parseInt(set[1]);
			index++;
			try {
				pair = br.readLine();
			} catch (Exception e) {
				System.out.println("nw get: " + e);
				return null;
			}
		}
		if(index != GameMain.PPSIZE) return null;
		else return pairList;
	}
	
	// ターゲットぷちゅペアの確認
	private void getPuchuIndex(){
		try {
			index = Integer.parseInt(br.readLine());
			System.out.println(index);
		} catch(Exception e) {
			System.out.println("nw get: " + e);
		}
	}
	
	// ライバルのフィールドを取得
	private void getRivalField() {
		int[][] cell = new int[6][14];
		String temp;
		String[] splited;
		int column = 0;
		while(true) {
			try {
				temp = br.readLine();
			}catch (Exception e) {
				System.out.println("nw get: " + e);
				return;
			}
			if(temp.equals("FIELDEND")) break;
			if(column > 5) {
				System.out.println("不正なデータ");
				return;
			}
			splited = temp.split(",");
			if(splited.length != 14) {
				System.out.println("不正なデータ");
				return;
			}
			for(int i = 0; i < 14; i++) {
				cell[column][i] = Integer.parseInt(splited[i]);
			}
			column++;
		}
		gm.setRivalField(cell);
	}
	
	// 初期ぷちゅペアリスト送信
	public void sendPuchu(String[] list) {
		pw.println("MAKESTART");
		for(String data : list) {
			pw.println(data);
		}
		pw.println("MAKEEND");
		pw.flush();
	}
	
	// 自分のステータスを送信
	public void sendStatus(String status) {
		if(isAlive == false) return;
		pw.println(status);
		pw.flush();
	}
	
	// ターゲットのぷちゅペアを送信
	public void sendPuchuIndex(int index) {
		pw.println("NEXT");
		pw.println(Integer.toString(index));
		pw.flush();
	}
	
	// フィールド情報送信
	public void sendField(Puchu[][] cell) {
		String column = "";
		pw.println("FIELDSTART");
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 14; j++) {
				column += cell[i][j].type + ",";
			}
			pw.println(column);
			column = "";
		}
		pw.println("FIELDEND");
		pw.flush();
	}
}
