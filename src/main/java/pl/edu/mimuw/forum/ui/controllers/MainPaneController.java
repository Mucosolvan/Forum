package pl.edu.mimuw.forum.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import pl.edu.mimuw.forum.exceptions.ApplicationException;
import pl.edu.mimuw.forum.ui.bindings.MainPaneBindings;
import pl.edu.mimuw.forum.ui.change.AddNodeChange;
import pl.edu.mimuw.forum.ui.change.Change;
import pl.edu.mimuw.forum.ui.change.RemoveNodeChange;
import pl.edu.mimuw.forum.ui.change.Change.changeStatus;
import pl.edu.mimuw.forum.ui.helpers.DialogHelper;
import pl.edu.mimuw.forum.ui.models.CommentViewModel;
import pl.edu.mimuw.forum.ui.models.NodeViewModel;
import pl.edu.mimuw.forum.ui.tree.ForumTreeItem;
import pl.edu.mimuw.forum.ui.tree.TreeLabel;
import pl.edu.mimuw.forum.xstream.FileHandling;

/**
 * Kontroler glownego widoku reprezentujacego forum. Widok sklada sie z drzewa
 * zawierajacego wszystkie wezly forum oraz panelu z polami do edycji wybranego
 * wezla.
 * 
 * @author konraddurnoga
 */
public class MainPaneController implements Initializable {

	private Optional<? extends NodeViewModel> lastClicked = Optional.empty();

	public Optional<? extends NodeViewModel> getLastClicked() {
		return lastClicked;
	}

	/**
	 * Korzen drzewa-modelu forum.
	 */
	private NodeViewModel document;

	/**
	 * Wiazania stosowane do komunikacji z
	 * {@link pl.edu.mimuw.forum.ui.controller.ApplicationController }.
	 */
	private MainPaneBindings bindings;

	/**
	 * Widok drzewa forum (wyswietlany w lewym panelu).
	 */
	@FXML
	private TreeView<NodeViewModel> treePane;

	/**
	 * Kontroler panelu wyswietlajacego pola do edycji wybranefgo wezla w
	 * drzewie.
	 */
	@FXML
	private DetailsPaneController detailsController;

	private boolean wasSaved = false;

	private ObservableList<Change> changesUndo = FXCollections.observableArrayList();

	private ObservableList<Change> changesRedo = FXCollections.observableArrayList();

	public ObservableList<Change> getChangesRedo() {
		return changesRedo;
	}

	public ObservableList<Change> getChangesUndo() {
		return changesUndo;
	}

