package xkayad00.pacman;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class ImageCache{
	static ImageCache current;
	BufferedImage wall;
	BufferedImage player;
	BufferedImage ghost;
	BufferedImage point;
	BufferedImage key;
	BufferedImage gate;
	private ImageCache(Path path){
		wall=ImageCache.this.loadImage(path.resolve("wall.png"));
		player=ImageCache.this.loadImage(path.resolve("player.png"));
		ghost=ImageCache.this.loadImage(path.resolve("ghost.png"));
		point=ImageCache.this.loadImage(path.resolve("point.png"));
		key=ImageCache.this.loadImage(path.resolve("key.png"));
		gate=ImageCache.this.loadImage(path.resolve("gate.png"));
	}
	private BufferedImage loadImage(Path path){
		BufferedImage image=null;
		System.out.println("Loading image: "+path);
		try{
			image=ImageIO.read(path.toFile());
		}catch(IOException e){
			e.printStackTrace();
		}
		return image;
	}
	static Thread loadCacheThread(Path path){
		return new Thread(() -> {
			System.out.println("Loading: "+path.toString());
			current=new ImageCache(path);
		});
	}
	static Thread loadCacheThread(String folder){
		return loadCacheThread(Paths.get(folder));
	}
}
