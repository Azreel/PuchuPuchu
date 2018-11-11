import java.awt.*;
import javax.swing.*;

public class Puchu {
	
	public static final int Emp = 0, Pat1 = 1, Pat2 = 2, Pat3 = 3, Pat4 = 4, Pat5 = 5, Pat6 = 6, Van = 7, Obs = 8, Vanishing = 9;
	// Van : 消えるときの顔
	// Obs : お邪魔
	// Emp : ぷちゅなし
	// Pat1 ~ Pat6
	
	public int type;			// ぷちゅの種類
	public int x, y;			// 判定座標
	public int draw_x, draw_y;	// 描画に使用する座標(アニメーションのため遅延して動作する)
	public boolean is_match_position_drop = true;			// 判定位置と描写位置が一致しているか
	private int drop_bound_time = 0;
	private int van_time = 0;
	private static final int drop_anim_speed = 20;	// 落下アニメーションの落下速度
	private static final int bound_anim_speed = 6; // バウンドアニメーションの速度(小さいほど早い)
	private static final int bound_anim_count = 1; // バウンドアニメーションの回数
	private static final int max_van_time = 50; // 死に際ぷちゅの顔が表示される時間
	private static final int max_van_time_after = 40; // 消滅アニメーション再生から終了判定をとるまでの時間
	
	Puchu(int set_type, int set_x, int set_y) {
		this.type = set_type;
		this.x = this.draw_x = set_x;
		this.y = this.draw_y = set_y;
		is_match_position_drop = true;
	}
	
	//-- ぷちゅの要素コピー(配列内容すり替え用)
	public void copyPuchu(Puchu _puchu) {
		this.type = _puchu.type;
		this.x = this.draw_x = _puchu.x;
		this.y = this.draw_y = _puchu.y;
	}
	
	//-- ぷちゅの落下処理
	public void dropDown(int arr_i) {
		y = (arr_i - 2) * Draw.Squares;
		if ( draw_y < y ) { is_match_position_drop = false; }
		drop_bound_time = 0;
	}
	
	//-- ぷちゅの落下アニメーション
	public void drawingDropDown() {
		draw_y += drop_anim_speed;
		if ( draw_y >= y ) {
			draw_y = y;
			drawingDropBound();
		}
	}
	
	//-- ぷちゅの落下後のバウンドアニメーション
	public void drawingDropBound() {
		drop_bound_time++;
		draw_y = y + (int)(5*Math.sin(Math.PI * (1.0*drop_bound_time/bound_anim_speed)));
		if ( drop_bound_time/(bound_anim_speed*2) >= bound_anim_count ) {
			is_match_position_drop = true;
		}
	}
	
	//-- 消滅開始
	public void vanishOut() {
		type = Puchu.Van;
		van_time = 0;
	}
	
	//-- 消滅のディレイ
	public void vanishOutDelay() {
		van_time++;
		if ( van_time > max_van_time ) {
			type = Puchu.Vanishing;
		}
	}
	
	//-- 消滅アニメーション
	public void vanishOutAnim() {
		van_time++;
		if ( van_time > max_van_time + max_van_time_after ) {
			type = Puchu.Emp;
		}
	}
}
