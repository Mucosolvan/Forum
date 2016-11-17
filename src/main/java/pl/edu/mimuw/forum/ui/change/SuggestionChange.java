package pl.edu.mimuw.forum.ui.change;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;
import pl.edu.mimuw.forum.ui.models.SuggestionViewModel;

public class SuggestionChange extends FieldChange<SuggestionViewModel, String> {

	public SuggestionChange(NodeViewModel model, String oldValue, String newValue) {
		super((SuggestionViewModel) model, oldValue, newValue);
	}

	@Override
	public void undo() {
		this.status = changeStatus.toUndo;
		this.model.getResponse().set(oldValue);
	}

	@Override
	public void redo() {
		this.status = changeStatus.toRedo;
		this.model.getResponse().set(newValue);
	}

	@Override
	public String toString() {
		return super.toString() + " " + oldValue + " " + newValue;
	}
}
