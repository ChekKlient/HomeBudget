package pl.tukanmedia.scrooge.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import pl.tukanmedia.scrooge.helper.Configuration;
import pl.tukanmedia.scrooge.model.controller.UserController;

@org.springframework.stereotype.Component
public class LoginFormFactory {

	@Autowired
	private DaoAuthenticationProvider daoAuthenticationProvider;
	
	@Autowired
	private UserController userController;
	
	private class LoginForm {
		
		private VerticalLayout root;
		private Panel panel;
		private TextField username;
		private PasswordField passwordField;
		private Button loginBtn;
		private Button signupBtn;
		
		public LoginForm init() {
			root = new VerticalLayout();
			root.setMargin(true);
			root.setHeight("100%");
			
			panel = new Panel("Авторизация");
			panel.setSizeUndefined();
			
			loginBtn = new Button("Войти");
			loginBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			signupBtn = new Button("Реигстрация");
			signupBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
			
			username = new TextField("Login");
			username.setDescription("Укажите логин");
			passwordField = new PasswordField("Password");
			passwordField.setDescription("Укажите пароль");
			passwordField.addShortcutListener(new ShortcutListener("ABC", KeyCode.ENTER, null) {
				private static final long serialVersionUID = 1L;

				@Override
				public void handleAction(Object sender, Object target) {
					doLogin();
				}
			});
			return this;
		}
		
		public Component layout() {
			root.addComponent(panel);
			root.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
			
			FormLayout loginLayout = new FormLayout();
			loginLayout.addComponent(username);
			loginLayout.addComponent(passwordField);
			
			loginLayout.addComponent(new HorizontalLayout(loginBtn, signupBtn));
			loginLayout.setSizeUndefined();
			loginLayout.setMargin(true);
			
			loginBtn.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					doLogin();
				}
			});
			
			signupBtn.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().getPage().setLocation(Configuration.PATH + "signup");
				}
			});
			
			panel.setContent(loginLayout);
			return root;
		}

		private void doLogin() {
			try {
				if(checkFields()) {
					Authentication auth = new UsernamePasswordAuthenticationToken(username.getValue(), passwordField.getValue());
					Authentication authenticated = daoAuthenticationProvider.authenticate(auth);
					SecurityContextHolder.getContext().setAuthentication(authenticated);
					UI.getCurrent().getPage().setLocation("/");
				}
			} catch (AuthenticationException e) {
				Notification.show(e.getMessage());
			}
		}
		
		private boolean checkFields() {
			try {
				if(username.getValue().isEmpty()) {
					throw new Exception("Не указан логин");
				} else if(passwordField.getValue().isEmpty()) {
					throw new Exception("Не указан пароль");
				}
				if(!userController.isUserExists(username.getValue())) {
					throw new Exception("Пользователь с таким логином не существует!");
				}
			} catch (Exception e) {
				Notification.show("Ошибка", e.getMessage(), Type.ERROR_MESSAGE);
				return false;
			}
			return true;
		}
		
	}

	public Component createComponent() {
		return new LoginForm().init().layout();
	}
}
