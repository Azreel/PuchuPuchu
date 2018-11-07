import java.io.*;
import java.net.*;

public class Network extends Thread {
	final int Port = 12345;
	
	public Mode programMode = Mode.SERVER;
	
	enum Mode { SERVER, CLIENT };
	GameMain gm;
	Boolean isConnect = false;
	ServerSocket ss;
	Socket sc;
	BufferedReader br;
	PrintWriter pw;
	
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
	
	public void run() {
		while(true) {
			try {
				switch(programMode){
				case SERVER:
					if(sc == null) {
						sc = ss.accept();
						//クライアントから送られてきたデータを一時保存するバッファ(受信バッファ)
			            br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			            //サーバがクライアントへ送るデータを一時保存するバッファ(送信バッファ)
			            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sc.getOutputStream())));
					} else {
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
				if(isConnect) return;
			}
		}
	}
	
	public String getIPaddr() {
		try {
			String addr = InetAddress.getLocalHost().getHostAddress();
			if(addr.equals("127.0.0.1")) throw new UnknownHostException();
			return "自分のIPアドレス: " + addr;
		} catch(UnknownHostException e) {
			return "ネットワークに接続されていません";
		}
	}
	
	public Boolean Connect(String addr) {
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
        	} catch(Exception ex) {
        		System.out.println("サーバー作成失敗");
    			programMode = Mode.CLIENT;
        	}
            return false;
        }
	}
	
	private void getRivalStatus() {
		String input;
		try {
			input = br.readLine();
		}catch(Exception e) {
			System.out.println("nw get: "+e);
			input = "END";
		}
		switch(input) {
		case "START":
			break;
		case "END":
			break;
		case "MAKE":
			break;
		case "LEFTPRESS":
			break;
		case "RIGHTPRESS:":
			break;
		case "DOWNPRESS":
			break;
		case "ZPRESS":
			break;
		case "XPRESS":
			break;
		case "LEFTRELEASE":
			break;
		case "RIGHTRELEASE":
			break;
		case "DOWNRELEASE":
			break;
		case "ZRELEASE":
			break;
		case "XRELEASE":
			break;
		}
	}
	
	public void sentPuchu(int type1, int type2) {
		pw.println("MAKE");
		pw.println(type1);
		pw.println(type2);
		pw.flush();
	}
	
	public void sentStatus(String status) {
		pw.println(status);
		pw.flush();
	}
}
