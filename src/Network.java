import java.io.*;
import java.net.*;

public class Network extends Thread {
	final int Port = 12345;
	public static enum Mode { SERVER, CLIENT };
	
	public Mode programMode = Mode.SERVER;
	
	GameMain gm;
	boolean isAlive = true;
	boolean isConnect = false;
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
			if(!isAlive) break;
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
				if(isConnect) break;
			}
			try { sleep(1); } catch(Exception e) {}
		}
	}
	
	public void Close() {
		isAlive = false;
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
		case "MAKESTART":
			break;
		default:
			gm.setRivalInput(input);
			break;
		}
	}
	
	public void sentPuchu(String[] list) {
		pw.println("MAKESTART");
		for(String data : list) {
			pw.println(data);
		}
		pw.println("MAKEEND");
		pw.flush();
	}
	
	public void sentStatus(String status) {
		pw.println(status);
		pw.flush();
	}
}
