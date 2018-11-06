
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Title extends JPanel{
	Toolkit tk;
	JButton solo;
	JButton duo;
	JLabel myIP;
	Image bg;
	
	public Title() {
		solo = new JButton("1Pプレイ");
        duo = new JButton("2Pプレイ");
        tk = Toolkit.getDefaultToolkit(); 
        bg = tk.getImage(getClass().getResource("title.png"));
        
        this.add(solo);
        this.add(duo);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(bg, null, this);
    }
}
