package com.josephs_projects.erovra2.units;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.josephs_projects.apricotLibrary.Apricot;
import com.josephs_projects.erovra2.Erovra2;

public enum UnitType {
	INFANTRY("ground", "ground", 1, 0.5, 0.2, 5, 1), CAVALRY("ground", "ground", 0.6, 0.5, 0.6, 5, 1),
	ARTILLERY("ground", "ground", 0.5, 0.5, 0.2, 5, 1),

	FIGHTER("air", "fighter", 0.5, 1.6, 1, 1, 1), ATTACKER("air", "attacker", 0.5, 1.6, 0.6, 1, 1),
	BOMBER("air", "bomber", 0.5, 1.6, 0.5, 1, 1),

	CITY("buildings", "city", 1, 0, 0, 0, 1), FACTORY("buildings", "factory", 1, 0, 0, 0, 1),
	AIRFIELD("buildings", "airfield", 1, 0, 0, 0, 1),

	BOMB("air", "bomber", 1, 0, 0, 0, 0);

	public String name;
	public String branch;
	double defense;
	public double attack;
	public double speed;
	public double population;
	public double scale;

	public BufferedImage image1;
	public BufferedImage hit;
	public BufferedImage icon;
	
	AffineTransform getIconAT(BufferedImage image) {
		AffineTransform scaleCenter = new AffineTransform();

		double scale = 32.0 / Math.max(image.getWidth(), image.getHeight());
		scaleCenter.scale(scale, scale);

		return scaleCenter;
	}

	UnitType(String branch, String name, double defense, double attack, double speed, double population, double scale) {
		this.name = name;
		this.defense = defense;
		this.attack = attack;
		this.speed = speed;
		this.population = population;
		this.scale = scale;
		
		BufferedImage preIcon;

		try {
			if (branch.equals("air")) {
				image1 = Apricot.image.loadImage("/res/units/" + branch + "/" + name + "1.png");
				hit = Apricot.image.loadImage("/res/units/" + branch + "/" + name + "Hit.png");
				preIcon = Apricot.image.loadImage("/res/units/" + branch + "/" + name + "1.png");
			} else {
				image1 = Apricot.image.loadImage("/res/units/" + branch + "/" + name + ".png");
				hit = Apricot.image.loadImage("/res/units/" + branch + "/hit.png");
				preIcon = Apricot.image.loadImage("/res/units/" + branch + "/" + name + ".png");
			}
			Apricot.image.overlayBlend(preIcon, Erovra2.friendlyColor);
			System.out.println(preIcon.getWidth() + " " + preIcon.getHeight());
			icon = new AffineTransformOp(getIconAT(preIcon), AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(preIcon, icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
