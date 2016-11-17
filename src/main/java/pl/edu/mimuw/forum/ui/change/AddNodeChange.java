package pl.edu.mimuw.forum.ui.change;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;

public class AddNodeChange extends Change<NodeViewModel> {

	private NodeViewModel model2;

	public AddNodeChange(NodeViewModel model, NodeViewModel model2) {
		super(model);
		this.model2 = model2;
	}

	@Override
	public void undo() {
		model.getChildren().remove(model2);
	}

	@Override
	public void redo() {
		model.getChildren().add(model2);
	}

}
