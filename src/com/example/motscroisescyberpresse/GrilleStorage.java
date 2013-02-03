package com.example.motscroisescyberpresse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class GrilleStorage {
	public static void saveListeToStorage(Context context, List<Grille> grilles)
			throws IOException {
		Log.i("liste", "Starting save to local storage: " + grilles.size());
		FileOutputStream out = context.openFileOutput("grilles",
				context.MODE_PRIVATE);
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(grilles);

		Log.i("liste", "Done writing to local storage");
	}

	@SuppressWarnings("unchecked")
	public static List<Grille> loadListeFromStorage(Context context) throws StreamCorruptedException, IOException, ClassNotFoundException {
		Log.i("liste", "Starting load from local storage");
		List<Grille> grilles = null;

		FileInputStream in = context.openFileInput("grilles");
		ObjectInputStream ois = new ObjectInputStream(in);
		grilles = ((List<Grille>) ois.readObject());

		Log.i("liste", "Done load from local storage: " + grilles.size());
		
		return grilles;
	}
}
