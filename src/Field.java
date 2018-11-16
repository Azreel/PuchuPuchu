import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;

public class Field {
	
	public int score = 0;
	private int sub_score = 0;
	private int rn_score = 0;
	public Puchu[][] cell = new Puchu[6][14];
	public Puchu first_puchu;
	public PuchuPair now;
	public PuchuPair[] next = new PuchuPair[3];
	public Draw draw;
	public Key key;
	public AudioClip turn_sound;
	public AudioClip slide_sound;
	public int unfallen_obs = 0;	//落ちてこないお邪魔ぷちゅ
	public int fallen_obs = 0;		//落ちてくるお邪魔ぷちゅ
	
	public boolean bottom_flag = false; 	//当たり判定
	public boolean moving_flag = false;		//移動できるかどうか
	private boolean bottom_p1_flag = false;	//どのぷちゅが底にあるか
	private boolean bottom_p2_flag = false;	
	private boolean turn_left_flag = true;	//左回転できるか
	private boolean turn_right_flag = true;	//右回転できるか
	private boolean slide_left_flag = true;	//左に移動できるか
	private boolean slide_right_flag = true;//右に移動できるか
	private boolean game_end_flag = false;
	private boolean van_puchu = false;
	private boolean is_drop = false;
	private boolean is_me = true;
	private boolean fallen = false;
	
	private int now_x = 0;
	private int now_y = 0;
	private int obs_count = 0;
	private int time = 0;
	private int landing_time = 0;
	private static final int max_land_time = 0;
	private int k = 0;
	private int comb_figure1 = 0;
	private int comb_figure2 = 0;
	private int chain_count = 0;	//連鎖数
	private int switch_figure;
	private int speed = 1;
	private int[] comb_point = {0, 8, 16, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480, 512, 544, 576, 608, 640, 672, 704, 736};
	private int[] link_point = {0, 2, 3, 4, 5, 6, 7, 10};
	private int[] color_point = {0, 3, 6, 12, 24, 48};
	private int[] link_all = {9, 16, 18, 34, 36, 45, 50, 52};
	private int[][] real_link = {{3, 3}, {4, 3}, {3, 3}, {3, 5}, {3, 6}, {3, 6}, {5, 5}, {6, 4}};
	private int[][] cp_player = new int[GameMain.PPSIZE][2];
	private int[] color_check = {0, 0, 0, 0, 0, 0};
	private GameMain gm;
	private int puchu_count = 0;	//消えるぷちゅの個数
	private int link_count = 0;		//消えるぷちゅの連結数
	private int color_count = 0;	//消えるぷちゅの種類
	private int puchu_index = 0;	//降った総数
	
	public Field(GameMain game, int[][] player, boolean me) {			//nullプレイヤー用
		
		gm = game;
		is_me = me;
		init_cell();
		if ( player != null ) {
			draw = new Draw(this);
			key = new Key(gm);
			init_player(player);
			create_puchu(player);
			turn_sound = Applet.newAudioClip(getClass().getResource("ojama.wav"));
			slide_sound = Applet.newAudioClip(getClass().getResource("slide.wav"));
			draw.addKeyListener(key);
		} else {
			draw = new Draw();
		}
		//gm.fadeOut();
	}

	
	private void create_puchu(int[][] rnd_list){	//first create
		
		for ( int i = 0; i < next.length; i++ ) {
			next[i] = new PuchuPair(rnd_list[i][0], rnd_list[i][1]);	//次のぷちゅ
			next[i].setPosition(265+25*i, -40+90*i);
		}
		switch_figure = 3;
	}
	
	public void switch_next() {	//ぷちゅの更新
		now = next[0];
		puchu_index++;
		
		for ( int i = 0; i < 2; i++ ) {
			next[i] = next[i+1];	//次のぷちゅの更新
		}
		
		next[2] = new PuchuPair(cp_player[switch_figure][0], cp_player[switch_figure][1]);	//次の次のぷちゅの更新
		switch_figure++;
		if ( switch_figure == GameMain.PPSIZE ) {
			switch_figure = 0;
		}
		next[0].setPosition(265, -40);
		next[1].setPosition(290, 50);
		next[2].setPosition(315, 140);
		moving_flag = true;
		key.canInput(true);
	}
	
