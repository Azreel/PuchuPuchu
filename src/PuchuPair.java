import java.awt.*;
import javax.swing.*;

public class PuchuPair {
	public static final int Up = 0;
	public static final int Right = 1;
	public static final int Bottom = 2;
	public static final int Left = 3;
	
	Puchu puchu1, puchu2;
	int _x = 315;  // 生成座標(next[2]の位置)
	int _y = 160;  //
	public int form;	// 姿勢状態(ぷちゅ1を中心にぷちゅ2がどこにいるか)
	public boolean is_match_posture_right = true;   // 右回転に対して判定位置と描写位置がずれているか
	public boolean is_match_posture_left = true;     // 左回転に対して判定位置と描写位置がずれているか
	public boolean is_match_position_slide = true;   // 横移動に対して判定位置と描写位置がずれているか
	public boolean is_match_position_move = true; // 移動に対して判定位置と描写位置がずれているか
	private int turn_anim_time = 0;
	private int slide_anim_dir = 0;					// 位置ずれの方向
	private int slide_anim_time = 0;
	private int move_anim_time = 0;
	private int move_range_x = 0;
	private int move_range_y = 0;
	private static final float max_turn_anim_time = 5.0f; // 回転アニメーションの時間
	private static final int slide_anim_speed = 20; // 横移動アニメーションの速度
	private static final int max_move_anim_time = 10; // 移動アニメーションの時間
	
	PuchuPair(int set_type1, int set_type2){
		this.form = PuchuPair.Up;
		this.puchu1 = new Puchu(set_type1, _x, _y);
		this.puchu2 = new Puchu(set_type2, _x, _y - Draw.Squares);		
	}
	
	//-- ぷちゅペアの移動(アニメーション無し, 初期設置用)
	public void setPosition(int x, int y) {
		puchu1.x = puchu1.draw_x = x;
		puchu1.y = puchu1.draw_y = y;
		puchu2.x = puchu2.draw_x = x;
		puchu2.y = puchu2.draw_y = y - Draw.Squares;
	}
	
	//-- ぷちゅペアの移動
	public void movePosition(int x, int y) {
		move_range_x = x - puchu1.x;
		move_range_y = y - puchu1.y;
		puchu1.x = x;
		puchu1.y = y;
		puchu2.x = x;
		puchu2.y = y - Draw.Squares;
		move_anim_time = 0;
		is_match_position_move = false;
	}
	
	//-- ぷちゅペアの移動アニメーション
	public void drawingMovePosition() {
		if ( move_anim_time < max_move_anim_time ) {
			puchu1.draw_x += move_range_x/max_move_anim_time;
			puchu1.draw_y += move_range_y/max_move_anim_time;			
			puchu2.draw_x += move_range_x/max_move_anim_time;
			puchu2.draw_y += move_range_y/max_move_anim_time;
		} else {
			puchu1.draw_x += move_range_x%max_move_anim_time;
			puchu1.draw_y += move_range_y%max_move_anim_time;
			puchu2.draw_x += move_range_x%max_move_anim_time;
			puchu2.draw_y += move_range_y%max_move_anim_time;
			is_match_position_move = true;
		}
		move_anim_time++;
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
	
	//-- ぷちゅの回転アニメーション
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
	
	//-- ぷちゅの自由落下処理(操作中のぷちゅ)
	public void fallDown(int _speed) {
		if ( Draw.Squares%_speed == 0 ) {
			puchu1.y += _speed; puchu1.draw_y += _speed;
			puchu2.y += _speed; puchu2.draw_y += _speed;
		} else {
			puchu1.y += 1;	puchu1.draw_y += 1;
			puchu2.y += 1;	puchu2.draw_y += 1;			
		}
		if ( puchu1.y > Draw.Ground ) { puchu1.y = Draw.Ground; }
		if ( puchu2.y > Draw.Ground ) { puchu2.y = Draw.Ground; }
	}
	
	//-- ぷちゅの横移動処理
	public void slideRight() {
		slide_anim_time = 0;
		puchu1.draw_x = puchu1.x;
		puchu2.draw_x = puchu2.x;
		puchu1.x += Draw.Squares;
		puchu2.x += Draw.Squares;
		if ( puchu1.x > Draw.WallRight ) { puchu1.x = Draw.WallRight; }
		if ( puchu2.x > Draw.WallRight ) { puchu2.x = Draw.WallRight; }
		is_match_position_slide = false;
		slide_anim_dir = 1;
	}
	
	public void slideLeft() {
		slide_anim_time = 0;
		puchu1.draw_x = puchu1.x;
		puchu2.draw_x = puchu2.x;
		puchu1.x -= Draw.Squares;
		puchu2.x -= Draw.Squares;
		if ( puchu1.x < Draw.WallLeft ) { puchu1.x = Draw.WallLeft; }
		if ( puchu2.x < Draw.WallLeft ) { puchu2.x = Draw.WallLeft; }
		is_match_position_slide = false;
		slide_anim_dir = -1;
	}
	
	//-- ぷちゅの横移動アニメーション
	public void drawingSlide() {
		slide_anim_time++;
		puchu1.draw_x += slide_anim_speed * slide_anim_dir;
		puchu2.draw_x += slide_anim_speed * slide_anim_dir;
		if ( puchu1.draw_x > Draw.WallRight ) { puchu1.draw_x = Draw.WallRight; }
		if ( puchu2.draw_x > Draw.WallRight ) { puchu2.draw_x = Draw.WallRight; }
		if ( puchu1.draw_x < Draw.WallLeft ) { puchu1.draw_x = Draw.WallLeft; }
		if ( puchu2.draw_x < Draw.WallLeft ) { puchu2.draw_x = Draw.WallLeft; }
		if ( slide_anim_time >= Draw.Squares/slide_anim_speed ) {
			is_match_position_slide = true;
		}
	}
}
