package godot.deckbuilder.ui;

import godot.api.LineEdit;
import godot.api.OptionButton;
import godot.core.Callable;
import godot.core.StringNames;
import godot.deckbuilder.DeckBuilderQueryService;
import godot.deckbuilder.DeckBuilderScreenController;

public class DeckBuilderUiBinder {
	private final LineEdit searchInput;
	private final OptionButton typeFilter;
	private final OptionButton monsterTypeFilter;
	private final OptionButton sortFilter;
	private final OptionButton sortOrderFilter;

	public DeckBuilderUiBinder(LineEdit searchInput, OptionButton typeFilter, OptionButton monsterTypeFilter,
			OptionButton sortFilter, OptionButton sortOrderFilter) {
		this.searchInput = searchInput;
		this.typeFilter = typeFilter;
		this.monsterTypeFilter = monsterTypeFilter;
		this.sortFilter = sortFilter;
		this.sortOrderFilter = sortOrderFilter;
	}

	public void setupDefaultOptions() {
		if (typeFilter != null) {
			typeFilter.clear();
			typeFilter.addItem(DeckBuilderQueryService.TYPE_ALL);
			typeFilter.addItem(DeckBuilderQueryService.TYPE_MONSTER);
			typeFilter.addItem(DeckBuilderQueryService.TYPE_MAGIE);
			typeFilter.addItem(DeckBuilderQueryService.TYPE_TRAP);
			typeFilter.select(0);
		}

		if (monsterTypeFilter != null) {
			monsterTypeFilter.clear();
			monsterTypeFilter.addItem(DeckBuilderQueryService.TYPE_ALL);
			String[] types = {"Dragon", "Machine", "Warrior", "Demon", "Spellcaster", "Beast", "Divine_Beast", "Aqua", "Elf", "Rock"};
			for (String t : types) {
				monsterTypeFilter.addItem(t);
			}
			monsterTypeFilter.select(0);
		}

		if (sortFilter != null) {
			sortFilter.clear();
			sortFilter.addItem(DeckBuilderQueryService.SORT_NAME);
			sortFilter.addItem(DeckBuilderQueryService.SORT_COST);
			sortFilter.addItem(DeckBuilderQueryService.SORT_ATK);
			sortFilter.addItem(DeckBuilderQueryService.SORT_DEFENSE);
			sortFilter.select(0);
		}

		if (sortOrderFilter != null) {
			sortOrderFilter.clear();
			sortOrderFilter.addItem(DeckBuilderQueryService.ORDER_ASC);
			sortOrderFilter.addItem(DeckBuilderQueryService.ORDER_DESC);
			sortOrderFilter.select(0);
		}
	}

	public void connect(DeckBuilderScreenController controller) {
		if (searchInput != null) {
			searchInput.getTextChanged().connect(
					Callable.create(controller, StringNames.toGodotName("_on_search_input_text_changed")),
					0
			);
		}
		if (typeFilter != null) {
			typeFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
		}
		if (monsterTypeFilter != null) {
			monsterTypeFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
		}
		if (sortFilter != null) {
			sortFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
		}
		if (sortOrderFilter != null) {
			sortOrderFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
		}
	}

	public String getSearchText() {
		return searchInput == null ? "" : searchInput.getText();
	}

	public String getSelectedType() {
		return typeFilter == null ? DeckBuilderQueryService.TYPE_ALL : typeFilter.getItemText(typeFilter.getSelected());
	}

	public String getSelectedMonsterType() {
		return monsterTypeFilter == null ? DeckBuilderQueryService.TYPE_ALL : monsterTypeFilter.getItemText(monsterTypeFilter.getSelected());
	}

	public String getSelectedSort() {
		return sortFilter == null ? DeckBuilderQueryService.SORT_NAME : sortFilter.getItemText(sortFilter.getSelected());
	}

	public String getSelectedSortOrder() {
		return sortOrderFilter == null ? DeckBuilderQueryService.ORDER_ASC : sortOrderFilter.getItemText(sortOrderFilter.getSelected());
	}
}
