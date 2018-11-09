import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Draw extends JPanel{
	
	public static final int Squares = 40;
	private static final int margin_w = 60, margin_h = 110;
	
	final int PanelW = 480;
	final int PanelH = 640;
	
	Toolkit tk;
	Image img_background;
	Image img_puchu[] = new Image[9];
	JLabel lb;	//ラベル
	Field fd;
	
	boolean is_alive = true;
	
	Draw() {	//nullプレイヤー用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img_background = tk.getImage(getClass().getResource("fieldbackground.png"));
		
		is_alive = false;
	}
	Draw(Field _fd) {	//プレイ用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img_background = tk.getImage(getClass().getResource("fieldbackground.png"));
		
		fd = _fd;
		fd.next[0].movePosition(290, 50);
		fd.next[1].movePosition(265,  -40);
		
		initImage();
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


		if ( is_alive ) {
			for( int i = 0; i < fd.cell.length; i++ ) {
				for ( int j = 0; j < fd.cell[i].length; j++ ) {
					if ( fd.cell[i][j].type != Puchu.Emp) {
						if ( !fd.cell[i][j].is_match_position ) { fd.cell[i][j].drawingDropDown(); }
						if ( fd.cell[i][j].type == Puchu.Van ) { fd.cell[i][j].vanishOutDelay(); }
						img_2d.drawImage(img_puchu[fd.cell[i][j].type], fd.cell[i][j].draw_x + 60, fd.cell[i][j].draw_y + 110, this);				
					}
				}
			}
			if ( !fd.now.is_match_posture_right ) { fd.now.drawingTurnRight(); }
			if ( !fd.now.is_match_posture_left ) { fd.now.drawingTurnLeft(); }
			if ( !fd.now.is_match_position_slide ) { fd.now.drawingSlide(); }
			img_2d.drawImage(img_puchu[fd.now.puchu1.type], fd.now.puchu1.draw_x + margin_w, fd.now.puchu1.draw_y + margin_h, this);
			img_2d.drawImage(img_puchu[fd.now.puchu2.type], fd.now.puchu2.draw_x + margin_w, fd.now.puchu2.draw_y + margin_h, this);
			for ( int i = 0; i < fd.next.length; i++ ) {
				if ( !fd.next[i].is_match_position_move ) { fd.next[i].drawingMovePosition(); }
				img_2d.drawImage(img_puchu[fd.next[i].puchu1.type], fd.next[i].puchu1.draw_x + margin_w, fd.next[i].puchu1.draw_y + margin_h, this);
				img_2d.drawImage(img_puchu[fd.next[i].puchu2.type], fd.next[i].puchu2.draw_x + margin_w, fd.next[i].puchu2.draw_y + margin_h, this);
			}
			
			
		}
		img_2d.drawImage(img_background, 0, 0, this);
	}
	
}