package pl.edu.mimuw.forum.ui.change;

import javafx.collections.ObservableList;
import pl.edu.mimuw.forum.ui.controllers.MainPaneController;
import pl.edu.mimuw.forum.ui.models.NodeViewModel;

public abstract class FieldChange<T extends NodeViewModel, P> extends Change<T> {

	protected P oldValue;
	protected P newValue;

	public FieldChange(T model, P oldValue, P newValue) {
		super(model);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public abstract void undo();

	@Override
	public abstract void redo();

	public void push(MainPaneController mainPane) {
		if (mainPane.getLastClicked().isPresent())
			if (mainPane.getLastClicked().get().equals(this.model)) {
				ObservableList<Change> undoL = mainPane.getChangesUndo();
				ObservableList<Change> redoL = mainPane.getChangesRedo();
				if (undoL.isEmpty()) {
					if (redoL.isEmpty()) {
						undoL.add(this);
						redoL.clear();
					} else if (redoL.get(redoL.size() - 1).getStatus().equals(Change.changeStatus.None)) {
						undoL.add(this);
						redoL.clear();
					}
				} else {
					if (redoL.isEmpty()) {
						if (undoL.get(undoL.size() - 1).getStatus().equals(Change.changeStatus.None)) {
							undoL.add(this);
							redoL.clear();
						}
					} else {
						if (redoL.get(redoL.size() - 1).getStatus().equals(Change.changeStatus.None)
								&& undoL.get(undoL.size() - 1).getStatus().equals(Change.changeStatus.None)) {
							undoL.add(this);
							redoL.clear();
						}
					}

				}
			}
	}

}
