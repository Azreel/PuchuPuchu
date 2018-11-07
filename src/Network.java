import java.io.*;
import java.net.*;

public class Network extends Thread {
	final int Port = 12345;
	
	enum Mode { SERVER, CLIENT };
	Mode programMode = Mode.SERVER;
	GameMain gm;
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
		}
	}
	
	public void run() {
		try {
			while(true) {
				switch(programMode){
				case SERVER:
					if(sc == null) {
						sc = ss.accept();
						//クライアントから送られてきたデータを一時保存するバッファ(受信バッファ)
			            br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			            //サーバがクライアントへ送るデータを一時保存するバッファ(送信バッファ)
			            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sc.getOutputStream())));
					} else {
						if(br.readLine().startsWith("PING")) {
							pw.println("PONG");
							pw.flush();
							gm.rivalApply();
						}
					}
					break;
				case CLIENT:
					break;
				}
			}
		}catch(Exception e) {
			System.out.println(e);
			return;
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
            	if(br.readLine().startsWith("PONG")) break;
            	sleep(1);
            }
            return true;
        }
        catch(Exception e){
        	System.out.println(e);
        	try {
	        	if(pw != null) pw.close();
	        	if(br != null) br.close();
	        	if(sc != null) sc.close();
        	} catch(Exception ex) {}
        	// 失敗したのでサーバーに戻る
            programMode = Mode.SERVER;
            return false;
        }
	}
}
