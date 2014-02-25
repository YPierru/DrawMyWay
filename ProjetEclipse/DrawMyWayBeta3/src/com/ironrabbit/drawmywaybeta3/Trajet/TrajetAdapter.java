package com.ironrabbit.drawmywaybeta3.Trajet;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ironrabbit.drawmyway.R;

public class TrajetAdapter extends BaseAdapter {
	
	private AllTrajets myAllTrajets;
	private int position;
	private LayoutInflater inflater;
	private Context ct;
	
	public TrajetAdapter(Context cnt, AllTrajets at){
		inflater = LayoutInflater.from(cnt);
		ct=cnt;
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
	public View getView(int pos, View convertView, ViewGroup parent) {
		ViewHolder holder;
		position=pos;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.itemtrajet, null);
			
			holder.tvNameTrajet = (TextView)convertView.findViewById(R.id.tv_nomTrajet);
			holder.tvKmtrage = (TextView)convertView.findViewById(R.id.tv_kmTrajet);

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvNameTrajet.setText(this.myAllTrajets.get(position).getName());
	
		double dist=this.myAllTrajets.get(position).getDistTotal();
		if(dist==0){
			holder.tvKmtrage.setText("en cours");
		}
		else if(dist<1000){
			holder.tvKmtrage.setText((int)dist+"m");
		}
		else{
			holder.tvKmtrage.setText((dist/1000)+"Km");
		}

		return convertView;
	}
	
	private class ViewHolder {
		TextView tvNameTrajet;
		TextView tvKmtrage;
	}
	
}
