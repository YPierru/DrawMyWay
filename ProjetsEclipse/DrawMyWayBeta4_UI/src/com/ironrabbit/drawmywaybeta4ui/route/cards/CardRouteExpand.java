package com.ironrabbit.drawmywaybeta4ui.route.cards;

import it.gmariotti.cardslib.library.internal.CardExpand;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ironrabbit.drawmywayui.R;

public class CardRouteExpand extends CardExpand {

    public CardRouteExpand(Context context) {
        super(context, R.layout.carddemo_example_inner_expand);
    }

    //You can set you properties here (example buttons visibility)

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
    	super.setupInnerViewElements(parent, view);
    }
}
