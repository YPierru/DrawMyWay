package com.ironrabbit.drawmywaybeta4.trajet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Trajet tj = this.myAllTrajets.get(position);
		if(tj.isValidate()){
			holder.tvNameTrajet.setText(this.myAllTrajets.get(position).getName());
		}else{
			holder.tvNameTrajet.setText("(en cours)"+this.myAllTrajets.get(position).getName());
		}

		return convertView;
	}

	
	public void updateData(AllTrajets newAT){
		Log.d("DEBUUUUUUG", "lAAAAAA");
		myAllTrajets.clear();
		myAllTrajets.addAll(newAT);
		this.notifyDataSetChanged();
	}

	
	private class ViewHolder {
		TextView tvNameTrajet;
	}
	
}
