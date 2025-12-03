package Modul_07.Util;

import java.text.NumberFormat;
import java.util.Locale;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class FormatterHelper {

    public static <T> void setCurrencyFormat(TableColumn<T, Double> col) {
        col.setCellFactory(column -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    setText(currencyFormat.format(item));
                }
            }
        });
    }
}