import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class Field {
	
	public int score = 0;
	public Puchu[][] cell = new Puchu[6][14];
	public Puchu first_puchu;
	public PuchuPair now;
	public PuchuPair[] next = new PuchuPair[3];
	public Draw draw;
	public Key key;
	public int[] obs_puyo = new int[6];
	
	public boolean bottom_flag = false; 	//当たり判定
	public boolean moving_flag = false;		//移動できるかどうか
	public boolean lose_flag = false;
	private boolean bottom_p1_flag = false;	//どのぷちゅが底にあるか
	private boolean bottom_p2_flag = false;	
	private boolean turn_left_flag = true;	//左回転できるか
	private boolean turn_right_flag = true;	//右回転できるか
	private boolean slide_left_flag = true;	//左に移動できるか
	private boolean slide_right_flag = true;//右に移動できるか
	private boolean game_end_flag = false;
	private boolean van_puchu = false;
	private boolean is_drop = false;
	
	private int now_x = 0;
	private int now_y = 0;
	private int time = 0;
	private int landing_time = 0;
	private static final int max_land_time = 0;
	private int k = 0;
	private int comb_figure1 = 0;
	private int comb_figure2 = 0;
	private int chain_count = 0;	//連鎖数
	private int all_comb = 0;
	private int comb_puyo = 0;
	private int switch_figure;
	private int speed = 1;
	private int[][] cp_player = new int[GameMain.PPSIZE][2];
	private GameMain gm;
	private int puchu_count = 0;
	
	public Field(GameMain game, int[][] player) {			//nullプレイヤー用
		
		gm = game;
		init_cell();
		if ( player != null ) {
			draw = new Draw(this);
			key = new Key(gm.nw);
			init_player(player);
			create_puchu(player);
			
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
		puchu_count++;
		
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
	}
	
	public void hit_puchu() {
		if ( now.form == 0 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			if ( now_y == 13 || cell[now_x][now_y+1].type != 0 ) {
				bottom_flag = true;
				bottom_p1_flag = true;
				bottom_p2_flag = true;
			}
			if ( now_x >= 5) {
				slide_right_flag = false;
				turn_right_flag = false;
				
				if ( cell[now_x-1][now_y].type != 0 ) {
					slide_left_flag = false;
					turn_left_flag = false;
				}
			} else if ( cell[now_x+1][now_y].type != 0 ) {
				slide_right_flag = false;
				turn_right_flag = false;
			}
			if ( now_x <= 0) {
				slide_left_flag = false;
				turn_left_flag = false;
				
				if ( cell[now_x+1][now_y].type != 0 ) {
					slide_right_flag = false;
					turn_right_flag = false;
				}
			} else if ( cell[now_x-1][now_y].type != 0 ) {
				slide_left_flag = false;
				turn_left_flag = false;
			}
		} else if ( now.form == 1 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			if ( now_y == 13 ) {
				bottom_flag = true;
				bottom_p1_flag = true;
				bottom_p2_flag = true;
			} else if ( cell[now_x][now_y+1].type != 0 ) {
				bottom_p1_flag = true;
				bottom_flag = true;
			} else if ( cell[now_x+1][now_y+1].type != 0 ) {
				bottom_p2_flag = true;
				bottom_flag = true;
			}
			if ( now_x >= 4 && cell[now_x-1][now_y].type != 0 ) {
				slide_right_flag = false;
				slide_left_flag = false;
			} else if ( now_x <= 0 && cell[now_x+2][now_y].type != 0 ) {
				slide_left_flag = false;
				slide_right_flag = false;
			} else if ( now_x >= 4 ) {
				slide_right_flag = false;
			} else if ( now_x <= 0 ) {
				slide_left_flag = false;
			} else if ( cell[now_x-1][now_y].type != 0 ) {
				slide_left_flag = false;
			} else if ( cell[now_x+2][now_y].type != 0 ) {
				slide_right_flag = false;
			}
		} else if ( now.form == 2 ) {
			now_x = ( now.puchu2.x ) / 40;
			now_y = ( now.puchu2.y ) / 40 + 2;
			
			if ( now_y == 13 || cell[now_x][now_y+1].type != 0 ) {
				bottom_flag = true;
				bottom_p1_flag = true;
				bottom_p2_flag = true;
			}
			if ( now_x >= 5) {
				slide_right_flag = false;
				turn_left_flag = false;
				
				if ( cell[now_x-1][now_y].type != 0 ) {
					slide_left_flag = false;
					turn_right_flag = false;
				}
			} else if ( cell[now_x+1][now_y].type != 0 ) {
				slide_right_flag = false;
				turn_left_flag = false;
			}
			if ( now_x <= 0) {
				slide_left_flag = false;
				turn_right_flag = false;
				
				if ( cell[now_x+1][now_y].type != 0 ) {
					slide_right_flag = false;
					turn_left_flag = false;
				}
			} else if ( cell[now_x-1][now_y].type != 0 ) {
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
			} else if ( cell[now_x][now_y+1].type != 0 ) {
				bottom_p1_flag = true;
				bottom_flag = true;
			} else if ( cell[now_x-1][now_y+1].type != 0 ) {
				bottom_p2_flag = true;
				bottom_flag = true;
			}
			if ( now_x >= 5 && cell[now_x-2][now_y].type != 0 ) {
				slide_right_flag = false;
				slide_left_flag = false;
			} else if ( now_x <= 1 && cell[now_x+1][now_y].type != 0 ) {
				slide_right_flag = false;
				slide_left_flag = false;
			} else if ( now_x >= 5 ) {
				slide_right_flag = false;
			} else if ( now_x <= 1 ) {
				slide_left_flag = false;
			} else if ( cell[now_x-2][now_y].type != 0 ) {
				slide_left_flag = false;
			} else if ( cell[now_x+1][now_y].type != 0 ) {
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
		int puchu_index = -1;
		
		if ( moving_flag == true ) {
			judge_key();
			hit_puchu();
			if ( bottom_flag != true ) {
				now.fallDown(speed);
				puchu_index = puchu_count;
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
		return puchu_index;
	}
	
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
		
		for ( int i = 5; i >= 0; i-- ) {
			for ( int j = 13; j >= 2; j-- ) {
				if ( cell[i][j].type != Puchu.Emp ) {
					cell[i][j].combine_count = puchu_comb(i, j);
				}
			}
		}

		for ( int i = 5; i >= 0; i-- ) {
			for ( int j = 13; j >= 2; j-- ) {
				if ( cell[i][j].type != Puchu.Emp && cell[i][j].combine_count >= 3 ) {
					van_puchu = true;
					all_comb += cell[i][j].combine_count;
					comb_puyo++;
					cell[i][j].vanishOut();
				}
			}
		}
		
		if ( van_puchu == false ) {
			chain_count = 0;
			if ( cell[2][2].type != Puchu.Emp ) {
				lose_flag = true;
				gm.finishGame();
				draw.startEndAnim(Draw.GameInfo.GAME_LOSE);
			}
			switch_start();
		} else {
			chain_count++;
			draw.startVanishAnim(chain_count, 0);
		}
	}
	
	public void vanish_finish() {
		drop_puchu();
	}
	
	public void judge_key() {
		if ( key.Left == true && slide_left_flag ) {
			now.slideLeft();
			slide_left_flag = false;
		} else if ( key.Left == false ) {
			slide_left_flag = true;
		}
		if ( key.Right == true && slide_right_flag ) {
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
			now.turnLeft();
			turn_left_flag = false;
		} else if ( key.TurnLeft == false ){
			turn_left_flag = true;
		}
		if ( key.TurnRight == true && turn_right_flag ) {
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
		
		for ( int j = 12; j >= 0; j-- ) {
			for ( int i = 5; i >= 0; i-- ) {
				if ( cell[i][j].type != Puchu.Emp && cell[i][j+1].type == Puchu.Emp ) {
					k = 1;
					while( j+k < 13 && cell[i][j+k+1].type == Puchu.Emp ) {
						k++;
					}
					
					cell[i][j+k].copyPuchu(cell[i][j]);
					cell[i][j+k].dropDown(j+k);
//					cell[i][j].type = Puchu.Emp;
					cell[i][j] = new Puchu(Puchu.Emp, i*Draw.Squares, j*Draw.Squares);
				}
			}
		}
		draw.startDropAnim();
	}
	
	public void game_end() {
		gm.resultDisp(score);
	}
	
	public void p1_defeat() {
		
	}
	
	public void p2_defeat() {

	}
	
	public void game_start() {
		switch_start();
	}
	
}