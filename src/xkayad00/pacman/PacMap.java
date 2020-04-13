package xkayad00.pacman;

import xkayad00.Engine.Engine;
import xkayad00.Engine.Map;
import xkayad00.Engine.Unit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PacMap extends Map{
	Tile[][] map;
	ArrayList<Ghost> ghosts=new ArrayList<>();
	Player player;
	int totalKeys=0;
	PacMap(Path path){
		try(BufferedReader is=Files.newBufferedReader(path)){
			String firstLine=is.readLine();
			String[] dimensions=firstLine.split(" ");
			int height=Integer.valueOf(dimensions[0]);
			int width=Integer.valueOf(dimensions[1]);
			Engine.engine.camera.setView(
					width*Pacman.TILE_SIZE,
					height*Pacman.TILE_SIZE);
			Engine.engine.camera.changePos(
					width*Pacman.TILE_SIZE/2,
					height*Pacman.TILE_SIZE/2);
			map=new Tile[height][width];
			for(int i=0;i<height;i++){
				String line=is.readLine();
				for(int j=0;j<width;j++){
					char c=line.charAt(j);
					Tiles type=Tiles.EMPTY;
					switch(c){
						case 'W': type=Tiles.WALL;
						break;

						case 'G': type=Tiles.GATE;
						break;

						case '.': break;

						case 'C':{
							int xPos=j*Pacman.TILE_SIZE+Pacman.TILE_SIZE/2;
							int yPos=i*Pacman.TILE_SIZE+Pacman.TILE_SIZE/2;
							ghosts.add(new Ghost(xPos,yPos,this));
						}
						break;

						case 'P':{
							int xPos=j*Pacman.TILE_SIZE+Pacman.TILE_SIZE/2;
							int yPos=i*Pacman.TILE_SIZE+Pacman.TILE_SIZE/2;
							player=new Player(xPos, yPos, this);
						}
						break;

						case 'K': type=Tiles.KEY;
						totalKeys++;
						break;

						case 'o': type=Tiles.POINT;
						break;
					}
					map[i][j]=new Tile(type);
				}
			}
			player.remainingKeys=totalKeys;
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void renderUnits(Graphics g){
		Tile.i=0;
		Tile.j=0;
		for(Tile[] tSet:map){
			for(Tile t:tSet){
				t.render(g);
				Tile.i++;
			}
			Tile.i=0;
			Tile.j++;
		}
		//for(Unit u:units)u.render(g);
		for(Unit u:ghosts)u.render(g);
		player.render(g);
	}

	public boolean updateUnits(double delta, Set<Integer> pressedKeys){
		{
			if(pressedKeys.contains(KeyEvent.VK_UP)){
				if(player.direction==Directions.DOWN){
					player.direction=Directions.UP;
					player.nextDirection=Directions.NONE;
				}else if(player.direction!=Directions.UP) player.nextDirection=Directions.UP;
			}else if(pressedKeys.contains(KeyEvent.VK_DOWN)){
				if(player.direction==Directions.UP){
					player.direction=Directions.DOWN;
					player.nextDirection=Directions.NONE;
				}else if(player.direction!=Directions.DOWN) player.nextDirection=Directions.DOWN;
			}
			if(pressedKeys.contains(KeyEvent.VK_RIGHT)){
				if(player.direction==Directions.LEFT){
					player.direction=Directions.RIGHT;
					player.nextDirection=Directions.NONE;
				}
				if(player.direction!=Directions.RIGHT) player.nextDirection=Directions.RIGHT;
			}else if(pressedKeys.contains((KeyEvent.VK_LEFT))){
				if(player.direction==Directions.RIGHT){
					player.direction=Directions.LEFT;
					player.nextDirection=Directions.NONE;
				}
				if(player.direction!=Directions.LEFT) player.nextDirection=Directions.LEFT;
			}
			final int[] unitTile=player.getTileCoords();
			final int x=unitTile[0];
			final int y=unitTile[1];
			if(player.canChangeDirection()){
				if(player.nextDirection!=Directions.NONE){
					boolean canSwitch=false;
					switch(player.nextDirection){
						case RIGHT:
							if(x+1<map[0].length&&map[y][x+1].isMovable()) canSwitch=true;
							break;
						case LEFT:
							if(x >= 1&&map[y][x-1].isMovable()) canSwitch=true;
							break;
						case DOWN:
							if(y+1<map.length&&map[y+1][x].isMovable()) canSwitch=true;
							break;
						case UP:
							if(y >= 1&&map[y-1][x].isMovable()) canSwitch=true;
							break;
					}
					if(canSwitch){
						player.direction=player.nextDirection;
						player.nextDirection=Directions.NONE;
					}
				}
				boolean stop=true;
				switch(player.direction){
					case RIGHT:
						if(x+1<map[0].length&&map[y][x+1].isMovable()) stop=false;
						break;
					case LEFT:
						if(x >= 1&&map[y][x-1].isMovable()) stop=false;
						break;
					case DOWN:
						if(y+1<map.length&&map[y+1][x].isMovable()) stop=false;
						break;
					case UP:
						if(y >= 1&&map[y-1][x].isMovable()) stop=false;
						break;
				}
				if(stop) player.direction=Directions.NONE;
			}
			Tile playerTile=map[y][x];
			if(playerTile!=null){
				switch(playerTile.type){
					case POINT:
						System.out.println("Another point!!!");        //TODO take the point
						player.pickPoint();
						playerTile.type=Tiles.EMPTY;
						break;
					case KEY:
						System.out.println("You got the key!!!");    //TODO take the key
						player.pickKey();
						playerTile.type=Tiles.EMPTY;
						break;
					case GATE:
						if(player.remainingKeys==0){
							return true;
						}
						break;
				}
			}
		}
		for(Ghost g:ghosts){
			if(g.canChangeDirection()){
				/*
				0 - right
				1 - bottom
				2 - left
				3 - top
				 */
				final int[] tile=g.getTileCoords();
				final int x=tile[0];
				final int y=tile[1];
				ArrayList<Directions> randomPool=new ArrayList<>(4);
				if(x+1<map[0].length)
					if(g.direction!=Directions.LEFT&&map[y][x+1].isMovable())
						randomPool.add(Directions.RIGHT);
				if(y+1<map.length)
					if(g.direction!=Directions.UP&&map[y+1][x].isMovable())
						randomPool.add(Directions.DOWN);
				if(x>0)
					if(g.direction!=Directions.RIGHT&&map[y][x-1].isMovable())
						randomPool.add(Directions.LEFT);
				if(y>0)
					if(g.direction!=Directions.DOWN&&map[y-1][x].isMovable())
						randomPool.add(Directions.UP);
				if(randomPool.size()>0){
					int rnd=ThreadLocalRandom.current().nextInt(randomPool.size());
					switch(randomPool.get(rnd)){
						case RIGHT: g.direction=Directions.RIGHT;
						break;
						case DOWN: g.direction=Directions.DOWN;
						break;
						case LEFT: g.direction=Directions.LEFT;
						break;
						case UP: g.direction=Directions.UP;
						break;
					}
					g.previousCoords=tile;
				}else g.direction=Directions.NONE;
			}
		}

		player.tick(delta,pressedKeys);
		for(Ghost u:ghosts){
			u.tick(delta,pressedKeys);
			int distanceWithPlayer=getDistance(player,u);
			if(distanceWithPlayer<Pacman.TILE_SIZE-Pacman.TURN_ACCEPTANCE_RADIUS){
				player.dead=true;
			}
		}
		return false;
	}
	public Player getPlayer(){return player;}

	int getDistance(PacmanUnit u1, PacmanUnit u2){
		double xDist=u1.getXPos()-u2.getXPos();
		double yDist=u1.getYPos()-u2.getYPos();
		double dist=Math.sqrt(Math.abs(xDist*xDist+yDist*yDist));
		return ((int) dist);
	}
}
