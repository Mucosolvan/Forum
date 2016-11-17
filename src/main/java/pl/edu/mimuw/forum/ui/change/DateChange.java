package pl.edu.mimuw.forum.ui.change;

import java.util.Date;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;
import pl.edu.mimuw.forum.ui.models.TaskViewModel;

public class DateChange extends FieldChange<TaskViewModel, Date> {

	public DateChange(NodeViewModel model, Date oldValue, Date newValue) {
		super((TaskViewModel) model, oldValue, newValue);
	}

	@Override
	public void undo() {
		this.status = changeStatus.toUndo;
		this.model.getDueDate().set(oldValue);
	}

	@Override
	public void redo() {
		this.status = changeStatus.toRedo;
		this.model.getDueDate().set(newValue);
	}

	@Override
	public String toString() {
		return super.toString() + " " + oldValue + " " + newValue;
	}
}
