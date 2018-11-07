import java.awt.*;
import javax.swing.*;

public class Puchu {
	
	public static enum Type { Obs, Emp, Pat1, Pat2, Pat3, Pat4, Pat5, Pat6 };
	// Obs : お邪魔
	// Emp : ぷちゅなし
	// Pat1 ~ Pat6
	
	public Type puchu_type;	// ぷちゅの種類
	public Image face;		// ぷちゅの顔
	public int x;			// 座標
	public int y;			//
	public int id;			// 1次元配列管理上のID
	
	Puchu(Type set_type, int set_x, int set_y, int set_id) {
		this.puchu_type = set_type;
		SetFace(set_type);
		this.x = set_x;
		this.y = set_y;
		this.id = set_id;
	}
	
	void SetFace(Type img_type) {
		//this.face = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/face"+img_type.values())); 
	}
}
