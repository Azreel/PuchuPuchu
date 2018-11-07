import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Title extends JPanel{
	final Toolkit tk = Toolkit.getDefaultToolkit();
	
	GameMain gm;
	Network nw;
	Image bg;
	JButton soloBtn, duoBtn;
	JTextField rivalIP;
	JLabel rival, myIP;
	Font btnFont = new Font("MS ゴシック", Font.PLAIN, 24);
	Font labelFont = new Font("MS ゴシック", Font.PLAIN, 20);
	
	Title(GameMain parent, Network client) {
		gm = parent;
		nw = client;
		this.setLayout(null);
		
		//背景画像
		bg = tk.getImage(getClass().getResource("title.png"));
		//自分のIPアドレス
        myIP = new JLabel(getIPaddr());
        myIP.setBounds(225, 470, 400, 30);
        myIP.setFont(labelFont);
		//1Pプレイ
		soloBtn = new JButton("1Pプレイ");
		soloBtn.setBounds(570, 450, 160, 60);
		soloBtn.setFont(btnFont);
		soloBtn.addActionListener(new SoloPlayBtn());
		//IPアドレス入力欄
		rival = new JLabel("相手のIPアドレス:");
		rival.setBounds(225, 550, 300, 30);
		rival.setFont(labelFont);
		rivalIP = new JTextField("0.0.0.0");
		rivalIP.setBounds(400, 550, 160, 30);
		rivalIP.setFont(labelFont);
        //2Pプレイ
		duoBtn = new JButton("2Pプレイ");
		duoBtn.setBounds(570, 530, 160, 60);
		duoBtn.setFont(btnFont);
		duoBtn.addActionListener(new DuoPlayBtn());
        
        this.add(soloBtn);
        this.add(duoBtn);
        this.add(rival);
        this.add(rivalIP);
        this.add(myIP);
    }
	
	private String getIPaddr() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return "自分のIPアドレス: " + addr.getHostAddress();
		} catch(UnknownHostException e) {
			return "ネットワークに接続されていません";
		}
	}
	
	private class SoloPlayBtn implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			gm.setStatus(GameMain.Status.GAME_SOLO);
		}
	}
	
	private class DuoPlayBtn implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(nw.Connect(rivalIP.getText())) {
				gm.setStatus(GameMain.Status.GAME_DUO);
			} else {
				JLabel label = new JLabel("接続に失敗しました");
			    JOptionPane.showMessageDialog(gm.frame, label);
			}
		}
	}
	
	public void rivalApply() {
		JLabel label = new JLabel("相手の接続を受けました");
	    JOptionPane.showMessageDialog(gm.frame, label);
	    gm.setStatus(GameMain.Status.GAME_DUO);
	}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(bg, null, this);
    }
}
