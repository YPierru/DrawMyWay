package com.example.drawmywaybeta3.Trajet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import android.os.Environment;

public class TrajetManager {

	private static final String FILE_NAME = ".allTrajets.dmw";
	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";
	private File myDataFile;

	public TrajetManager() {
		this.myDataFile = new File(FILE_PATH + FILE_NAME);
	}

	public void saveAllTrajet(AllTrajets at) {
		try {
			ObjectOutputStream ooStream = new ObjectOutputStream(
					new FileOutputStream(this.myDataFile));
			ooStream.writeObject(at);
			ooStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AllTrajets loadAllTrajet() {
		AllTrajets at=null;
		try {
			ObjectInputStream oiStream = new ObjectInputStream(
											new FileInputStream(
											this.myDataFile));
			 at = (AllTrajets)oiStream.readObject();
			 oiStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return at;
	}
	
	public void delete(){
		this.myDataFile.delete();
		this.myDataFile = new File(FILE_PATH + FILE_NAME);
	}

}
