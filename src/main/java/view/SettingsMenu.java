package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.persistance.Flags;
import model.persistance.Settings;

import org.controlsfx.control.PopOver;

@SuppressWarnings("restriction")
public class SettingsMenu extends PopOver/* implements EventHandler<ActionEvent> */ {
	private static Button localIliasPath;
	private final GridPane gridPane;
	private Button autoUpdate;
	private Button autoLogin;
	private Button connectSecure;
	private boolean promptUpdater;
	private static FadeTransition t;
	private static Dashboard dashboard;

	public SettingsMenu(Dashboard dashboard) {
		SettingsMenu.dashboard = dashboard;
		gridPane = new GridPane();
		setContentNode(gridPane);
		setArrowSize(0);
		setDetachable(false);
		hideOnEscapeProperty().set(true);
		/*
		 * @see http://stackoverflow.com/questions/25336796/tooltip-background-with
		 * -javafx-css
		 */
		this.getScene().getRoot().getStyleClass().add("main-root");
		/* ********************************************************** */
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		initDialog();
		changeLocalIliasFolderButton();
	}

	private void initDialog() {

		final Button hideSettingsMenu = new Button("Fertig");
		hideSettingsMenu.setId("greenButton");
		hideSettingsMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hide();
			}
		});

		gridPane.add(hideSettingsMenu, 0, 0);

		Label selectIliasLocalBtn = new Label("Mein lokaler Ilias-Ordner");
		gridPane.add(selectIliasLocalBtn, 0, 1);

		localIliasPath = new Button();
		localIliasPath.setPrefWidth(250);
		localIliasPath.setMaxWidth(250);
		localIliasPath.setOnAction(event -> {
			showFileChooser();
		});

		Button help = new Button("?");
		help.setId("greenButton");
		help.setOnAction(event -> {
			PopOver helpText = new PopOver();
			helpText.setArrowSize(0);
			helpText.getScene().getRoot().getStyleClass().add("main-root");
			helpText.setDetachable(false);
			Label text = new Label("Der lokale ILIAS-Ordner ist der Ordner, "
					+ "in dem du auf deinem Computer deine Dateien " + "aus dem ILIAS speicherst.\nDiese Angabe wird "
					+ "ben\u00F6tigt, damit ein Abgleich stattfinden kann, "
					+ "welche Dateien du bereits besitzt und welche noch nicht."
					+ "\nDie Benennung deiner Unterordner oder Dateien spielt dabei keine Rolle.");
			text.setPadding(new Insets(10, 10, 10, 10));
			Button okBtn = new Button("X");
			okBtn.setOnAction(event2 -> {
				helpText.hide();
			});
			HBox boxHelp = new HBox();
			boxHelp.getChildren().addAll(text, okBtn);
			helpText.setContentNode(boxHelp);
			helpText.show(help);
		});

		HBox boxPath = new HBox();
		boxPath.setSpacing(10);
		boxPath.getChildren().addAll(localIliasPath, help);

		gridPane.add(boxPath, 1, 1);

		Label startActions = new Label("Bei jedem Start ausführen");
		final EventHandler<ActionEvent> toggleButton = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				toggleButtonColor(event);
			}
		};
		autoLogin = new Button("Anmelden");
		autoLogin.setPrefWidth(120);
		autoLogin.setOnAction(toggleButton);
		autoUpdate = new Button("Aktualisieren");
		autoUpdate.setPrefWidth(120);
		autoUpdate.setOnAction(toggleButton);
		HBox boxAuto = new HBox();
		boxAuto.setSpacing(10);
		boxAuto.getChildren().addAll(autoLogin, autoUpdate);
		Flags flags = Settings.getInstance().getFlags();
		if (flags.isAutoLogin()) {
			autoLogin.setId("autoButtonActive");
		}
		if (flags.autoUpdate()) {
			autoUpdate.setId("autoButtonActive");
		}

		gridPane.add(startActions, 0, 2);
		gridPane.add(boxAuto, 1, 2);

		Label contactDeveloper = new Label("Noch Fragen?");
		Button emailAdress = new Button("Entwickler kontaktieren");
		emailAdress.setPrefWidth(180);
		emailAdress.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openEmailDialog();
			}

		});
		Button faq = new Button("FAQ");
		faq.setPrefWidth(60);
		faq.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openIliasWiki();
			}

		});
		HBox boxContact = new HBox();
		boxContact.setSpacing(10);
		boxContact.getChildren().addAll(faq, emailAdress);
		gridPane.add(contactDeveloper, 0, 3);
		gridPane.add(boxContact, 1, 3);

		Label experimental = new Label("Experimentelle Einstellungen");
		connectSecure = new Button("Sichere Verbindung benutzen");
		connectSecure.setPrefWidth(250);
		connectSecure.setOnAction(toggleButton);
		if (flags.isConnectSecure()) {
			connectSecure.setId("autoButtonActive");
		}

		Button helpSecure = new Button("?");
		helpSecure.setId("greenButton");
		helpSecure.setOnAction(event -> {
			PopOver helpText = new PopOver();
			helpText.setArrowSize(0);
			helpText.getScene().getRoot().getStyleClass().add("main-root");
			helpText.setDetachable(false);
			Label text = new Label("Sollte dein IliasDownloader Probleme beim Login oder Aktualisieren "
					+ "haben, ermöglicht dieser Button,\ndass die Sicherheit der Verbindung zu Ilias "
					+ "weniger stark überprüft wird.\nDies sollte in den meisten Fällen das Problem "
					+ "beheben.\nZwar wird dadurch die Verbindung nicht direkt unsicher,\ntrotzdem "
					+ "sollte diese Funktion mit Vorsicht genossen werden.");
			text.setPadding(new Insets(10, 10, 10, 10));
			Button okBtn = new Button("X");
			okBtn.setOnAction(event2 -> {
				helpText.hide();
			});
			HBox boxSecure = new HBox();
			boxSecure.getChildren().addAll(text, okBtn);
			helpText.setContentNode(boxSecure);
			helpText.show(helpSecure);
		});

		HBox boxExperimental = new HBox();
		boxExperimental.setSpacing(10);
		boxExperimental.getChildren().addAll(connectSecure, helpSecure);

		gridPane.add(experimental, 0, 4);
		gridPane.add(boxExperimental, 1, 4);
	}

	private void showFileChooser() {
		double x = getX();
		double y = getY();
		hide();
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Lokaler Ilias Ordner");

		final String localIliasFolderPath = Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath();
		if (!localIliasFolderPath.equals(".")) {
			directoryChooser.setInitialDirectory(new File(localIliasFolderPath));
		}

		final File selectedFile = directoryChooser.showDialog(new Stage());

		if (selectedFile != null) {
			Settings.getInstance().getIliasFolderSettings().setLocalIliasFolderPath(selectedFile.getAbsolutePath());
			Settings.getInstance().getFlags().setLocalIliasPathStored(true);
			localIliasPath.setText(selectedFile.getAbsolutePath());
			updateLocalIliasFolderPath();
		} else if (!localIliasFolderPath.equals(".")) {
			localIliasPath.setText(localIliasFolderPath);
		} else {
			localIliasPath.setText("Ilias Ordner auswählen");
			Settings.getInstance().getFlags().setLocalIliasPathStored(false);
		}
		changeLocalIliasFolderButton();
		new SettingsMenu(dashboard).show(this, x, y);
	}

	private void updateLocalIliasFolderPath() {
		if (promptUpdater) {
			dashboard.iliasTreeReloaded(true);
			promptUpdater = false;
		}
	}

	public void activatePromptUpdater() {
		promptUpdater = true;
	}

	private void openEmailDialog() {
		try {
			Desktop.getDesktop().mail(new URI("mailto:DeOldSax@gmx.de?subject=Bugreport/Verbesserungsvorschlag/Frage"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void openIliasWiki() {
		try {
			Desktop.getDesktop().browse(new URI("https://github.com/DeOldSax/iliasDownloaderTool/wiki"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void changeLocalIliasFolderButton() {
		if (Settings.getInstance().getFlags().isLocalIliasPathStored()) {
			localIliasPath.setId("localIliasPath");
			localIliasPath.setText(Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath());
			getBlinkyTransition().stop();
			localIliasPath.setOpacity(1);
		} else {
			if (Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath().equals(".")) {
				localIliasPath.setText("Ilias Ordner auswählen");
			}
			localIliasPath.setId("localIliasPathNotSelected");
			getBlinkyTransition().play();
		}
	}

	private static FadeTransition getBlinkyTransition() {
		if (t == null) {
			t = new FadeTransition(Duration.millis(500), localIliasPath);
			t.setToValue(0.1);
			t.setFromValue(1.0);
			t.setCycleCount(Timeline.INDEFINITE);
			t.setAutoReverse(true);
		}
		return t;
	}

	private void toggleButtonColor(ActionEvent event) {
		Flags flags = Settings.getInstance().getFlags();
		Button button = (Button) event.getSource();
		if (button.equals(autoLogin)) {
			if (flags.isAutoLogin()) {
				flags.setAutoLogin(false);
				button.setId(null);
			} else {
				flags.setAutoLogin(true);
				button.setId("autoButtonActive");
			}
			return;
		}
		if (button.equals(autoUpdate)) {
			if (flags.autoUpdate()) {
				flags.setAutoUpdate(false);
				button.setId(null);
			} else {
				flags.setAutoUpdate(true);
				button.setId("autoButtonActive");
			}
			return;
		}
		if (button.equals(connectSecure)) {
			if (flags.isConnectSecure()) {
				flags.setConnectSecure(false);
				button.setId(null);
			} else {
				flags.setConnectSecure(true);
				button.setId("autoButtonActive");
			}
			return;
		}
	}
}
