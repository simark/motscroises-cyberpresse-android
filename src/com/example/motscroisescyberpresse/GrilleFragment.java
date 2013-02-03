package com.example.motscroisescyberpresse;

import java.sql.RowIdLifetime;

import javax.xml.datatype.Duration;

import com.example.motscroisescyberpresse.CaseView.DirectionActive;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridLayout.Spec;
import android.widget.TextView;
import android.widget.Toast;

public class GrilleFragment extends Fragment implements OnClickListener {
	private ViewGroup container;
	private GridLayout grilleLayout;
	private Grille grille;
	private CaseView caseActive = null;
	private InputMethodManager inputMethodManager;
	private MyKeyListener myKeyListener = new MyKeyListener();

	@Override
	public void setArguments(Bundle args) {
		grille = (Grille) args.getSerializable("grille");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			this.grille = (Grille) savedInstanceState.getSerializable("grille");
		}

		// Pour le clavier
		this.inputMethodManager = ((InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE));

		this.container = container;
		View layout = inflater.inflate(R.layout.fragment_grille, container,
				false);

		grilleLayout = (GridLayout) layout.findViewById(R.id.grille);
		for (int j = 0; j < 12; j++) {
			for (int i = 0; i < 12; i++) {
				Spec colSpec = GridLayout.spec(i);
				Spec rowSpec = GridLayout.spec(j);

				LayoutParams params = new LayoutParams(rowSpec, colSpec);
				CaseView e = new CaseView(getActivity(), i, j);

				e.setGravity(Gravity.CENTER);
				e.setOnClickListener(this);
				e.setKeyListener(myKeyListener);
				params.leftMargin = params.rightMargin = params.topMargin = params.bottomMargin = 0;

				if (grille.getCharUser(i, j) == Grille.CHAR_BLOC) {
					e.setPleine(true);
				} else {
					e.setText("" + grille.getCharUser(i, j));
				}

				grilleLayout.addView(e, params);
			}
		}

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/* On resize les cases pour prendre toute la largeur. */
		GridLayout l = (GridLayout) container.findViewById(R.id.grille);

		int dim = container.getMeasuredWidth() / 12;

		int childCount = l.getChildCount();
		for (int i = 0; i < childCount; i++) {
			TextView child = (TextView) l.getChildAt(i);
			android.view.ViewGroup.LayoutParams layoutParams = child
					.getLayoutParams();
			layoutParams.height = dim;
			layoutParams.width = dim;
			child.setLayoutParams(layoutParams);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("grille", this.grille);
	}

	// Clic sur une case
	@Override
	public void onClick(View v) {
		if (caseActive != null) {
			if (caseActive == v) {
				caseActive.switchDirectionActive();
			} else {
				caseActive.setCaseActive(false);
			}
		}

		caseActive = (CaseView) v;
		caseActive.setCaseActive(true);
		boolean requestFocus = caseActive.requestFocus();
		Log.e("grille", "rf = " + requestFocus + " focusable = "
				+ caseActive.isFocusable());
	}

	public void prochaineCaseActive() {
		DirectionActive directionActive = caseActive.getDirectionActive();
		int x = caseActive.getPosX();
		int y = caseActive.getPosY();
		int absPos = y * grille.getTailleX() + x;
		CaseView c;
		if (directionActive == DirectionActive.DROITE) {
			while (true) {
				absPos = (absPos + 1) % (grille.getTailleX() * grille.getTailleY());
				c = (CaseView) grilleLayout.getChildAt(absPos);
				if (!c.isPleine()) {
					break;
				}
			}
			onClick(c);
			c.setDirectionActive(DirectionActive.DROITE);
		} else {
			while (true) {
				absPos = (absPos + 12) % (grille.getTailleX() * grille.getTailleY());
				c = (CaseView) grilleLayout.getChildAt(absPos);
				if (!c.isPleine()) {
					break;
				}
			}
			onClick(c);
			c.setDirectionActive(DirectionActive.BAS);
		}
		
		
	}

	public void ouvrirClavierClicked(View v) {
		if (caseActive != null) {
			boolean requestFocus = caseActive.requestFocus();
			Log.e("grille", "rf = " + requestFocus + " focusable = "
					+ caseActive.isFocusable());
			inputMethodManager.showSoftInput(caseActive,
					InputMethodManager.SHOW_FORCED);
		}
	}

	private class MyKeyListener implements KeyListener {

		@Override
		public void clearMetaKeyState(View view, Editable content, int states) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getInputType() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean onKeyDown(View view, Editable text, int keyCode,
				KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				return false;
			}
			// On assume que les keycodes se suivent...
			if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
				keyCode -= KeyEvent.KEYCODE_A;
				int c = 'A' + keyCode;

				caseActive.setText("" + (char) c);
				prochaineCaseActive();
			}
			return true;
		}

		@Override
		public boolean onKeyOther(View view, Editable text, KeyEvent event) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onKeyUp(View view, Editable text, int keyCode,
				KeyEvent event) {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
