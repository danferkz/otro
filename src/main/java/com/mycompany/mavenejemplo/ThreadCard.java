package com.mycompany.mavenejemplo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.*;

public class ThreadCard extends Thread {
    TerminalFactory TermFact = TerminalFactory.getDefault();
    CardTerminals CT = this.TermFact.terminals();
    CardTerminal CTerm = null;
    public static CardChannel CardCh;

    public void run() {
        Card card = null;
        while (true) {
            try {
                List<CardTerminal> cs = this.CT.list(CardTerminals.State.ALL);
                int tot = cs.size();
                for (int i = 0; i < tot; i++) {
                    CardTerminal c = cs.get(i);
                    String lector = c.getName();
                    /*
                    if (lector.contains("CL")){
                        continue;
                    }
                    if (!c.isCardPresent()){
                        continue;
                    }*/
                    this.CTerm = cs.get(i);
                    card = this.CTerm.connect("*");
                    CardCh = card.getBasicChannel();

                    // Obtener el identificador único de la tarjeta
                    ATR atr = card.getATR();
                    byte[] historical = atr.getHistoricalBytes();
                    String uid = bytesToHex(atr.getBytes());

                    System.out.println("ATR: " + bytesToHex(atr.getBytes()));
                    System.out.println("Historical bytes: " + bytesToHex(historical));
                    System.out.println("Card UID: " + uid);
                    System.out.println("Card Protocol: " + card.getProtocol());

                    this.CTerm.waitForCardAbsent(0L);
                }
            } catch (CardException ex) {
                Logger.getLogger(ThreadCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Método de utilidad para convertir bytes a una representación hexadecimal
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }
}