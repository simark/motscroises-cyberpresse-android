package com.example.motscroisescyberpresse;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class CaseView extends TextView {
	enum DirectionActive {
		DROITE, BAS
	};

	private final int posX;
	private final int posY;

	private boolean caseActive = false;
	private boolean pleine = false;
	private DirectionActive directionActive = DirectionActive.DROITE;

	public CaseView(Context context, int posX, int posY) {
		super(context);
		this.posX = posX;
		this.posY = posY;

		setGravity(Gravity.CENTER);
		setTextColor(Color.BLACK);
		setFocusable(true);
		setFocusableInTouchMode(true);
		updateBackground();
	}
	

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setCaseActive(boolean caseActive) {
		this.caseActive = caseActive;
		updateBackground();
	}
	
	public void setDirectionActive(DirectionActive directionActive) {
		this.directionActive = directionActive;
		updateBackground();
	}
	
	public DirectionActive getDirectionActive() {
		return directionActive;
	}
	
	public void switchDirectionActive() {
		if (getDirectionActive() == DirectionActive.DROITE) {
			setDirectionActive(DirectionActive.BAS);
		} else {
			setDirectionActive(DirectionActive.DROITE);
		}
	}

	public void setPleine(boolean pleine) {
		this.pleine = pleine;
		updateBackground();
	}
	
	public boolean isPleine() {
		return pleine;
	}

	private void updateBackground() {
		if (pleine) {
			setBackgroundColor(Color.BLACK);
		} else {
			if (caseActive) {
				if (directionActive == DirectionActive.DROITE) {
					setBackgroundResource(R.drawable.border_right_arrow);
				} else {
					setBackgroundResource(R.drawable.border_down_arrow);
				}
			} else {
				setBackgroundResource(R.drawable.border);
			}
		}
	}
}
