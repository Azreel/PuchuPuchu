import java.awt.*;
import javax.swing.*;

public class PuchuPair {
	public static final int Up = 0;
	public static final int Right = 1;
	public static final int Bottom = 2;
	public static final int Left = 3;
	
	Puchu puchu1;
	Puchu puchu2;
	int _x = 200;		// 生成座標
	int _y = 200; 		//
	public int form;	// 姿勢状態(ぷちゅ1を中心にぷちゅ2がどこにいるか)
	
	PuchuPair(int set_id, int set_type1, int set_type2){
		this.form = PuchuPair.Up;
		this.puchu1 = new Puchu(set_type1, _x, _y, set_id);
		this.puchu2 = new Puchu(set_type2, _x, _y - 40, set_id + 1);		
	}
	
	//-- ぷちゅの回転処理
	public void TurnRight() {
		form = (form + 1) % 4;
		puchu2.x = puchu1.x + (int)(40 * Math.cos((form - 1) * Math.PI/2));
		puchu2.y = puchu1.y + (int)(40 * Math.sin((form - 1) * Math.PI/2));
	}
	
	public void TurnLeft() {
		form = (form + 3) % 4;
		puchu2.x = puchu1.x + (int)(40 * Math.cos((form - 1) * Math.PI/2));
		puchu2.y = puchu1.y + (int)(40 * Math.sin((form - 1) * Math.PI/2));
	}
	
}
