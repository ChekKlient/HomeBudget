package pl.tukanmedia.scrooge.ui.views;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;

import pl.tukanmedia.scrooge.enums.Entries;
import pl.tukanmedia.scrooge.helper.Refreshable;
import pl.tukanmedia.scrooge.model.controller.EntryController;
import pl.tukanmedia.scrooge.model.controller.EntryTypeController;
import pl.tukanmedia.scrooge.model.entity.Entry;

@SpringView(name=ShowAllEntriesView.NAME)
public class ShowAllEntriesView extends VerticalLayout implements View, Refreshable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "showAll";
	
	private Grid<Entry> table;
	private List<Entry> list;
	private Entries currentTable; 
	
	@Autowired
	private EntryController entryController;
	@Autowired
	private EntryTypeController entryTypeController; 
	
    void init() {
    	list = entryController.getLast10();
    	currentTable = Entries.GET_LAST_10;
    	
    	VerticalLayout layout = new VerticalLayout();
    	layout.setMargin(false);
    	layout.setSizeFull();
    	
    	table = new Grid<>(Entry.class);
    	table.setItems(list);
    	table.setColumnOrder("date", "entryType", "description", "amount");
    	table.getColumn("date").setCaption("Data zdarzenia");
    	table.getColumn("entryType").setCaption("Rodzaj wpisu");
    	table.getColumn("description").setCaption("Opis");
    	table.getColumn("amount").setCaption("Kwota");
    	table.removeColumn("id");
    	table.removeColumn("user");
    	table.setSizeFull();

    	table.addItemClickListener(new ItemClickListener<Entry>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClick<Entry> event) {
				if(event.getMouseEventDetails().isDoubleClick()) {
					if (UI.getCurrent().getWindows().isEmpty()) {
						AddEditEntryWindow window = new AddEditEntryWindow(event.getItem(), entryController, entryTypeController, UI.getCurrent());
						UI.getCurrent().addWindow(window);
					} else {
						Notification.show("Okno jest już otwarte", Notification.Type.WARNING_MESSAGE);
					}
				}
			}
    	});
    	
    	layout.addComponent(createButtonsLayout());
    	layout.addComponent(table);
    	layout.setExpandRatio(table, 1.0f);
        addComponent(layout);
        setSizeFull();
    }
    
    private Layout createButtonsLayout() {
    	Layout buttonsLayout = new HorizontalLayout();
    	Button allBtn = new Button("Wszystkie", e->refreshTable(Entries.GET_ALL));
    	Button yearBtn = new Button("Aktualny rok", e->refreshTable(Entries.GET_CURRENT_YEAR));
    	Button monthBtn = new Button("Aktualny miesiąc", e->refreshTable(Entries.GET_CURRENT_MONTH));
    	Button last10Btn = new Button("Ostatnie 10", e->refreshTable(Entries.GET_LAST_10));
    	buttonsLayout.addComponent(allBtn);
    	buttonsLayout.addComponent(yearBtn);
    	buttonsLayout.addComponent(monthBtn);
    	buttonsLayout.addComponent(last10Btn);
    	return buttonsLayout;
    }
    
    private void refreshTable(Entries entryType) {
    	currentTable = entryType;
    	switch (entryType) {
		case GET_ALL:
			list = entryController.getAll();
			break;
		case GET_CURRENT_YEAR:
			list = entryController.getAllFromCurrentYear();
			break;
		case GET_CURRENT_MONTH:
			list = entryController.getAllFromCurrentMonth();
			break;
		case GET_LAST_10:
			list = entryController.getLast10();
		}
    	table.setItems(list);
    }
    
	@Override
	public void enter(ViewChangeEvent event) {
		init();
	}

	@Override
	public void refresh() {
		refreshTable(currentTable);
	}

}
