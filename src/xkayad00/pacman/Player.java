package xkayad00.pacman;

import xkayad00.Engine.Engine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends PacmanUnit{
	boolean dead=false;
	int remainingKeys=0;
	int points=0;
	Player(int startX, int startY, PacMap map){
		xPos=startX;
		yPos=startY;
		map.units.add(this);
		xSpeed=ySpeed=Pacman.PLAYER_SPEED;
	}

	@Override
	public void render(Graphics g){
		final BufferedImage playerImage=ImageCache.current.player;
		g.drawImage(playerImage,
				Engine.engine.scaleX(xPos-Pacman.TILE_SIZE/2),
				Engine.engine.scaleY(yPos-Pacman.TILE_SIZE/2),
				Engine.engine.scaleSizeX(Pacman.TILE_SIZE),
				Engine.engine.scaleSizeY(Pacman.TILE_SIZE),
				null);
	}

	public void pickPoint(){
		points++;
	}
	public void pickKey(){
		remainingKeys--;
	}
}