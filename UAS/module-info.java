module Sporta {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;
    
	
	opens application to javafx.graphics, javafx.fxml;
	opens model to javafx.base;
	opens controller to javafx.fxml;
	opens view to javafx.graphics, javafx.fxml;
    exports application;
}
