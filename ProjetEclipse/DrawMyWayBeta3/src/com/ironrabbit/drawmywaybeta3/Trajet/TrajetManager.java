package com.ironrabbit.drawmywaybeta3.Trajet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.os.Environment;

public class TrajetManager {

	private static final String FILE_NAME = ".allTrajets.dmw";
	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";

	public static void saveAllTrajet(AllTrajets at) {
		
		File f= new File(FILE_PATH+FILE_NAME);
		
		try {
			ObjectOutputStream ooStream = new ObjectOutputStream(
					new FileOutputStream(f));
			ooStream.writeObject(at);
			ooStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static AllTrajets loadAllTrajet() {

		File f= new File(FILE_PATH+FILE_NAME);
		AllTrajets at=null;
		try {
			ObjectInputStream oiStream = new ObjectInputStream(
											new FileInputStream(f));
			 at = (AllTrajets)oiStream.readObject();
			 oiStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return at;
	}
	
	public static void deleteFile(){
		File f= new File(FILE_PATH+FILE_NAME);
		if(f.exists()){
			f.delete();
		}
	}

}
