package pl.edu.mimuw.forum.ui.change;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;
import pl.edu.mimuw.forum.ui.models.SuggestionViewModel;

public class ResponseChange extends FieldChange<SuggestionViewModel, Boolean> {

	public ResponseChange(NodeViewModel model, Boolean oldValue, Boolean newValue) {
		super((SuggestionViewModel) model, oldValue, newValue);
	}

	@Override
	public void undo() {
		this.status = changeStatus.toUndo;
		this.model.getIsResponseAccepted().set(oldValue);
	}

	@Override
	public void redo() {
		this.status = changeStatus.toRedo;
		this.model.getIsResponseAccepted().set(newValue);
	}

	@Override
	public String toString() {
		return super.toString() + " " + oldValue + " " + newValue;
	}
}
