package com.example.drawmywaybeta3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.drawmywaybeta3.Trajet.AllTrajets;
import com.example.gmapstests.R;

public class TrajetAdapter extends BaseAdapter {
	
	private AllTrajets myAllTrajets;
	private LayoutInflater inflater;
	
	public TrajetAdapter(Context ct, AllTrajets at){
		inflater = LayoutInflater.from(ct);
		this.myAllTrajets=at;
	}

	@Override
	public int getCount() {
		return this.myAllTrajets.size();
	}

	@Override
	public Object getItem(int position) {
		return this.myAllTrajets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.itemtrajet, null);

			holder.tvNameTrajet = (TextView)convertView.findViewById(R.id.tv_nomTrajet);
			holder.tvDateCrea = (TextView)convertView.findViewById(R.id.tv_dateCrea);
			holder.tvDateDerModif = (TextView)convertView.findViewById(R.id.tv_lastModif);
			holder.tvKmtrage = (TextView)convertView.findViewById(R.id.tv_kmTrajet);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvNameTrajet.setText(this.myAllTrajets.get(position).getName());
		
		//String[] dateHour = this.myAllTrajets.get(position).getDateCreation().split(" ");
		holder.tvDateCrea.setText("Créer le "+this.myAllTrajets.get(position).getDateCreation());

		//String[] dateHourMod = this.myAllTrajets.get(position).getDateDerModif().split(" ");
		holder.tvDateDerModif.setText("Modifié le "+this.myAllTrajets.get(position).getDateDerModif());
		int dist=this.myAllTrajets.get(position).getDistTotal();
		if(dist==0){
			holder.tvKmtrage.setText("en cours");
		}
		else{
			holder.tvKmtrage.setText(dist/1000+"Km");
		}

		return convertView;
	}

	
	private class ViewHolder {
		TextView tvNameTrajet;
		TextView tvDateCrea;
		TextView tvDateDerModif;
		TextView tvKmtrage;
	}
	
}
