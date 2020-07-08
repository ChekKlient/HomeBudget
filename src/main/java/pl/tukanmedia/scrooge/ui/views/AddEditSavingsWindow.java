package pl.tukanmedia.scrooge.ui.views;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.UI;
import pl.tukanmedia.scrooge.helper.Refreshable;
import pl.tukanmedia.scrooge.model.controller.SavingsController;
import pl.tukanmedia.scrooge.model.entity.Savings;
import pl.tukanmedia.scrooge.ui.MainUI;

public class AddEditSavingsWindow extends Window implements ClickListener {
	private static final long serialVersionUID = 1L;

	private SavingsController savingsController;
	private TextField name;
	private TextField amount;
	private Button saveBtn;
	private Button clearBtn;
	private Button removeBtn;
	private Binder<Savings> binder;
	private Savings element;
	private VerticalLayout root;
	private MainUI UI;
	
	public AddEditSavingsWindow(SavingsController savingsController, UI UI) {
		this.savingsController = savingsController;
		element = new Savings();
		this.UI = (MainUI) UI;
		init();
	}

	public AddEditSavingsWindow(Savings element, SavingsController savingsController, UI UI) {
		this.savingsController = savingsController;
		this.element = element;
		this.UI = (MainUI) UI;
		init();
	}
	
	private void init() {
		setWindowSettings();
		layout();
		bind();
	}

	private void setWindowSettings() {
		String title = element.getId() == null ? "Добавить" : "Удалить";
		setCaption(title);
		center();
		setSizeUndefined();
		setClosable(true);
		setResizable(false);
	}
	
	private void bind() {
		binder = new Binder<>(Savings.class);
		binder.setBean(element);	
		binder.forMemberField(amount).withConverter(new StringToBigDecimalConverter("Сумма не правильная!"));
		binder.bindInstanceFields(this);
	}

	private void layout() {
		root = new VerticalLayout();
		name = new TextField("Заказ");
		amount = new TextField("Сумма С НДС");
		String title = element.getId() == null ? "Добавить" : "Удалить";
		saveBtn = new Button(title, this);
		clearBtn = new Button("Очистить", this);
		removeBtn = new Button("Удалить", this);
		
		root.addComponent(name);
		root.addComponent(amount);
		if(element.getId()==null) {
			root.addComponent(new HorizontalLayout(saveBtn, clearBtn));
		} else {
			root.addComponent(new HorizontalLayout(saveBtn, removeBtn));
		}
		saveBtn.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		clearBtn.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		removeBtn.setStyleName(ValoTheme.BUTTON_DANGER);
		setContent(root);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(event.getSource() == this.saveBtn) {
			save();
			close();
		} else if(event.getSource() == this.removeBtn) {
			delete(element.getId());
			close();
		}
		clearFields();
	}
	
	private void save() {
		try {
			binder.writeBean(element);
			savingsController.saveOrUpdateEntry(element);
			Notification.show("Записано");
			refreshUI();
		} catch (ValidationException e) {
			Notification.show(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void delete(Long id) {
		savingsController.delete(id);
		Notification.show("Запись удалена");
		refreshUI();
	}
	
	private void clearFields() {
		name.clear();
		amount.clear();
	}

	private void refreshUI() {
		if(UI.getCurrentView() != null && UI.getCurrentView() instanceof Refreshable) {
			((Refreshable)UI.getCurrentView()).refresh();
		}
	}
	
}
