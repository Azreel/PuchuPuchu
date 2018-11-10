import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Draw extends JPanel{
	
	public static final int Squares = 40;
	private static final int margin_w = 60, margin_h = 110;
	private static final int PanelW = 480;
	private static final int PanelH = 640;
	
	private Toolkit tk;
	private Image img_background;
	private Image img_puchu[] = new Image[9];
	private JLabel lb;	//ラベル
	private Field fd;
	
	private boolean is_alive = true;
	private boolean is_drop_anim = false, is_drop_all = false;
	private boolean is_move_anim = false, is_move_all = false;
	
	//テスト用変数
	int time = 0;
	boolean time_flg = true;
	int time2 = 0;
	
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
		
		fd = _fd;
		
		initImages();
	}
	
	// 落下テスト用メソッド
	private void testDrop(int x, int before, int after ) {
		fd.cell[x][before].type = Puchu.Pat1;
		fd.cell[x][after].copyPuchu(fd.cell[x][before]);
		fd.cell[x][after].dropDown(after);
		if ( before != after ) { fd.cell[x][before].type = Puchu.Emp; }
	}
	// 移動テスト用メソッド
	private void testMove(PuchuPair _pp, int x, int y) {
		_pp.movePosition(x, y);
	}
	private void testSwitch() {
		Random rnd = new Random();
		fd.now = fd.next[0];
		fd.next[0] = fd.next[1];
		fd.next[1] = fd.next[2];
		fd.next[2] = new PuchuPair(rnd.nextInt(6)+1, rnd.nextInt(6)+1);
	}
	
	//-- Image変数の初期化
	private void initImages() {
		for ( int i = 1; i <= 8; i++ ) {
			img_puchu[i] = tk.getImage(getClass().getResource("puchu"+i+".png"));			
		}
		img_background = tk.getImage(getClass().getResource("fieldbackground.png"));
	}
	
	//-- 落下アニメーション開始
	public void startDropAnim() {
		is_drop_anim = true;
	}
	
	//-- 落下アニメーション終了
	private void finishDropAnim() {
		is_drop_anim = false;
		
		// テスト
		fd.cell[0][13].vanishOut();
		fd.cell[1][13].vanishOut();
		System.out.println("落下アニメーションぜんぶおわったよー");
	}
	
	//-- ぷちゅペアの移動アニメーション開始
	public void startMoveAnim() {
		is_move_anim = true;
	}
	
	//-- ぷちゅペアの移動アニメーション終了
	private void finishMoveAnim() {
		is_move_anim = false;
		
		// テスト
		System.out.println("移動アニメーションおわったー");
		testSwitch();
	}
		
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D img_2d = (Graphics2D) g;

		if ( is_alive ) {
			// テスト
//			if ( time_flg ) { time++; }
//			if ( time > 200 ) { 
//				testDrop(0, 1, 13);
//				testDrop(1, 3, 13);
//				testDrop(4, 7, 13);
//				time = 0; 
//				startDropAnim(); 
//				time_flg = false;
//			}
//			time2++;
//			if ( time2 > 200 ) {
//				time2 = 0;
//				testMove(fd.next[0], 80, -40);
//				testMove(fd.next[1], 265, -40);
//				testMove(fd.next[2], 290, 50);
//				startMoveAnim();
//			}
			
			// 初期処理
			if ( is_drop_anim ) { is_drop_all = true; }
			if ( is_move_anim ) { is_move_all = true; }
			
			// 盤面配列内のぷちゅ描写
			for( int i = 0; i < fd.cell.length; i++ ) {
				for ( int j = 0; j < fd.cell[i].length; j++ ) {
					if ( fd.cell[i][j].type != Puchu.Emp) {
						// 落下アニメーション管理
						if ( is_drop_anim && !fd.cell[i][j].is_match_position_drop ) {
							is_drop_all = false;
							fd.cell[i][j].drawingDropDown();
						}
						// 消滅アニメーション管理
						if ( fd.cell[i][j].type == Puchu.Van ) { fd.cell[i][j].vanishOutDelay(); }
						img_2d.drawImage(img_puchu[fd.cell[i][j].type], fd.cell[i][j].draw_x + margin_w, fd.cell[i][j].draw_y + margin_h, this);				
					}
				}
			}
			
			// 操作中のぷちゅペア描写(回転及びスライドアニメーション)
			if ( !fd.now.is_match_posture_right ) { fd.now.drawingTurnRight(); }
			if ( !fd.now.is_match_posture_left ) { fd.now.drawingTurnLeft(); }
			if ( !fd.now.is_match_position_slide ) { fd.now.drawingSlide(); }
			img_2d.drawImage(img_puchu[fd.now.puchu1.type], fd.now.puchu1.draw_x + margin_w, fd.now.puchu1.draw_y + margin_h, this);
			img_2d.drawImage(img_puchu[fd.now.puchu2.type], fd.now.puchu2.draw_x + margin_w, fd.now.puchu2.draw_y + margin_h, this);
			
			// nextぷちゅ描写
			for ( int i = 0; i < fd.next.length; i++ ) {
				// 移動アニメーション管理
				if ( is_move_anim && !fd.next[i].is_match_position_move ) {
					is_move_all = false; 
					fd.next[i].drawingMovePosition(); 
				}
				img_2d.drawImage(img_puchu[fd.next[i].puchu1.type], fd.next[i].puchu1.draw_x + margin_w, fd.next[i].puchu1.draw_y + margin_h, this);
				img_2d.drawImage(img_puchu[fd.next[i].puchu2.type], fd.next[i].puchu2.draw_x + margin_w, fd.next[i].puchu2.draw_y + margin_h, this);
			}
			// 終端処理
			if ( is_drop_anim && is_drop_all ) { finishDropAnim(); }
			if ( is_move_anim && is_move_all ) { finishMoveAnim(); }
		}
		img_2d.drawImage(img_background, 0, 0, this);
	}
}