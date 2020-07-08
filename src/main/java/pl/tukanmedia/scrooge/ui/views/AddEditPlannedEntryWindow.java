package pl.tukanmedia.scrooge.ui.views;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
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
import pl.tukanmedia.scrooge.model.controller.EntryTypeController;
import pl.tukanmedia.scrooge.model.controller.PlannedEntryController;
import pl.tukanmedia.scrooge.model.entity.EntryType;
import pl.tukanmedia.scrooge.model.entity.PlannedEntry;
import pl.tukanmedia.scrooge.ui.MainUI;

public class AddEditPlannedEntryWindow extends Window implements ClickListener {

	private static final long serialVersionUID = 1L;

	private PlannedEntryController plannedEntryController;
	private EntryTypeController entryTypeController;
	private TextField amount;
	private ComboBox<EntryType> entryType;
	private PlannedEntry plannedEntry;
	private Binder<PlannedEntry> binder;
	private TextField description;
	private VerticalLayout root;
	private Button saveBtn;
	private Button clearBtn;
	private Button removeBtn;
	private MainUI UI;
	private RadioButtonGroup<OperationType> typeGroup;
	
	//dodanie nowego ogolnego
	public AddEditPlannedEntryWindow(PlannedEntryController plannedEntryController, EntryTypeController entryTypeController, UI UI) {
		this.plannedEntryController = plannedEntryController;
		this.entryTypeController = entryTypeController;
		this.UI = (MainUI)UI;
		plannedEntry = new PlannedEntry();
		init();
	}
	
	//dodanie nowego konkretnego dla daty
	public AddEditPlannedEntryWindow(PlannedEntryController plannedEntryController, EntryTypeController entryTypeController, UI UI, Long month, Long year) {
		this(plannedEntryController, entryTypeController, UI);
		plannedEntry.setMonth(month);
		plannedEntry.setYear(year);
		init();
	}
	
	//edycja (daty sie nie zmienia)
	public AddEditPlannedEntryWindow(PlannedEntry plannedEntry, PlannedEntryController plannedEntryController, EntryTypeController entryTypeController, UI UI) {
		this(plannedEntryController, entryTypeController, UI);
		this.plannedEntry = plannedEntry;
		init();
	}
	
	private void init() {
		setWindowSettings();
		layout();
		bind();
	}
	
	private void layout() {
		entryType = new ComboBox<>("Статья");
		typeGroup = new RadioButtonGroup<>();
		typeGroup.setItems(OperationType.getAll());
		typeGroup.setSelectedItem(OperationType.LOSS);
		typeGroup.setItemCaptionGenerator(item -> item.getDescription());
		description = new TextField("Заказ");
		amount = new TextField("Сумма С НДС");
		String title = plannedEntry.getId() == null ? "Добавить" : "Изменить";
		saveBtn = new Button(title, this);
		clearBtn = new Button("Очистить", this);
		removeBtn = new Button("Удалить", this);
		
		root = new VerticalLayout();
		root.addComponent(entryType);
		root.addComponent(typeGroup);
		root.addComponent(description);
		root.addComponent(amount);
		if(plannedEntry.getId()==null) {
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
	
	private void clear() {
		amount.setValue("0");
		description.clear();
	}

	private void save() {
		try {
			binder.writeBean(plannedEntry);
			plannedEntryController.save(plannedEntry);
			Notification.show("Запись добавлено");
			refreshUI();
			close();
		} catch (ValidationException e) {
			Notification.show(e.getMessage());
			e.printStackTrace();
		}
	}

	private void reloadEntryTypes() {
		entryType.clear();
		entryType.setItems(entryTypeController.getAllBySign(typeGroup.getValue()));
	}
	
	public void bind() {
		binder = new Binder<>(PlannedEntry.class);
		binder.setBean(plannedEntry);	
		binder.forMemberField(amount).withConverter(new StringToBigDecimalConverter("Стоимость не правильная"));
		binder.bindInstanceFields(this);
	}
	
	private void setWindowSettings() {
		String title = plannedEntry.getId() == null ? "Добавить запись" : "Редактировать запись";
		setCaption(title);
		center();
		setSizeUndefined();
		setClosable(true);
		setResizable(false);
	}
	
	private void refreshUI() {
		if(UI.getCurrentView() != null && UI.getCurrentView() instanceof Refreshable) {
			((Refreshable)UI.getCurrentView()).refresh();
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(event.getSource() == this.saveBtn) {
			save();
			close();
		} else if(event.getSource() == this.removeBtn) {
			delete(plannedEntry.getId());
			close();
		}
		clear();
	}
	
	private void delete(Long id) {
		plannedEntryController.delete(id);
		Notification.show("Запись удалена");
		refreshUI();
	}

}
