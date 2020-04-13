package xkayad00.pacman;

import xkayad00.Engine.Engine;
import xkayad00.Engine.Unit;

import java.awt.*;

public class Tile extends Unit{
	static int i=0;
	static int j=0;
	Tiles type;
	Tile(Tiles type){
		changeType(type);
	}
	void changeType(Tiles type){
		this.type=type;
	}
	boolean isMovable(){
		return type!=Tiles.WALL;
	}

	@Override
	public void render(Graphics g){
		if(ImageCache.current==null){
			System.out.println("No image cache!!!");
			return;
		}
		switch(type){
			case KEY: image=ImageCache.current.key;
			break;

			case GATE: image=ImageCache.current.gate;
			break;

			case WALL: image=ImageCache.current.wall;
			break;

			case POINT: image=ImageCache.current.point;
			break;

			case EMPTY: return;

			default:
				System.out.println(type);
				throw new UnsupportedOperationException("Unhandled tile type???");
		}
		try{/*
			g.drawImage(image,
					Engine.engine.scaleX(i*Pacman.TILE_SIZE+Pacman.TILE_SIZE/2-image.getWidth()/2),
					Engine.engine.scaleY(j*Pacman.TILE_SIZE+Pacman.TILE_SIZE/2-image.getHeight()/2),
					Engine.engine.scaleSizeX(image.getWidth()),
					Engine.engine.scaleSizeY(image.getHeight()),
					null);*/
			g.drawImage(image,
					Engine.engine.scaleX(i*Pacman.TILE_SIZE),
					Engine.engine.scaleY(j*Pacman.TILE_SIZE),
					Engine.engine.scaleSizeX(Pacman.TILE_SIZE),
					Engine.engine.scaleSizeY(Pacman.TILE_SIZE),
					null);
		}catch(NullPointerException e){
			System.out.println("cant draw "+type);
			if(Engine.engine==null) System.out.println("Engine.engine is null!!!");
			if(image==null) System.out.println("unit image is null!!!");
		}
	}
}
