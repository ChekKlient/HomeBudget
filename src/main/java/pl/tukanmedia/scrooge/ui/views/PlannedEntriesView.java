package pl.tukanmedia.scrooge.ui.views;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;

import pl.tukanmedia.scrooge.enums.OperationType;
import pl.tukanmedia.scrooge.enums.PlannedEntryType;
import pl.tukanmedia.scrooge.helper.Refreshable;
import pl.tukanmedia.scrooge.model.controller.EntryController;
import pl.tukanmedia.scrooge.model.controller.EntryTypeController;
import pl.tukanmedia.scrooge.model.controller.PlannedEntryController;
import pl.tukanmedia.scrooge.model.controller.SavingsController;
import pl.tukanmedia.scrooge.model.entity.EntryType;
import pl.tukanmedia.scrooge.model.entity.PlannedEntry;
import pl.tukanmedia.scrooge.model.entity.Savings;

@SpringView(name=PlannedEntriesView.NAME)
public class PlannedEntriesView extends VerticalLayout implements View, Refreshable{

	@Autowired
	private EntryController entryController;
	
	@Autowired
	private SavingsController savingsController;
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "planned";
	
	private List<PlannedEntry> predictionList;
	private List<PlannedEntry> monthList;
	private List<PlannedEntry> itemsOfOneType;
	private List<EntryType> lossTypes;
	private List<EntryType> profitTypes;
	private BigDecimal typeSum;
	private BigDecimal lossSum;
	private BigDecimal incomeSum;
	private BigDecimal realLossSum;
	private BigDecimal realIncomeSum;
	private Long year;
	private Long month;
	private Label monthPanelTitle;
	
	private HorizontalLayout rootPanel;
	private HorizontalLayout predictionsPanel;
	private HorizontalLayout monthPanel;
	private HorizontalLayout savingsPanel;
	private Grid<Savings> savingsTable;
	private List<Savings> savingsList;
	
	@Autowired
	private PlannedEntryController plannedEntryController;
	
	@Autowired
	private EntryTypeController entryTypeController; 
	
	@Override
	public void enter(ViewChangeEvent event) {
		init();
	}
	
	private void init() {
		loadEntryTypes();
		setMonthYear();
		monthPanelTitle = new Label("Harmonogram na " + month + "-" + year);
		rootPanel = new HorizontalLayout();
		predictionsPanel = new HorizontalLayout();
		monthPanel = new HorizontalLayout();
		savingsPanel = new HorizontalLayout();
		addComponent(rootPanel);
		refresh();
	}

	private void loadEntryTypes() {
		lossTypes = entryTypeController.getAllBySign(OperationType.LOSS);
		profitTypes = entryTypeController.getAllBySign(OperationType.INCOME);
	}
	
	@Override
	public void refresh() {
		refreshPredictionsPanel();
		refreshMonthPanel();
		refreshSavingsPanel();
	}
	
	private void setMonthYear() {
		year = new Long(LocalDate.now().getYear());
		month = new Long(LocalDate.now().getMonthValue());
	}
	
	private void refreshPredictionsPanel() {
		predictionList = plannedEntryController.getAllPredictions();
		predictionsPanel.removeAllComponents();
		buildPanel(predictionsPanel, predictionList, "Prognozowane miesięczne wydatki", PlannedEntryType.PREDICTION);
	}
	
	private void refreshMonthPanel() {
		monthList = plannedEntryController.getAllForDate(year, month);
		monthPanel.removeAllComponents();
		recalculateRealLossAndIncome();
		buildPanel(monthPanel, monthList, "Prognoza na " + month + "-" + year, PlannedEntryType.REAL);
		changeMonthPanelTitle();
	}
	
	private void refreshSavingsPanel() {
		savingsList = savingsController.getAllSavings();
		savingsPanel.removeAllComponents();
		buildSavingsPanel();
	}

	private void recalculateRealLossAndIncome() {
		realIncomeSum = entryController.getSumForTypeAndMonthAndYear(OperationType.INCOME, month, year);
		realLossSum = entryController.getSumForTypeAndMonthAndYear(OperationType.LOSS, month, year);
	}
	
	private void previousNextButtonsActions(Long value) {
		changeMonthYear(value);
		refreshMonthPanel();
	}
	
