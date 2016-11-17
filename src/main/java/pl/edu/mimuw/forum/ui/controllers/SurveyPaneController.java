package pl.edu.mimuw.forum.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.edu.mimuw.forum.ui.change.DownvoteChange;
import pl.edu.mimuw.forum.ui.change.UpvoteChange;
import pl.edu.mimuw.forum.ui.models.SurveyViewModel;

public class SurveyPaneController extends BasePaneController {

	private SurveyViewModel model;

	@FXML
	private Button upVoteButton;

	@FXML
	private Button downVoteButton;

	private static boolean isNum(String strNum) {
		if (strNum.isEmpty() || strNum == null)
			return false;
		boolean ret = true;
		try {

			Integer.parseInt(strNum);

		} catch (NumberFormatException e) {
			ret = false;
		}
		return ret;
	}

	public void setModel(SurveyViewModel model) {
		if (this.model != null) {
			upVoteButton.textProperty().unbind();
			downVoteButton.textProperty().unbind();
		}

		this.model = model;

		if (this.model != null) {
			bind(upVoteButton.textProperty(), upVoteButton, this.model.getLikes());
			bind(downVoteButton.textProperty(), downVoteButton, this.model.getDislikes());
		}

		setHasModel(model != null);
	}

	private void bind(StringProperty stringProperty, Button button, IntegerProperty property) {
		stringProperty.bind(Bindings.createStringBinding(() -> String.valueOf(property.get()), property));
		button.setOnAction(evt -> property.set(property.get() + 1));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		upVoteProperty().addListener((observable, oldValue, newValue) -> {
			if (isNum(oldValue)) {
				UpvoteChange change = new UpvoteChange(model, Integer.parseInt(oldValue), Integer.parseInt(newValue));
				change.push(mainPane);
			}
		});

		downVoteProperty().addListener((observable, oldValue, newValue) -> {
			if (isNum(oldValue)) {
				DownvoteChange change = new DownvoteChange(model, Integer.parseInt(oldValue),
						Integer.parseInt(newValue));
				change.push(mainPane);
			}
		});
	}

	public StringProperty upVoteProperty() {
		return upVoteButton.textProperty();
	}

	public StringProperty downVoteProperty() {
		return downVoteButton.textProperty();
	}
}
