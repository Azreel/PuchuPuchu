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
	
	public boolean bottom_flag = false; 	//当たり判定
	public boolean moving_flag = false;		//移動できるかどうか
	private boolean bottom_p1_flag = false;	//どのぷちゅが底にあるか
	private boolean bottom_p2_flag = false;	
	private boolean turn_left_flag = true;	//左回転できるか
	private boolean turn_right_flag = true;	//右回転できるか
	private boolean slide_left_flag = true;	//左に移動できるか
	private boolean slide_right_flag = true;//右に移動できるか
	private int now_x = 0;
	private int now_y = 0;
	private int switch_figure;
	private int speed = 1;
	private int[][] cp_player = new int[GameMain.PPSIZE][2];
	
	public Field(GameMain gm, int[][] player) {			//nullプレイヤー用
		
		init_cell();
		if ( player != null ) {
			draw = new Draw(this);
			key = new Key(gm.nw);
			init_player(player);
			create_puchu(player);
			switch_start();
			draw.addKeyListener(key);
		} else {
			draw = new Draw();
		}
		gm.fadeOut();
	}

	
	private void create_puchu(int[][] rnd_list){	//first create
		
		for ( int i = 0; i < next.length; i++ ) {
			next[i] = new PuchuPair(rnd_list[i][0], rnd_list[i][1]);	//次のぷちゅ
			next[i].setPosition(265+25*i, -40+90*i);
		}
		switch_figure = 3;
	}
	
	public void switch_next() {	//ぷちゅの更新
		moving_flag = true;
		now = next[0];
		
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
	}
	
	public void hit_puchu() {
		if ( now.form == 0 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			System.out.println("now_x:" + now_x);
			System.out.println("now_y:" + now_y);
			if ( now_y == 13 || cell[now_x][now_y+1].type != 0 ) {
				bottom_flag = true;
			}
			if ( now_x >= 5 && cell[now_x-1][now_y].type != 0 ) {
				slide_right_flag = false;
				slide_left_flag = false;
				turn_right_flag = false;
				turn_left_flag = false;
			} else if ( now_x <= 0 && cell[now_x+1][now_y].type != 0 ) {
				slide_left_flag = false;
				slide_right_flag = false;
				turn_left_flag = false;
				turn_right_flag = false;
			} else if ( now_x >= 5 ) {
				slide_right_flag = false;
				turn_right_flag = false;
			} else if ( now_x <= 0 ) {
				slide_left_flag = false;
				turn_left_flag = false;
			} else if ( cell[now_x-1][now_y].type != 0 ) {
				slide_left_flag = false;
				turn_left_flag = false;
			} else if ( cell[now_x+1][now_y].type != 0 ) {
				slide_right_flag = false;
				turn_right_flag = false;
			}
		} else if ( now.form == 1 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			if ( now_y == 13 ) {
				bottom_flag = true;
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
			}
			if ( now_x >= 5 && cell[now_x-2][now_y].type != 0 ) {
				slide_right_flag = false;
				slide_left_flag = false;
				turn_right_flag = false;
				turn_left_flag = false;
			} else if ( now_x <= 0 && cell[now_x+1][now_y].type != 0 ) {
				slide_right_flag = false;
				slide_left_flag = false;
				turn_right_flag = false;
				turn_left_flag =false;
			} else if ( now_x >= 5 ) {
				slide_right_flag = false;
				turn_left_flag = false;
			} else if ( now_x <= 0 ) {
				slide_left_flag = false;
				turn_right_flag = false;
			} else if ( cell[now_x+1][now_y].type != 0 ) {
				slide_right_flag = false;
				turn_left_flag = false;
			} else if ( cell[now_x-1][now_y].type != 0 ) {
				slide_left_flag = false;
				turn_right_flag = false;
			}
		} else {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			
			if ( now_y == 13 ) {
				bottom_flag = true;
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
	
	public void deside_pos() {		//落下先の場所決め
		
		if ( bottom_flag == true && now_y != 11 ) {
			
		}
		
	}
	
	private void deside_vanish() {
		
	}
	
	private void vanish_puchu() {
		
	}
	
	public int update() {			//連続処理
		
		if ( moving_flag == true ) {
			judge_key();
			hit_puchu();
			if ( bottom_flag != true ) {
				now.fallDown(speed);
			} else {
				cell[now.puchu1.x/40][now.puchu1.y/40+2].copyPuchu(now.puchu1);
				cell[now.puchu2.x/40][now.puchu2.y/40+2].copyPuchu(now.puchu2);
				cell[now.puchu1.x/40][now.puchu1.y/40+2].dropDown(now.puchu1.y/40+2);
				cell[now.puchu2.x/40][now.puchu2.y/40+2].dropDown(now.puchu2.y/40+2);
				now = null;
				draw.startDropAnim();
				moving_flag = false;
				bottom_flag = false;
			}
		}
		if ( now != null ) {
			return -1;
		} else {
			return switch_figure;
		}
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
		switch_start();
	}
	
	public void vanish_finish() {
		
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
}