	private void changeMonthYear(Long value) {
		LocalDate date = LocalDate.of(year.intValue(), month.intValue(), 1);
		date = date.plusMonths(value.intValue());
		month = Long.valueOf(date.getMonthValue());
		year = Long.valueOf(date.getYear());
	}
	
	private void changeMonthPanelTitle() {
		monthPanelTitle.setValue("Wydatki na " + month + "-" + year);
	}
	
	private void copyPredictions() {
		for (PlannedEntry element : predictionList) {
			PlannedEntry newElement = element;
			newElement.setId(null);
			newElement.setMonth(month);
			newElement.setYear(year);
			plannedEntryController.save(newElement);
		}
		refreshMonthPanel();
	}
		
	private void saveItem(PlannedEntryType plannedEntryType) {
		if (UI.getCurrent().getWindows().isEmpty()) {
			AddEditPlannedEntryWindow window;
			if(plannedEntryType.equals(PlannedEntryType.REAL)) {
				window = new AddEditPlannedEntryWindow(plannedEntryController, entryTypeController, this.getUI(), month, year);
				refreshMonthPanel();
			} else {
				window = new AddEditPlannedEntryWindow(plannedEntryController, entryTypeController, this.getUI());
				refresh();
			}
			UI.getCurrent().addWindow(window);
		} else {
			Notification.show("Okno jest już otwarte", Notification.Type.WARNING_MESSAGE);
		}
	}
	
	private void addSavings() {
		if (UI.getCurrent().getWindows().isEmpty()) {
			AddEditSavingsWindow window = new AddEditSavingsWindow(savingsController, this.getUI());
			refresh();//???
			UI.getCurrent().addWindow(window);
		} else {
			Notification.show("Okno jest już otwarte", Notification.Type.WARNING_MESSAGE);
		}
	}

