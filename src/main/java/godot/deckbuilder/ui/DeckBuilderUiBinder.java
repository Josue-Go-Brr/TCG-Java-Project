package godot.deckbuilder.ui;

import godot.api.LineEdit;
import godot.api.OptionButton;
import godot.core.Callable;
import godot.core.Error;
import godot.core.StringNames;
import godot.global.GD;
import godot.deckbuilder.DeckBuilderQueryService;
import godot.deckbuilder.DeckBuilderScreenController;

/**
 * Wires deck builder filter controls to {@link DeckBuilderScreenController} callbacks.
 */
public class DeckBuilderUiBinder {
	private final LineEdit searchInput;
	private final OptionButton typeFilter;
	private final OptionButton sortFilter;

	public DeckBuilderUiBinder(LineEdit searchInput, OptionButton typeFilter, OptionButton sortFilter) {
		this.searchInput = searchInput;
		this.typeFilter = typeFilter;
		this.sortFilter = sortFilter;
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

		if (sortFilter != null) {
			sortFilter.clear();
			sortFilter.addItem(DeckBuilderQueryService.SORT_NAME);
			sortFilter.addItem(DeckBuilderQueryService.SORT_COST);
			sortFilter.addItem(DeckBuilderQueryService.SORT_ATK);
			sortFilter.select(0);
		}
	}

	public void connect(DeckBuilderScreenController controller) {
		if (searchInput != null) {
			Error err = searchInput.getTextChanged().connect(
					Callable.create(controller, StringNames.toGodotName("_on_search_input_text_changed")),
					0
			);
			if (err != Error.OK) {
				GD.INSTANCE.printErr("[DeckBuilder] Failed to connect search signal: " + err);
			}
		}

		if (typeFilter != null) {
			Error err = typeFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_type_filter_item_selected")),
					0
			);
			if (err != Error.OK) {
				GD.INSTANCE.printErr("[DeckBuilder] Failed to connect type signal: " + err);
			}
		}

		if (sortFilter != null) {
			Error err = sortFilter.getItemSelected().connect(
					Callable.create(controller, StringNames.toGodotName("_on_sort_filter_item_selected")),
					0
			);
			if (err != Error.OK) {
				GD.INSTANCE.printErr("[DeckBuilder] Failed to connect sort signal: " + err);
			}
		}
	}

	public String getSearchText() {
		return searchInput == null ? "" : searchInput.getText();
	}

	public String getSelectedType() {
		if (typeFilter == null || typeFilter.getItemCount() == 0) {
			return DeckBuilderQueryService.TYPE_ALL;
		}
		return typeFilter.getItemText(typeFilter.getSelected());
	}

	public String getSelectedSort() {
		if (sortFilter == null || sortFilter.getItemCount() == 0) {
			return DeckBuilderQueryService.SORT_NAME;
		}
		return sortFilter.getItemText(sortFilter.getSelected());
	}
}
