package com.example.motscroisescyberpresse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.example.motscroisescyberpresse.Grille.MalformedGrille;

public class FetchThread extends AsyncTask<URL, Integer, List<Grille>> {
	private final FetchCallback callback;
	private SparseArray<String> errors = new SparseArray<String>();
	private static final int NB_GRILLES = 7;
	private static final String URL_GRILLES = "http://www.ludipresse.com/cgi-bin/CGI_FLASH/cyber.cgi?";

	public FetchThread(FetchCallback callback) {
		this.callback = callback;
	}

	@Override
	protected List<Grille> doInBackground(URL... params) {
		List<Grille> grilles = new ArrayList<Grille>();
		for (int i = 0; i < NB_GRILLES; i++) {
			publishProgress(i);

			Grille g;
			try {
				g = obtenirGrille(i);
				grilles.add(g);
			} catch (Exception e) {
				errors.put(i, e.getMessage());
			}

		}

		return grilles;
	}

	private Grille obtenirGrille(int n) throws ClientProtocolException,
			IOException, MalformedGrille, InterruptedException {
		String line = "";
		Grille g;
		StringBuilder sb = new StringBuilder();

		HttpClient client = new DefaultHttpClient();

		HttpGet get = new HttpGet(URL_GRILLES + n);
		HttpResponse response = client.execute(get);
		/*BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));*/
		
		String content = EntityUtils.toString(response.getEntity());
		byte[] bytes = content.getBytes("ISO-8859-1");
		
		String contenuEncode = new String(bytes, "ISO-8859-1");

		g = Grille.grilleFromServer(contenuEncode);
		g.setDateRecuperee(new Date());

		return g;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		callback.progress(values[0], NB_GRILLES);
	}

	@Override
	protected void onPostExecute(List<Grille> result) {
		callback.done(result, errors);
	}

	public interface FetchCallback {
		void done(List<Grille> result, SparseArray<String> errors);

		void progress(int nbDone, int nbTotal);
	}
}
