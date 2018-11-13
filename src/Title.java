import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Title extends JPanel{
	final Toolkit tk = Toolkit.getDefaultToolkit();
	final int fadeSpeed = 60;
	
	GameMain gm;
	Network nw;
	Image bg;
	JButton soloBtn, duoBtn;
	JTextField rivalIP;
	JLabel rival, myIP;
	Font btnFont = new Font(Font.DIALOG, Font.PLAIN, 24);
	Font labelFont = new Font(Font.DIALOG, Font.PLAIN, 20);
	GameMain.Status next = null;
	boolean isFade = false;
	float fadeAlpha = 0.0f;
	
	// コンストラクタ
	Title(GameMain parent, Network client) {
		gm = parent;
		nw = client;
		this.setLayout(null);
		
		//背景画像
		bg = tk.getImage(getClass().getResource("title.png"));
		//自分のIPアドレス
        myIP = new JLabel(nw.getIPaddr());
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
	
	// 1Pプレイボタン用イベントリスナー
	private class SoloPlayBtn implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			setFade(GameMain.Status.GAME_SOLO);
		}
	}
	
	// 2Pプレイボタン用イベントリスナー
	private class DuoPlayBtn implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(nw.Connect(rivalIP.getText())) {
				setFade(GameMain.Status.GAME_DUO);
			} else {
				JLabel label = new JLabel("接続に失敗しました");
			    JOptionPane.showMessageDialog(gm.frame, label);
			}
		}
	}
	
	// クライアントの接続確認
	public void rivalApply() {
	    JOptionPane.showMessageDialog(gm.frame, new JLabel("相手の接続を受けました"));
	    setFade(GameMain.Status.GAME_DUO);
	}
	
	// フェード開始用
	private void setFade(GameMain.Status status) {
		this.removeAll();
		gm.fadeIn(status);
	}
	
	// 描画
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(bg, null, this);
    }
}
