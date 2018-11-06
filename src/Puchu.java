import java.awt.*;
import javax.swing.*;

public class Puchu {
	
	public static final int Obs = -1;
	public static final int Empty = 0;
	public static final int Pat1 = 1;
	public static final int Pat2 = 2;
	public static final int Pat3 = 3;
	public static final int Pat4 = 4;
	public static final int Pat5 = 5;
	public static final int Pat6 = 6;
	public static final int Pat7 = 7;
	
	public int type;
	public Image face;
	public int x;
	public int y;
	public int id;
	
	public Puchu(int set_type, int set_x, int set_y, int set_id) {
		this.type = set_type;
		SetFace(set_type);
		this.x = set_x;
		this.y = set_y;
		this.id = set_id;
	}
	
	void SetFace(int img_type) {
		//this.face = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/face"+img_type)); 
	}
}
