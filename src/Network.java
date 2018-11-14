import java.io.*;
import java.net.*;

public class Network extends Thread {
	final int Port = 28385;
	public static enum Mode { SERVER, CLIENT };
	
	public Mode programMode = Mode.SERVER;
	public boolean isConnect = false;
	public int index = 0; //操作中のぷちゅ
	
	GameMain gm;
	boolean isAlive = true;
	ServerSocket ss;
	Socket sc;
	BufferedReader br;
	PrintWriter pw;
	//int sendCount = 0;
	//int recvCount = 0;
	boolean isFlush = true; //バッファが空か
	
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
		while(isAlive) {
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
				if(isConnect) getRivalStatus();
			}catch(Exception e) {
				System.out.println("nw run: "+e);
				break;
			}
			try { sleep(100); } catch(Exception e) {}
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
	public void getRivalStatus() {
		String input;
		while(true) {
			// コマンドを読む
			try {
				if(br.ready()) input = br.readLine();
				else break;
			}catch(Exception e) {
				System.out.println("nw get: "+e);
				input = "END";
			}
			// コマンドによって分岐
			switch(input) {
			// 受信データなし
			case "null":
				return;
			// 相手が初期ぷちゅペアリストを受信完了すると送られてくる
			case "START":
				gm.canStart = true;
				break;
			// 相手が負けた場合に送られてくる
			case "END":
				gm.canStart = true; // 無理やりスタート
				gm.finishRival();
				Close();
				return;
			// 初期ぷちゅペア受信開始
			case "MAKESTART":
				gm.makePuchuByServer(getPuchuList());
				gm.canStart = true;
				break;
			// 操作対象が変わると送られてくる
			case "NEXT":
				getPuchuIndex();
				gm.resetRivalInput();
				break;
			// キー入力
			default:
				gm.setRivalInput(input);
				break;
			}
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
		} catch(Exception e) {
			System.out.println("nw get: " + e);
		}
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
}
