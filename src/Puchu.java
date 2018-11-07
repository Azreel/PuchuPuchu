import java.awt.*;
import javax.swing.*;

public class Puchu {
	
	public static final int Obs = -1, Emp = 0, Pat1 = 1, Pat2 = 2, Pat3 = 3, Pat4 = 4, Pat5 = 5, Pat6 = 6;
	// Obs : お邪魔
	// Emp : ぷちゅなし
	// Pat1 ~ Pat6
	
	public int type;		// ぷちゅの種類
	public int x;			// 座標
	public int y;			//
	
	Puchu(int set_type, int set_x, int set_y) {
		this.type = set_type;
		this.x = set_x;
		this.y = set_y;
	}
}
