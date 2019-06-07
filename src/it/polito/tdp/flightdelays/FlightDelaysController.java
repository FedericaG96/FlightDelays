

/**
 * Sample Skeleton for 'ExtFlightDelays.fxml' Controller Class
 */

package it.polito.tdp.flightdelays;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
/**
 * Sample Skeleton for 'FlightDelays.fxml' Controller Class
 */



public class FlightDelaysController {
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="distanzaMinima"
    private TextField distanzaMinima; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalizza"
    private Button btnAnalizza; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoPartenza"
    private ComboBox<Airport> cmbBoxAeroportoPartenza; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoArrivo"
    private ComboBox<Airport> cmbBoxAeroportoArrivo; // Value injected by FXMLLoader

    @FXML // fx:id="btnAeroportiConnessi"
    private Button btnAeroportiConnessi; // Value injected by FXMLLoader

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {
    	txtResult.clear();
    	Integer distanzaInserita;
    	try {
    		distanzaInserita = Integer.parseInt(distanzaMinima.getText());
    	} catch (NumberFormatException e) {
    		txtResult.appendText("Inserire una distanza numerica! ");
    		return;
    	}
    	
    	model.creaGrafo(distanzaInserita);
    	txtResult.setText("Grafo creato!");
    	
    }

    @FXML
    void doTestConnessione(ActionEvent event) {
    	txtResult.clear();
    	
    	Airport partenza = cmbBoxAeroportoPartenza.getValue();
    	Airport arrivo = cmbBoxAeroportoArrivo.getValue();
    	
    	if(partenza == null|| arrivo == null) {
    		txtResult.appendText("Selezionare aeroporto di arrivo e di partenza \n");
    		return;
    	}
    	
    	
    	List<Airport> percorso = model.trovaPercorso(partenza.getId(), arrivo.getId());
    	if(percorso == null) {
    		txtResult.appendText("Non è possibile raggiungere l’aeroporto di arrivo a partire da quello di partenza.");
    	}
    	else
			txtResult.setText("I due aeroporti sono connessi dal seguente percorso: " + percorso);

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert distanzaMinima != null : "fx:id=\"distanzaMinima\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxAeroportoArrivo != null : "fx:id=\"cmbBoxAeroportoArrivo\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert btnAeroportiConnessi != null : "fx:id=\"btnAeroportiConnessi\" was not injected: check your FXML file 'FlightDelays.fxml'.";

    }
    
    public void setModel(Model model) {
		this.model = model;
		
		
		cmbBoxAeroportoPartenza.getItems().addAll(model.getAeroporti());
		cmbBoxAeroportoArrivo.getItems().addAll(model.getAeroporti());
	}
}




