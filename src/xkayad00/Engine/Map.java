package xkayad00.Engine;

import xkayad00.Engine.Unit;

import java.awt.*;
import java.util.ArrayList;

public abstract class Map{
	long width=100, height=100;
	public ArrayList<Unit> units=new ArrayList<Unit>();
	abstract public void renderUnits(Graphics g);
}
