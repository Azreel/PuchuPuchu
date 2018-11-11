import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.*;

import javax.swing.*;

public class Draw extends JPanel{
	
	public static final int Squares = 40;
	private static final int margin_w = 60, margin_h = 110;
	private static final int PanelW = 480;
	private static final int PanelH = 640;
	
	private Toolkit tk;
	private Image img_background;
	private Image img_puchu[] = new Image[10];
	private JLabel lb;	//ラベル
	private Field fd;
	private Image buffer;
	
	private String chain_text;
	private int chain_x = 0, chain_y = 0;
	private int chain_display_time = 0;
	private static final int max_chain_display_time = 60;
	private boolean is_chain_get = false;
	private boolean is_chain_display = false;
	
	private boolean is_alive = true;
	private boolean is_drop_anim = false, is_drop_all = false;
	private boolean is_move_anim = false, is_move_all = false;
	private boolean is_vanish_delay = false, is_vanish_delay_all = false;
	private boolean is_vanish_anim = false, is_vanish_all = false;
	
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
	Draw(Field _fd) { //プレイ用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		
		tk = Toolkit.getDefaultToolkit();
		img_background = tk.getImage(getClass().getResource("fieldbackground.png"));
		
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
		img_puchu[9] = tk.getImage(getClass().getResource("VanishAnimation.gif"));
	}
	
	//-- 落下アニメーション開始
	public void startDropAnim() {
		is_drop_anim = true;
	}
	
	//-- 落下アニメーション終了
	private void finishDropAnim() {
		is_drop_anim = false;
		
		// テスト
		fd.cell[4][13].vanishOut();
		fd.cell[5][12].vanishOut();
		fd.cell[5][13].vanishOut();
		fd.cell[3][13].vanishOut();
//		fd.cell[1][13].vanishOut();
		startVanishAnim(1);
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
	
	public void startVanishAnim(int _chain) {
		is_vanish_delay = true;
		chain_text = _chain + "れんさ";
		is_chain_get = true;
	}
	
	private void nextVanishAnim() {
		is_vanish_delay = false;
		is_vanish_anim = true;
		is_chain_display = true;
		chain_display_time = 0;
		
		// テスト
		System.out.println("消滅アニメーション開始します");
	}
	
	private void finishVanishAnim() {
		is_vanish_anim = false;
		
		// テスト
		System.out.println("消滅アニメーション終わりました");
	}
		
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D img_2d = (Graphics2D) g;
//		Graphics img_2d = buffer.getGraphics();
		img_2d.setFont(new Font("Dialog",Font.BOLD,30));
		img_2d.setColor(Color.BLUE);

		if ( is_alive ) {
			// テスト
			if ( time_flg ) { time++; }
			if ( time > 200 ) { 
				testDrop(5, 3, 13);
				testDrop(5, 1, 12);
				testDrop(4, 7, 13);
				testDrop(3, 7, 13);
				time = 0; 
				startDropAnim(); 
				time_flg = false;
			}
			time2++;
			if ( time2 > 200 ) {
				time2 = 0;
				testMove(fd.next[0], 80, -40);
				testMove(fd.next[1], 265, -40);
				testMove(fd.next[2], 290, 50);
				startMoveAnim();
			}
			
			// 初期処理
			if ( is_drop_anim ) { is_drop_all = true; }
			if ( is_move_anim ) { is_move_all = true; }
			if ( is_vanish_delay ) { is_vanish_delay_all = true; }
			if ( is_vanish_anim ) { is_vanish_all = true; }
			
			// 盤面配列内のぷちゅ描写
			for( int i = 0; i < fd.cell.length; i++ ) {
				for ( int j = 0; j < fd.cell[i].length; j++ ) {
					if ( fd.cell[i][j].type != Puchu.Emp) {
						// 落下アニメーション管理
						if ( is_drop_anim && !fd.cell[i][j].is_match_position_drop ) {
							is_drop_all = false;
							fd.cell[i][j].drawingDropDown();
						}
						// 消滅前のディレイ管理
						if ( is_vanish_delay && fd.cell[i][j].type == Puchu.Van ) {
							is_vanish_delay_all = false;
							fd.cell[i][j].vanishOutDelay();
							if ( is_chain_get ) { // れんさテキスト表示のための座標取得(1個目の場所)
								is_chain_get = false;
								chain_x = fd.cell[i][j].draw_x + 60;
								chain_y = fd.cell[i][j].draw_y + 60;
							}
						}
						// 消滅アニメーション管理
						if ( is_vanish_anim && fd.cell[i][j].type == Puchu.Vanishing ) {
							is_vanish_all = false;
							fd.cell[i][j].vanishOutAnim();
							img_2d.drawImage(img_puchu[fd.cell[i][j].type], fd.cell[i][j].draw_x + margin_w - Draw.Squares/2, fd.cell[i][j].draw_y + margin_h - Draw.Squares/2, this);
						} else {
							img_2d.drawImage(img_puchu[fd.cell[i][j].type], fd.cell[i][j].draw_x + margin_w, fd.cell[i][j].draw_y + margin_h, this);
						}
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
			if ( is_vanish_delay && is_vanish_delay_all ) { nextVanishAnim(); }
			if ( is_vanish_anim && is_vanish_all ) { finishVanishAnim(); }
		}
		// 背景描写
		img_2d.drawImage(img_background, 0, 0, this);
		
		// れんさテキスト描写
		if ( is_chain_display ) {
			chain_display_time++;
			img_2d.drawString(chain_text, chain_x, chain_y - chain_display_time);
			if ( chain_display_time > max_chain_display_time ) { is_chain_display = false; }
		}
//		g.drawImage(buffer, 0, 0, this);
	}
}