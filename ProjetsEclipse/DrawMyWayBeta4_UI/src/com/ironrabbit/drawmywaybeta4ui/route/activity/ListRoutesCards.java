package com.ironrabbit.drawmywaybeta4ui.route.activity;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnUndoSwipeListListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.ironrabbit.drawmywaybeta4ui.route.Route;
import com.ironrabbit.drawmywaybeta4ui.route.RoutesCollection;
import com.ironrabbit.drawmywaybeta4ui.route.cards.CardRoute;
import com.ironrabbit.drawmywayui.R;
import com.navdrawer.SimpleSideDrawer;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

public class ListRoutesCards extends Activity {
	
	private static String mTypeRouteCurrent;// VOITURE ou COUREUR
	private float x1, x2;// Points permettant de stocker l'abscisse de l'user
	static final int MIN_DISTANCE = 100;
	private SimpleSideDrawer mSlidingMenuLeft;
	private static CardListView mCardListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display_cards);
		getActionBar().setHomeButtonEnabled(true);
		
		try {
			mTypeRouteCurrent = getIntent().getExtras().getString("TYPEROUTE");
		} catch (NullPointerException npe) {
			mTypeRouteCurrent = "VOITURE";
		}
		
		mCardListView = (CardListView)findViewById(R.id.myList);
		
		if(mTypeRouteCurrent.equals("VOITURE")){
			populateCards(true);
		}else{
			populateCards(false);
		}
		
		mSlidingMenuLeft = new SimpleSideDrawer(this);
		mSlidingMenuLeft.setLeftBehindContentView(R.layout.side_menu_typeroute);
		
		mCardListView.setOnTouchListener(new ActionOnTouchEvent());

	}
	
	private void populateCards(boolean voitureType){
		RoutesCollection mRoutesCollection = RoutesCollection.getInstance();
		ArrayList<Route> listRoutes;
		if(voitureType){
			listRoutes = mRoutesCollection.getListRoutesVoiture();
			getActionBar().setTitle("Vos trajets voiture");
		}else{
			listRoutes = mRoutesCollection.getListRoutesCoureur();
			getActionBar().setTitle("Vos trajets piétons");
		}
		
		ArrayList<Card> listCardRoute = new ArrayList<Card>();
		for(int i=0;i<listRoutes.size();i++){
			Card card = new CardRoute(getApplicationContext(), listRoutes.get(i));
			card.setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
				
				@Override
				public void onUndoSwipe(Card card) {
					Log.d("DEBUUU","YOLOOOOOOO");
					if(mTypeRouteCurrent.equals("VOITURE")){
						populateCards(true);
					}else{
						populateCards(false);
					}
				}
			});
			listCardRoute.add(card);
		}
		CardArrayAdapter cardArrayAdapter = new CardArrayAdapter(this, listCardRoute);
		cardArrayAdapter.setEnableUndo(true);
		mCardListView = (CardListView)findViewById(R.id.myList);
		AnimationAdapter animCardArrayAdapter = new SwingLeftInAnimationAdapter(cardArrayAdapter);
	    animCardArrayAdapter.setAbsListView(mCardListView);
	    mCardListView.setExternalAdapter(animCardArrayAdapter,cardArrayAdapter);
		//mCardListView.setAdapter(cardArrayAdapter);
		
		if(listRoutes.size()==0){
			final AlertDialog.Builder alert = new AlertDialog.Builder(
					ListRoutesCards.this).setTitle("Aucun trajet !");
			alert.setMessage(Html.fromHtml("Vous n'avez <b>aucun trajets</b>, commencez par en créer un !"));
			alert.setCancelable(true);
			alert.setPositiveButton("Créer mon trajet",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialogCreateNewRoute();
						}
					});
			alert.setNegativeButton("Plus tard", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alert.show();
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			showLeftSideMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showLeftSideMenu() {
		mSlidingMenuLeft.toggleLeftDrawer();
		
		Button btn_routeVoiture = (Button) findViewById(R.id.btn_routeVoiture);
		btn_routeVoiture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTypeRouteCurrent = "VOITURE";
				populateCards(true);
				mSlidingMenuLeft.toggleLeftDrawer();
			}
		});
		
		Button btn_routeRunner = (Button) findViewById(R.id.btn_routeRunner);
		btn_routeRunner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTypeRouteCurrent = "COUREUR";
				populateCards(false);
				mSlidingMenuLeft.toggleLeftDrawer();
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item_NouveauTrajet = menu.add("Créer un trajet").setIcon(
				R.drawable.ic_action_new);
		item_NouveauTrajet.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_NouveauTrajet
				.setOnMenuItemClickListener(new ActionOnMenuItemClick());

		MenuItem item_deleteAll = menu.add("Tout supprimer").setIcon(
				R.drawable.ic_action_discard);
		item_deleteAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_deleteAll
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RoutesCollection mRoutesCollection = RoutesCollection.getInstance();
						mRoutesCollection.deleteFile();
						//populateCards(true);
						//populateCards(false);
						return false;
					}
				});
		return true;
	}
	
	private String getCurrentDayTime() {
		Date aujourdhui = new Date();

		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
				DateFormat.SHORT, DateFormat.SHORT);

		return shortDateFormat.format(aujourdhui);
	}
	
	public static String getRouteType(){
		return mTypeRouteCurrent;
	}
	
	public static void setRouteType(String t){
		mTypeRouteCurrent=t;
	}
	
	public static void updateDataList(String typeRoute) {
		
	}
	
	public void onResume(){
		super.onResume();
		if(mTypeRouteCurrent.equals("VOITURE")){
			populateCards(true);
		}else{
			populateCards(false);
		}
		
	}
	
	public void dialogCreateNewRoute(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				ListRoutesCards.this).setTitle("Saisir le nom du trajet");
		final EditText input = new EditText(getApplicationContext());
		input.setHint("Nom du trajet");
		input.setTextColor(Color.BLACK);
		alert.setView(input);
		alert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						String value = input.getText().toString().trim();
						Route newTrajet = new Route(value, false, false,
								getCurrentDayTime(), mTypeRouteCurrent);
						Intent toCreateTrajetActivity = new Intent(
								ListRoutesCards.this, CreateRoute.class);
						toCreateTrajetActivity.putExtra("trajet",
								(Parcelable) newTrajet);
						toCreateTrajetActivity.putExtra("MODE", "Cr??ation");
						startActivity(toCreateTrajetActivity);
					}
				});
		alert.show();
	}
	
	private class ActionOnMenuItemClick implements OnMenuItemClickListener {
		public boolean onMenuItemClick(MenuItem item) {
			dialogCreateNewRoute();
			return true;
		}
	}
	
	private class ActionOnTouchEvent implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (event.getX() <= 30) {
					x1 = event.getX();
				} else {
					x1 = 40;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (x1 <= 30) {
					x2 = event.getX();
					float deltaX = x2 - x1;
					if (deltaX > MIN_DISTANCE) {
						showLeftSideMenu();
					}
				}
				break;
			}
			return false;
		}
	}

}
