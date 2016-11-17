package pl.edu.mimuw.forum.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import jfxtras.styles.jmetro8.ToggleSwitch;
import pl.edu.mimuw.forum.ui.change.ResponseChange;
import pl.edu.mimuw.forum.ui.change.SuggestionChange;
import pl.edu.mimuw.forum.ui.models.SuggestionViewModel;

public class SuggestionPaneController extends BasePaneController {

	private SuggestionViewModel model;

	@FXML
	private TextArea suggestionField;

	@FXML
	private ToggleSwitch acceptableField;

	public void setModel(SuggestionViewModel model) {
		if (this.model != null) {
			suggestionField.textProperty().unbindBidirectional(this.model.getResponse());
			acceptableField.selectedProperty().unbindBidirectional(this.model.getIsResponseAccepted());
		}
		// model.presentOn(detailsPaneController);
		this.model = model;

		if (this.model != null) {
			suggestionField.textProperty().bindBidirectional(this.model.getResponse());
			acceptableField.selectedProperty().bindBidirectional(this.model.getIsResponseAccepted());
		}

		setHasModel(model != null);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		suggestionProperty().addListener((observable, oldValue, newValue) -> {
			SuggestionChange change = new SuggestionChange(model, oldValue, newValue);
			change.push(mainPane);
		});

		acceptableProperty().addListener((observable, oldValue, newValue) -> {
			ResponseChange change = new ResponseChange(model, oldValue, newValue);
			change.push(mainPane);
		});
	}

	public StringProperty suggestionProperty() {
		return suggestionField.textProperty();
	}

	public BooleanProperty acceptableProperty() {
		return acceptableField.selectedProperty();
	}
}
