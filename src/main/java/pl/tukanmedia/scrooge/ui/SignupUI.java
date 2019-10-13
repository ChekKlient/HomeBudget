package pl.tukanmedia.scrooge.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

@SpringUI(path=SignupUI.NAME)
@Theme("valo")
public class SignupUI extends UI {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "signup";
	
	@Autowired
	private SignupFormFactory signupFormFactory;
	
	@Override
	protected void init(VaadinRequest request) {
		setContent(signupFormFactory.createComponent());
	}
}
