import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;

public class Draw extends JPanel{
	
	public static final int Squares = 40;
	private static final int margin_w = 60, margin_h = 110;
	private static final int PanelW = 480;
	private static final int PanelH = 640;
	public static enum GameInfo { GAME_READY, GAME_PLAYNOW, GAME_WIN, GAME_LOSE };
	private GameInfo game = GameInfo.GAME_PLAYNOW;
	
	private Toolkit tk;
	private Image img_background, img_field;
	private Image img_puchu[] = new Image[9];
	private BufferedImage img_van_anim;
	private BufferedImage[] imgs_van = new BufferedImage[10];
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
	
	private Font score_font = new Font("Dialog", Font.BOLD, 40);
	private Color score_color = Color.white;
	
	private boolean is_alive = true;
	private boolean is_drop_anim = false, is_drop_all = false;
	private boolean is_move_anim = false, is_move_all = false;
	private boolean is_vanish_delay = false, is_vanish_delay_all = false;
	private boolean is_vanish_anim = false, is_vanish_all = false;
	
	private static enum AnimState {state1, state2, state3, state4, state5, end };
	
	private Image img_ready, img_start;
	private AnimState ready_state = AnimState.end;
	private int ready_anim_time = 0;
	private int ready_image_height = -350, ready_image_speed = 100;
	private int start_image_height = -350, start_image_speed = 0, start_image_accel = 4;
	private static final int ready_anim_timing = 100;
	private static final int start_anim_timing = 150;
	private static final int start_delay = 200;
	
	private Image img_end;
	private AnimState end_state = AnimState.state1;
	private int end_anim_time = 0;
	private int end_image_height = -350, end_image_speed = 0, end_image_accel = 2;
	private static final int end_anim_delay = 10;
	private static final int end_image_timing = 100;
	private static final int end_time = 200;
	
	private static enum Meteor {attack, counter, offset};
	private Image img_meteor;
	private AnimState send_state = AnimState.end;
	private Meteor send_mode = Meteor.attack;
	private int send_anim_time = 0;
	private int send_anim_speed = 1;
	private int send_anim_accel = 1;
	private int send_obs_num = 0;
	private int send_image_x = 0, send_image_y = 0;
	private double send_image_angle = 0.0f;
	
	private int obs_num = 0;
	
	//テスト
	int score = 9999;
	
	Draw() {	//nullプレイヤー用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		tk = Toolkit.getDefaultToolkit();
		try {
			img_field = ImageIO.read(getClass().getResource("background.png"));			
		} catch(Exception e) {
			System.out.println(e);
		}
		img_background = tk.getImage(getClass().getResource("backmark.png"));
		
		is_alive = false;
	}
	Draw(Field _fd) { //プレイ用
		this.setPreferredSize(new Dimension(PanelW, PanelH));
		lb = new JLabel();
		lb.setPreferredSize(new Dimension(PanelW, PanelH));
		tk = Toolkit.getDefaultToolkit();
		try {
			img_field = ImageIO.read(getClass().getResource("background.png"));			
		} catch(Exception e) {
			System.out.println(e);
		}
		img_background = tk.getImage(getClass().getResource("backmark.png"));

		is_alive = true;
		fd = _fd;
		initImages();
		
//		for ( int i = 0; i < fd.cell.length; i++ ) {
//			for ( int j = 5; j < fd.cell[i].length; j++ ) {
//				fd.cell[i][j].type = Puchu.Obs;
//			}
//		}
		startReadyAnim();
	}
	
