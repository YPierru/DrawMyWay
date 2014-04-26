package com.ironrabbit.drawmywaybeta4ui.route.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4ui.route.Route;

public class CardRoute extends Card {
	private Route mRoute;
	private Context context;
	
	public CardRoute(Context context, Route r) {
		this(context, R.layout.cardroute_inner_layout,r);
	}

	private CardRoute(Context context, int innerLayout,Route r) {
		super(context, innerLayout);
		this.mRoute=r;
		this.context=context;
		init();
	}

	private void init() {
		CardHeader cardRouteHeader = new CardRouteHeader(this.context, R.layout.cardroute_header_layout,this.mRoute);
		addCardHeader(cardRouteHeader);
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {

		TextView kilometrage = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_kilometrage);
		TextView duree = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_duree);
		TextView addrDepart = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_addrdepart);
		TextView addrArrivee = (TextView) view
				.findViewById(R.id.tv_cardroute_innerlayout_addrarrivee);

		double dist = this.mRoute.getDistTotal();
		if (dist < 1000) {
			kilometrage.setText((int) dist + "m - ");
		} else {
			kilometrage.setText((dist / 1000) + "Km - ");
		}

		int dureeSecond = this.mRoute.getDureeTotal();
		int heures = (dureeSecond / 3600);
		int minutes = ((dureeSecond % 3600) / 60);
		if (heures == 0) {
			duree.setText(minutes + "min");
		} else {
			duree.setText(heures + "h" + minutes + "min");
		}

		addrDepart.setText(this.mRoute.getStartAddress());
		addrArrivee.setText(this.mRoute.getEndAddress());
	}
}
