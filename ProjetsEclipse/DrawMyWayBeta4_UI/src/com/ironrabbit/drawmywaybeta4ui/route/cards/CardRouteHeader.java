package com.ironrabbit.drawmywaybeta4ui.route.cards;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ironrabbit.drawmywaybeta4ui.gps.activity.GPSRunner;
import com.ironrabbit.drawmywaybeta4ui.route.Route;
import com.ironrabbit.drawmywaybeta4ui.route.activity.SeeRoute;
import com.ironrabbit.drawmywayui.R;

public class CardRouteHeader extends CardHeader {

	
	private Route mRoute;
	private Context context;

	public CardRouteHeader(Context context, int headerLayout, Route r) {
		super(context, headerLayout);
		this.mRoute=r;
		this.context=context;
		init();
	}
	
	private void init(){
		//setButtonExpandVisible(true);
		setTitle(this.mRoute.getName());
		setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
            	switch(item.getItemId()){
            		case R.id.item_voir:
            			Intent toSeeRoute = new Intent(context,
								SeeRoute.class);
						toSeeRoute.putExtra("trajet", (Parcelable) mRoute);
						toSeeRoute.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(toSeeRoute);
            		break;
            		
            		case R.id.item_gps:
						Intent toGPSRunner = new Intent(context,
								GPSRunner.class);
						toGPSRunner.putExtra("TRAJET", (Parcelable) mRoute);
						toGPSRunner.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(toGPSRunner);
            		break;
            	}
            }
        });
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		super.setupInnerViewElements(parent, view);
		String mStatus;
		if(this.mRoute.isValidate()){
			mStatus="Terminé";
			String mDate = this.mRoute.getDateCreation();
			TextView tvSubDate = (TextView)view.findViewById(R.id.tv_cardroute_headerlayout_subtitle_date);
			tvSubDate.setText(" - "+mDate);
		}else{
			mStatus="En cours";
		}

		TextView tvSubtitleStatus = (TextView) view.findViewById(R.id.tv_cardroute_headerlayout_subtitle_status);
		if(mStatus.equals("En cours")){
			tvSubtitleStatus.setTextColor(Color.parseColor("#ffa62d"));
		}else{
			tvSubtitleStatus.setTextColor(Color.parseColor("#55bc00"));
		}
		tvSubtitleStatus.setText(mStatus);
		
		
	}


}
