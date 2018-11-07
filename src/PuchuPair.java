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
	
	public PuchuPair(int set_id){
		this.form = PuchuPair.Up;
		this.puchu1 = new Puchu(Puchu.Type.valueOf("Pat"+(int)(Math.random()*6)+1), _x, _y, set_id);
		this.puchu2 = new Puchu(Puchu.Type.valueOf("Pat"+(int)(Math.random()*6)+1), _x, _y - 40, set_id + 1);
		//System.out.println(puchu1.type);
		//System.out.println(puchu2.type);
	}
	
	//-- ぷちゅの回転処理
	public void TurnRight() {
		puchu2.x = puchu1.x + (int)(40 * Math.cos((form - 1) * -Math.PI/2));	// 
		puchu2.y = puchu1.y + (int)(40 * Math.sin((form - 1) * -Math.PI/2));
		form = (form + 1) % 4;
	}
	
	public void TurnLeft() {
		puchu2.x = puchu1.x + (int)(40 * Math.cos((form - 1) * Math.PI/2));
		puchu2.y = puchu1.y + (int)(40 * Math.sin((form - 1) * Math.PI/2));
		form = (form + 4) % 4;
	}
	
}
