package control;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import model.persistance.Flags;
import model.persistance.IliasTreeProvider;
import model.persistance.Settings;
import plugin.IliasPlugin.LoginStatus;
import view.Dashboard;

@SuppressWarnings("restriction")
public class IliasStarter {
	private Logger LOGGER = Logger.getLogger(getClass());
	private Dashboard dashboard;

	public IliasStarter(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public boolean login(String username, String password) {
		LoginStatus loginStatusMessage = IliasManager.getInstance().login(username, password);

		if (loginStatusMessage.equals(LoginStatus.WRONG_PASSWORD)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					dashboard.showLoader(false);
					dashboard.setLoginState(false);
					dashboard.showLoginPopup();
					Dashboard.setStatusText("Falsches Passwort!", true);
				}
			});
			return false;
		}
		if (loginStatusMessage.equals(LoginStatus.CONNECTION_FAILED)) {
			LOGGER.warn("Connection failed!");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					dashboard.showLoader(false);
					dashboard.setLoginState(false);
					dashboard.setSigInTransparent(false);
					Dashboard.setStatusText("Verbindung fehlgeschlagen!", true);
				}
			});
			return false;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dashboard.setTitle("Ilias - Angemeldet als " + username);
				Dashboard.setStatusText("Angemeldet als: " + username, false);
			}
		});

		Settings.getInstance().getFlags().setLogin(true);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dashboard.showLoader(false);
				dashboard.setLoginState(true);
				dashboard.setSigInTransparent(true);
				dashboard.setSignInColor();
				if (!Settings.getInstance().getFlags().autoUpdate()) {
					Dashboard.setStatusText(
							"Aktualisiere über den Button in der Menüleiste die Kurse auf deinem Schreibtisch!", false);
				}
			}
		});
		return true;
	}

	public void loadIliasTree() {
		final IliasScraper Scraper = new IliasScraper(dashboard);
		Scraper.run(IliasManager.getInstance().getDashboardHTML());
		while (Scraper.threadCount.get() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.warn(e.getStackTrace());
			}
		}
		Flags flags = Settings.getInstance().getFlags();
		if (!(flags.updateCanceled())) {
			IliasTreeProvider.setTree(Scraper.getIliasTree());
			flags.setUpdateCanceled(false);
		} else {
			flags.setUpdateCanceled(false);
			Dashboard.setStatusText("Aktualisierung abgebrochen.", false);
			return;
		}

		if (flags.isLocalIliasPathStored()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					dashboard.iliasTreeReloaded(true);
					dashboard.showLoader(false);
				}
			});
			return;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dashboard.showSettingsPrompt();
			}
		});
		dashboard.showLoader(false);
	}
}
