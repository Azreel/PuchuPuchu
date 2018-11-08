import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Draw extends JPanel{
	
	public static final int Squares = 40;
	
	final int PanelW = 480;
	final int PanelH = 640;
	
	Toolkit tk;
	Image img, img_puchu;
	JLabel lb;	//ラベル
	Field fd;
	
	//--- テスト用仮処理 ---
	Puchu[][] cell = new Puchu[12][6];
	PuchuPair now_puchu;
	private void puchu_cell_init() {
		for ( int i = 0; i < 12; i++ ) {
			for ( int j = 0; j < 6; j++ ) {
				cell[i][j] = new Puchu(Puchu.Emp, j*Draw.Squares, i*Draw.Squares);
			}
		}		
	}
	int time = 0;
	int time2 = 0;
	int time3 = 0;
	boolean moveflg = false;
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
		
		//--- テスト用仮処理 ---
		puchu_cell_init();
		cell[11][0].type = Puchu.Pat1;
		cell[11][1].type = Puchu.Pat1;
		cell[11][2].type = Puchu.Pat1;
		cell[11][4].type = Puchu.Pat1;
		cell[1][0].type = Puchu.Pat1;
		cell[1][0].dropDown(10);
		now_puchu = new PuchuPair(Puchu.Pat1, Puchu.Pat1);
		//-------------
	}
		
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D img_2d = (Graphics2D) g;
		for (int i = 0; i < 6; i++ ) {
			for (int j = 0; j < 3; j++ ) {
				img_2d.drawImage(img, j*img.getWidth(this) + 60, i*img.getHeight(this) + 110, this);	//連続描画
			}
		}
		if ( !is_alive ) { return; }
		
//		Puchu[][] cell = fd.cell;
		
		for( int i = 0; i < 12; i++ ) {
			for ( int j = 0; j < 6; j++ ) {
				if ( cell[i][j].type != Puchu.Emp) {
					if ( !cell[i][j].is_match_position ) { cell[i][j].drawingDropDown(); }
					img_2d.drawImage(img_puchu, cell[i][j].draw_x + 60, cell[i][j].draw_y + 110, this);				
				}
			}
		}
		
		//--- テスト用仮処理 ---
		if ( time > 110 ) {
			time = 0;
			now_puchu.turnLeft();
		}
		if ( time2 > 40 ) {
			time2 = 0;
			moveflg = !moveflg;
			if ( moveflg ) { now_puchu.moveRight(); }
			else { now_puchu.moveLeft(); }
		}
		if ( time3 > 6 ) {
			time3 = 0;
			now_puchu.fallDown();
		}
		time++;
		time2++;
		time3++;
		//-------------
		
		if ( !now_puchu.is_match_posture_right ) { now_puchu.drawingTurnRight(); }
		if ( !now_puchu.is_match_posture_left ) { now_puchu.drawingTurnLeft(); }
		if ( !now_puchu.is_match_position ) { now_puchu.drawingMove(); }
		img_2d.drawImage(img_puchu, now_puchu.puchu1.draw_x, now_puchu.puchu1.draw_y, this);
		img_2d.drawImage(img_puchu, now_puchu.puchu2.draw_x, now_puchu.puchu2.draw_y, this);
	}
}