	private void buildSavingsPanel() {
		VerticalLayout layout = new VerticalLayout();
    	layout.setMargin(false);
    	layout.setWidth(100, Unit.PERCENTAGE);
    	layout.setHeightUndefined();
    	savingsTable = new Grid<>(Savings.class);
    	savingsTable.setItems(savingsList);
    	savingsTable.getColumn("name").setCaption("Nazwa konta");
    	savingsTable.getColumn("amount").setCaption("Kwota");
    	savingsTable.removeColumn("id");
    	savingsTable.removeColumn("user");
    	savingsTable.setColumnOrder("name", "amount");
    	savingsTable.setSizeFull();	
    	savingsTable.addItemClickListener(new ItemClickListener<Savings>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClick<Savings> event) {
				if(event.getMouseEventDetails().isDoubleClick()) {
					if (UI.getCurrent().getWindows().isEmpty()) {
						AddEditSavingsWindow window = new AddEditSavingsWindow(event.getItem(), savingsController, UI.getCurrent());
						UI.getCurrent().addWindow(window);
					} else {
						Notification.show("Okno jest już otwarte", Notification.Type.WARNING_MESSAGE);
					}
				}
			}
    	});
    	layout.addComponent(savingsTable);
    	
    	HorizontalLayout hl = new HorizontalLayout();
    	hl.addComponent(new Button("Dodaj nowy wpis", e -> addSavings()));
    	
    	//przerobić na strumień
    	BigDecimal sum = BigDecimal.ZERO;
    	for (Savings s : savingsList) {
			sum = sum.add(s.getAmount());
		}
    	//BigDecimal b = savingsList.stream().reduce((a,b) -> a.getAmount().add(b.getAmount())).get();
    	hl.addComponent(new Label("Suma: " + sum.toString()));
    	
    	layout.addComponent(hl);
    	Panel containerPanel = new Panel(layout);
		containerPanel.setCaption("Zgromadzone środki");
		savingsPanel.addComponent(containerPanel);
		rootPanel.addComponent(savingsPanel);
	}

	private void buildPanel(HorizontalLayout panel, List<PlannedEntry> list, String title, PlannedEntryType plannedEntryType) {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(buildHeader(plannedEntryType));
		layout.addComponent(new Label(OperationType.INCOME.getDescription()));
		layout.addComponent(buildContent(OperationType.INCOME,list));
		layout.addComponent(new Label(OperationType.LOSS.getDescription()));
		layout.addComponent(buildContent(OperationType.LOSS, list));
		layout.addComponent(buildFooter(plannedEntryType));
		Panel containerPanel = new Panel(layout);
		containerPanel.setCaption(title);
		containerPanel.setWidth(600.0f, Unit.PIXELS);
		panel.addComponent(containerPanel);
		rootPanel.addComponent(panel);
	}
	
	private HorizontalLayout buildHeader(PlannedEntryType plannedEntryType) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setHeight(40.0f, Unit.PIXELS);
		if(plannedEntryType.equals(PlannedEntryType.REAL)) {
			layout.addComponent(new Button("Poprzedni", e -> previousNextButtonsActions(-1L)));
			layout.addComponent(monthPanelTitle);
			layout.addComponent(new Button("Następny", e -> previousNextButtonsActions(1L)));
		}
		return layout;
	}
	
	private Accordion buildContent(OperationType type, List<PlannedEntry> list) {
		Accordion accordion = new Accordion();
		BigDecimal sum = BigDecimal.ZERO;
		for (EntryType entry : type.equals(OperationType.LOSS) ? lossTypes : profitTypes) {
			typeSum = BigDecimal.ZERO;
			itemsOfOneType = new ArrayList<>();
			for (PlannedEntry item : list) {
				if(item.getEntryType().getId().equals(entry.getId())) {
					itemsOfOneType.add(item);
					typeSum = typeSum.add(item.getAmount());
				}
			}
			Grid<PlannedEntry> table = buildTable4Accordeon();
            accordion.addTab(table, entry.getDescription() + " " + typeSum.toString() + " zł");
            sum = sum.add(typeSum);
		}
		
		if(type.equals(OperationType.INCOME)) {
			incomeSum = sum;
		} else {
			lossSum = sum;
		}
		return accordion;
	}
	
	private Panel createSummaryPanel(String title, BigDecimal income, BigDecimal loss) {
		Panel p = new Panel(title);
		Label incomeLabel = new Label();
		Label lossLabel = new Label();
		Label summaryLabel = new Label();
		incomeLabel.setValue(income.toString());
		lossLabel.setValue(loss.toString());
		summaryLabel.setValue((income.subtract(loss)).toString());
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(incomeLabel);
		layout.addComponent(lossLabel);
		Label line = new Label("<hr>", ContentMode.HTML);
		line.setSizeFull();
		layout.addComponent(line);
		layout.addComponent(summaryLabel);
		p.setContent(layout);
		return p;
	}
	
	private Grid<PlannedEntry> buildTable4Accordeon() {
		Grid<PlannedEntry> table = new Grid<>(PlannedEntry.class);
        table.setItems(itemsOfOneType);
        table.removeAllColumns();
        table.addColumn("description");
        table.addColumn("amount");
        table.removeHeaderRow(0);
        table.setHeightMode(HeightMode.UNDEFINED);
        table.setWidth(100.0f, Unit.PERCENTAGE);
        table.addItemClickListener(new ItemClickListener<PlannedEntry>(){
			private static final long serialVersionUID = 1L;
			@Override
			public void itemClick(ItemClick<PlannedEntry> event) {
				if(event.getMouseEventDetails().isDoubleClick()) {
					if (UI.getCurrent().getWindows().isEmpty()) {
						AddEditPlannedEntryWindow window = new AddEditPlannedEntryWindow(event.getItem(), plannedEntryController, entryTypeController, UI.getCurrent());	
						UI.getCurrent().addWindow(window);
					} else {
						Notification.show("Okno jest już otwarte", Notification.Type.WARNING_MESSAGE);
					}
				}
			}
    	});
		return table;
	}
	
	private HorizontalLayout buildFooter(PlannedEntryType plannedEntryType) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Button("Dodaj wpis", (e-> saveItem(plannedEntryType))));
		if(plannedEntryType.equals(PlannedEntryType.PREDICTION)) {
			layout.addComponent(new Button("Kopiuj na dany miesiąc", e -> copyPredictions()));
		}

		layout.addComponent(createSummaryPanel("Prognoza", incomeSum, lossSum));
		if(plannedEntryType.equals(PlannedEntryType.REAL)) {
			layout.addComponent(createSummaryPanel("Rzeczywiste wydatki", realIncomeSum, realLossSum));
		}
		layout.setSizeFull();
		return layout;
	}

}