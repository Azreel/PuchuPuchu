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
	
	public boolean bottom_flag = false; //当たり判定
	public boolean left_flag = false;
	public boolean right_flag = false;
	public boolean moving_flag = false;

	private int now_x = 0;
	private int now_y = 0;
	private int switch_figure;
	private int[][] cp_player = new int[GameMain.PPSIZE][2];
	
	public Field(GameMain gm, int[][] player) {			//nullプレイヤー用
		
		init_cell();
		if ( player != null ) {
			draw = new Draw(this);
			key = new Key();
			init_player(player);
			create_puchu(player);
			draw.addKeyListener(key);
		} else {
			draw = new Draw();
		}
	}

	
	private void create_puchu(int[][] rnd_list){	//first create
		
		for ( int i = 1; i < next.length; i++ ) {
			next[i] = new PuchuPair(rnd_list[i][0], rnd_list[i][1]);	//次のぷちゅ
			next[i].setPosition(265+25*i, -40+90*i);
		}
		switch_figure = 4;
	}
	
	public void switch_next() {	//ぷちゅの更新
		moving_flag = true;
		now = next[0];
		
		for ( int i = 0; i < 2; i++ ) {
			next[i] = next[i+1];	//次のぷちゅの更新
		}
		
		next[2] = new PuchuPair(cp_player[switch_figure][0], cp_player[switch_figure][1]);	//次の次のぷちゅの更新
		switch_figure++;
	}
	
	public void hit_puchu() {
		if ( now.form == 0 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 2;
			System.out.println("now_x:" + now_x);
			System.out.println("now_y:" + now_y);
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( now_y == 13 || cell[now_x][now_y+1].type != 0 ) {
				bottom_flag = true;
			}
			
		} else if ( now.form == 1 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 1;
			
			if ( cell[now_x+2][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( now_y == 13 || cell[now_x][now_y+1].type != 0 || cell[now_x+1][now_y+1].type != 0 ) {
				bottom_flag = true;
			}
		} else if ( now.form == 2 ) {
			now_x = ( now.puchu2.x ) / 40;
			now_y = ( now.puchu2.y ) / 40 + 1;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( now_y == 13 || cell[now_x][now_y+1].type != 0 ) {
				bottom_flag = true;
			}
		} else {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40 + 1;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-2][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( now_y == 13 || cell[now_x][now_y+1].type != 0 || cell[now_x-2][now_y].type != 0 ) {
				bottom_flag = true;
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
	
	public void update() {			//連続処理
		if ( moving_flag == true ) {
			hit_puchu();
			now.fallDown(1);
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
		next[2].movePosition(290, 60);
		draw.startMoveAnim();
	}
}