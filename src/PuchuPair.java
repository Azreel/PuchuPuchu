import java.awt.*;
import javax.swing.*;

public class PuchuPair {
	public static final int Up = 0;
	public static final int Right = 1;
	public static final int Bottom = 2;
	public static final int Left = 3;
	
	Puchu puchu1;
	Puchu puchu2;
	int _x = 200;	// ¶¬À•W(‰º‘¤‚Ì‚Õ‚¿‚ã‚ª’†S‚É‚È‚é)
	int _y = 200;	// 
	public int form;	// p¨ó‘Ô(‚Õ‚¿‚ã2‚ªãA‰EA‰ºA¶)
	
	public PuchuPair(int set_id){
		this.form = PuchuPair.Up;
		this.puchu1 = new Puchu((int)(Math.random()*7)+1, _x, _y, set_id);
		this.puchu2 = new Puchu((int)(Math.random()*7)+1, _x, _y - 40, set_id + 1);
		//System.out.println(puchu1.type);
		//System.out.println(puchu2.type);
	}
	
	
//	-1 : p/2
//	 0 : 0
//	 1: -p/2
//	 2: -p
	
	//-- ‰ñ“]ˆ—	
	public void TurnRight() {
		puchu2.x = puchu1.x + (int)(40 * Math.cos((form - 1) * -Math.PI/2));	// ’´’Pƒ‚È‰ñ“]ˆ—(b’è)
		puchu2.y = puchu1.y + (int)(40 * Math.sin((form - 1) * -Math.PI/2));
		form = (form + 1) % 4;	// form‚ğ1‰ñ‰E‰ñ“]ó‘Ô‚É‚·‚é
	}
	
	public void TurnLeft() {
		puchu2.x = puchu1.x + (int)(40 * Math.cos((form - 1) * Math.PI/2));
		puchu2.y = puchu1.y + (int)(40 * Math.sin((form - 1) * Math.PI/2));
		form = (form + 4) % 4;	// form‚ğ1‰ñ¶‰ñ“]ó‘Ô‚É‚·‚é
	}
}
