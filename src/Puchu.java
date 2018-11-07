import java.awt.*;
import javax.swing.*;

public class Puchu {
	
	public static enum Type { Obs, Emp, Pat1, Pat2, Pat3, Pat4, Pat5, Pat6, Pat7 }
	
	public int puchu_type;
	public Image face;
	public int x;
	public int y;
	public int id;
	
	public Puchu(int set_type, int set_x, int set_y, int set_id) {
		this.puchu_type = set_type;
		SetFace(set_type);
		this.x = set_x;
		this.y = set_y;
		this.id = set_id;
	}
	
	void SetFace(int img_type) {
		//this.face = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/face"+img_type)); 
	}
}
