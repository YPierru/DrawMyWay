package com.ironrabbit.waybeta4.route;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ironrabbit.waybeta4.R;


public class RouteAdapter extends BaseAdapter {

	private ArrayList<Route> mListRoute;
	private int position;
	private LayoutInflater inflater;

	public RouteAdapter(Context cnt, ArrayList<Route> at){
		inflater = LayoutInflater.from(cnt);
		this.mListRoute=at;
	}

	@Override
	public int getCount() {
		return this.mListRoute.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mListRoute.get(position);
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

		Route tj = this.mListRoute.get(position);
		if(tj.isValidate()){
			holder.tvNameTrajet.setText(this.mListRoute.get(position).getName());
		}else{
			holder.tvNameTrajet.setText("(en cours)"+this.mListRoute.get(position).getName());
		}

		return convertView;
	}


	public void updateData(ArrayList<Route> newAT){
		mListRoute.clear();
		mListRoute.addAll(newAT);
		this.notifyDataSetChanged();
	}


	private class ViewHolder {
		TextView tvNameTrajet;
	}

}