package com.ironrabbit.drawmywaybeta3.Trajet;

import java.io.Serializable;
import java.util.ArrayList;


public class AllTrajets extends ArrayList<Trajet> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AllTrajets(){
		super();
	}
	
	public boolean isPresent(Trajet t){
		for(int i=0;i<this.size();i++){
			if(this.get(i).getIdHash()==t.getIdHash()){
				return true;
			}
		}
		
		return false;
	}
	
	public void remove(Trajet t){
		if(isPresent(t)){
			super.remove(t);
		}
	}
	
	public Trajet getByHashId(int id){
		Trajet tj=null;
		for(int i=0;i<this.size();i++){
			if(this.get(i).getIdHash()==id){
				tj=this.get(i);
			}
		}
		
		return tj;
	}
	
	public boolean replace(Trajet tj){
		for(int i=0;i<this.size();i++){
			if(System.identityHashCode(this.get(i))==System.identityHashCode(tj)){
				this.remove(i);
				this.add(i, tj);
				return true;
			}
		}
		return false;
	}

	public void merge(AllTrajets at){
		
		for(int i=0;i<at.size();i++){
			this.add(at.get(i));
		}
	}
}
