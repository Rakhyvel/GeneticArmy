package com.josephs_projects.erovra2.units.buildings;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import com.josephs_projects.apricotLibrary.Apricot;
import com.josephs_projects.apricotLibrary.Tuple;
import com.josephs_projects.erovra2.Erovra2;
import com.josephs_projects.erovra2.Nation;
import com.josephs_projects.erovra2.gui.Label;
import com.josephs_projects.erovra2.units.UnitType;
import com.josephs_projects.erovra2.units.ground.Infantry;

public class City extends Building {
	boolean capital;
	int workTimer = 6000;
	boolean producing = true;
	private static Point[] decoration = new Point[1];
	private static Point[] dst = new Point[1];
	String name;
	Font bigFont = new Font("Arial", Font.PLAIN, 24);
	double oreMined = 5;
	Label oreMinedLabel = new Label("", Erovra2.colorScheme);

	static {
		decoration[0] = new Point(16, 16);
		dst[0] = new Point();
	}

	public City(Tuple position, Nation nation) {
		super(position, nation, UnitType.CITY);
		name = nation.cityNames.randName(6, 9);
		nation.cities.add(this);
		infoLabel.text = name;
		oreMinedLabel.fontSize = 14;
		info.addGUIObject(oreMinedLabel);
	}

	public City(Tuple position, Nation nation, int id) {
		super(position, nation, UnitType.CITY, id);
	}

	@Override
	public void tick() {
		if (Erovra2.apricot.ticks % 300 == 0) {
			nation.coins++;
		}
		if (capital) {
			if ((Erovra2.net == null || nation == Erovra2.home) && workTimer == 0) {
				new Infantry(position, nation);
				workTimer = 6000;
			}
		}
		workTimer--;
		oreMined += Erovra2.terrain.ore[(int) position.x][(int) position.y] * (1 / 120.0);
		oreMinedLabel.text = "Ore: " + (int) oreMined;
		super.tick();
	}

	@Override
	public void render(Graphics2D g) {
		AffineTransform af = getAffineTransform(image);
		af.transform(decoration, 0, dst, 0, 1);
		super.render(g);
		if (nation == Erovra2.enemy && engagedTicks <= 0 && !dead)
			return;

		g.setColor(Color.white);
		bigFont = new Font("sitka text", Font.BOLD, (int) (14 * (Erovra2.zoom + 0.5)));
		g.setFont(bigFont);
		if (name != null) {
			int width = g.getFontMetrics(bigFont).stringWidth(name);
			g.drawString(name, dst[0].x - width / 2, dst[0].y + (int) (24 * Erovra2.zoom));
		}
	}

	public void changeToCapital() {
		try {
			image = Apricot.image.loadImage("/res/units/buildings/capital.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Apricot.image.overlayBlend(image, nation.color);
		this.capital = true;
		new Infantry(position, nation);
	}

	public void setProducing(boolean producing) {
		this.producing = producing;
	}

	@Override
	public void remove() {
		nation.population -= 1;

		super.remove();
	}
}
