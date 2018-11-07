package control;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.persistance.Settings;
import model.persistance.User;
import view.Dashboard;
import view.LoginPopup;

@SuppressWarnings("restriction")
public class LoginProvider implements EventHandler<ActionEvent> {

	private final TextField usernameField;
	private final PasswordField passwordField;
	private final RadioButton savePwd;
	private final Dashboard dashboard;
	private final LoginPopup login;
	private final IliasStarter starter;

	public LoginProvider(Dashboard dashboard, TextField usernameField, PasswordField passwordField,
			RadioButton savePwd, LoginPopup login) {
		this.dashboard = dashboard;
		this.usernameField = usernameField;
		this.passwordField = passwordField;
		this.savePwd = savePwd;
		this.login = login;
		starter = new IliasStarter(dashboard);
	}

	@Override
	public void handle(ActionEvent event) {
		final String username = usernameField.getText();
		final String password = passwordField.getText();
		if (password.length() < 1) {
			toggleDashboardLoginState("Ungültiges Passwort");
			return;
		}
		
		login.hide();
		Dashboard.setStatusText("Login wird durchgeführt", false);
		dashboard.showLoader(true);
		dashboard.setMenuTransparent(false);

		User user = Settings.getInstance().getUser();
		if (savePwd.isSelected()) {
			user.setName(username);
			user.setPassword(password);
		} else {
			user.setName("");
			user.setPassword("");
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				starter.login(username, password);
			}
		}).start();
	}

	private void toggleDashboardLoginState(final String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Dashboard.setStatusText(message, true);
				usernameField.requestFocus();
				usernameField.selectAll();
			}
		});
	}

}
