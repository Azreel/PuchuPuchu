import java.io.*;
import java.net.*;

public class Network extends Thread {
	final int Port = 12345;
	
	enum Mode { SERVER, CLIENT };
	Mode programMode = Mode.SERVER;
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
						if(br.readLine().equals("PING")) {
							pw.println("PONG");
							pw.flush();
							isConnect = true;
							programMode = Mode.CLIENT;
							gm.rivalApply();
						}
					}
					break;
				case CLIENT:
					break;
				}
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
	
	public String getRivalInput() {
		try {
			String input = br.readLine();
			return input;
		}catch(Exception e) {
			System.out.println("nw get: "+e);
			if(e.getMessage().equals("Connection reset")) return "END";
			else return null;
		}
	}
	
	public void sentMyInput(String input) {
		pw.println(input);
		pw.flush();
	}
}
