package pl.tukanmedia.scrooge.ui.views;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;

import pl.tukanmedia.scrooge.enums.Months;
import pl.tukanmedia.scrooge.enums.OperationType;
import pl.tukanmedia.scrooge.helper.Refreshable;
import pl.tukanmedia.scrooge.model.controller.EntryController;
import pl.tukanmedia.scrooge.model.controller.EntryTypeController;
import pl.tukanmedia.scrooge.model.entity.Entry;
import pl.tukanmedia.scrooge.model.entity.EntryFilter;
import pl.tukanmedia.scrooge.model.entity.EntryType;

@SpringView(name=SummaryView.NAME)
public class SummaryView extends VerticalLayout implements View, Refreshable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "summary";

	private Grid<Entry> table;	
	private List<Entry> list;
	
	private DateField dateFrom;
	private DateField dateTo;
	private ListSelect<EntryType> entryTypeList;
	private TextField amountFrom;
	private TextField amountTo;
	private TextField description;
	
	private VerticalLayout content;
	private Layout filterPanel;
	private Integer selectedYear;
	private RadioButtonGroup<OperationType> typeGroup;
	
	@Autowired
	private EntryController entryController;
	@Autowired
	private EntryTypeController entryTypeController; 
	
	@Override
	public void enter(ViewChangeEvent event) {
		setMargin(true);
		content = new VerticalLayout();
		filterPanel = prepareFilterPanel();
		selectedYear = LocalDate.now().getYear();
		prepareTable();
		loadListWithCondition();
		rebuildComponents();
	}

	private Layout prepareYearChangePanel() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Button("Poprzedni", e->changeYear(-1)));
		layout.addComponent(new Label(selectedYear.toString()));
		layout.addComponent(new Button("Następny", e->changeYear(1)));
		return layout;
	}
	
	private void changeYear(int number) {
		selectedYear += number;
		refresh();
	}
	
	private void loadListWithCondition() {
		EntryFilter obj = new EntryFilter();
		obj.setDateFrom(dateFrom.isEmpty() ? null : dateFrom.getValue());
		obj.setDateTo(dateTo.isEmpty() ? null : dateTo.getValue());
		obj.setAmountFrom(amountFrom.isEmpty() ? null : BigDecimal.valueOf(Long.valueOf(amountFrom.getValue())));
		obj.setAmountTo(amountTo.isEmpty() ? null : BigDecimal.valueOf(Long.valueOf(amountTo.getValue())));
		obj.setDescription(description.isEmpty() ? null : description.getValue());
		
		if(entryTypeList.getSelectedItems().isEmpty()) {
			obj.setEntryTypes(entryTypeController.getAllBySign(typeGroup.getValue()));			
		} else {
			obj.setEntryTypes(entryTypeList.getSelectedItems().stream().collect(Collectors.toList()));
		}
		list = entryController.getAllWithCondition(obj);
		table.setItems(list);
	}
	
	private void prepareTable() {
    	table = new Grid<>(Entry.class);
    	table.setSizeFull();
    	table.setHeight("800px");
		table.setColumnOrder("date", "entryType", "description", "amount");
    	table.removeColumn("id");
    	table.removeColumn("user");
    	table.getColumn("date").setCaption("Data zdarzenia");
    	table.getColumn("entryType").setCaption("Rodzaj wpisu");
    	table.getColumn("description").setCaption("Opis");
    	table.getColumn("amount").setCaption("Kwota");
    	table.addItemClickListener(new ItemClickListener<Entry>(){
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
	}

	private Layout prepareMonthSummaryPanel() {
		GridLayout root = new GridLayout(12,1);
		root.setSizeFull();
		root.setSpacing(true);
		root.setMargin(false);
		for(Months m : Months.values()) {
			root.addComponent(createMonthPanel(m));
		}
		return root;
	}
	
	private Layout prepareTypeSummaryPanelIncomeCost(OperationType operationType) {
		GridLayout root = new GridLayout(8,3);
		root.setCaption(operationType.getDescription());
		root.setResponsive(true);
		root.setSizeFull();
		root.setSpacing(true);
		root.setMargin(false);
		
		List<EntryType> types = entryTypeController.getAllBySign(operationType);

		for(EntryType t : types) {
			root.addComponent(createTypePanel(t));
		}
		return root;
	}

	private Layout prepareBalanceSummaryPanel() {
		GridLayout root = new GridLayout(6,1);
		root.setSizeFull();
		root.setSpacing(true);
		root.setMargin(false);

		root.addComponent(createBalancePanel());
		
		return root;
	}
	
	private LocalDate getFirstDayOfYear() {
		LocalDate date = LocalDate.now();		
		return date.withDayOfYear(1);
	}
	
	private Layout prepareFilterPanel() {
		VerticalLayout mainLayout = new VerticalLayout();
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.addComponent(new Label("Filtrowanie"));
		
		GridLayout searchLayout = new GridLayout(4, 4); 
		searchLayout.setSpacing(true);
		searchLayout.setSizeFull();
		
		typeGroup = new RadioButtonGroup<>("Rodzaj wpisu");
		typeGroup.setItems(OperationType.values());
		typeGroup.setSelectedItem(OperationType.ALL);
		
		typeGroup.addSelectionListener(e-> reloadEntryTypes());
		
		typeGroup.setSizeUndefined();
		typeGroup.setItemCaptionGenerator(item -> item.getDescription());
		dateFrom = new DateField("Data od", getFirstDayOfYear());
		dateFrom.setSizeFull();
		dateTo = new DateField("Data do", LocalDate.now());
		dateTo.setSizeFull();
		entryTypeList = new ListSelect<>("Lista typów");
		reloadEntryTypes();
		entryTypeList.setSizeFull();
		amountFrom = new TextField("Kwota od");
		amountFrom.setSizeFull();
		amountTo = new TextField("Kwota do");
		amountTo.setSizeFull();
		description = new TextField("Opis");
		description.setSizeFull();
		Button searchBtn = new Button("Szukaj", e->refresh());
		searchBtn.setIcon(VaadinIcons.SEARCH);
		Button clearBtn = new Button("Wyczyść", e->clearFilters());
		clearBtn.setIcon(VaadinIcons.ERASER);
		
		searchLayout.addComponent(dateFrom, 0, 0);
		searchLayout.addComponent(dateTo, 1, 0);
		searchLayout.addComponent(typeGroup, 2, 0, 2, 1);
		searchLayout.addComponent(entryTypeList, 3, 0, 3, 2);
		searchLayout.addComponent(amountFrom, 0, 1);
		searchLayout.addComponent(amountTo, 1, 1);
		searchLayout.addComponent(description, 0, 2, 1, 2);
		
		searchLayout.setColumnExpandRatio(0, 1);
		searchLayout.setColumnExpandRatio(1, 1);
		searchLayout.setColumnExpandRatio(3, 1);
		
		HorizontalLayout buttonsLayout = new HorizontalLayout(searchBtn, clearBtn);

		mainLayout.addComponent(headerLayout);
		mainLayout.addComponent(searchLayout);
		mainLayout.addComponent(buttonsLayout);
		mainLayout.setComponentAlignment(headerLayout, Alignment.MIDDLE_CENTER);
		mainLayout.setComponentAlignment(searchLayout, Alignment.MIDDLE_CENTER);
		mainLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_CENTER);
		
		return mainLayout;
	}
	
	private void reloadEntryTypes() {
		entryTypeList.clear();
		entryTypeList.setItems(entryTypeController.getAllBySign(typeGroup.getValue()));
	}
	
	private void clearFilters() {
		dateFrom.clear();
		dateTo.clear();
		entryTypeList.clear();
		amountFrom.clear();
		amountTo.clear();
		description.clear();
	}

	private Component createBalancePanel() {
		Panel p = new Panel();
		Label nameLabel = new Label("Wybrany rok");
		Label incomeLabel = new Label();
		Label lossLabel = new Label();
		Label summaryLabel = new Label();
		BigDecimal income = calculateBalanceSummary(OperationType.INCOME);
		BigDecimal loss = calculateBalanceSummary(OperationType.LOSS);
		
		incomeLabel.setValue(income.toString());
		lossLabel.setValue(loss.toString());
		summaryLabel.setValue((income.subtract(loss)).toString());
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(nameLabel);
		layout.addComponent(incomeLabel);
		layout.addComponent(lossLabel);
		Label line = new Label("<hr>", ContentMode.HTML);
		line.setSizeFull();
		layout.addComponent(line);
		layout.addComponent(summaryLabel);
		p.setContent(layout);
		return p;
	}
	
	@SuppressWarnings("deprecation")
	private BigDecimal calculateBalanceSummary(OperationType t) {
		BigDecimal sum = BigDecimal.ZERO;
		for (Entry entry : list) {		
			if(entry.getEntryType().getSign().equals(t.getSign()) && entry.getDate().getYear()+1900 == selectedYear) {			
				sum = sum.add(entry.getAmount());
			}
		}
		return sum;
	}

	private Panel createTypePanel(EntryType t) {
		Panel p = new Panel();
		Label nameLabel = new Label(t.getDescription());
		Label summaryLabel = new Label();
		summaryLabel.setValue(calculateTypeSummary(t).toString());
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(nameLabel);
		layout.addComponent(summaryLabel);
		p.setContent(layout);
		return p;
	}
	
	@SuppressWarnings("deprecation")
	private BigDecimal calculateTypeSummary(EntryType t) {
		BigDecimal sum = BigDecimal.ZERO;
		for (Entry entry : list) {
			if(entry.getEntryType().getId().equals(t.getId()) && entry.getDate().getYear()+1900 == selectedYear) {
				sum = sum.add(entry.getAmount());
			}
		}
		return sum;
	}
	
	private Panel createMonthPanel(Months m) {
		Panel p = new Panel();
		Label nameLabel = new Label(m.getName());
		Label incomeLabel = new Label();
		Label lossLabel = new Label("Strata");
		Label summaryLabel = new Label("Suma");
		BigDecimal income = calculateMonthSummary(m, OperationType.INCOME);
		BigDecimal loss = calculateMonthSummary(m, OperationType.LOSS);
		
		incomeLabel.setValue(income.toString());
		lossLabel.setValue(loss.toString());
		summaryLabel.setValue((income.subtract(loss)).toString());
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(nameLabel);
		layout.addComponent(incomeLabel);
		layout.addComponent(lossLabel);
		Label line = new Label("<hr>", ContentMode.HTML);
		line.setSizeFull();
		layout.addComponent(line);
		layout.addComponent(summaryLabel);
		p.setContent(layout);
		return p;
	}
	
	@SuppressWarnings("deprecation")
	private BigDecimal calculateMonthSummary(Months m, OperationType type) {
		BigDecimal sum = BigDecimal.ZERO;
		for (Entry entry : list) {
			if(entry.getDate().getMonth()+1 == m.getId() && entry.getDate().getYear()+1900 == selectedYear) {
				if(entry.getEntryType().getSign().equals(type.getSign())) {
					sum = sum.add(entry.getAmount());
				}
			}
		}
		return sum;
	}

	@Override
	public void refresh() {
		loadListWithCondition();
		rebuildComponents();
	}
	
	private void rebuildComponents() {
		content.removeAllComponents();
		content.addComponent(filterPanel);
		content.addComponent(prepareYearChangePanel());
		content.addComponent(prepareMonthSummaryPanel());
		content.addComponent(prepareTypeSummaryPanelIncomeCost(OperationType.INCOME));
		content.addComponent(prepareTypeSummaryPanelIncomeCost(OperationType.LOSS));
		content.addComponent(prepareBalanceSummaryPanel());
		content.addComponent(table);
		removeAllComponents();
		addComponent(content);
	}
}

