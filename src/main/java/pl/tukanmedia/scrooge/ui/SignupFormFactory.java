package pl.tukanmedia.scrooge.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import pl.tukanmedia.scrooge.helper.Configuration;
import pl.tukanmedia.scrooge.model.controller.RegisterUserController;

@org.springframework.stereotype.Component
public class SignupFormFactory {

	@Autowired
	private RegisterUserController registerUserController;
	
	private class SignupForm {
		
		private VerticalLayout root;
		private Panel panel;
		private TextField username;
		private PasswordField passwordField;
		private PasswordField passwordAgainField;
		private Button saveBtn;
		private Button backBtn;
		
		public SignupForm init() {
			root = new VerticalLayout();
			root.setMargin(true);
			root.setHeight("100%");
			
			panel = new Panel("Signup");
			panel.setSizeUndefined();
			
			username = new TextField("username");
			passwordField = new PasswordField("Password");
			passwordAgainField = new PasswordField("Password again");
			saveBtn = new Button("Save");
			saveBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			backBtn = new Button("Back");
			backBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
			
			saveBtn.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					if(!username.getValue().isEmpty()) {
						if(registerUserController.isUserExists(username.getValue())) {
							Notification.show("Uwaga", "Konto o takiej nazwie już istnieje w systemie", Type.WARNING_MESSAGE);
							return;
						}
					}					
					if(username.getValue().isEmpty() || passwordField.getValue().isEmpty() || passwordAgainField.getValue().isEmpty()) {
						Notification.show("Brak danych", "Nie wszystkie pola zostały wypełnione", Type.WARNING_MESSAGE);
						return;
					}
					if(!passwordField.getValue().equals(passwordAgainField.getValue())) {
						Notification.show("Error", "Podane hasła są różne", Type.ERROR_MESSAGE);
						return;
					}
					registerUserController.save(username.getValue(), passwordField.getValue());
					Notification.show("Utworzono nowe konto");
					UI.getCurrent().getPage().setLocation(Configuration.PATH + "login");
				}
			});
			
			
			backBtn.addClickListener(new ClickListener() {				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().getPage().setLocation(Configuration.PATH + "login");
				}
			});
			
			return this;
		}
		
		public Component layout() {
			root.addComponent(panel);
			root.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
			
			FormLayout signupLayout = new FormLayout();
			signupLayout.addComponent(username);
			signupLayout.addComponent(passwordField);
			signupLayout.addComponent(passwordAgainField);
			signupLayout.addComponent(new HorizontalLayout(saveBtn, backBtn));
			signupLayout.setSizeUndefined();
			signupLayout.setMargin(true);
			
			panel.setContent(signupLayout);
			
			return root;
		}
	}
	
	public Component createComponent() {
		return new SignupForm().init().layout();
	}

}
