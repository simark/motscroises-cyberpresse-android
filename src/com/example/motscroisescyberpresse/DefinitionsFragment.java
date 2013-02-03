package com.example.motscroisescyberpresse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DefinitionsFragment extends Fragment {

	private String[] definitions;

	@Override
	public void setArguments(Bundle args) {
		definitions = args.getStringArray("definitions");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			this.definitions = savedInstanceState.getStringArray("definitions");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_definitions, container,
				false);

		LinearLayout listeDefinitions = (LinearLayout) root
				.findViewById(R.id.listeDefinitions);

		int i = 1;
		for (String def : definitions) {
			TextView tv = new TextView(getActivity());
			tv.setText("" + i + ": " + def);
			tv.setTextSize(16);
			listeDefinitions.addView(tv);
			
			i++;
		}

		return root;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putStringArray("definitions", definitions);
	}
}
