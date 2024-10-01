package reti.erogatore;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.HashMap;
import java.util.Map;

public class ErogatoreService {
    private Map<String, Integer> consumabili;
    private final MqttClient mqttClient;
    private double parziale;

    public ErogatoreService() throws MqttException {
        this.consumabili = new HashMap<>();
        this.consumabili.put("caffe", 100);
        this.consumabili.put("zucchero", 1000);
        this.consumabili.put("bicchieri", 100);
        this.parziale = 0.0;
        
        MemoryPersistence persistence = new MemoryPersistence();
        this.mqttClient = new MqttClient("tcp://localhost:1883", "ErogatoreService", persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        this.mqttClient.connect(connOpts);
        this.mqttClient.subscribe("erogatore/+", this::messageArrived);
    }

    public Map<String, Integer> verificaConsumabili() {
        return new HashMap<>(consumabili);
    }

    public double verificaParziale() {
        return parziale;
    }

    public void erogaBevanda(String tipo, int quantita) {
        if (consumabili.getOrDefault(tipo, 0) >= quantita && consumabili.get("bicchieri") > 0) {
            consumabili.put(tipo, consumabili.get(tipo) - quantita);
            consumabili.put("bicchieri", consumabili.get("bicchieri") - 1);
            inviaMessaggioFineErogazione();
        } else {
            inviaMessaggioErrore("Consumabili insufficienti per " + tipo);
        }
    }

    public void inviaMessaggioErrore(String messaggio) {
        try {
            mqttClient.publish("erogatore/errore", new MqttMessage(messaggio.getBytes()));
            System.out.println("Errore: " + messaggio);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void inviaMessaggioFineErogazione() {
        try {
            mqttClient.publish("erogatore/fineErogazione", new MqttMessage("Bevanda erogata".getBytes()));
            System.out.println("Bevanda erogata con successo");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        switch(topic) {
            case "erogatore/richiestaBevanda":
                String[] parts = payload.split(",");
                if (parts.length == 2) {
                    erogaBevanda(parts[0], Integer.parseInt(parts[1]));
                } else {
                    inviaMessaggioErrore("Formato richiesta non valido");
                }
                break;
            case "erogatore/aggiornaParziale":
                parziale = Double.parseDouble(payload);
                System.out.println("Parziale aggiornato: " + parziale);
                break;
            case "erogatore/rifornisci":
                String[] rifornimento = payload.split(",");
                if (rifornimento.length == 2) {
                    consumabili.put(rifornimento[0], Integer.parseInt(rifornimento[1]));
                    System.out.println("Rifornito " + rifornimento[0] + " con quantità " + rifornimento[1]);
                } else {
                    inviaMessaggioErrore("Formato rifornimento non valido");
                }
                break;
        }
    }

    public static void main(String[] args) {
        try {
            ErogatoreService erogatore = new ErogatoreService();
            
            // Simuliamo alcune operazioni
            System.out.println("Stato iniziale consumabili: " + erogatore.verificaConsumabili());
            
            // Simuliamo una richiesta di bevanda
            erogatore.messageArrived("erogatore/richiestaBevanda", new MqttMessage("caffe,1".getBytes()));
            
            System.out.println("Stato consumabili dopo erogazione: " + erogatore.verificaConsumabili());
            
            // Simuliamo un rifornimento
            erogatore.messageArrived("erogatore/rifornisci", new MqttMessage("caffe,50".getBytes()));
            
            System.out.println("Stato consumabili dopo rifornimento: " + erogatore.verificaConsumabili());
            
            // Simuliamo un aggiornamento del parziale
            erogatore.messageArrived("erogatore/aggiornaParziale", new MqttMessage("2.5".getBytes()));
            
            System.out.println("Parziale attuale: " + erogatore.verificaParziale());
            
            // Chiudiamo la connessione MQTT alla fine
            erogatore.mqttClient.disconnect();
            System.out.println("Disconnesso dal broker MQTT");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}