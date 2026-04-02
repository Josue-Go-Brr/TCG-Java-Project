package godot.cards;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
public class CardLibrary {
	private  Map<Integer,BaseCarte> cards;
	public CardLibrary(){
		cards=new HashMap<>();
		loadCards();
	}
	private void loadCards(){

	}
	public BaseCarte getCardById(int id){
		return cards.get(id);
	}
	public void addCard(BaseCarte card){
		cards.put(card.getId(),card);
	}
	public Collection<BaseCarte> getAllcards() {
		return cards.values();
	}
	public boolean containsCard(int id){
		return cards.containsKey(id);
	}
}
