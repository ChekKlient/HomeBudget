package pl.tukanmedia.scrooge.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import pl.tukanmedia.scrooge.helper.Configuration;
import pl.tukanmedia.scrooge.model.controller.EntryController;
import pl.tukanmedia.scrooge.model.controller.EntryTypeController;
import pl.tukanmedia.scrooge.ui.views.AddEditEntryWindow;
import pl.tukanmedia.scrooge.ui.views.PlannedEntriesView;
import pl.tukanmedia.scrooge.ui.views.ShowAllEntriesView;
import pl.tukanmedia.scrooge.ui.views.SummaryView;
import pl.tukanmedia.scrooge.ui.views.ViewScopedView;

@SpringUI
@SpringViewDisplay
@Title("Tytuł programu")
@Theme("scrooge")
public class MainUI extends UI implements ViewDisplay {

	private static final long serialVersionUID = 1L;
	private Panel menuBarPanel;
	private MenuBar menubar;
	private Panel contentPanel;

	@Autowired
	private EntryController entryService;
	@Autowired
	private EntryTypeController entryTypeService; 
	
    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setSizeFull();
        rootLayout.setMargin(false);
        setContent(rootLayout);

		menubar = new MenuBar();
		menubar.setWidthUndefined();
		MenuItem settingsItem = menubar.addItem("Użytkownik", null);
			settingsItem.addItem("Zmień hasło", VaadinIcons.PASSWORD, e->getUI().getNavigator().navigateTo(ViewScopedView.NAME));
			settingsItem.addItem("Wyloguj", VaadinIcons.SIGN_OUT, e->logout());
		menubar.addItem("Planowanie", VaadinIcons.LINE_BAR_CHART, e->getUI().getNavigator().navigateTo(PlannedEntriesView.NAME));
		menubar.addItem("Zestawienia", VaadinIcons.MODAL_LIST, e->getUI().getNavigator().navigateTo(ShowAllEntriesView.NAME));
		menubar.addItem("Przeglądanie", VaadinIcons.SEARCH, e->getUI().getNavigator().navigateTo(SummaryView.NAME));
		menubar.addItem("Dodaj nowy", event -> {
			if (UI.getCurrent().getWindows().isEmpty()) {
				AddEditEntryWindow window = new AddEditEntryWindow(entryService, entryTypeService, this);
				UI.getCurrent().addWindow(window);
			} else {
				Notification.show("Okno jest już otwarte", Notification.Type.WARNING_MESSAGE);
			}
		});
		
		menuBarPanel = new Panel();
		menuBarPanel.setWidth("100%");
		menuBarPanel.setContent(menubar);

        contentPanel = new Panel();
        contentPanel.setSizeFull();
        
        rootLayout.addComponent(menuBarPanel);
        rootLayout.addComponent(contentPanel);
        rootLayout.setExpandRatio(contentPanel, 1.0f);
    }

    private void logout() {
    	SecurityContextHolder.clearContext();
		UI.getCurrent().getPage().setLocation(Configuration.PATH + "login");
	}

	@Override
    public void showView(View view) {
		try {
			contentPanel.setContent((Component) view);
		} catch (NullPointerException e) {
			
		}
    }
	
    public View getCurrentView() {
    	if (contentPanel.getContent() instanceof View) {
    		return (View)contentPanel.getContent();    		
    	}
    	return null;
    }
}
