package xkayad00.pacman;

import xkayad00.Engine.Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class Pacman extends Engine{
	static final int TURN_ACCEPTANCE_RADIUS=1;
	static final int TILE_SIZE=80;
	static final int PLAYER_SPEED=100;
	static final int GHOST_SPEED=80;
	private Button pointsButton=new Button("Points: ");
	private Button keysButton=new Button("Remaining remainingKeys: ");
	private Player player;
	private PacMap pacMap;
	private Path mapPath=Paths.get("resources\\map1.txt");
	private Pacman(){
		Thread cacheLoader=ImageCache.loadCacheThread(Paths.get("resources\\pack1"));
		cacheLoader.start();

		JMenuItem mapLoader=new JMenuItem("Load Map:");
		mapLoader.addActionListener((e)->{
			loadMap(JOptionPane.showInputDialog("Enter map file: ", mapPath.toString()));
		});
		menu1.add(mapLoader);
		JMenuItem imageLoadButton=new JMenuItem("Load Image Pack:");
		imageLoadButton.addActionListener((e)->{
			ImageCache.loadCacheThread(JOptionPane.showInputDialog("Enter image folder: ",
					"resources\\")).start();
		});
		menu1.add(imageLoadButton);
		pointsButton.setFocusable(false);
		keysButton.setFocusable(false);
		menuBar.add(pointsButton);
		menuBar.add(keysButton);
		loadMap(mapPath);
		System.out.println(map);

		try{
			cacheLoader.join();
		}catch(InterruptedException ignored){
		}
	}

	public static void main(String[] args){
		new Pacman().run();
	}
	@Override
	protected void gameCodes(double delta, Set<Integer> pressedKeys){
		if(pacMap.updateUnits(delta,pressedKeys)){
			JOptionPane.showMessageDialog(frame,"You Won!!!");
			loadMap(new PacMap(mapPath));
		}
		setPoints(player.points);
		setKeys(player.remainingKeys);
		if(player.dead){
			//JOptionPane.showMessageDialog(frame,"You died!!");
			loadMap(new PacMap(mapPath));
		}
	}

	private void setPoints(int points){
		pointsButton.setLabel("Points: "+points);
	}
	private void loadMap(PacMap map){
		pacMap=map;
		this.map=map;
		player=pacMap.getPlayer();
	}
	private void loadMap(Path path){
		mapPath=path;
		loadMap(new PacMap(path));
	}
	private void loadMap(String mapFile){
		loadMap(Paths.get(mapFile));
	}

	private void setKeys(int keys){
		keysButton.setLabel("Remaining remainingKeys: "+keys);
	}

	@Override
	protected void reset(){
	}
	@Override
	protected void menuBar(){

	}
	@Override
	protected void actions(ActionEvent e){
	}
	@Override
	public void resolutions(){
		resolutions.add("800*600");
	}
}
