package pl.tukanmedia.scrooge.ui.views;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import pl.tukanmedia.scrooge.enums.OperationType;
import pl.tukanmedia.scrooge.helper.Refreshable;
import pl.tukanmedia.scrooge.model.controller.EntryController;
import pl.tukanmedia.scrooge.model.controller.EntryTypeController;
import pl.tukanmedia.scrooge.model.entity.Entry;
import pl.tukanmedia.scrooge.model.entity.EntryType;
import pl.tukanmedia.scrooge.ui.MainUI;

public class AddEditEntryWindow extends Window implements ClickListener {
	private static final long serialVersionUID = 1L;

	private EntryController entryController;
	private EntryTypeController entryTypeController;
	private DateField date;
	private TextField amount;
	private ComboBox<EntryType> entryType;
	private TextField description;
	private Button saveBtn;
	private Button clearBtn;
	private Button removeBtn;
	private Binder<Entry> binder;
	private Entry entry;
	private VerticalLayout root;
	private MainUI UI;
	private RadioButtonGroup<OperationType> typeGroup;
	
	public AddEditEntryWindow(EntryController entryService, EntryTypeController entryTypeService, MainUI UI) {
		this.entryController = entryService;
		this.entryTypeController = entryTypeService;
		entry = new Entry();
		this.UI = UI;
		init();
	}
	
	public AddEditEntryWindow(Entry entry, EntryController entryService, EntryTypeController entryTypeService, UI UI) {
		this.entryController = entryService;
		this.entryTypeController = entryTypeService;
		this.entry = entry;
		this.UI = (MainUI)UI;
		init();
	}
	
	private void init() {
		setWindowSettings();
		layout();
		bind();
	}
	
	private void setWindowSettings() {
		String title = entry.getId() == null ? "Dodaj nowy wpis" : "Edytuj wpis";
		setCaption(title);
		center();
		setSizeUndefined();
		setClosable(true);
		setResizable(false);
	}
	
	private void layout() {
		root = new VerticalLayout();
		date = new DateField("Data");
		typeGroup = new RadioButtonGroup<>();
		typeGroup.setItems(OperationType.getAll());
		if(entry.getEntryType() != null && entry.getEntryType().getSign().equals("+")) {
			typeGroup.setSelectedItem(OperationType.INCOME);
		} else {
			typeGroup.setSelectedItem(OperationType.LOSS);
		}		
		typeGroup.setItemCaptionGenerator(item -> item.getDescription());
		entryType = new ComboBox<>("Typ");
		description = new TextField("Opis");
		amount = new TextField("Kwota");
		String title = entry.getId() == null ? "Dodaj" : "Zmień";
		saveBtn = new Button(title, this);
		clearBtn = new Button("Wyczyść", this);
		removeBtn = new Button("Usuń", this);
		
		root.addComponent(date);
		root.addComponent(typeGroup);
		root.addComponent(entryType);
		root.addComponent(description);
		root.addComponent(amount);
		if(entry.getId()==null) {
			root.addComponent(new HorizontalLayout(saveBtn, clearBtn));
		} else {
			root.addComponent(new HorizontalLayout(saveBtn, removeBtn));
		}
		
		typeGroup.addSelectionListener(e-> reloadEntryTypes());
		entryType.setEmptySelectionAllowed(false);
		reloadEntryTypes();
		saveBtn.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		clearBtn.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		removeBtn.setStyleName(ValoTheme.BUTTON_DANGER);
		setContent(root);
	}
	
	private void reloadEntryTypes() {
		entryType.clear();
		entryType.setItems(entryTypeController.getAllBySign(typeGroup.getValue()));
	}

	public void bind() {
		binder = new Binder<>(Entry.class);
		binder.setBean(entry);	
		binder.forMemberField(amount).withConverter(new StringToBigDecimalConverter("Wprowadzona wartość nie jest liczbą"));
		binder.forMemberField(date).withConverter(new LocalDateToDateConverter());
		binder.bindInstanceFields(this);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(event.getSource() == this.saveBtn) {
			save();
			close();
		} else if(event.getSource() == this.removeBtn) {
			delete(entry.getId());
			close();
		}
		clearFields();
	}
	
	private void save() {
		try {
			binder.writeBean(entry);
			entryController.saveOrUpdateEntry(entry);
			Notification.show("Zapisano");
			refreshUI();
			
		} catch (ValidationException e) {
			Notification.show(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void delete(Long id) {
		entryController.delete(id);
		Notification.show("Wpis został usunięty");
		refreshUI();
	}
	
	private void clearFields() {
		date.clear();
		amount.clear();
		description.clear();
	}
	
	private void refreshUI() {
		if(UI.getCurrentView() != null && UI.getCurrentView() instanceof Refreshable) {
			((Refreshable)UI.getCurrentView()).refresh();
		}
	}

}