	//ぷちゅの当たり判定
	public void hit_puchu() {
		if ( now.form == PuchuPair.Up ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			if ( now_y == 13 || cell[now_x][now_y+1].type != Puchu.Emp ) {
				bottom_flag = true;
				bottom_p1_flag = true;
				bottom_p2_flag = true;
			}
			if ( now_x >= 5) {
				slide_right_flag = false;
				turn_right_flag = false;
				
				if ( cell[now_x-1][now_y].type != Puchu.Emp ) {
					slide_left_flag = false;
					turn_left_flag = false;
				}
			} else if ( cell[now_x+1][now_y].type != Puchu.Emp ) {
				slide_right_flag = false;
				turn_right_flag = false;
			}
			if ( now_x <= 0) {
				slide_left_flag = false;
				turn_left_flag = false;
				
				if ( cell[now_x+1][now_y].type != Puchu.Emp ) {
					slide_right_flag = false;
					turn_right_flag = false;
				}
			} else if ( cell[now_x-1][now_y].type != Puchu.Emp ) {
				slide_left_flag = false;
				turn_left_flag = false;
			}
		} else if ( now.form == PuchuPair.Right ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			if ( now_y == 13 ) {
				bottom_flag = true;
				bottom_p1_flag = true;
				bottom_p2_flag = true;
			} else if ( cell[now_x][now_y+1].type != Puchu.Emp ) {
				bottom_p1_flag = true;
				bottom_flag = true;
			} else if ( now_x < 5 && cell[now_x+1][now_y+1].type != Puchu.Emp ) {
				bottom_p2_flag = true;
				bottom_flag = true;
			}
			if ( now_x >= 4 && cell[now_x-1][now_y].type != Puchu.Emp ) {
				slide_right_flag = false;
				slide_left_flag = false;
			} else if ( now_x <= 0 && cell[now_x+2][now_y].type != Puchu.Emp ) {
				slide_left_flag = false;
				slide_right_flag = false;
			} else if ( now_x >= 4 ) {
				slide_right_flag = false;
			} else if ( now_x <= 0 ) {
				slide_left_flag = false;
			} else if ( cell[now_x-1][now_y].type != Puchu.Emp ) {
				slide_left_flag = false;
			} else if ( cell[now_x+2][now_y].type != Puchu.Emp ) {
				slide_right_flag = false;
			}
		} else if ( now.form == PuchuPair.Bottom ) {
			now_x = ( now.puchu2.x ) / 40;
			now_y = ( now.puchu2.y ) / 40 + 2;
			
			if ( now_y == 13 || cell[now_x][now_y+1].type != Puchu.Emp ) {
				bottom_flag = true;
				bottom_p1_flag = true;
				bottom_p2_flag = true;
			}
			if ( now_x >= 5) {
				slide_right_flag = false;
				turn_left_flag = false;
				
				if ( cell[now_x-1][now_y].type != Puchu.Emp ) {
					slide_left_flag = false;
					turn_right_flag = false;
				}
			} else if ( cell[now_x+1][now_y].type != Puchu.Emp ) {
				slide_right_flag = false;
				turn_left_flag = false;
			}
			if ( now_x <= 0) {
				slide_left_flag = false;
				turn_right_flag = false;
				
				if ( cell[now_x+1][now_y].type != Puchu.Emp ) {
					slide_right_flag = false;
					turn_left_flag = false;
				}
			} else if ( cell[now_x-1][now_y].type != Puchu.Emp ) {
				slide_left_flag = false;
				turn_right_flag = false;
			}
		} else {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			if ( now_y == 13 ) {
				bottom_flag = true;
				bottom_p1_flag = true;
				bottom_p2_flag = true;
			} else if ( cell[now_x][now_y+1].type != Puchu.Emp ) {
				bottom_p1_flag = true;
				bottom_flag = true;
			} else if ( cell[now_x-1][now_y+1].type != Puchu.Emp ) {
				bottom_p2_flag = true;
				bottom_flag = true;
			}
			if ( now_x >= 5 && cell[now_x-2][now_y].type != Puchu.Emp ) {
				slide_right_flag = false;
				slide_left_flag = false;
			} else if ( now_x <= 1 && cell[now_x+1][now_y].type != Puchu.Emp ) {
				slide_right_flag = false;
				slide_left_flag = false;
			} else if ( now_x >= 5 ) {
				slide_right_flag = false;
			} else if ( now_x <= 1 ) {
				slide_left_flag = false;
			} else if ( cell[now_x-2][now_y].type != Puchu.Emp ) {
				slide_left_flag = false;
			} else if ( cell[now_x+1][now_y].type != Puchu.Emp ) {
				slide_right_flag = false;
			}
		}
	}
	
