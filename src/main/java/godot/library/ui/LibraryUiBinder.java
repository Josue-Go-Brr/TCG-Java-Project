package godot.library.ui;

import godot.api.LineEdit;
import godot.api.OptionButton;
import godot.core.Callable;
import godot.core.StringNames;
import godot.library.LibraryQueryService;
import godot.library.LibraryScreenController;

public class LibraryUiBinder {
	private final LineEdit searchInput;
	private final OptionButton typeFilter;
	private OptionButton monsterTypeFilter;
	private OptionButton sortFilter;
	private OptionButton sortOrderFilter;

	private boolean monsterFilterSignalConnected;
	private boolean sortBySignalConnected;
	private boolean sortOrderSignalConnected;

	public LibraryUiBinder(LineEdit searchInput, OptionButton typeFilter, OptionButton monsterTypeFilter,
			OptionButton sortFilter, OptionButton sortOrderFilter) {
		this.searchInput = searchInput;
		this.typeFilter = typeFilter;
		this.monsterTypeFilter = monsterTypeFilter;
		this.sortFilter = sortFilter;
		this.sortOrderFilter = sortOrderFilter;
	}

	/**
	 * When {@code getNodeOrNull} misses on the first frame (godot-kotlin-jvm), deferred code can supply
	 * the real OptionButton references. All three must be refreshable so DEFENSE items and signals apply.
	 */
	public void setLibraryFilterOptionButtons(OptionButton monsterTypeFilter, OptionButton sortFilter,
			OptionButton sortOrderFilter) {
		if (this.monsterTypeFilter != monsterTypeFilter) {
			monsterFilterSignalConnected = false;
		}
		if (this.sortFilter != sortFilter) {
			sortBySignalConnected = false;
		}
		if (this.sortOrderFilter != sortOrderFilter) {
			sortOrderSignalConnected = false;
		}
		this.monsterTypeFilter = monsterTypeFilter;
		this.sortFilter = sortFilter;
		this.sortOrderFilter = sortOrderFilter;
	}

	public void setupDefaultOptions() {
		if (typeFilter != null) {
			typeFilter.clear();
			typeFilter.addItem(LibraryQueryService.TYPE_ALL);
			typeFilter.addItem(LibraryQueryService.TYPE_MONSTER);
			typeFilter.addItem(LibraryQueryService.TYPE_MAGIE);
			typeFilter.addItem(LibraryQueryService.TYPE_TRAP);
			typeFilter.select(0);
		}

		if (monsterTypeFilter != null) {
			monsterTypeFilter.clear();
			monsterTypeFilter.addItem(LibraryQueryService.TYPE_ALL);
			String[] types = {"Dragon", "Machine", "Warrior", "Demon", "Spellcaster", "Beast", "Divine_Beast", "Aqua", "Elf", "Rock"};
			for (String t : types) {
				monsterTypeFilter.addItem(t);
			}
			monsterTypeFilter.select(0);
		}

		if (sortFilter != null) {
			sortFilter.clear();
			sortFilter.addItem(LibraryQueryService.SORT_NAME);
			sortFilter.addItem(LibraryQueryService.SORT_COST);
			sortFilter.addItem(LibraryQueryService.SORT_ATK);
			sortFilter.addItem(LibraryQueryService.SORT_DEFENSE);
			sortFilter.select(0);
		}

		if (sortOrderFilter != null) {
			sortOrderFilter.clear();
			sortOrderFilter.addItem(LibraryQueryService.ORDER_ASC);
			sortOrderFilter.addItem(LibraryQueryService.ORDER_DESC);
			sortOrderFilter.select(0);
		}
	}

	public void connect(LibraryScreenController controller) {
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
		if (monsterTypeFilter != null && !monsterFilterSignalConnected) {
			monsterTypeFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
			monsterFilterSignalConnected = true;
		}
		if (sortFilter != null && !sortBySignalConnected) {
			sortFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
			sortBySignalConnected = true;
		}
		if (sortOrderFilter != null && !sortOrderSignalConnected) {
			sortOrderFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
			sortOrderSignalConnected = true;
		}
	}

	public void connectMonsterSortByAndOrderSignalsIfNeeded(LibraryScreenController controller) {
		if (monsterTypeFilter != null && !monsterFilterSignalConnected) {
			monsterTypeFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
			monsterFilterSignalConnected = true;
		}
		if (sortFilter != null && !sortBySignalConnected) {
			sortFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
			sortBySignalConnected = true;
		}
		if (sortOrderFilter != null && !sortOrderSignalConnected) {
			sortOrderFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_dropdown_item_selected")),
					0
			);
			sortOrderSignalConnected = true;
		}
	}

	public String getSearchText() {
		return searchInput == null ? "" : searchInput.getText();
	}

	public String getSelectedType() {
		return typeFilter == null ? LibraryQueryService.TYPE_ALL : typeFilter.getItemText(typeFilter.getSelected());
	}

	public String getSelectedMonsterType() {
		return monsterTypeFilter == null ? LibraryQueryService.TYPE_ALL : monsterTypeFilter.getItemText(monsterTypeFilter.getSelected());
	}

	public String getSelectedSort() {
		return sortFilter == null ? LibraryQueryService.SORT_NAME : sortFilter.getItemText(sortFilter.getSelected());
	}

	public String getSelectedSortOrder() {
		return sortOrderFilter == null ? LibraryQueryService.ORDER_ASC : sortOrderFilter.getItemText(sortOrderFilter.getSelected());
	}
}