	public MainPaneController() {
		bindings = new MainPaneBindings();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		BooleanBinding nodeSelectedBinding = Bindings.isNotNull(treePane.getSelectionModel().selectedItemProperty());
		bindings.nodeAdditionAvailableProperty().bind(nodeSelectedBinding);
		bindings.nodeRemovaleAvailableProperty()
				.bind(nodeSelectedBinding.and(
						Bindings.createBooleanBinding(() -> getCurrentTreeItem().orElse(null) != treePane.getRoot(),
								treePane.rootProperty(), nodeSelectedBinding)));

		changesUndo.addListener(new ListChangeListener<Change>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends pl.edu.mimuw.forum.ui.change.Change> c) {
				while (c.next()) {
					bindings.hasChangesProperty().set(true);
					if (c.wasRemoved()) {
						if (changesUndo.size() == 0) {
							bindings.undoAvailableProperty().set(false);
							if (wasSaved)
								bindings.hasChangesProperty().set(false);
						}
					}
					if (c.wasAdded())
						bindings.undoAvailableProperty().set(true);
				}
			}
		});

		changesRedo.addListener(new ListChangeListener<Change>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends pl.edu.mimuw.forum.ui.change.Change> c) {
				while (c.next()) {
					if (c.wasRemoved()) {
						if (changesRedo.size() == 0)
							bindings.redoAvailableProperty().set(false);
					}
					if (c.wasAdded())
						bindings.redoAvailableProperty().set(true);
				}
			}
		});

		bindings.undoAvailableProperty().set(false);
		bindings.redoAvailableProperty().set(false);

		detailsController.memPane(this);

	}

	public MainPaneBindings getPaneBindings() {
		return bindings;
	}

	/**
	 * Otwiera plik z zapisem forum i tworzy reprezentacje graficzna wezlow
	 * forum.
	 * 
	 * @param file
	 * @return
	 * @throws ApplicationException
	 */
	public Node open(File file) throws ApplicationException {
		if (file != null) {
			document = FileHandling.openFile(file);
			bindings.hasChangesProperty().set(false);
			wasSaved = true;
		} else {
			document = new CommentViewModel("Welcome to a new forum", "Admin");
			bindings.hasChangesProperty().set(true);
		}
		/**
		 * Dzieki temu kontroler aplikacji bedzie mogl wyswietlic nazwe pliku
		 * jako tytul zakladki. Obsluga znajduje sie w
		 * {@link pl.edu.mimuw.forum.ui.controller.ApplicationController#createTab }
		 */
		getPaneBindings().fileProperty().set(file);
		return openInView(document);
	}

	/**
	 * Zapisuje aktualny stan forum do pliku.
	 * 
	 * @throws ApplicationException
	 * @throws IOException
	 */
	public void save() throws ApplicationException, IOException {
		if (document != null) {
			System.out.println("On save " + document.toNode());
			FileHandling.saveToFile(getPaneBindings().fileProperty().get(), document.toNode());
			wasSaved = true;
			changesRedo.clear();
			changesUndo.clear();
		}
	}

	/**
	 * Cofa ostatnio wykonana operacje na forum.
	 * 
	 * @throws ApplicationException
	 */
	public void undo() throws ApplicationException {
		System.out.println("On undo");
		if (changesUndo.size() != 0) {
			Change x = changesUndo.get(changesUndo.size() - 1);
			x.undo();
			changesUndo.remove(changesUndo.size() - 1);
			x.setStatus(changeStatus.None);
			changesRedo.add(x);
		}
	}

	/**
	 * Ponawia ostatnia cofnieta operacje na forum.
	 * 
	 * @throws ApplicationException
	 */
	public void redo() throws ApplicationException {
		System.out.println("On redo");
		if (changesRedo.size() != 0) {
			Change x = changesRedo.get(changesRedo.size() - 1);
			x.redo();
			changesRedo.remove(changesRedo.size() - 1);
			x.setStatus(changeStatus.None);
			changesUndo.add(x);
		}
	}

	/**
	 * Podaje nowy wezel jako ostatnie dziecko aktualnie wybranego wezla.
	 * 
	 * @param node
	 * @throws ApplicationException
	 */
	public void addNode(NodeViewModel node) throws ApplicationException {
		getCurrentNode().ifPresent(currentlySelected -> {
			currentlySelected.getChildren().add(node); // Zmieniamy jedynie
														// model, widok
														// (TreeView) jest
														// aktualizowany z
														// poziomu
														// funkcji nasluchujacej
														// na zmiany w modelu
														// (zob. metode
														// createViewNode
														// ponizej)
			Change change = new AddNodeChange(currentlySelected, node);
			if (changesRedo.size() != 0) {
				changesRedo.clear();
			}
			changesUndo.add(change);
		});
	}

	/**
	 * Usuwa aktualnie wybrany wezel.
	 */
	public void deleteNode() {
		getCurrentTreeItem().ifPresent(currentlySelectedItem -> {
			TreeItem<NodeViewModel> parent = currentlySelectedItem.getParent();

			NodeViewModel parentModel;
			NodeViewModel currentModel = currentlySelectedItem.getValue();
			if (parent == null) {
				return; // Blokujemy usuniecie korzenia - TreeView bez korzenia
						// jest niewygodne w obsludze
			} else {
				parentModel = parent.getValue();
				int i = parentModel.getChildren().indexOf(currentModel);
				parentModel.getChildren().remove(currentModel); // Zmieniamy
																// jedynie
																// model, widok
																// (TreeView)
																// jest
																// aktualizowany
																// z poziomu
																// funkcji
																// nasluchujacej
																// na zmiany w
																// modelu (zob.
																// metode
																// createViewNode
																// ponizej)
				Change change = new RemoveNodeChange(parentModel, currentModel, i);
				if (changesRedo.size() != 0) {
					changesRedo.clear();
				}
				changesUndo.add(change);
			}
		});
	}

	public Node openInView(NodeViewModel document) throws ApplicationException {
		Node view = loadFXML();

		treePane.setCellFactory(tv -> {
			try {
				// Do reprezentacji graficznej wezla uzywamy niestandardowej
				// klasy wyswietlajacej 2 etykiety
				return new TreeLabel();
			} catch (ApplicationException e) {
				DialogHelper.ShowError("Error creating a tree cell.", e);
				return null;
			}
		});

		ForumTreeItem root = createViewNode(document);
		root.addEventHandler(TreeItem.<NodeViewModel> childrenModificationEvent(), event -> {
			// TODO Moze przydac sie do wykrywania usuwania/dodawania wezlow w
			// drzewie (widoku)
			if (event.wasAdded()) {
				System.out.println("Adding to " + event.getSource());
			}

			if (event.wasRemoved()) {
				System.out.println("Removing from " + event.getSource());
			}
		});

		treePane.setRoot(root);

		for (NodeViewModel w : document.getChildren()) {
			addToTree(w, root);
		}

		expandAll(root);

		treePane.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> onItemSelected(oldValue, newValue));

		return view;
	}

	private Node loadFXML() throws ApplicationException {
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(getClass().getResource("/fxml/main_pane.fxml"));

		try {
			return loader.load();
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
	}

	private Optional<? extends NodeViewModel> getCurrentNode() {
		return getCurrentTreeItem().<NodeViewModel> map(TreeItem::getValue);
	}

	private Optional<TreeItem<NodeViewModel>> getCurrentTreeItem() {
		return Optional.ofNullable(treePane.getSelectionModel().getSelectedItem());
	}

	private void addToTree(NodeViewModel node, ForumTreeItem parentViewNode, int position) {
		ForumTreeItem viewNode = createViewNode(node);

		List<TreeItem<NodeViewModel>> siblings = parentViewNode.getChildren();
		siblings.add(position == -1 ? siblings.size() : position, viewNode);

		node.getChildren().forEach(child -> addToTree(child, viewNode));
	}

	private void addToTree(NodeViewModel node, ForumTreeItem parentViewNode) {
		addToTree(node, parentViewNode, -1);
	}

	private void removeFromTree(ForumTreeItem viewNode) {
		viewNode.removeChildListener();
		TreeItem<NodeViewModel> parent = viewNode.getParent();
		if (parent != null) {
			viewNode.getParent().getChildren().remove(viewNode);
		} else {
			treePane.setRoot(null);
		}
	}

	private ForumTreeItem createViewNode(NodeViewModel node) {
		ForumTreeItem viewNode = new ForumTreeItem(node);
		viewNode.setChildListener(change -> { // wywolywanem, gdy w modelu dla
												// tego wezla zmieni sie
												// zawartosc kolekcji dzieci
			while (change.next()) {
				if (change.wasAdded()) {
					int i = change.getFrom();
					for (NodeViewModel child : change.getAddedSubList()) {

						// TODO Tutaj byc moze nalezy dodac zapisywanie jaka
						// operacja jest wykonywana
						// by mozna bylo ja odtworzyc przy undo/redo
						addToTree(child, viewNode, i); // uwzgledniamy nowy
														// wezel modelu w widoku
						i++;
					}
				}

				if (change.wasRemoved()) {
					for (int i = change.getFrom(); i <= change.getTo(); ++i) {
						// TODO Tutaj byc moze nalezy dodac zapisywanie jaka
						// operacja jest wykonywana
						// by mozna bylo ja odtworzyc przy undo/redo
						removeFromTree((ForumTreeItem) viewNode.getChildren().get(i)); // usuwamy
																						// wezel
																						// modelu
																						// z
																						// widoku
					}
				}
			}
		});

		return viewNode;
	}

	private void expandAll(TreeItem<NodeViewModel> item) {
		item.setExpanded(true);
		item.getChildren().forEach(this::expandAll);
	}

	private void onItemSelected(TreeItem<NodeViewModel> oldItem, TreeItem<NodeViewModel> newItem) {
		detailsController.setModel(newItem != null ? newItem.getValue() : null);
		lastClicked = getCurrentNode();
	}

}
