package me.krypek.igb.charlib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import me.krypek.igb.cl1.IGB_MA;
import me.krypek.utils.Pair;
import me.krypek.utils.Utils;

public class IGB_charlib {

	public static void main(String[] args) throws IOException {
		Font font = new Font("Dialog", Font.PLAIN, 12);

		IGB_charlib charlib = new IGB_charlib(font, Color.black, Color.white, 32, 127);

		String l2Code = charlib.getL2Code();
		Utils.writeIntoFile("/home/krypek/Desktop/IGB/L2/charlib.igb_l2", l2Code);

		var pair = charlib.getFormatedL2Code();
		Utils.serialize(pair, "/home/krypek/Desktop/IGB/L2/charlib.bin");

		// System.out.println(l2Code);
	}

	private final Font font;
	private final Color fontColor;
	private final Color backgroundColor;
	private final int height;
	private final int width;
	private final int charMin;
	private final int charMax;

	public IGB_charlib(Font font, Color fontColor, Color backgroundColor, int charMin, int charMax) {
		this.font = font;
		this.fontColor = fontColor;
		this.backgroundColor = backgroundColor;
		this.charMin = charMin;
		this.charMax = charMax;

		width = (int) Math.ceil(getCharSize('W')[0]);
		height = (int) Math.ceil(getCharSize('a')[1]);
		System.out.println("width: " + width);
		System.out.println("height: " + height);
		if(width + height > 35)
			throw new IllegalArgumentException("Size too big");
	}

	private int[] getCharSize(char c) {
		BufferedImage t1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = t1.createGraphics();
		g.setFont(font);
		int width = g.getFontMetrics().stringWidth(c + "");
		int height = g.getFontMetrics().getHeight();
		return new int[] { width, height };
	}

	public BufferedImage getCharImage(char c) {
		if(!font.canDisplay(c)) {
			System.out.println("Cant display: \'" + c + "\'");
			return null;
		}
		int[] size = getCharSize(c);
		int width = size[0];
		int height = size[1];

		if(width == 0)
			return null;

		final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = img.createGraphics();
		g.setColor(backgroundColor);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());

		g.setFont(font);
		g.setColor(fontColor);
		g.setColor(Color.black);
		g.drawString(c + "", 0, 10);

		g.dispose();
		return img;
	}

	private final int START_CELL = IGB_MA.CHARLIB_TEMP_START;

	private final String COMPILERVAR_STRING = """
			$ramcell = 0;
			$ramlimit = 0;
			$startLine = 0;
			$lenlimit = 3500;
			$thread = 0;
			""";

	private final String FUNC_STRING = """
			int drawChar(int w0|%d|, int h0|%d|, int c|%d|) {
			""".formatted(IGB_MA.CHARLIB_X, IGB_MA.CHARLIB_Y, IGB_MA.CHARLIB_CHAR);

	private String setVariables() {
		StringBuilder sb = new StringBuilder();
		int cell = START_CELL;
		// width

		for (int i = 1; i < width; i++, cell++) {
			String space = (i < 10 ? " " : "");
			sb.append("\tint w" + i + space + "|" + cell + "|" + " = w0 + " + i + space + ";\n");
		}
		sb.append("\n");
		// height
		for (int i = 1; i < height; i++, cell++) {
			String space = (i < 10 ? " " : "");
			sb.append("\tint h" + i + space + "|" + cell + "|" + " = h0 + " + i + space + ";\n");
		}
		return sb.toString();
	}

	public Pair<String[], int[]> getFormatedL2Code() {
		String l2 = getL2Code();
		var pair = me.krypek.igb.cl2.PrecompilationFile.format(l2);
		return pair;
	}

	public String getL2Code() {
		StringBuilder sb = new StringBuilder(100);
		sb.append(COMPILERVAR_STRING);
		sb.append(FUNC_STRING);

		// int r = fontColor.getRed(), g = fontColor.getBlue(), b = fontColor.getBlue();
		// sb.append("\tpixelcache(" + r + ", " + g + ", " + b + ");\n");

		sb.append(setVariables());

		for (char c = (char) charMin; c < charMax; c++) {
			sb.append(getL2CodeFromChar(c));

		}
		sb.append("}");
		return sb.toString();
	}

	public String getL2CodeFromChar(char c) {
		BufferedImage img = getCharImage(c);
		if(img == null)
			return "";

		StringBuilder sb = new StringBuilder(100);
		sb.append("\tif(c==\'" + c + "\') {\n");

		final int maxWidth = "setpixel(w10, h10); ".length();

		for (int y = 0; y < img.getHeight(); y++) {
			StringBuilder sb1 = new StringBuilder(100);
			sb1.append("\t\t");
			for (int x = 0; x < img.getWidth(); x++) {
				Color color = new Color(img.getRGB(x, y));

				if(!color.equals(backgroundColor)) {
					String str = "setpixel(w" + x + ", h" + y + ");";
					sb1.append(str);
					final int spacesToAdd = maxWidth - str.length();
					for (int i = 0; i < spacesToAdd; i++)
						sb1.append(" ");
				} else
					for (int i = 0; i < maxWidth; i++)
						sb1.append(" ");
			}
			sb1.append("\n");
			String str = sb1.toString();
			if(!str.isBlank())
				sb.append(str);
		}
		sb.append("\t\treturn " + img.getWidth() + ";\n");

		sb.append("\t}\n");
		return sb.toString();
	}

}