	private void init_cell() {
		
		for ( int i = 0; i < 6; i++ ) {
			for ( int j = 0; j < 14; j++ ) {
				cell[i][j] = new Puchu(Puchu.Emp, i*Draw.Squares, (j-2)*Draw.Squares);
			}
		}
	}
	
	private int puchu_comb(int coord_x, int coord_y) {
		int figure = 1;
		cell[coord_x][coord_y].is_check = true;
		
		if ( coord_y > 2 && cell[coord_x][coord_y-1].is_check == false && cell[coord_x][coord_y].type == cell[coord_x][coord_y-1].type ) {		//フレーム内かつ上が同じpuchu
			figure += puchu_comb(coord_x, coord_y-1);
		}
		if ( coord_y < 13 && cell[coord_x][coord_y+1].is_check == false && cell[coord_x][coord_y].type == cell[coord_x][coord_y+1].type ) {		//フレーム内かつ下が同じpuchu
			figure += puchu_comb(coord_x, coord_y+1);
		}
		if ( coord_x > 0 && cell[coord_x-1][coord_y].is_check == false && cell[coord_x][coord_y].type == cell[coord_x-1][coord_y].type ) {		//フレーム内かつ左が同じpuchu
			figure += puchu_comb(coord_x-1, coord_y);
		}
		if ( coord_x < 5 && cell[coord_x+1][coord_y].is_check == false && cell[coord_x][coord_y].type == cell[coord_x+1][coord_y].type ) {		//フレーム内かつ右が同じpuchu
			figure += puchu_comb(coord_x+1, coord_y);
		}
		cell[coord_x][coord_y].is_check = false;
		return figure;
	}
	
	public int update() {			//連続処理
		
		int drop_pos = 0;
		int return_index = -1;
		
		if ( moving_flag == true ) {
			judge_key();
			hit_puchu();
			if ( bottom_flag != true ) {
				now.fallDown(speed);
				return_index = puchu_index;
			} else {
				if ( bottom_p1_flag == false ) {
					drop_pos = drop_p1_pos();
					cell[now.puchu1.x/40][now.puchu1.y/40+2+drop_pos].copyPuchu(now.puchu1);
					cell[now.puchu2.x/40][now.puchu2.y/40+2].copyPuchu(now.puchu2);
					cell[now.puchu1.x/40][now.puchu1.y/40+2+drop_pos].dropDown(now.puchu1.y/40+2+drop_pos);
					cell[now.puchu2.x/40][now.puchu2.y/40+2].dropDown(now.puchu2.y/40+2);
				} else if ( bottom_p2_flag == false ) {
					drop_pos = drop_p2_pos();
					cell[now.puchu1.x/40][now.puchu1.y/40+2].copyPuchu(now.puchu1);
					cell[now.puchu2.x/40][now.puchu2.y/40+2+drop_pos].copyPuchu(now.puchu2);
					cell[now.puchu1.x/40][now.puchu1.y/40+2].dropDown(now.puchu1.y/40+2);
					cell[now.puchu2.x/40][now.puchu2.y/40+2+drop_pos].dropDown(now.puchu2.y/40+2+drop_pos);
				} else {
					cell[now.puchu1.x/40][now.puchu1.y/40+2].copyPuchu(now.puchu1);
					cell[now.puchu2.x/40][now.puchu2.y/40+2].copyPuchu(now.puchu2);
					cell[now.puchu1.x/40][now.puchu1.y/40+2].dropDown(now.puchu1.y/40+2);
					cell[now.puchu2.x/40][now.puchu2.y/40+2].dropDown(now.puchu2.y/40+2);
				}
				now = null;
				draw.startDropAnim();
				moving_flag = false;
				bottom_flag = false;
				bottom_p1_flag = false;
				bottom_p2_flag = false;
			}
		}
		return return_index;
	}
	
	//ぷちゅの種類のコピー
	private void init_player(int[][] player) {
		for ( int i = 0; i < GameMain.PPSIZE; i++ ) {
			for ( int j = 0; j < 2; j++ ) {
				cp_player[i][j] = player[i][j];
			}
		}
	}
	
	
	private void switch_start() {
		next[0].movePosition(80, -40);
		next[1].movePosition(265, -40);
		next[2].movePosition(290, 50);
		draw.startMoveAnim();
	}
	
