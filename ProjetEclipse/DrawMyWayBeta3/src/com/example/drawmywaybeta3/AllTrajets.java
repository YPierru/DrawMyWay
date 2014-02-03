package com.example.drawmywaybeta3;

import java.util.ArrayList;

import com.example.drawmywaybeta3.Parcours.Trajet;

public class AllTrajets extends ArrayList<Trajet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AllTrajets(){
		super();
	}
	
	public boolean isPresent(Trajet t){
		for(int i=0;i<this.size();i++){
			if(this.get(i).getName().equals(t.getName())){
				return true;
			}
		}
		
		return false;
	}
	
	public void remove(Trajet t){
		if(isPresent(t)){
			this.remove(t);
		}
	}

}
