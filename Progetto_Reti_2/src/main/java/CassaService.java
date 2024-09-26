
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class CassaService {
    private double parziale;
    private double totale;
    private final double CASSA_MAX = 1000.0; // Esempio di limite massimo
    private final MqttClient mqttClient;

    public CassaService() throws MqttException {
        this.parziale = 0;
        this.totale = 0;
        MemoryPersistence persistence = new MemoryPersistence();
        this.mqttClient = new MqttClient("tcp://localhost:1883", "CassaService", persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        this.mqttClient.connect(connOpts);
        this.mqttClient.subscribe("cassa/+", this::messageArrived);
    }

    public void mantieneParziale() {
        // Qui implementiamo la logica per mantenere il parziale
        // Per ora, assumiamo che questo metodo semplicemente stampi il valore parziale
        System.out.println("Parziale attuale: " + parziale);
    }

    public void mantieneTotale() {
        // Qui implementiamo la logica per mantenere il totale
        // Per ora, assumiamo che questo metodo semplicemente stampi il valore totale
        System.out.println("Totale in cassa: " + totale);
    }

    public double calcolaResto(double importo) {
        if (importo <= parziale) {
            double resto = parziale - importo;
            parziale = 0;
            totale += importo;
            return resto;
        } else {
            return -1; // Indica che non c'è abbastanza credito
        }
    }

    public void erogaMonete(double importo) {
        if (importo <= parziale) {
            parziale -= importo;
            System.out.println("Erogazione di " + importo + " euro");
        } else {
            System.out.println("Errore: credito insufficiente");
        }
    }

    public void notificaCassaPiena() {
        if (totale >= CASSA_MAX) {
            try {
                mqttClient.publish("cassa/piena", new MqttMessage("Cassa piena".getBytes()));
                System.out.println("Notifica inviata: Cassa piena");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        switch(topic) {
            case "cassa/aggiungiCredito":
                double credito = Double.parseDouble(payload);
                parziale += credito;
                System.out.println("Credito aggiunto: " + credito);
                break;
            case "cassa/prelevaTotale":
                double prelevato = totale;
                totale = 0;
                try {
                    mqttClient.publish("cassa/totalePrelevato", new MqttMessage(String.valueOf(prelevato).getBytes()));
                    System.out.println("Totale prelevato: " + prelevato);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // Metodo main per testare la classe
    public static void main(String[] args) {
        try {
            CassaService cassa = new CassaService();
            
            // Simuliamo alcune operazioni
            cassa.mantieneParziale();
            cassa.mantieneTotale();
            
            // Aggiungiamo del credito
            cassa.messageArrived("cassa/aggiungiCredito", new MqttMessage("5.0".getBytes()));
            
            cassa.mantieneParziale();
            
            // Calcoliamo il resto
            double resto = cassa.calcolaResto(3.0);
            System.out.println("Resto calcolato: " + resto);
            
            cassa.mantieneParziale();
            cassa.mantieneTotale();
            
            // Eroghiamo delle monete
            cassa.erogaMonete(1.5);
            
            cassa.mantieneParziale();
            
            // Simuliamo il riempimento della cassa
            for (int i = 0; i < 200; i++) {
                cassa.messageArrived("cassa/aggiungiCredito", new MqttMessage("5.0".getBytes()));
                cassa.calcolaResto(5.0);
            }
            
            cassa.notificaCassaPiena();
            cassa.mqttClient.disconnect();
            System.out.println("Disconnesso dal broker MQTT");
            
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}