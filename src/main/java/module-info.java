module com.tournamentmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;

    opens com.tournamentmanager to javafx.fxml;
    opens com.tournamentmanager.controller to javafx.fxml;
    opens com.tournamentmanager.model to javafx.fxml;

    exports com.tournamentmanager;
}