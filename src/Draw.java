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
	private Image img_puchu[] = new Image[9];
	private Image img_van_anim;
	private JLabel lb;	//ラベル
	private Field fd;
	private Image buffer;
	
	private String chain_text;
	private Font chain_font = new Font("Dialog",Font.BOLD,30);
	private Color chain_color = Color.blue;
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
		
		is_alive = true;
		fd = _fd;
		initImages();
	}
	
	//-- Image変数の初期化
	private void initImages() {
		for ( int i = 1; i <= 8; i++ ) {
			img_puchu[i] = tk.getImage(getClass().getResource("puchu"+i+".png"));			
		}
		img_van_anim = tk.getImage(getClass().getResource("VanishAnimation.gif"));
	}
	
	//-- 落下アニメーション開始
	public void startDropAnim() {
		is_drop_anim = true;
	}
	
	//-- 落下アニメーション終了
	private void finishDropAnim() {
		is_drop_anim = false;
		fd.drop_finish();
	}
	
	//-- ぷちゅペアの移動アニメーション開始
	public void startMoveAnim() {
		is_move_anim = true;
	}
	
	//-- ぷちゅペアの移動アニメーション終了
	private void finishMoveAnim() {
		is_move_anim = false;
		fd.switch_next();
	}
	
	//-- ぷちゅの消滅アニメーション開始
	public void startVanishAnim(int _chain) {
		is_vanish_delay = true;
		
		chain_text = _chain + "れんさ";
		is_chain_get = true;
	}
	
	//-- ぷちゅの消滅準備完了
	private void nextVanishAnim() {
		is_vanish_delay = false;
		is_vanish_anim = true;
		is_chain_display = true;
		chain_display_time = 0;
	}
	
	//-- ぷちゅの消滅アニメーション終了
	private void finishVanishAnim() {
		is_vanish_anim = false;
		fd.vanish_finish();
	}
	
	//-- 盤面のアニメーション状況更新
	private void updateCellAnim(Puchu _puchu) {
		// 落下アニメーション管理
		if ( is_drop_anim && !_puchu.is_match_position_drop ) {
			is_drop_all = false;
			_puchu.drawingDropDown();
		}
		// 消滅前のディレイ管理
		if ( is_vanish_delay && _puchu.type == Puchu.Van ) {
			is_vanish_delay_all = false;
			_puchu.vanishOutDelay();
			if ( is_chain_get ) { // れんさテキスト表示のための座標取得(1個目の場所)
				is_chain_get = false;
				chain_x = _puchu.draw_x + 60;
				chain_y = _puchu.draw_y + 60;
			}
		}
		// 消滅アニメーション管理
		if ( is_vanish_anim && _puchu.type == Puchu.Vanishing ) {
			is_vanish_all = false;
			_puchu.vanishOutAnim();
		}
	}
	
	//-- ぷちゅペアのアニメーション状況更新
	private void updatePuchuPairAnim(PuchuPair _pair) {
		if ( !_pair.is_match_posture_right ) { _pair.drawingTurnRight(); } // 右回転
		if ( !_pair.is_match_posture_left ) { _pair.drawingTurnLeft(); } // 左回転
		if ( !_pair.is_match_position_slide ) { _pair.drawingSlide(); } // 横移動
		// 位置移動アニメーション管理
		if ( is_move_anim && !_pair.is_match_position_move ) {
			is_move_all = false; 
			_pair.drawingMovePosition(); 
		}
	}
		
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D img_2d = (Graphics2D) g;
//		buffer = createImage(PanelW, PanelH);
//		Graphics img_2d = buffer.getGraphics();
		img_2d.setFont(new Font("Dialog",Font.BOLD,30));
		img_2d.setColor(Color.BLUE);

		if ( is_alive ) {
			// 初期処理
			if ( is_drop_anim ) { is_drop_all = true; }
			if ( is_move_anim ) { is_move_all = true; }
			if ( is_vanish_delay ) { is_vanish_delay_all = true; }
			if ( is_vanish_anim ) { is_vanish_all = true; }
			
			// 盤面配列内のぷちゅ描写
			for( int i = 0; i < fd.cell.length; i++ ) {
				for ( int j = 0; j < fd.cell[i].length; j++ ) {
					if ( fd.cell[i][j].type == Puchu.Emp) { continue; }
					updateCellAnim(fd.cell[i][j]);
					if ( fd.cell[i][j].type == Puchu.Vanishing ) {
						img_2d.drawImage(img_van_anim, fd.cell[i][j].draw_x + margin_w - Draw.Squares/2, fd.cell[i][j].draw_y + margin_h - Draw.Squares/2, this);
					} else {
						img_2d.drawImage(img_puchu[fd.cell[i][j].type], fd.cell[i][j].draw_x + margin_w, fd.cell[i][j].draw_y + margin_h, this);
					}
				}
			}
			// 操作中のぷちゅペア描写
			if ( fd.now != null ) {
				updatePuchuPairAnim(fd.now);
				img_2d.drawImage(img_puchu[fd.now.puchu1.type], fd.now.puchu1.draw_x + margin_w, fd.now.puchu1.draw_y + margin_h, this);
				img_2d.drawImage(img_puchu[fd.now.puchu2.type], fd.now.puchu2.draw_x + margin_w, fd.now.puchu2.draw_y + margin_h, this);				
			}
			// nextぷちゅ描写
			for ( int i = 0; i < fd.next.length; i++ ) {
				updatePuchuPairAnim(fd.next[i]);
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