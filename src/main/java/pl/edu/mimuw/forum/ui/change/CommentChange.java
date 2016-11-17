package pl.edu.mimuw.forum.ui.change;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;

public class CommentChange extends FieldChange<NodeViewModel, String> {

	public CommentChange(NodeViewModel model, String oldValue, String newValue) {
		super(model, oldValue, newValue);
	}

	@Override
	public void undo() {
		this.status = changeStatus.toUndo;
		this.model.getContent().set(oldValue);
	}

	@Override
	public void redo() {
		this.status = changeStatus.toRedo;
		this.model.getContent().set(newValue);
	}

	@Override
	public String toString() {
		return super.toString() + " " + oldValue + " " + newValue;
	}
}
