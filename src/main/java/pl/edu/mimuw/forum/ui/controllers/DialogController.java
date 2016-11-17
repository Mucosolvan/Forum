package pl.edu.mimuw.forum.ui.controllers;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Window;
import pl.edu.mimuw.forum.ui.models.CommentViewModel;
import pl.edu.mimuw.forum.ui.models.NodeViewModel;
import pl.edu.mimuw.forum.ui.models.SuggestionViewModel;
import pl.edu.mimuw.forum.ui.models.SurveyViewModel;
import pl.edu.mimuw.forum.ui.models.TaskViewModel;

public class DialogController implements Initializable {

	@FXML
	private ToggleGroup group;

	@FXML
	private Dialog<NodeViewModel> dialog;

	@FXML
	private Window view;

	@FXML
	private TextField userField;

	@FXML
	private TextArea commentField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.disableProperty().bind(Bindings.isNull(group.selectedToggleProperty()));

		dialog.setResultConverter(this::convertDialogResult);

	}

	private NodeViewModel convertDialogResult(ButtonType buttonType) {
		if (ButtonType.OK.equals(buttonType)) {
			return getSelectedToggleValue();
		} else {
			return null;
		}
	}

	private NodeViewModel getSelectedToggleValue() {
		RadioButton selected = (RadioButton) group.getSelectedToggle();

		if (selected == null) {
			return null;
		}
		return emptyNode(selected.getText());
	}

	private NodeViewModel emptyNode(String name) {
		String author = userField.textProperty().get();
		String comment = commentField.textProperty().get();
		if (author.isEmpty() || author.equals(""))
			author = "Author";
		if (comment.isEmpty() || comment.equals(""))
			comment = "Comment";
		switch (name) {
		case "Comment":
			return new CommentViewModel(comment, author);
		case "Survey":
			return new SurveyViewModel(comment, author);
		case "Task":
			return new TaskViewModel(comment, author, new Date());
		case "Suggestion":
			return new SuggestionViewModel(comment, author, "Suggestion");
		default:
			return null;
		}
	}
}
