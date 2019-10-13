package pl.tukanmedia.scrooge.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = ViewScopedView.NAME)
public class ViewScopedView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "";

    void init() {
        addComponent(new Label("Start point"));
    }

    @Override
    public void enter(ViewChangeEvent event) {
        init();
    }
}