	public void drop_finish() {
		
		van_puchu = false;
		puchu_count = 0;
		link_count = 0;
		color_count = 0;
		for ( int i = 5; i >= 0; i-- ) {
			for ( int j = 13; j >= 2; j-- ) {
				if ( cell[i][j].type != Puchu.Emp && cell[i][j].type != Puchu.Obs ) {
					cell[i][j].combine_count = puchu_comb(i, j);
				}
			}
		}

		for ( int i = 5; i >= 0; i-- ) {
			for ( int j = 13; j >= 2; j-- ) {
				if ( cell[i][j].type != Puchu.Emp && cell[i][j].type != Puchu.Obs && cell[i][j].type < Puchu.Van && cell[i][j].combine_count >= 3 ) {
					link_count += cell[i][j].combine_count;
					van_puchu = true;
					color_check[cell[i][j].type-1] = 1;
					puchu_count++;
					if ( i < 5 && cell[i+1][j].type == Puchu.Obs ) {
						cell[i+1][j].vanishOut();
					} else if ( i > 0 && cell[i-1][j].type == Puchu.Obs ) {
						cell[i-1][j].vanishOut();
					} else if ( j > 0 && cell[i][j-1].type == Puchu.Obs ) {
						cell[i][j-1].vanishOut();
					}
					
					cell[i][j].vanishOut();
					if ( j < 13 && cell[i][j+1].type == Puchu.Obs ) {
						cell[i][j+1].vanishOut();
					}
				}
			}
		}

		if ( van_puchu == false ) {		//ぷちゅが消えないとき
			chain_reset();
		} else{							//ぷちゅが消えるとき
			cal_color();
			cal_link();
			//System.out.println("link:" + link_count);
			//System.out.println("color:" + color_count);
			//System.out.println("puchu" + puchu_count);
			if ( moving_flag == false ) {
				chain_count++;
			}
			sub_score = cal_score(puchu_count, chain_count, link_count, color_count);
			score += sub_score;
			//System.out.println("score:" + score);
			cal_obs();
			//System.out.println("obs:" + obs_count);
			if ( unfallen_obs + fallen_obs > 0 ) {					//相殺処理
				if ( obs_count >= unfallen_obs + fallen_obs ) {
					obs_count = obs_count - (unfallen_obs + fallen_obs);
					unfallen_obs = 0;
					fallen_obs = 0;
				} else if ( obs_count >= fallen_obs ) {
					obs_count = obs_count - fallen_obs;
					fallen_obs = 0;
					unfallen_obs = unfallen_obs - obs_count;
					obs_count = 0;
				} else {
					fallen_obs = fallen_obs - obs_count;
					obs_count = 0;
				}
			}
			if ( score >= 9999999 ) {
				score = 9999999;
			}
			gm.sendObs(obs_count ,is_me);
			draw.startVanishAnim(chain_count, obs_count , score, fallen_obs + unfallen_obs);
			
		}
	}
	
	public void vanish_finish() {
		drop_puchu();
	}
	
	//key操作関係
	public void judge_key() {
		if ( key.Left == true && slide_left_flag ) {
			slide_sound.play();
			now.slideLeft();
			slide_left_flag = false;
		} else if ( key.Left == false ) {
			slide_left_flag = true;
		}
		if ( key.Right == true && slide_right_flag ) {
			slide_sound.play();
			now.slideRight();
			slide_right_flag = false;
		} else if ( key.Right == false ) {
			slide_right_flag = true;
		}
		if ( key.Down == true ) {
			speed = 10;
		} else {
			speed = 1;
		}
		if ( key.TurnLeft == true && turn_left_flag ) {
			turn_sound.play();
			now.turnLeft();
			turn_left_flag = false;
		} else if ( key.TurnLeft == false ){
			turn_left_flag = true;
		}
		if ( key.TurnRight == true && turn_right_flag ) {
			turn_sound.play();
			now.turnRight();
			turn_right_flag = false;
		} else if ( key.TurnRight == false ) {
			turn_right_flag = true;
		}
	}
	
	private int drop_p1_pos() {
		int pos_y = 0;
		
		if ( bottom_p2_flag == true ) {
			while( now.puchu1.y/40+2+pos_y <= 13 && cell[now.puchu1.x/40][now.puchu1.y/40+2+pos_y].type == 0 ) {
				pos_y++;
			}
		}
		return pos_y-1;
	}
	private int drop_p2_pos() {
		int pos_y = 0;
		
		if ( bottom_p1_flag == true ) {
			while( now.puchu2.y/40+2+pos_y <= 13 && cell[now.puchu2.x/40][now.puchu2.y/40+2+pos_y].type == 0 ) {
				pos_y++;
			}
		}
		return pos_y-1;
	}
	
