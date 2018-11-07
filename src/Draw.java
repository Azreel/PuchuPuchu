import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Draw extends JPanel{
	
	final int PanelW = 480;
	final int PanelH = 640;
	
	Toolkit tk;
	Image img, img_puchu;
	JLabel lb;	//ラベル
	Field fd;
	
	//--- 仮処理 ---
	Puchu[][] cell = new Puchu[12][6];
	private void puchu_cell_init() {
		for ( int i = 0; i < 12; i++ ) {
			for ( int j = 0; j < 6; j++ ) {
				cell[i][j] = new Puchu(Puchu.Emp, i*40+60, j*40+60);
			}
		}		
	}
	//-------------
	
	boolean is_alive = true;
	
	public Draw() {	//nullプレイヤー用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img = tk.getImage(getClass().getResource("BackGroundPattern.png"));  //フィールドの画像
		
		is_alive = false;
	}
	public Draw(Field _fd) {	//プレイ用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img = tk.getImage(getClass().getResource("BackGroundPattern.png"));
		img_puchu = tk.getImage(getClass().getResource("puchumodoki.png"));
		
		fd = _fd;
		
		//--- 仮処理 ---
		puchu_cell_init();
		cell[11][0].type = Puchu.Pat1;
		cell[11][1].type = Puchu.Pat1;
		cell[11][2].type = Puchu.Pat1;
		cell[11][4].type = Puchu.Pat1;
		//-------------
	}
		
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D img_2d = (Graphics2D) g;
		for (int i = 0; i < 6; i++ ) {
			for (int j = 0; j < 3; j++ ) {
				img_2d.drawImage(img, j*img.getWidth(this) + 60, i*img.getHeight(this) + 120, this);	//連続描画
			}
		}
		if ( !is_alive ) { return; }
		
//		Puchu[][] cell = fd.cell;
		
		for( int i = 0; i < 12; i++ ) {
			for ( int j = 0; j < 6; j++ ) {
				if ( cell[i][j].type != Puchu.Emp) {
					img_2d.drawImage(img_puchu, j*img_puchu.getWidth(this) + 60, i*img_puchu.getHeight(this) + 120, this);					
				}
			}
		}
		
	}
}