	//-- Image変数の初期化
	private void initImages() {
		for ( int i = 1; i <= 8; i++ ) {
			img_puchu[i] = tk.getImage(getClass().getResource("puchu"+i+".png"));			
		}
//		img_van_anim = tk.getImage(getClass().getResource("vanishingAnime.png"));
		try {
			img_van_anim = ImageIO.read(getClass().getResource("vanishingAnime.png"));
//			img_van_anim = ImageIO.read(new File("vanishingAnime.png"));
			for ( int i = 0; i < 10; i++ ) {
				imgs_van[i] = img_van_anim.getSubimage(i*Draw.Squares*2, 0, Draw.Squares*2, Draw.Squares*2);
			}			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	//-- SE発火
	private void soundIgnition(String _wav) {
		AudioClip _se = Applet.newAudioClip(getClass().getResource(_wav));
		_se.play();
	}
	
	//-- 開幕アニメーション開始
	public void startReadyAnim() {
		game = GameInfo.GAME_READY;
		ready_state = AnimState.state1;
		img_ready = tk.getImage(getClass().getResource("ready.png"));
		img_start = tk.getImage(getClass().getResource("start.png"));
		soundIgnition("youi.wav");
	}
	
	//-- 開幕アニメーションスタート落下開始
	private void nextReadyAnim() {
		ready_state = AnimState.state4;
		soundIgnition("Start.wav");
	}
	
	//-- 開幕アニメーション終了
	private void finishReadyAnim() {
		game = GameInfo.GAME_PLAYNOW;
		ready_state = AnimState.state5;
		System.out.println("ゲーム開始");
		soundIgnition("pahu.wav");
		fd.game_start();
	}
	
	//-- 落下アニメーション開始
	public void startDropAnim() {
		is_drop_anim = true;
		soundIgnition("rakka.wav");
	}
	
	//-- 落下アニメーション終了
	private void finishDropAnim() {
		is_drop_anim = false;
		is_drop_all = false;
//		fd.drop_finish();
		
		//テスト
		if ( fd.cell[2][2].type != Puchu.Emp ) { startEndAnim(GameInfo.GAME_LOSE);}
		else if ( fd.cell[0][2].type!= Puchu.Emp ) { startEndAnim(GameInfo.GAME_WIN);}
		else { fd.drop_finish();}
//		else {
//			fd.cell[5][13].vanishOut();
//			fd.cell[4][13].vanishOut();
//			fd.cell[3][13].vanishOut();
//			fd.cell[5][12].vanishOut();
//			startVanishAnim(1, 10);
//		}
	}
	
	//-- ぷちゅペアの移動アニメーション開始
	public void startMoveAnim() {
		is_move_anim = true;
	}
	
	//-- ぷちゅペアの移動アニメーション終了
	private void finishMoveAnim() {
		is_move_anim = false;
		is_move_all = false;
		fd.switch_next();
	}
	
	//-- ぷちゅの消滅アニメーション開始
	public void startVanishAnim(int _chain, int _generate_obs_num) {
		is_vanish_delay = true;
		is_vanish_delay_all = false;
		chain_text = _chain + "れんさ";
		is_chain_get = true;
		send_obs_num = _generate_obs_num;
	}
	
	//-- ぷちゅの消滅準備完了
	private void nextVanishAnim() {
		soundIgnition("delpuchu.wav");
		is_vanish_delay = false;
		is_vanish_all = false;
		is_vanish_anim = true;
		is_chain_display = true;
		chain_display_time = 0;
//		startAttackAnim();
	}
	
	//-- ぷちゅの消滅アニメーション終了
	private void finishVanishAnim() {
		is_vanish_anim = false;
		is_vanish_all = false;
		fd.vanish_finish();
	}
	
	//-- おじゃま発射アニメーション開始
	private void startAttackAnim() {
		send_state = AnimState.state1;
		send_image_x = chain_x;
		send_image_y = chain_y;
//		send_image_angle = Math.atan2((send_image_x - send_center_x), (send_image_y - send_center_y));
//		send_image_angle = Math.atan2(send_center_x - send_image_y, send_center_y - send_image_y );
		if ( obs_num == 0 ) { send_mode = Meteor.attack; }
		else if ( obs_num - send_obs_num < 0 ) { send_mode = Meteor.counter; }
		else { send_mode = Meteor.offset; }
	}
	
	private void finishAttackAnim() {
		
	}
	
	private void setObsNum() {
//		obs_num = fd.obs_num;
	}
	
	//-- 決着時のアニメーション開始
	public void startEndAnim(GameInfo _game) {
		game = _game;
		if ( _game == GameInfo.GAME_WIN ) { img_end = tk.getImage(getClass().getResource("yatta.png")); }
		if ( _game == GameInfo.GAME_LOSE ) { img_end = tk.getImage(getClass().getResource("patankyu.png")); soundIgnition("lose.wav"); }
		end_state = AnimState.state1;
		end_anim_time = 0;
	}
	
	//-- イメージ落下開始
	private void startEndImgDrop() {
		end_state = AnimState.state2;
	}
	
	//-- イメージバウンド(1回目)
	private void nextEndAnim() {
		end_state = AnimState.state3;
		 soundIgnition("wintext.wav");
	}
	
	//-- イメージバウンド停止
	private void finishEndImageMovement() {
		end_state = AnimState.state4;
		if ( game == GameInfo.GAME_LOSE ) { soundIgnition("losegirl.wav"); }
		if ( game == GameInfo.GAME_WIN ) { soundIgnition("wingirl.wav"); }
	}
	
	//-- 決着時のアニメーション終了
	private void finishEndAnim() {
		System.out.println("おわり");
		end_state = AnimState.end;
		fd.game_end();
	}
	
	//-- 開幕アニメーション状況更新
	private void updateReadyAnim() {
		switch(ready_state) {
		case state1:
			ready_anim_time++;
			ready_image_height = -(int)(Math.pow((ready_anim_timing - ready_anim_time),2)/10);
			if ( ready_anim_time > ready_anim_timing ) { ready_state = AnimState.state2; }
			break;
		case state2:
			ready_anim_time++;
			if ( ready_anim_time > start_anim_timing ) { ready_state = AnimState.state3; }
			break;
		case state3:
			ready_anim_time++;
			start_image_speed += start_image_accel;
			start_image_height += start_image_speed/5;
			if ( start_image_height > -80 && start_image_speed >0 ) { start_image_speed = (int)(start_image_speed * -0.7); nextReadyAnim(); }
			break;
		case state4:
			ready_anim_time++;
			start_image_speed += start_image_accel;
			start_image_height += start_image_speed/10;
			ready_image_height += ready_image_speed;
			if ( ready_anim_time > start_delay ) { finishReadyAnim(); }
			break;
		case state5:
			ready_anim_time++;
			start_image_speed += start_image_accel;
			start_image_height += start_image_speed/10;
			if ( ready_anim_time > 300 ) { ready_state = AnimState.end; }
			break;
		}
	}
	
	//-- 決着時のアニメーション状況更新
	private void updateEndImgDrop() {
		switch(end_state) {
		case state1:
			end_anim_time++;
			if ( end_anim_time > end_image_timing ) { startEndImgDrop(); }
			break;
		case state2 :
			end_anim_time++;
			end_image_speed += end_image_accel; 
			end_image_height += end_image_speed;
			if ( end_image_height > 0 && end_image_speed > 0) { end_image_speed = (int)(end_image_speed * -0.7); nextEndAnim(); }
			break;
		case state3 :
			end_anim_time++;
			end_image_speed += end_image_accel; 
			end_image_height += end_image_speed;
			if ( end_image_height > 0 && end_image_speed > 0) { end_image_speed = (int)(end_image_speed * -0.7); }
			if ( end_image_height >= 0 && Math.abs( end_image_speed ) <= 1 ) { finishEndImageMovement();}
			break;
		case state4 :
			end_anim_time++;
			if ( end_anim_time > end_time ) { finishEndAnim(); }
			break;
		}
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
	
	private void updateAttackAnim() {
		send_anim_time++;
		switch(send_mode) {
		case attack:
			updateSendAnim();
			break;
		case offset:
			updateOffsetAnim();
			break;
		case counter:
			updateCounterAnim();
			break;
		}
	}
	
	private void updateSendAnim() {
		switch(send_state) {
		case state1:
			break;
		case state2:
			break;
		}
	}
	private void updateOffsetAnim() {
		
	}
	private void updateCounterAnim() {
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D img_2d = (Graphics2D) g;
//		buffer = createImage(PanelW, PanelH);
//		Graphics img_2d = buffer.getGraphics();
		img_2d.drawImage(img_background, 0, 0, this);

		if ( is_alive ) {
			// 初期処理
			if ( is_drop_anim ) { is_drop_all = true; }
			if ( is_move_anim ) { is_move_all = true; }
			if ( is_vanish_anim ) { is_vanish_all = true; }
			if ( is_vanish_delay ) { is_vanish_delay_all = true; }
			
			// 盤面配列内のぷちゅ描写
			for( int i = 0; i < fd.cell.length; i++ ) {
				for ( int j = 0; j < fd.cell[i].length; j++ ) {
					if ( fd.cell[i][j].type == Puchu.Emp) { continue; }
					switch( game ) {
					case GAME_PLAYNOW: // プレイ中の盤面更新
						updateCellAnim(fd.cell[i][j]);
						break;
					case GAME_WIN: // 勝利時の盤面更新
						fd.cell[i][j].endGameAnim(true, end_anim_time - j*end_anim_delay/3);
						break;
					case GAME_LOSE: // 敗北時の盤面更新
						fd.cell[i][j].endGameAnim(false, end_anim_time - i*end_anim_delay);
						break;
					}
					if ( fd.cell[i][j].type == Puchu.Vanishing ) {
						img_2d.drawImage(imgs_van[fd.cell[i][j].van_flame_count], fd.cell[i][j].draw_x + margin_w - Draw.Squares/2, fd.cell[i][j].draw_y + margin_h - Draw.Squares/2, this);
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
		img_2d.drawImage(img_field, 0, 0, this);
		
		if ( is_alive ) {
			// 予告おじゃまぷちゅ描写
			if ( obs_num != 0 ) {
				for ( int i = obs_num-1; i >= 0; i-- ) {
					if ( obs_num <= fd.cell.length ) {
						img_2d.drawImage(img_puchu[Puchu.Obs], margin_w + Draw.Squares*i, margin_h - Draw.Squares - 5, this);									
					} else {
						img_2d.drawImage(img_puchu[Puchu.Obs], margin_w + (int)(Draw.Squares * ((fd.cell.length - 1.0) / (obs_num-1)))*i, margin_h - Draw.Squares - 5, this);									
					}
				}
			}
			if ( send_state != AnimState.end ) {
				updateAttackAnim();
				img_2d.drawImage(img_puchu[Puchu.Obs], send_image_x, send_image_y, this);
			}
			
			// スコア表示
			img_2d.setColor(score_color);
			img_2d.setFont(score_font);
			img_2d.drawString(String.format("%07d", score ), margin_w+Draw.Squares*6+5, 500);
			
			// れんさテキスト描写
			if ( is_chain_display ) {
				chain_display_time++;
				img_2d.setColor(chain_color);
				img_2d.setFont(chain_font);
				img_2d.drawString(chain_text, chain_x, chain_y - chain_display_time);
				if ( chain_display_time > max_chain_display_time ) { is_chain_display = false; }
			}
			
			// ゲーム中以外のタイミング管理
			switch(game) {
			case GAME_PLAYNOW:
			case GAME_READY :
				if ( ready_state != AnimState.end ) {			
					updateReadyAnim();
					img_2d.drawImage(img_ready, margin_w, ready_image_height+ margin_h, this);
					img_2d.drawImage(img_start, margin_w, start_image_height + margin_h, this);
				}
				break;
			case GAME_WIN :
			case GAME_LOSE :
				updateEndImgDrop();
				img_2d.drawImage(img_end, margin_w, end_image_height + margin_h, this);
				break;
			}
		}	
//		g.drawImage(buffer, 0, 0, this);		
	}
}