import java.awt.*;
import javax.swing.*;

public class PuchuPair {
	public static final int Up = 0;
	public static final int Right = 1;
	public static final int Bottom = 2;
	public static final int Left = 3;
	
	Puchu puchu1, puchu2;
	int _x = 80;		// 生成座標
	int _y = 40; 	//
	public int form;	// 姿勢状態(ぷちゅ1を中心にぷちゅ2がどこにいるか)
	public boolean is_match_posture_right = true;	// 右回転に対して判定位置と描写位置がずれているか
	public boolean is_match_posture_left = true;	// 左回転に対して判定位置と描写位置がずれているか
	public boolean is_match_position = true;		// 横移動に対して判定位置と描写位置がずれているか
	private int turn_anim_time = 0;
	private int move_anim_dir = 0;					// 位置ずれの方向
	private static final float max_turn_anim_time = 5.0f;	// 回転アニメーションの時間
	private static final int move_anim_speed = 20;			// 横移動アニメーションの速度
	
	PuchuPair(int set_type1, int set_type2){
		this.form = PuchuPair.Up;
		this.puchu1 = new Puchu(set_type1, _x, _y);
		this.puchu2 = new Puchu(set_type2, _x, _y - Draw.Squares);		
	}
	
	//-- ぷちゅペアのポジション変更
	public void setPosition(int x, int y) {
		puchu1.x = x;
		puchu1.y = y;
		puchu2.x = x;
		puchu2.y = y + Draw.Squares;
	}
	
	//-- ぷちゅの回転処理
	public void turnRight() {
		form = (form + 1) % 4;
		puchu2.x = puchu1.x + (int)(Draw.Squares * Math.cos((form - 1) * Math.PI/2));
		puchu2.y = puchu1.y + (int)(Draw.Squares * Math.sin((form - 1) * Math.PI/2));
		is_match_posture_right = false;
		is_match_posture_left = true;
		turn_anim_time = (int)max_turn_anim_time;
	}
	
	public void turnLeft() {
		form = (form + 3) % 4;
		puchu2.x = puchu1.x + (int)(Draw.Squares * Math.cos((form - 1) * Math.PI/2));
		puchu2.y = puchu1.y + (int)(Draw.Squares * Math.sin((form - 1) * Math.PI/2));
		is_match_posture_left = false;
		is_match_posture_right = true;
		turn_anim_time = (int)max_turn_anim_time;
	}
	
	//-- ぷちゅの回転アニメーション処理
	public void drawingTurnRight() {
		puchu2.draw_x = puchu1.draw_x + (int)(Draw.Squares * Math.cos((form - 2) * Math.PI/2 + Math.PI/2*(1.0 - turn_anim_time/max_turn_anim_time)));
		puchu2.draw_y = puchu1.draw_y + (int)(Draw.Squares * Math.sin((form - 2) * Math.PI/2 + Math.PI/2*(1.0 - turn_anim_time/max_turn_anim_time)));
		turn_anim_time--;
		if ( turn_anim_time < 0 ) {
			is_match_posture_right = true;
		}
	}
	
	public void drawingTurnLeft() {
		puchu2.draw_x = puchu1.draw_x + (int)(Draw.Squares * Math.cos(form * Math.PI/2 - Math.PI/2*(1.0 - turn_anim_time/max_turn_anim_time)));			
		puchu2.draw_y = puchu1.draw_y + (int)(Draw.Squares * Math.sin(form * Math.PI/2 - Math.PI/2*(1.0 - turn_anim_time/max_turn_anim_time)));			
		turn_anim_time--;
		if ( turn_anim_time < 0 ) {
			is_match_posture_left = true;
		}
	}
	
	//-- ぷちゅの自由落下処理(操作上のぷちゅ)
	public void fallDown() {
		puchu1.y += 1;	puchu1.draw_y += 1;
		puchu2.y += 1;	puchu2.draw_y += 1;
	}
	
	//-- ぷちゅの横移動処理
	public void moveRight() {
		puchu1.x += Draw.Squares;
		puchu2.x += Draw.Squares;
		is_match_position = false;
		move_anim_dir = 1;
	}
	
	public void moveLeft() {
		puchu1.x -= Draw.Squares;
		puchu2.x -= Draw.Squares;
		is_match_position = false;
		move_anim_dir = -1;
	}
	
	//-- ぷちゅの横移動アニメーション処理
	public void drawingMove() {
		puchu1.draw_x += move_anim_speed * move_anim_dir;
		puchu2.draw_x += move_anim_speed * move_anim_dir;
		if ( puchu1.draw_x == puchu1.x ) {
			is_match_position = true;
		}
	}
}
