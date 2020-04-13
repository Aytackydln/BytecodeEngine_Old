package xkayad00.pacman;

import java.awt.*;
import java.awt.image.BufferedImage;

import xkayad00.Engine.Engine;

public class Ghost extends PacmanUnit{
	int[] previousCoords={-1,-1};
	Ghost(int startX, int startY, PacMap map){
		xPos=startX;
		yPos=startY;
		map.units.add(this);
		xSpeed=ySpeed=Pacman.GHOST_SPEED;
	}

	@Override
	boolean canChangeDirection(){
		int[] currentCoords=getTileCoords();
		boolean samePlace=(previousCoords[0]==currentCoords[0])&&(previousCoords[1]==currentCoords[1]);
		return super.canChangeDirection()&&!samePlace;
	}

	@Override
	public void render(Graphics g){
		final BufferedImage playerImage=ImageCache.current.ghost;
		g.drawImage(playerImage,
				Engine.engine.scaleX(xPos-Pacman.TILE_SIZE/2),
				Engine.engine.scaleY(yPos-Pacman.TILE_SIZE/2),
				Engine.engine.scaleSizeX(Pacman.TILE_SIZE),
				Engine.engine.scaleSizeY(Pacman.TILE_SIZE),
				null);
	}
}
