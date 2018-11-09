import java.awt.*;
import java.util.*;

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

	private int now_x = 0;
	private int now_y = 0;
	
	public Field(boolean flag) {			//nullプレイヤー用
		
		init_cell();
		create_puchu();
		if ( flag == true ) {
			draw = new Draw(this);
			key = new Key();
		} else {
			draw = new Draw();
		}
	}

	
	private void create_puchu(){	//first create
		
		Random rnd = new Random();
		
		now = new PuchuPair(rnd.nextInt(6)+1, rnd.nextInt(6)+1);	//振ってくるぷちゅ
		now.setPosition(80, -80);
		for ( int i = 0; i < 3; i++ ) {
			next[i] = new PuchuPair(rnd.nextInt(6)+1, rnd.nextInt(6)+1);	//次のぷちゅ
			next[i].setPosition(265+25*i, -40+90*i);
		}
	}
	
	public void switch_next() {	//ぷちゅの更新
		
		Random rnd = new Random();
		
		now.puchu1.type = next[0].puchu1.type;	//振ってくるぷちゅの更新
		now.puchu2.type = next[0].puchu2.type;
		
		for ( int i = 0; i < 2; i++ ) {
			next[i].puchu1.type = next[i+1].puchu1.type;	//次のぷちゅの更新
			next[i].puchu2.type = next[i+1].puchu2.type;
		}
		
		next[2] = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//次の次のぷちゅの更新
		
	}
	
	public void hit_puchu() {
		
		if ( now.form == 0 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 || now_y == 11 ) {
				bottom_flag = true;
			}
			
		} else if ( now.form == 1 ) {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40;
			
			if ( cell[now_x+2][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 || cell[now_x+1][now_y+1].type != 0 || now_y == 11 ) {
				bottom_flag = true;
			}
		} else if ( now.form == 2 ) {
			now_x = ( now.puchu2.x ) / 40;
			now_y = ( now.puchu2.y ) / 40;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 || now_y == 11 ) {
				bottom_flag = true;
			}
		} else {
			now_x = ( now.puchu1.x ) / 40;
			now_y = ( now.puchu1.y ) / 40;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-2][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 || cell[now_x-2][now_y].type != 0 || now_y == 11 ) {
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
		now.fallDown();
		hit_puchu();
	}
}