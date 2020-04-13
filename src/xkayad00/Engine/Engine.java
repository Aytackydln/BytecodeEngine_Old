package xkayad00.Engine;
/*0.1.1

 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;


public abstract class Engine extends JPanel implements KeyListener, ActionListener, ItemListener{
	protected static boolean run=true;
	public static Engine engine;
	public final static Set<Integer> pressed=new TreeSet<>();

	public static int width=1200;
	public static int height=800;
	static final double maxDelta=0.02091;    //as seconds
	public static double delta=0;    //as seconds

	protected final JFrame frame;
	private final JPanel panel;

	private static long lastUpdateTime=System.nanoTime();
	private static int GAME_HERTZ=128;
	private static long TIME_BETWEEN_UPDATES=1000000000/GAME_HERTZ;
	private static int target_fps=64;
	private static long TARGET_TIME_BETWEEN_RENDERS=1000000000/target_fps;
	private static long lastRenderTime=0;
	public int fps=0;
	public int ups=0;
	private int topInset, rightInset;
	private int frameCount=0;
	private int updateCount=0;

	protected static ArrayList<String> resolutions=new ArrayList<>();
	protected static ArrayList<String> variables=new ArrayList<>();
	private String[] res={"10", "10"};

	protected JMenuBar menuBar;
	protected JMenu menu1; //Game
	private JMenuItem m11; //reset
	private JMenuItem m21; //Show stats
	private ArrayList<JMenuItem> resolutionObjs=new ArrayList<>();
	private JMenuItem m2s1; //128Hz speed
	private JMenuItem m2s2; //64Hz speed
	private JMenuItem m2s3; //48Hz speed
	private JMenuItem m2f1; //128 fps
	private JMenuItem m2f2; //64 fps
	private JMenuItem m2f3; //32 fps

	protected boolean showStats=true;
	public static Random rng=new Random();
	private ArrayList<Text> texts=new ArrayList<>();

	protected Map map;
	public Camera camera=new Camera(0, 0);

	public Engine(){
		System.setProperty("sun.java2d.opengl", "true");

		engine=this;
		variables.addAll(new ArrayList<>(Arrays.asList("fps", "ups")));

		frame=new JFrame("Football");
		frame.setSize(width+5, height+30);
		frame.setLocation(200, 20);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				run=false;
				System.exit(0);
			}
		});
		frame.setResizable(true);
		//panel=new JPanel(new BorderLayout());
		panel=this;
		panel.addComponentListener(new ComponentListener(){
			@Override
			public void componentResized(ComponentEvent e){
				camera.updateScales(panel.getWidth(),panel.getHeight());
				System.out.println("resize");
			}
			@Override
			public void componentMoved(ComponentEvent e){}
			@Override
			public void componentShown(ComponentEvent e){}
			@Override
			public void componentHidden(ComponentEvent e){}
		});
		System.out.println(frame.getGraphicsConfiguration().getBufferCapabilities().isPageFlipping());


		frame.pack();
		topInset=frame.getInsets().top+frame.getInsets().bottom;
		rightInset=frame.getInsets().right+frame.getInsets().left;

		resolutions();  //abstact
		for(String s : resolutions){
			resolutionObjs.add(new JMenuItem(s));
		}
		res=resolutions.get(0).split("\\*");
		int firstW=Integer.parseInt(res[0]);
		int firstH=Integer.parseInt(res[1]);

		System.out.println("first Width: "+firstW+" first height: "+firstH);

		//System.out.println("x scale: "+scaleX());
		//System.out.println("y scale: "+scaleY());

		frame.setJMenuBar(menuBarimiz());
		frame.setContentPane(panel);    //TODO
		//menuBar.setVisible(false);
		menuBar.setFocusable(false);
		menuBar.setRequestFocusEnabled(false);

		setLayout(null);
		setSize(width, height);
		setLocation(0, 0);
		addKeyListener(this);
		setFocusable(true);
		setBackground(Color.BLACK);
		requestFocusInWindow();
		requestFocus();
		//panel.requestFocusInWindow();
		//menuBar.setVisible(true);


		System.out.println("sup created");
	}

	public void initialize(){
		res=resolutionObjs.get(0).getText().split("\\*");
		setFrame(Integer.parseInt(res[0]), Integer.parseInt(res[1]));
		readSett();
	}
	private void readSett(){//TODO
		System.out.println("reading settings");
		try{
			List<String> readSettings=Files.readAllLines(Paths.get("settings.txt"));
			String[] change;
			for(String s : readSettings){
				change=s.split("=");
				String var=change[0];
				String val=change[1];
				System.out.println("set "+var+" to "+val);
				try{
					this.getClass().getDeclaredField(var).set(this, val);
				}catch(Throwable e){
					try{
						this.getClass().getField(var).get(this);
					}catch(Throwable e1){
						System.out.println("Could not set saved variable ("+var+")");
					}
				}
			}
		}catch(IOException e){
			try{
				new Formatter("settings.txt");
				System.out.println("settings.txt created");
			}catch(FileNotFoundException ignored){
				System.out.println("Could not create settings.txt");
			}
		}
	}

	protected abstract void gameCodes(double delta, Set<Integer> pressedKeys);
	protected abstract void reset();

	/**
	 * ?ref=new JMenuItem("?buttonName");
	 * ?ref.addActionListener(this);
	 * menu1.add(?ref);
	 */
	protected abstract void menuBar();
	protected abstract void actions(ActionEvent e);

	/**
	 * add resolutions as:
	 * resolutions.add("?width*?height")
	 * each resolution should be at same ratio
	 */
	public abstract void resolutions();
	private void saveConf() throws IllegalAccessException{
		String s="";
		for(String a:variables)
			try{
				s+=a+"="+this.getClass().getDeclaredField(a).get(this)+"\n";
			}catch(Exception e){
				try{
					s+=a+"="+this.getClass().getField(a).get(this)+"\n";
				}catch(Exception e1){
					System.out.println("There was an error saving "+a+e1);
					e1.printStackTrace();
				}
			}
		Formatter f=null;
		try{
			f=new Formatter(new File("settings.txt"));
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		f.format(s);
		f.close();
		System.out.println("saved configs");
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(map!=null)map.renderUnits(g);
		if(showStats){
			g.drawString("FPS: "+fps+"    UPS:"+ups, 5, 10);
		}
		for(Text t : texts) t.render(g);
	}
	public void keyTyped(KeyEvent e){}
	public void run(){
		initialize();

		frame.setVisible(true);
		requestFocusInWindow();
		int sleepTime=0;

		new TimedEvent(500){
			@Override
			public void run(){
				while(run){
					super.run();
					ups=updateCount*2;
					updateCount=0;
					fps=frameCount*2;
					frameCount=0;
					getGraphics().dispose();
				}
			}
		}.start(); //fps and ups updater
		// Game loop
		while(run){
			if(System.nanoTime()-lastUpdateTime>=TIME_BETWEEN_UPDATES){				//check if it is time to update
				if(delta>maxDelta){
					if(showStats)System.out.println("latency detected "+delta);
					delta=maxDelta;
				}
				long previousUpdateTime=lastUpdateTime;
				lastUpdateTime=System.nanoTime();
				gameCodes(delta, pressed);
				updateCount++;
				if(System.nanoTime()-lastRenderTime>=TARGET_TIME_BETWEEN_RENDERS){	//check if it is time to render
					lastRenderTime=lastUpdateTime;
					frame.repaint();
					frameCount++;
				}
				delta=(System.nanoTime()-(double) previousUpdateTime)/1000000000;

				sleepTime=(int) (TIME_BETWEEN_UPDATES*2/1000000-delta*1000);
				if(sleepTime>20) sleepTime=19;
				try{
					Thread.sleep(sleepTime, 0);
				}catch(Exception ignored){
				}
			}
		}
		try{
			saveConf();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}
	}
	public void keyPressed(KeyEvent e){
		int a=e.getKeyCode();
		pressed.add(a);
		if(a=='K'){
			if(!menuBar.isVisible()){
				menuBar.setVisible(true);
			}
		}
		else if(a=='L'){
			if(menuBar.isVisible()){
				menuBar.setVisible(false);
			}
		}else if(a==KeyEvent.VK_NUMPAD6) camera.move(2, 0);
		else if(a==KeyEvent.VK_NUMPAD4) camera.move(-2, 0);
		else if(a==KeyEvent.VK_NUMPAD8) camera.move(0, -2);
		else if(a==KeyEvent.VK_NUMPAD2) camera.move(0, 2);
		else if(a==KeyEvent.VK_NUMPAD9) camera.zoom(0.04);
		else if(a==KeyEvent.VK_NUMPAD3) camera.zoom(-0.04);
	}
	public void keyReleased(KeyEvent e){
		pressed.remove(e.getKeyCode());
	}
	public static void main(){
		System.out.println("you must run implemented class");
	}
	public void actionPerformed(ActionEvent e){		//handle menubar's buttons etc. warning!! badly written
		System.out.println("pressed something");
		for(JMenuItem source : resolutionObjs){
			if((e.getSource())==source){
				String[] res=source.getText().split("\\*");
				setFrame(Integer.parseInt(res[0]),Integer.parseInt(res[1]));
			}
		}
		if((e.getSource())==m21){
			System.out.println("show stats");
			showStats=!showStats;
		}else if((e.getSource())==m11){
			reset();
		/*}else if((e.getSource())==m2r1){
			setFrame(1200,800);
		}else if((e.getSource())==m2r2){
			setFrame(900,600);*/
		}else if(e.getSource()==m2s1){
			setUps(128);
		}else if(e.getSource()==m2s2){
			setUps(64);
		}else if(e.getSource()==m2s3){
			setUps(52);
		}else if(e.getSource()==m2f1){
			setFps(128);
		}else if(e.getSource()==m2f2){
			setFps(64);
		}else if(e.getSource()==m2f3){
			setFps(52);
		}else actions(e);
	}
	@Override
	public void itemStateChanged(ItemEvent e){}

	private JMenuBar menuBarimiz(){ //add items to the menubar. warning!! very badly written
		menuBar=new JMenuBar();
		menu1=new JMenu("Game");
		JMenu menu2=new JMenu("Engine");
		m11=new JMenuItem("Reset");
		m21=new JMenuItem("Show Stats");
		m2s1=new JMenuItem("128Hz");
		m2s2=new JMenuItem("64Hz");
		m2s3=new JMenuItem("50Hz");
		m2f1=new JMenuItem("128");
		m2f2=new JMenuItem("64");
		m2f3=new JMenuItem("50");
		m21.addActionListener(this);
		m11.addActionListener(this);
		m2s1.addActionListener(this);
		m2s2.addActionListener(this);
		m2s3.addActionListener(this);
		m2f1.addActionListener(this);
		m2f2.addActionListener(this);
		m2f3.addActionListener(this);
		menu1.add(m11);
		menu2.add(m21);
		menu2.addSeparator();
		menu2.add("Resolution:");
		for(JMenuItem i : resolutionObjs){
			i.addActionListener(this);
			menu2.add(i);
		}
		menu2.addSeparator();
		menu2.add("Game speed:");
		menu2.add(m2s1);
		menu2.add(m2s2);
		menu2.add(m2s3);
		menu2.add("FPS:");
		menu2.add(m2f1);
		menu2.add(m2f2);
		menu2.add(m2f3);
		menuBar.add(menu1);
		menuBar.add(menu2);
		menuBar();
		return menuBar;
	}

	//world-to-screen scaling
	public int scaleSizeX(double a){
		return camera.screenXSize(a);
	}
	public int scaleSizeY(double a){
		return camera.screenYSize(a);
	}
	public int scaleX(double a){ return camera.screenXPos(a); }
	public int scaleY(double a){ return camera.screenYPos(a); }

	private void setFps(int fps){
		target_fps=fps;
		TARGET_TIME_BETWEEN_RENDERS=1000000000/target_fps;
		System.out.println(fps+" fps");
	}
	private void setUps(int ups){
		GAME_HERTZ=ups;
		TIME_BETWEEN_UPDATES=1000000000/GAME_HERTZ;
		System.out.println(ups+" ups");
		System.out.println("wait time: "+TIME_BETWEEN_UPDATES);
	}
	private void setFrame(int x, int y){
		width=x;
		height=y;
		frame.setSize(width+rightInset, height+topInset);
		//updateScales();
		camera.updateScales(panel.getWidth(),panel.getHeight());

	}

	//unused methods
	public void addText(String text, int xpos, int ypos){
		texts.add(new Text(text, xpos, ypos));
	}
	public void removeText(String identity){
		for(Text t:texts)if(t.identity.equals(identity))texts.remove(t);
	}
}