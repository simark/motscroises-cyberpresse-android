package com.example.motscroisescyberpresse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motscroisescyberpresse.FetchThread.FetchCallback;

public class ListeActivity extends Activity implements FetchCallback,
		OnItemClickListener {
	ProgressDialog mProgressDialog;

	List<Grille> grilles = new ArrayList<Grille>();;
	ListView listeViewGrilles;
	ListeGrillesAdapter listeViewGrillesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_liste);

		listeViewGrilles = (ListView) findViewById(R.id.listeGrilles);

		listeViewGrillesAdapter = new ListeGrillesAdapter(this,
				R.layout.item_liste, R.id.valeurItemListe, grilles);

		listeViewGrilles.setAdapter(listeViewGrillesAdapter);

		listeViewGrilles.setOnItemClickListener(this);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage("Téléchargement des dernières grilles.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_liste_menu, menu);

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			List<Grille> nouvellesGrilles = GrilleStorage.loadListeFromStorage(this);
			grilles.clear();
			grilles.addAll(nouvellesGrilles);
			listeViewGrillesAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Toast.makeText(this,
					"Erreur lors du chargement des grilles locales.",
					Toast.LENGTH_LONG).show();
		}
	}

	public void refreshClicked(MenuItem v) {
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
		new FetchThread(this).execute();

	}

	public void clearClicked(MenuItem v) {
		grilles.clear();
		try {
			GrilleStorage.saveListeToStorage(this, grilles);
		} catch (IOException e) {
			Toast.makeText(this,
					"Erreur lors de la sauvegarde de la liste de grilles.",
					Toast.LENGTH_LONG).show();
		}
		listeViewGrillesAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Grille g = grilles.get(position);
		Intent intent = new Intent(this, GrilleActivity.class);

		intent.putExtra("grille", g);

		startActivity(intent);
	}

	@Override
	public void done(List<Grille> grilles, SparseArray<String> errors) {
		mProgressDialog.dismiss();

		for (Grille g : grilles) {
			addNewGrille(g);
		}

		Collections.sort(grilles, new Comparator<Grille>() {

			@Override
			public int compare(Grille lhs, Grille rhs) {
				return (int) (lhs.getDateRecuperee().getTime() - rhs
						.getDateRecuperee().getTime());
			}
		});

		try {
			GrilleStorage.saveListeToStorage(this, grilles);
		} catch (IOException e) {
			Toast.makeText(this,
					"Erreur lors de la sauvegarde de la liste de grilles.",
					Toast.LENGTH_LONG).show();
		}

		listeViewGrillesAdapter.notifyDataSetChanged();

		for (int i = 0; i < errors.size(); i++) {
			int n = errors.keyAt(i);

			Toast.makeText(this, "Grille " + n + ": " + errors.get(n),
					Toast.LENGTH_LONG).show();
		}
	}

	private void addNewGrille(Grille g) {
		for (Grille gg : grilles) {
			if (g.getGrilleNum() == gg.getGrilleNum()) {
				return;
			}
		}

		grilles.add(g);
	}

	@Override
	public void progress(int nbDone, int nbTotal) {
		mProgressDialog.setMax(nbTotal);
		mProgressDialog.setProgress(nbDone);

	}

	class ListeGrillesAdapter extends ArrayAdapter<Grille> {
		LayoutInflater inflater;

		public ListeGrillesAdapter(Context context, int resource,
				int textViewResourceId, List<Grille> objects) {
			super(context, resource, textViewResourceId, objects);

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = inflater.inflate(R.layout.item_liste, parent, false);
			TextView tv = (TextView) v.findViewById(R.id.valeurItemListe);
			tv.setText("#" + grilles.get(position).getGrilleNum());
			return v;
		}

	}

}
