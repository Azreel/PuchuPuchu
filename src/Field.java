import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Field {
	
	public int score = 0;
	public Puchu[][] cell = new Puchu[6][12];
	public Puchu first_puchu;
	public PuchuPair now, next1, next2;
	public Draw draw;
	
	public Key key;
	
	public boolean bottom_flag = false; //当たり判定
	public boolean left_flag = false;
	public boolean right_flag = false;

	private int now_x = 0;
	private int now_y = 0;
	
	public Field(boolean flag) {			//nullプレイヤー用
		if ( flag == true ) {
			draw = new Draw(this);
			key = new Key();
		} else {
			draw = new Draw();
		}
	}

	
	public void create_puchu(){	//first create
		
		Random rnd = new Random();
		
		now = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//振ってくるぷちゅ
		next1 = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//次のぷちゅ
		next2 = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//次の次のぷちゅ
	}
	
	public void update_puchu() {	//ぷちゅの更新
		
		Random rnd = new Random();
		
		now.puchu1.type = next1.puchu1.type;	//振ってくるぷちゅの更新
		now.puchu2.type = next1.puchu2.type;
		
		next1.puchu1.type = next2.puchu1.type;	//次のぷちゅの更新
		next1.puchu2.type = next2.puchu2.type;
		
		next2 = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//次の次のぷちゅの更新
	}
	
	public void hit_puchu() {
		
		if ( now.form == 0 ) {
			now_x = ( now.puchu1.x - 60 ) / 40;
			now_y = ( now.puchu1.y - 110 ) / 40;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 ) {
				bottom_flag = true;
			}
			
		} else if ( now.form == 1 ) {
			now_x = ( now.puchu1.x - 60 ) / 40;
			now_y = ( now.puchu1.y - 110 ) / 40;
			
			if ( cell[now_x+2][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 || cell[now_x+1][now_y+1].type != 0 ) {
				bottom_flag = true;
			}
		} else if ( now.form == 2 ) {
			now_x = ( now.puchu2.x - 60 ) / 40;
			now_y = ( now.puchu2.y - 110 ) / 40;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-1][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 ) {
				bottom_flag = true;
			}
		} else {
			now_x = ( now.puchu1.x - 60 ) / 40;
			now_y = ( now.puchu1.y - 110 ) / 40;
			
			if ( cell[now_x+1][now_y].type != 0 ) {
				right_flag = true;
			}
			if ( cell[now_x-2][now_y].type != 0 ) {
				left_flag = true;
			}
			if ( cell[now_x][now_y+1].type != 0 || cell[now_x-2][now_y].type != 0 ) {
				bottom_flag = true;
			}
		}
	}
	private void init_cell() {
		
		for ( int i = 0; i < 6; i++ ) {
			for ( int j = 0; j < 12; j++ ) {
				cell[j][i] = new Puchu(Puchu.Emp, i*Draw.Squares, j*Draw.Squares);
			}
		}
	}
	
}