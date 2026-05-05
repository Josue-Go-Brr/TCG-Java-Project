package godot.deck;

import godot.cards.BaseCarte;
import godot.cards.CardLibrary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;





public final class DeckState {
	public static final int MIN_DECK_SIZE = 20;
	public static final int MAX_DECK_SIZE = 40;
	public static final int MAX_COPIES_PER_CARD = 3;

	private static final Map<Integer, Integer> copiesByCardId = new LinkedHashMap<>();

	private DeckState() {
	}

	public static int getMaxCopiesPerCard() {
		return MAX_COPIES_PER_CARD;
	}

	public static int getMinDeckSize() {
		return MIN_DECK_SIZE;
	}

	public static int getMaxDeckSize() {
		return MAX_DECK_SIZE;
	}

	public static void clear() {
		copiesByCardId.clear();
	}

	public static int getCopies(int cardId) {
		return copiesByCardId.getOrDefault(cardId, 0);
	}

	
	public static int getTotalCardCount() {
		int sum = 0;
		for (int n : copiesByCardId.values()) {
			sum += n;
		}
		return sum;
	}

	public static boolean isDeckValid() {
		int n = getTotalCardCount();
		return n >= MIN_DECK_SIZE && n <= MAX_DECK_SIZE;
	}

	public static boolean isDeckFull() {
		return getTotalCardCount() >= MAX_DECK_SIZE;
	}

	public static String buildDeckStatusLabel() {
		int n = getTotalCardCount();
		String validity = isDeckValid() ? "Valid" : "Invalid";
		return "Deck: " + n + "/" + MAX_DECK_SIZE + " — " + validity;
	}

	public static boolean isAtOrOverMax(int cardId) {
		return getCopies(cardId) >= MAX_COPIES_PER_CARD;
	}

	




	public static boolean tryAddOneCopy(int cardId) {
		if (isDeckFull()) {
			return false;
		}
		int current = getCopies(cardId);
		if (current >= MAX_COPIES_PER_CARD) {
			return false;
		}
		copiesByCardId.put(cardId, current + 1);
		return true;
	}

	
	public static List<String> serializeLines() {
		List<String> lines = new ArrayList<>();
		for (Map.Entry<Integer, Integer> e : copiesByCardId.entrySet()) {
			if (e.getValue() != null && e.getValue() > 0) {
				lines.add(e.getKey() + "\t" + e.getValue());
			}
		}
		return lines;
	}

	public static void deserializeFromLines(Iterable<String> lines, CardLibrary library) {
		clear();
		if (lines == null || library == null) {
			return;
		}
		for (String line : lines) {
			if (line == null || line.isBlank()) {
				continue;
			}
			String[] parts = line.trim().split("\\s+");
			if (parts.length < 2) {
				continue;
			}
			try {
				int id = Integer.parseInt(parts[0]);
				int copies = Integer.parseInt(parts[1]);
				if (library.getCardById(id) == null) {
					continue;
				}
				copies = Math.max(0, Math.min(MAX_COPIES_PER_CARD, copies));
				int remaining = MAX_DECK_SIZE - getTotalCardCount();
				copies = Math.min(copies, Math.max(0, remaining));
				if (copies > 0) {
					copiesByCardId.put(id, copies);
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	
	public static List<BaseCarte> expandDeckForGrid(CardLibrary library) {
		List<BaseCarte> out = new ArrayList<>();
		if (library == null) {
			return out;
		}
		for (Map.Entry<Integer, Integer> e : copiesByCardId.entrySet()) {
			BaseCarte card = library.getCardById(e.getKey());
			if (card == null) {
				continue;
			}
			int n = e.getValue() == null ? 0 : e.getValue();
			for (int i = 0; i < n; i++) {
				out.add(card);
			}
		}
		return out;
	}
}
