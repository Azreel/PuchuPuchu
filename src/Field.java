import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Field {
	
	public int score = 0;
	public Puchu[][] cell = new Puchu[12][6];
	public PuchuPair now, next1, next2;
	
	public Field() {	

	}
	
	public void create_puchu(){	//first create
		
		Random rnd = new Random();
		
		now = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//振ってくるぷちゅ
		next1 = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//次のぷちゅ
		next2 = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));	//次の次のぷちゅ
	}
	
	public void update_puchu() {	//ぷちゅの更新
		
		Random rnd = new Random();
		
		now.puchu1.type = next1.puchu1.type;
		now.puchu2.type = next1.puchu2.type;
		
		next1.puchu1.type = next2.puchu1.type;
		next1.puchu2.type = next2.puchu2.type;
		
		next2 = new PuchuPair(rnd.nextInt(6), rnd.nextInt(6));
	}
	
}