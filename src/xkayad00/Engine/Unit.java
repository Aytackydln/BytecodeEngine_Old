package xkayad00.Engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

public class Unit{
	protected double xPos=0;
	protected double yPos=0;
	protected long SPEED=0;
	protected double xSpeed=0;
	protected double ySpeed=0;
	protected BufferedImage image;

	@Deprecated
	public void tick(double delta){};
	public void tick(double delta, Set<Integer> pressedKeys){};
	public void render(Graphics g){
		g.drawImage(image, Engine.engine.scaleX(xPos), Engine.engine.scaleY(yPos), Engine.engine.scaleX(image.getWidth()), Engine.engine.scaleY(image.getHeight()), null);
	}
}
