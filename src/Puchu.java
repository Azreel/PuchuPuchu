import java.awt.*;
import javax.swing.*;

public class Puchu {
	
	public static final int Obs = -1, Emp = 0, Pat1 = 1, Pat2 = 2, Pat3 = 3, Pat4 = 4, Pat5 = 5, Pat6 = 6;
	// Obs : お邪魔
	// Emp : ぷちゅなし
	// Pat1 ~ Pat6
	
	public int type;			// ぷちゅの種類
	public int x, y;			// 判定座標
	public int draw_x, draw_y;	// 描画に使用する座標(アニメーションのため遅延して動作する)
	public boolean is_match_position = true;		// 判定位置と描写位置が一致しているか
	private static final int drop_anim_speed = 20;	// 落下アニメーションの落下速度
	
	Puchu(int set_type, int set_x, int set_y) {
		this.type = set_type;
		this.x = this.draw_x = set_x;
		this.y = this.draw_y = set_y;
	}
	
	public void drawingDropDown() {
		draw_y += drop_anim_speed;
		if ( draw_y >= y ) { is_match_position = true; }
	}
	
	public void dropDown(int arr_i) {
		y = arr_i * Draw.Squares;
		if ( draw_y < y ) { is_match_position = false; }
	}
}
