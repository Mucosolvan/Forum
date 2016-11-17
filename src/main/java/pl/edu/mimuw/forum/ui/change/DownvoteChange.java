package pl.edu.mimuw.forum.ui.change;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;
import pl.edu.mimuw.forum.ui.models.SurveyViewModel;

public class DownvoteChange extends FieldChange<SurveyViewModel, Integer> {

	public DownvoteChange(NodeViewModel model, Integer oldValue, Integer newValue) {
		super((SurveyViewModel) model, oldValue, newValue);
	}

	@Override
	public void undo() {
		this.status = changeStatus.toUndo;
		this.model.getDislikes().set(oldValue);
	}

	@Override
	public void redo() {
		this.status = changeStatus.toRedo;
		this.model.getDislikes().set(newValue);
	}

	@Override
	public String toString() {
		return super.toString() + " " + oldValue + " " + newValue;
	}
}
