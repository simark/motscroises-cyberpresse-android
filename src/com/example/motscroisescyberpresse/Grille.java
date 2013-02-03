package com.example.motscroisescyberpresse;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import android.util.Log;

public class Grille implements Serializable {
	public enum Direction {
		HORIZONTAL, VERTICAL
	};

	private static final long serialVersionUID = 1L;
	public static final char CHAR_BLOC = ')';

	private int grilleNum;
	private char[][] contenuUser;
	private char[][] reponse;
	private final String[] horizontal;
	private final String[] vertical;
	private final int tailleY;
	private final int tailleX;
	private Date dateRecuperee = new Date(0);

	public Grille(char[][] reponse, int tailleX, int tailleY, String[] horizontal, String[] vertical) {
		this.reponse = reponse;
		this.tailleX = tailleX;
		this.tailleY = tailleY;
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.contenuUser = reponse.clone();
		
		for (int j = 0; j < tailleY; j++) {
			for (int i = 0; i < tailleX; i++) {
				if (contenuUser[i][j] != ')') {
					contenuUser[i][j] = ' ';
				}
			}
		}
	}

	public int getGrilleNum() {
		return grilleNum;
	}

	public String[] getDefinitionsH() {
		return horizontal;
	}

	public String[] getDefinitionsV() {
		return vertical;
	}
	
	public char getCharUser(int x, int y) {
		return contenuUser[x][y];
	}
	
	public void setCharUser(int x, int y, char c) {
		contenuUser[x][y] = c;
	}
	
	public char getCharReponse(int x, int y) {
		return reponse[x][y];
	}
	
	public int getTailleX() {
		return tailleX;
	}
	
	public int getTailleY() {
		return tailleY;
	}
	
	public void setDateRecuperee(Date dateRecuperee) {
		this.dateRecuperee = dateRecuperee;
	}
	
	public Date getDateRecuperee() {
		return dateRecuperee;
	}

	public static Grille grilleFromServer(String encoded)
			throws MalformedGrille {
		String[] split = encoded.split("&");
		Map<String, String> params = new TreeMap<String, String>();

		for (String s : split) {
			String[] kv = s.split("=", 2);
			if (kv.length != 2) {
				Log.e("grille", "Split len > 2");
				continue;
			}

			params.put(kv[0], kv[1]);
		}

		String contenu = params.get("grilleCor");
		String smaxX = params.get("maxX");
		String smaxY = params.get("maxY");
		String sgrilleNum = params.get("grille_num");
		if (contenu == null || smaxX == null || smaxY == null
				|| sgrilleNum == null) {
			throw new MalformedGrille("Parametre manquant");
		}

		int maxX = Integer.parseInt(smaxX);
		int maxY = Integer.parseInt(smaxY);

		if (contenu.length() != maxX * maxY) {
			throw new MalformedGrille("contenu.length() != maxX * maxY");
		}

		char[][] map = new char[maxX][maxY];

		for (int j = 0; j < maxY; j++) {
			for (int i = 0; i < maxX; i++) {
				map[i][j] = contenu.charAt(maxY * j + i);
			}
		}

		String[] vertical = new String[maxX];
		String[] horizontal = new String[maxY];

		for (int i = 1; i <= maxX; i++) {
			String key = "v" + i;
			String def = params.get(key);
			if (def == null)
				throw new MalformedGrille("Parametre " + key + " manquant");

			vertical[i - 1] = def;
		}

		for (int j = 1; j <= maxY; j++) {
			String key = "h" + j;
			String def = params.get(key);
			if (def == null)
				throw new MalformedGrille("Parametre " + key + " manquant");

			horizontal[j - 1] = def;
		}

		Grille g = new Grille(map, maxX, maxY, horizontal, vertical);
		g.grilleNum = Integer.parseInt(sgrilleNum);

		return g;
	}

	static class MalformedGrille extends Exception {
		private static final long serialVersionUID = 1L;

		public MalformedGrille(String raison) {
			super(raison);
		}
	}
}
