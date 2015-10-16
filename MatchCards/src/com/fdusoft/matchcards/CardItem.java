package com.fdusoft.matchcards;

public class CardItem {
	
	private Integer[] pic = {
			R.drawable.card_back,
			R.drawable.card_circle, R.drawable.card_square,
			R.drawable.card_triangle, R.drawable.card_diamond,
			R.drawable.card_up, R.drawable.card_down,
			R.drawable.card_right, R.drawable.card_left,
			R.drawable.card_heart, R.drawable.card_thunder
	};
	
	private int currentState;
	
	public CardItem() {
		this.currentState = 0;
	}
	
	public CardItem(int state) {
		this.currentState = state;
	}
	
	public Integer getImage() {
		return pic[currentState];
	}
	
	public void setState(int state) {
		this.currentState = state;
	}
	
	public int getState() {
		return currentState;
	}
	
	public boolean isOpen() {
		return currentState > 0;
	}

}
