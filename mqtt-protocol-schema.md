# Schema Protocollo MQTT per Macchina Distributrice

## Topic MQTT

### Cassa
- `cassa/parziale`: Mantiene il parziale
- `cassa/totale`: Mantiene il totale
- `cassa/resto`: Richiesta/risposta per il calcolo del resto
- `cassa/eroga_monete`: Comando per erogare monete
- `cassa/notifica_piena`: Notifica quando la cassa è piena

### Erogatore
- `erogatore/verifica_consumabili`: Richiesta/risposta per la verifica dei consumabili
- `erogatore/verifica_parziale`: Richiesta/risposta per la verifica del parziale
- `erogatore/eroga_bevanda`: Comando per erogare una bevanda
- `erogatore/errore`: Messaggi di errore per lo schermo
- `erogatore/fine_erogazione`: Messaggio di fine erogazione

### Tastierino
- `tastierino/input`: Input inviato all'erogatore

### Gettoniera
- `gettoniera/aggiorna_parziale`: Aggiornamento del parziale
- `gettoniera/eroga_parziale`: Richiesta di erogazione del parziale

### Consumabili
- `consumabili/stato`: Stato attuale dei consumabili
- `consumabili/aggiorna`: Aggiornamento dei consumabili
- `consumabili/server`: Comunicazione con il server

## Struttura dei Messaggi

### Cassa
```json
{
  "parziale": {
    "importo": 0.0
  },
  "totale": {
    "importo": 0.0
  },
  "resto": {
    "richiesta": 0.0,
    "risposta": 0.0
  },
  "eroga_monete": {
    "importo": 0.0
  },
  "notifica_piena": {
    "stato": true
  }
}
```

### Erogatore
```json
{
  "verifica_consumabili": {
    "richiesta": true,
    "risposta": {
      "caffe": 0,
      "acqua": 0,
      "bicchieri": 0
    }
  },
  "verifica_parziale": {
    "richiesta": true,
    "risposta": 0.0
  },
  "eroga_bevanda": {
    "tipo": "",
    "quantita": 0
  },
  "errore": {
    "codice": 0,
    "messaggio": ""
  },
  "fine_erogazione": {
    "stato": true
  }
}
```

### Tastierino
```json
{
  "input": {
    "tasto": ""
  }
}
```

### Gettoniera
```json
{
  "aggiorna_parziale": {
    "importo": 0.0
  },
  "eroga_parziale": {
    "importo": 0.0
  }
}
```

### Consumabili
```json
{
  "stato": {
    "caffe": 0,
    "acqua": 0,
    "bicchieri": 0
  },
  "aggiorna": {
    "caffe": 0,
    "acqua": 0,
    "bicchieri": 0
  },
  "server": {
    "tipo": "",
    "dati": {}
  }
}
```
