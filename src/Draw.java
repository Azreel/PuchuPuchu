import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Draw extends JPanel{
	
	final int PanelW = 480;
	final int PanelH = 640;
	
	Toolkit tk;
	Image img;
	JLabel lb;	//ラベル
	private int i, j;
	public int score = 0;
	public ArrayList<Puchu> puchu_list;
	public Puchu[][] cell = new Puchu[12][6];
	
	public Draw() {	//1P用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img = tk.getImage(getClass().getResource("BackGroundPattern.png"));  //フィールドの画像
	}
	public Draw(PuchuPair first, PuchuPair[] next) {	//対戦用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img = tk.getImage(getClass().getResource("BackGroundPattern.png"));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D img_2d = (Graphics2D) g;
		for ( i = 0; i < 6; i++ ) {
			for ( j = 0; j < 3; j++ ) {
				img_2d.drawImage(img, j*img.getWidth(this) + 60, i*img.getHeight(this) + 120, this);	//連続描画
			}
		}
	}
}