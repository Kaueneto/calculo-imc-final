module org.imc.calculoimc {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.imc.calculoimc.model.entity to javafx.base;
    opens org.imc.calculoimc to javafx.fxml;
    opens org.imc.calculoimc.controller to javafx.fxml;

    exports org.imc.calculoimc;
    exports org.imc.calculoimc.controller;
}