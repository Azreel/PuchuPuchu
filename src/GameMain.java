import java.awt.*;
import javax.swing.*;

public class GameMain {
	final int ScreenW = 960;
	final int ScreenH = 640;
	
	public JFrame frame = new JFrame();
	
	private Field me;
	private Field rival;
	private Title title;
	
	public GameMain(){
		MakeWindow();
		title = new Title();
		title.setPreferredSize(new Dimension(ScreenW, ScreenH));
		frame.add(title);
		frame.pack();
	}
	
	private void MakeWindow() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 終了処理
		frame.setSize(ScreenW, ScreenH);
	    frame.setLocationRelativeTo(null);
		frame.setTitle("ぷちゅぷちゅ");
		frame.setResizable(false);// サイズ変更不可
		frame.setLayout(new GridLayout());
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new GameMain();
	}
}
