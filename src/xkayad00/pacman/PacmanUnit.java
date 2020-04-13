package xkayad00.pacman;

import xkayad00.Engine.Unit;

import java.util.Set;

public class PacmanUnit extends Unit{
	Directions direction=Directions.NONE;
	Directions nextDirection=Directions.NONE;

	public double getXPos(){return xPos;}
	public double getYPos(){return yPos;}
	boolean canChangeDirection(){
		int xDist=(((int) xPos)-(Pacman.TILE_SIZE/2))%Pacman.TILE_SIZE;
		int yDist=(((int) yPos)-(Pacman.TILE_SIZE/2))%Pacman.TILE_SIZE;
		boolean xOK=xDist<Pacman.TURN_ACCEPTANCE_RADIUS||xDist>Pacman.TILE_SIZE-Pacman.TURN_ACCEPTANCE_RADIUS;
		boolean yOK=yDist<Pacman.TURN_ACCEPTANCE_RADIUS||yDist>Pacman.TILE_SIZE-Pacman.TURN_ACCEPTANCE_RADIUS;
		return xOK&&yOK;
	}

	@Override
	public void tick(double delta, Set<Integer> pressedKeys){
		switch(direction){
			case UP: yPos-=xSpeed*delta;
				break;
			case DOWN: yPos+=xSpeed*delta;
				break;
			case LEFT: xPos-=ySpeed*delta;
				break;
			case RIGHT: xPos+=ySpeed*delta;
				break;
		}
	}

	int[] getTileCoords(){
		int x=((int) getXPos())/Pacman.TILE_SIZE;
		int y=((int) getYPos())/Pacman.TILE_SIZE;
		return new int[]{x, y};
	}
}
