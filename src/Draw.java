import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Draw extends JPanel{
	
	public static final int Squares = 40;
	private static final int margin_w = 60, margin_h = 110;
	
	final int PanelW = 480;
	final int PanelH = 640;
	
	Toolkit tk;
	Image img;
	Image img_puchu[] = new Image[9];
	JLabel lb;	//ラベル
	Field fd;
	
	//--- テスト用仮処理 ---
//	Puchu[][] cell = new Puchu[12][6];
//	PuchuPair now_puchu;
//	private void puchu_cell_init() {
//		for ( int i = 0; i < 12; i++ ) {
//			for ( int j = 0; j < 6; j++ ) {
//				cell[i][j] = new Puchu(Puchu.Emp, j*Draw.Squares, i*Draw.Squares);
//			}
//		}		
//	}
//	int time = 0;
//	int time2 = 0;
//	int time3 = 0;
//	int time4 = 0;
//	boolean moveflg = false;
//	boolean vanflg = false;
	//-------------
	
	boolean is_alive = true;
	
	Draw() {	//nullプレイヤー用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img = tk.getImage(getClass().getResource("BackGroundPattern.png"));  //フィールドの画像
		
		is_alive = false;
	}
	Draw(Field _fd) {	//プレイ用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img = tk.getImage(getClass().getResource("BackGroundPattern.png"));
		img_puchu[Puchu.Pat1] = tk.getImage(getClass().getResource("puchumodoki.png"));
		img_puchu[Puchu.Van] = tk.getImage(getClass().getResource("puchumodokivan.png")); 
		
		fd = _fd;
		
		initImage();
		
		//--- テスト用仮処理 ---
//		puchu_cell_init();
//		cell[11][0].type = Puchu.Pat1;
//		cell[11][1].type = Puchu.Pat1;
//		cell[11][2].type = Puchu.Pat1;
//		cell[11][4].type = Puchu.Pat1;
//		cell[1][0].type = Puchu.Pat1;
//		cell[10][0].copyPuchu(cell[1][0]);
//		cell[10][0].dropDown(10);
//		cell[1][0].type = Puchu.Emp;
//		now_puchu = new PuchuPair(Puchu.Pat1, Puchu.Pat1);
		//-------------
	}
	
	private void initImage() {
		for ( int i = 1; i <= 8; i++ ) {
			img_puchu[i] = tk.getImage(getClass().getResource("puchu"+i+".png"));			
		}
	}
	
	public void startDropAnim() {
		
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
		
		for( int i = 0; i < fd.cell.length; i++ ) {
			for ( int j = 0; j < fd.cell[i].length; j++ ) {
				if ( fd.cell[i][j].type != Puchu.Emp) {
					if ( !fd.cell[i][j].is_match_position ) { fd.cell[i][j].drawingDropDown(); }
					if ( fd.cell[i][j].type == Puchu.Van ) { fd.cell[i][j].vanishOutDelay(); }
					img_2d.drawImage(img_puchu[fd.cell[i][j].type], fd.cell[i][j].draw_x + 60, fd.cell[i][j].draw_y + 110, this);				
				}
			}
		}
		
/*		//--- テスト用仮処理 ---
		if ( time > 110 ) {
			time = 0;
			now_puchu.turnLeft();
		}
		if ( time2 > 40 ) {
			time2 = 0;
			moveflg = !moveflg;
//			if ( moveflg ) { now_puchu.moveRight(); }
//			else { now_puchu.moveLeft(); }
			now_puchu.turnRight();
		}
		if ( time3 > 6 ) {
			time3 = 0;
			now_puchu.fallDown();
		}
		if ( time4 > 200 ) {
			cell[11][1].vanishOut();
			vanflg = true;
			time4 = 0;
		}
		time++;
		time2++;
		time3++;
		if ( !vanflg ) { time4++; }
*/		//-------------
		
		if ( !fd.now.is_match_posture_right ) { fd.now.drawingTurnRight(); }
		if ( !fd.now.is_match_posture_left ) { fd.now.drawingTurnLeft(); }
		if ( !fd.now.is_match_position ) { fd.now.drawingMove(); }
		img_2d.drawImage(img_puchu[fd.now.puchu1.type], fd.now.puchu1.draw_x + margin_w, fd.now.puchu1.draw_y + margin_h, this);
		img_2d.drawImage(img_puchu[fd.now.puchu2.type], fd.now.puchu2.draw_x + margin_w, fd.now.puchu2.draw_y + margin_h, this);
	}
}