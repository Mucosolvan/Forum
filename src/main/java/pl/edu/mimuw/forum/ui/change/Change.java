package pl.edu.mimuw.forum.ui.change;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;

public abstract class Change<T extends NodeViewModel> {
	protected T model;

	public enum changeStatus {
		toUndo, toRedo, None
	};

	protected changeStatus status = changeStatus.None;

	public abstract void undo();

	public abstract void redo();

	public Change(T model) {
		this.model = model;
	}

	public changeStatus getStatus() {
		return status;
	}

	public void setStatus(changeStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.model.toString();
	}
}
