package com.ironrabbit.drawmywaybeta3.Trajet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.os.Environment;

/*
 * Singleton Classe fille d'ArrayList<Trajet>, permet de manipuler la liste de tous les trajets sauvegardés
 * Certains méthodes ont été réécrites.
 * Possibilité de sauvegarder dans un fichier cet objet.
 */
public class AllTrajets extends ArrayList<Trajet> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static AllTrajets INSTANCE;//Singleton

	private static final String FILE_NAME = ".allTrajets.dmw";
	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";

	//Récupération de l'instance
	public static AllTrajets getInstance() {
		INSTANCE = loadAllTrajet();
		if (INSTANCE == null) {
			INSTANCE = new AllTrajets();
		}
		return INSTANCE;
	}

	private AllTrajets() {
		super();
	}

	public boolean isPresent(Trajet t) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash() == t.getIdHash()) {
				return true;
			}
		}

		return false;
	}

	public void remove(Trajet t) {
		if (isPresent(t)) {
			super.remove(t);
		}
	}

	public Trajet getByHashId(int id) {
		Trajet tj = null;
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash() == id) {
				tj = this.get(i);
			}
		}

		return tj;
	}

	public boolean replace(Trajet tj) {
		for (int i = 0; i < this.size(); i++) {
			if (System.identityHashCode(this.get(i)) == System
					.identityHashCode(tj)) {
				this.remove(i);
				this.add(i, tj);
				return true;
			}
		}
		return false;
	}

	public void saveAllTrajet() {

		File f = new File(FILE_PATH + FILE_NAME);

		try {
			ObjectOutputStream ooStream = new ObjectOutputStream(
					new FileOutputStream(f));
			ooStream.writeObject(this);
			ooStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static AllTrajets loadAllTrajet() {

		File f = new File(FILE_PATH + FILE_NAME);
		AllTrajets at = null;
		try {
			ObjectInputStream oiStream = new ObjectInputStream(
					new FileInputStream(f));
			at = (AllTrajets) oiStream.readObject();
			oiStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return at;
	}

	public void deleteFile() {
		File f = new File(FILE_PATH + FILE_NAME);
		if (f.exists()) {
			f.delete();
		}
	}
}
