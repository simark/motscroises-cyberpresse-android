package com.example.motscroisescyberpresse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

	List<Grille> grilles = new ArrayList<Grille>();
	ListView listeViewGrilles;
	ListeGrillesAdapter listeViewGrillesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadListeFromStorage();

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

	public void refreshClicked(MenuItem v) {
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
		new FetchThread(this).execute();

	}

	public void clearClicked(MenuItem v) {
		grilles.clear();
		saveListeToStorage();
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

		saveListeToStorage();
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

	private void saveListeToStorage() {
		Log.i("liste", "Starting save to local storage");
		try {
			FileOutputStream out = openFileOutput("grilles", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(grilles);
		} catch (Exception e) {
			Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

		Log.i("liste", "Done writing to local storage");
	}

	@SuppressWarnings("unchecked")
	private void loadListeFromStorage() {
		Log.i("liste", "Starting load from local storage");
		try {
			FileInputStream in = openFileInput("grilles");
			ObjectInputStream ois = new ObjectInputStream(in);
			grilles = ((List<Grille>) ois.readObject());
		} catch (FileNotFoundException e) {
			// Si le fichier n'existe pas, too bad.
		} catch (Exception e) {
			Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
		Log.i("liste", "Done load from local storage");
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
