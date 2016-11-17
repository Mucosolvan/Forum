package pl.edu.mimuw.forum.ui.change;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;

public class RemoveNodeChange extends Change<NodeViewModel> {

	private NodeViewModel model2;

	private int position;

	public RemoveNodeChange(NodeViewModel model, NodeViewModel model2, int position) {
		super(model);
		this.model2 = model2;
		this.position = position;
	}

	@Override
	public void undo() {
		model.getChildren().add(position, model2);
	}

	@Override
	public void redo() {
		model.getChildren().remove(model2);
	}

}
