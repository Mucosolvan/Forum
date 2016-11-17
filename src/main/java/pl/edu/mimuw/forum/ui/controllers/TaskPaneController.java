package pl.edu.mimuw.forum.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import pl.edu.mimuw.forum.ui.change.DateChange;
import pl.edu.mimuw.forum.ui.helpers.DateTimePicker;
import pl.edu.mimuw.forum.ui.models.TaskViewModel;

public class TaskPaneController extends BasePaneController {

	private TaskViewModel model;

	@FXML
	private DateTimePicker dateTimeField;

	public void setModel(TaskViewModel model) {
		if (this.model != null) {
			dateTimeField.dateTimeValueProperty().unbindBidirectional(this.model.getDueDate());
		}

		this.model = model;

		if (this.model != null) {
			dateTimeField.dateTimeValueProperty().bindBidirectional(this.model.getDueDate());
		}

		setHasModel(this.model != null);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		dateTimeField.dateTimeValueProperty().addListener((observable, oldValue, newValue) -> {
			DateChange change = new DateChange(model, oldValue, newValue);
			change.push(mainPane);
		});
	}

}