	private void drop_puchu() {
		
		is_drop = false;
		for ( int j = 12; j >= 0; j-- ) {
			for ( int i = 5; i >= 0; i-- ) {
				if ( cell[i][j].type != Puchu.Emp && cell[i][j+1].type == Puchu.Emp ) {
					k = 1;
					while( j+k < 13 && cell[i][j+k+1].type == Puchu.Emp ) {
						k++;
					}
					cell[i][j+k].copyPuchu(cell[i][j]);
					cell[i][j+k].dropDown(j+k);
					is_drop = true;
//					cell[i][j].type = Puchu.Emp;
					cell[i][j] = new Puchu(Puchu.Emp, i*Draw.Squares, j*Draw.Squares);
				}
			}
		}
		if ( is_drop ) {
			draw.startDropAnim();
		} else {
			chain_count = 0;
			obs_count = 0;
			if ( cell[2][2].type != Puchu.Emp ) {
				gm.finishGame(is_me);
				draw.startEndAnim(Draw.GameInfo.GAME_LOSE);
			}
			switch_start();
		}
	}
	
	public void game_end() {
		gm.resultDisp(score, is_me);
	}
	
	public void win() {
		
		moving_flag = false;		//移動できるかどうか

		draw.startEndAnim(Draw.GameInfo.GAME_WIN);
	}
	
	public void defeat() {
		draw.startEndAnim(Draw.GameInfo.GAME_LOSE);
	}
	
	public void game_start() {
		
		switch_start();
	}
	
	private int cal_score(int puchu_count, int comb, int link, int color) {
		int score = 0;
		
		if ( comb_point[comb-1] + link + color_point[color-1] == 0 ) {
			score = 40;
		} else {
			score = puchu_count*10*(comb_point[comb-1] + link + color_point[color-1]);
		}
		return score;
	}
	
	//ぷちゅの種類の計算
	private void cal_color() {
		
		for ( int i = 0; i < color_check.length; i++ ) {
			if ( color_check[i] != 0 ) {
				color_count++;
			}
			color_check[i] = 0;
		}
	}
	
	//連結数の計算
	private void cal_link() {
		
		if ( link_count > 50 ) {
			link_count = 10;
		} else {
			for ( int i = 0; i < link_all.length; i++ ) {
				if ( link_all[i] == link_count ) {
					link_count = link_point[real_link[i][0]-3] + link_point[real_link[i][1]-3];
				}
			}
			if ( puchu_count == 5 ) {
					link_count = link_point[2];
			} else if ( link_count == 25 ) {
					link_count = link_point[0]+link_point[1];
			}
		}
		
	}
	
	private void cal_obs() {
		
		obs_count = (sub_score + rn_score) / 90;
		rn_score = (sub_score + rn_score) % 90;
	
	}
	
	private void chain_reset() {
		chain_count = 0;
		obs_count = 0;
		
		gm.sendObs(0, is_me);
		if ( fallen_obs > 0 && fallen == false ) {
			obs_fall();
			return;
		}
		if ( cell[2][2].type != Puchu.Emp ) {
			gm.finishGame(is_me);
			draw.startEndAnim(Draw.GameInfo.GAME_LOSE);
		} else {
			fallen = false;
			switch_start();			
		}
	}
	
	private void obs_fall() {
		
		int line = 0;
		int count = 0;
		boolean obs_flag = false;
		fallen = true;
		
		while( count < fallen_obs && count < 30 ) {
			obs_flag = false;
			for ( int j = 0; j < 6; j++ ) {
				if ( count >= fallen_obs ) {
					break;
				}
				for ( int k = 13; k >= 0; k-- ) {
					if ( cell[j][k].type == Puchu.Emp ) {
						
						if ( k != 0 ) {
							cell[j][k].dropDownObs(count/6+1);
						} else {
							cell[j][0].dropDownObs(count/6+1);
						}
						obs_flag = true;
						count++;
						break;
					}
				}
			}
			if ( obs_flag == false ) {
				break;
			}
		}
		fallen_obs -= count;
		draw.setObsNum(fallen_obs + unfallen_obs);
		draw.startDropAnim();
	}
	
	public void receive_obs(int count) {
		
		if ( count == 0 ) {
			fallen_obs += unfallen_obs;
			unfallen_obs = 0;
		} else {
			unfallen_obs += count;
			draw.startDamageAnim(fallen_obs + unfallen_obs);
		}
	}
}