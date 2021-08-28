package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class Main {

	static final int cellXStart = 15;
	static final int cellYStart = 0;
	
	public static void main(String[] args) throws Exception {

		final BufferedImage img = new BufferedImage(1000, 15, BufferedImage.TYPE_INT_RGB);
		final Graphics g = img.getGraphics();

		Font f = new Font("Dialog", Font.PLAIN, 12);

		final int maxWidth = 20;

		StringBuilder out = new StringBuilder("StartLine 10000\n");

		{
			int alloLine = cellYStart;
			for (int h = 1; h < 16; h++) out.append("Add 38 n ").append(h).append(" ").append(alloLine++).append("\n");
			out.append("\n");
		}
		{
			int alloLine = cellXStart;
			for (int h = 1; h < maxWidth + 1; h++) out.append("Add 37 n ").append(h).append(" ").append(alloLine++).append("\n");
			out.append("\n");
		}


		int x = 0;
		int pointerIndex = 0;


		for (int i = 32; i < 128; i++) {
			char c = (char) i;
			BufferedImage image = getImage(c, f);

			out.append("If == 36 n " + i + " pointer" + pointerIndex + "\n");
			String txt = getText(c, image);
			out.append(txt);

			out.append("Init " + image.getWidth() + " 39\n" + "Cell Return\n" + ": pointer" + pointerIndex++ + "\n");


			g.drawImage(image, x, 0, null);
			x += image.getWidth();
		}

		out.append("Init 0 39\nCell Return");


		KLLL_Compiler_L1 comp = new KLLL_Compiler_L1();
		String compiled = comp.compileEmulated(out.toString());


		new File("C:\\charLib\\").mkdirs();
		PrintWriter w = new PrintWriter("C:\\charLib\\charLib.txt");
		w.println(compiled.toString());
		w.close();

		ImageIO.write(img, "png", new File("C:\\charLib\\img.png"));

	}


	static final Color textColor = Color.black;

	static String getText(char c, BufferedImage image) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = new Color(image.getRGB(x, y));
				if (!(color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255)) {
					sb.append("Pixel c " + (cellXStart + x) + " c " + (cellYStart + y) + " n " + textColor.getRed() + " n " + textColor.getGreen() + " n " + textColor.getBlue() + "\n");
				}
			}
		}
		return sb.toString();
	}



	public static BufferedImage getImage(char letter, Font font) {
		String letterS = "" + letter;

		Rectangle2D rect;
		{
			BufferedImage t1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = t1.createGraphics();
			g.setFont(font);
			rect = g.getFontMetrics(font).getStringBounds(letterS, null);
		}

		int letterWidth = (int) rect.getWidth();
		int letterHeight = (int) rect.getHeight();
		if (letterWidth == 0) return null;
		BufferedImage img = new BufferedImage(letterWidth, letterHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = img.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, letterWidth, letterHeight);
		g.setFont(font);
		g.setColor(Color.black);
		g.drawString(letterS, 0, 10);
		g.dispose();
		return img;
	}




}
