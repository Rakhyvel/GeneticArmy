package com.josephs_projects.erovra2;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.josephs_projects.apricotLibrary.Apricot;
import com.josephs_projects.apricotLibrary.Tuple;
import com.josephs_projects.apricotLibrary.World;
import com.josephs_projects.apricotLibrary.audio.AudioClip;
import com.josephs_projects.apricotLibrary.input.InputEvent;
import com.josephs_projects.apricotLibrary.interfaces.InputListener;
import com.josephs_projects.erovra2.ai.NewAI;
import com.josephs_projects.erovra2.gui.ColorScheme;
import com.josephs_projects.erovra2.net.Client;
import com.josephs_projects.erovra2.net.NetworkAdapter;
import com.josephs_projects.erovra2.net.Server;
import com.josephs_projects.erovra2.units.buildings.City;

/*
 * Principles:
 * - Winning should not be random
 * - No pay to win
 * 
 * Name Ideas:
 * - Civitania
 * - 
 * 
 * This shit pisses me off so fucking much they wont do what I tell them to wtf
 * I dont want to make this game, I just want to have made this game
 */

public class Erovra2 implements InputListener {
	public static Apricot apricot;
	public static World world;
	public Image icon = new ImageIcon(getClass().getResource("/res/icon.png")).getImage();

	public static NetworkAdapter net;

	public static Nation home;
	public static Nation enemy;

	public static Color friendlyColor = new Color(105, 105, 210);
	public static Color enemyColor = new Color(210, 105, 105);

	public static Terrain terrain;
	public static GUI gui;

	public static int size = 25;
	public static double zoom = 1;
	public static double dt = 16;

	public static final int TERRAIN_LEVEL = 0;
	public static final int BUILDING_LEVEL = 1;
	public static final int SURFACE_PROJECTILE_LEVEL = 2;
	public static final int SURFACE_LEVEL = 3;
	public static final int SURFACE_AIR_LEVEL = 4;
	public static final int AIR_LEVEL = 5;
	public static final int GUI_LEVEL = 6;
	public static final ColorScheme colorScheme = new ColorScheme(new Color(40, 40, 40, 180), new Color(250, 250, 250),
			new Color(128, 128, 128, 180), new Color(250, 250, 250), new Color(128, 128, 128), new Color(211, 86, 64));

	public static AudioClip gun;
	public static AudioClip mortar;
	public static AudioClip explode;

	public static boolean geneticTournament = false;

	public static void main(String[] args) {
		apricot = new Apricot("Civitania", 1366, 768);
		apricot.setIcon(new Erovra2().icon);
		world = new World();
		Apricot.rand.setSeed(0);

		try {
			gun = new AudioClip("src/res/audio/gun.wav");
			mortar = new AudioClip("src/res/audio/mortar.wav");
			explode = new AudioClip("src/res/audio/artFire.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (args.length > 0) {
			if (args[0].equals("server")) {
				terrain = new Terrain(size * 64, Apricot.rand.nextInt());
				net = new Server();
				net.start();
			} else {
				net = new Client(args[0]);
				net.start();
			}
		} else {
			terrain = new Terrain(size * 64, Apricot.rand.nextInt());
			startNewMatch();
			gui = new GUI(home);
			Erovra2.home.setCapital(new City(Erovra2.home.capitalPoint, Erovra2.home));
			Erovra2.enemy.setCapital(new City(Erovra2.enemy.capitalPoint, Erovra2.enemy));
			Erovra2.terrain.setOffset(new Tuple(Erovra2.size / 2 * 64, Erovra2.size / 2 * 64));
			terrain.setOffset(home.capitalPoint);
//			apricot.setDeltaT(1);
		}

		apricot.setWorld(world);
		world.add(new Erovra2());
		apricot.start();
	}

	public static void startTournament(Terrain terrain) {
		apricot = new Apricot("Campaign: Direct Strike", 1366, 768, Apricot.Modifier.INVISIBLE);
		apricot.isSimulation = true;
//		apricot.setDeltaT(0.1);
		world = new World();
		if (terrain == null) {
			terrain = new Terrain(size * 64, Apricot.rand.nextInt());
		}
		Erovra2.terrain = terrain;
		startNewMatch();
		Erovra2.home.setCapital(new City(Erovra2.home.capitalPoint, Erovra2.home));
		Erovra2.enemy.setCapital(new City(Erovra2.enemy.capitalPoint, Erovra2.enemy));
		Erovra2.terrain.setOffset(new Tuple(Erovra2.size / 2 * 64, Erovra2.size / 2 * 64));

		terrain.setOffset(home.capitalPoint);

		apricot.setWorld(world);
		world.add(new Erovra2());
		apricot.start();
	}

	public static void startNewMatch() {
		home = new Nation("Home nation", friendlyColor, new NewAI());
		enemy = new Nation("Enemy nation", enemyColor, new NewAI());
		home.enemyNation = enemy;
		enemy.enemyNation = home;

		Tuple testPoint = new Tuple(64 + 32, 32);
		do {
			testPoint.y += 64;
			if (testPoint.y > 928 + 32) {
				testPoint.x += 64;
				testPoint.y = 64 + 32;
			}
		} while (terrain.getHeight(testPoint) <= 0.55);

		Tuple testPoint2 = new Tuple((64 * size) - 64 - 32, (64 * size) - 32);
		do {
			testPoint2.y -= 64;
			if (testPoint2.y < 64 + 32) {
				testPoint2.x -= 64;
				testPoint2.y = (64 * size) - 32;
			}
		} while (terrain.getHeight(testPoint2) <= 0.55);

		home.capitalPoint = testPoint2;
		enemy.capitalPoint = testPoint;

		Erovra2.world.add(home);
		Erovra2.world.add(enemy);
	}

	public static void setNationColors() {
		home.color = friendlyColor;
		enemy.color = enemyColor;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List copyList(List orig) {
		ArrayList retval = new ArrayList();
		for (int i = 0; i < orig.size(); i++) {
			retval.add(orig.get(i));
		}
		return retval;
	}

	@Override
	public void input(InputEvent e) {
		if (e == InputEvent.KEY_RELEASED) {
			if (apricot.keyboard.lastKey == KeyEvent.VK_PERIOD) {
				dt /= 2;
				apricot.setDeltaT(dt);
				gui.messageContainer.addMessage("Time warp: " + String.format("%.0f", 16.0 / dt) + "x", Color.white);
			} else if (apricot.keyboard.lastKey == KeyEvent.VK_COMMA) {
				dt *= 2;
				apricot.setDeltaT(dt);
				gui.messageContainer.addMessage("Time warp: " + String.format("%.0f", 16.0 / dt) + "x", Color.white);
			}
		}
	}

	@Override
	public void remove() {
	}
}
