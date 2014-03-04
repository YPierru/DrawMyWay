package com.ironrabbit.drawmywaybeta4.route;

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
 * Singleton Classe fille d'ArrayList<Trajet>, permet de manipuler la liste de tous les trajets sauvegard??es
 * Certains m??thodes ont ????t???? r????crites.
 * Possibilit?? de sauvegarder dans un fichier cet objet.
 */
public class RoutesCollection extends ArrayList<Route> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static RoutesCollection INSTANCE;//Singleton

	private static final String FILE_NAME = ".routescollection.dmw";
	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";

	//R??cup??ration de l'instance
	public static RoutesCollection getInstance() {
		INSTANCE = loadAllTrajet();
		if (INSTANCE == null) {
			INSTANCE = new RoutesCollection();
			INSTANCE.saveAllTrajet();
		}
		return INSTANCE;
	}

	private RoutesCollection() {
		super();
	}

	public static void delete(){
		File f = new File(FILE_PATH+FILE_NAME);
		f.delete();
	}
	
	public boolean isPresent(Route t) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash() == t.getIdHash()) {
				return true;
			}
		}

		return false;
	}
	
	public boolean nameExists(String n){
		for(int i=0;i<this.size();i++){
			if(this.get(i).getName().equals(n)){
				return true;
			}
		}
		
		return false;
	}

	public void remove(Route t) {
		if (isPresent(t)) {
			super.remove(t);
		}
	}

	public Route getByHashId(int id) {
		Route tj = null;
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash() == id) {
				tj = this.get(i);
			}
		}

		return tj;
	}

	public boolean replace(Route tj) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash()==tj.getIdHash()) {
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

	public static RoutesCollection loadAllTrajet() {

		File f = new File(FILE_PATH + FILE_NAME);
		RoutesCollection at = null;
		try {
			ObjectInputStream oiStream = new ObjectInputStream(
					new FileInputStream(f));
			at = (RoutesCollection) oiStream.readObject();
			oiStream.close();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
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
