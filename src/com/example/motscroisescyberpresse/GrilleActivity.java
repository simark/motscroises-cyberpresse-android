package com.example.motscroisescyberpresse;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class GrilleActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	Grille grille;
	GrilleFragment grilleFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_grille);

		Intent intent = getIntent();
		grille = (Grille) intent.getSerializableExtra("grille");

		if (savedInstanceState == null) {
			// Create the adapter that will return a fragment for each of the
			// three
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			mViewPager.setCurrentItem(1, false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_grille, menu);
		return true;
	}

	public void ouvrirClavierClicked(View v) {
		grilleFragment.ouvrirClavierClicked(v);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("grille", "onpause");
		sauverGrille();
	}

	private void sauverGrille() {
		grille = grilleFragment.getGrille();

		try {
			List<Grille> grilles = GrilleStorage.loadListeFromStorage(this);
			for (int i = 0; i < grilles.size(); i++) {
				if (grilles.get(i).getGrilleNum() == grille.getGrilleNum()) {
					grilles.set(i, grille);
					break;
				}
			}
			GrilleStorage.saveListeToStorage(this, grilles);
		} catch (Exception e) {
			Toast.makeText(this, "Erreur lors de la sauvegarde de la grille.",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 1) {
				grilleFragment = new GrilleFragment();
				Bundle args = new Bundle();
				args.putSerializable("grille", grille);
				grilleFragment.setArguments(args);
				return grilleFragment;
			} else {
				DefinitionsFragment frag = new DefinitionsFragment();
				Bundle args = new Bundle();
				args.putStringArray(
						"definitions",
						(position == 0) ? grille.getDefinitionsH() : grille
								.getDefinitionsV());
				frag.setArguments(args);
				return frag;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Horizontal";
			case 1:
				return "Grille";
			case 2:
				return "Vertical";
			}
			return null;
		}
	}
}
