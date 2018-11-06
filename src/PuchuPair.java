import java.awt.*;
import javax.swing.*;

public class PuchuPair {
	public static final int Ver_Up = 0;
	public static final int Hor_Right = 1;
	public static final int Ver_Bottom = 2;
	public static final int Hor_Left = 3;
	
	public static final int Status_Now = 0;
	public static final int Status_Next1 = 1;
	public static final int Status_Next2 = 2;
	
	Puchu puchu1;
	Puchu puchu2;
	int _x;
	int _y;
	public int form;
	
	//PuchuPair pp = new PuchuPair(0, PuchuPair.Status_Now);
	
	public PuchuPair(int set_id, int status){
		switch(status) {
			case PuchuPair.Status_Now : _x = 10; _y = 10; break;
			case PuchuPair.Status_Next1 : _x = 2; _y = 2; break;
			case PuchuPair.Status_Next2 : _x = 1; _y = 1; break;
		}
		puchu1 = new Puchu((int)(Math.random()*7)+1, _x, _y, set_id);
		puchu2 = new Puchu((int)(Math.random()*7)+1, _x, _y, set_id + 1);
		//System.out.println(puchu1.type);
		//System.out.println(puchu2.type);
	}
	
	public void TurnRight() {
		
	}
	
	public void TurnLeft() {
		
	}
}
