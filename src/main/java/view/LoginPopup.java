package view;

import org.controlsfx.control.PopOver;

import control.LoginProvider;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.persistance.Settings;

@SuppressWarnings("restriction")
public class LoginPopup extends PopOver {

	private final GridPane gridPane;
	private static Dashboard dashboard;

	public LoginPopup(Dashboard dashboard) {
		LoginPopup.dashboard = dashboard;
		gridPane = new GridPane();
		setContentNode(gridPane);
		setArrowSize(0);
		setDetachable(false);
		hideOnEscapeProperty().set(true);
		this.getScene().getRoot().getStyleClass().add("main-root");

		gridPane.setId("loginBackground");
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setHgap(10);
		gridPane.setVgap(5);

		initDialog();
	}

	private void initDialog() {

		Button goBack = new Button();
		goBack.setId("loginButtonCancel");
		goBack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dashboard.hideLoginPopup();
			}
		});

		TextField username = new TextField();
		username.setId("textField");
		username.setPromptText("Benutzererkennung");
		username.setText(Settings.getInstance().getUser().getName());

		PasswordField password = new PasswordField();
		password.setText(Settings.getInstance().getUser().getPassword());
		password.setId("textField");
		password.setPromptText("Passwort");

		RadioButton savePwd = new RadioButton("Speichern");
		savePwd.setSelected(true);

		Button loginBtn = new Button();
		loginBtn.setId("loginButtonGO");

		EventHandler<ActionEvent> submit = new LoginProvider(dashboard, username, password, savePwd, this);

		loginBtn.setOnAction(submit);
		username.setOnAction(submit);
		password.setOnAction(submit);

		gridPane.add(goBack, 0, 0);
		gridPane.add(username, 1, 0);
		gridPane.add(password, 1, 1);
		gridPane.add(loginBtn, 0, 1);
		gridPane.add(savePwd, 1, 2);

	}

}
