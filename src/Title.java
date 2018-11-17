import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Title extends JPanel{
	final Toolkit tk = Toolkit.getDefaultToolkit();
	final int fadeSpeed = 60;
	final Font dispFont = new Font(Font.DIALOG, Font.PLAIN, 24);
	final Font inputFont = new Font(Font.DIALOG, Font.PLAIN, 20);
	final Font dialogFont = new Font(Font.DIALOG, Font.PLAIN, 16);
	
	GameMain gm;
	Network nw;
	Image bg;
	JButton soloBtn, duoBtn;
	JTextField rivalIP;
	JLabel rival, myIP;
	GameMain.Status next = null;
	boolean isFade = false;
	float fadeAlpha = 0.0f;
	
	// コンストラクタ
	Title(GameMain parent, Network client) {
		gm = parent;
		nw = client;
		this.setLayout(null);
		
		//背景画像
		try {
			bg = ImageIO.read(getClass().getResource("title.png"));
		} catch (Exception e) {}
		//自分のIPアドレス
        myIP = new JLabel(nw.getIPaddr());
        myIP.setBounds(200, 467, 400, 30);
        myIP.setFont(dispFont);
        myIP.setForeground(Color.WHITE);
		//1Pプレイ
		soloBtn = new JButton("1Pプレイ");
		soloBtn.setBounds(580, 450, 160, 60);
		soloBtn.setFont(dispFont);
		soloBtn.addActionListener(new SoloPlayBtn());
		//IPアドレス入力欄
		rival = new JLabel("相手のIPアドレス:");
		rival.setBounds(200, 547, 300, 30);
		rival.setFont(dispFont);
		rival.setForeground(Color.WHITE);
		rivalIP = new JTextField(gm.rivalIP);
		rivalIP.setBounds(408, 547, 160, 30);
		rivalIP.setFont(inputFont);
        //2Pプレイ
		duoBtn = new JButton("2Pプレイ");
		duoBtn.setBounds(580, 530, 160, 60);
		duoBtn.setFont(dispFont);
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
			Sound pushSound = new Sound(getClass().getResource("selectmode.wav"));
			pushSound.play();
		}
	}
	
	// 2Pプレイボタン用イベントリスナー
	private class DuoPlayBtn implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(nw.Connect(rivalIP.getText())) {
				gm.rivalIP = rivalIP.getText();
				setFade(GameMain.Status.GAME_DUO);
				Sound pushSound = new Sound(getClass().getResource("selectmode.wav"));
				pushSound.play();
			} else {
				JLabel label = new JLabel("<html>接続に失敗しました<br>IPアドレスを確認してください<html>");
				label.setFont(dialogFont);
			    JOptionPane.showMessageDialog(gm.frame, label);
			}
		}
	}
	
	// クライアントの接続確認
	public void rivalApply() {
		JLabel label = new JLabel("相手の接続を受けました");
		label.setFont(dialogFont);
	    JOptionPane.showMessageDialog(gm.frame, label);
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bg, null, this);
    }
}
