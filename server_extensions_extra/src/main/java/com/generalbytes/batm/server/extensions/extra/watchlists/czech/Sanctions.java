/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.watchlists.czech;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sanctions {
    private List<Record> records = new ArrayList<>();
    private static final int INDIVIDUAL = 0;
    private static final int ENTITY = 1;

    class Record {
        private String id;
        private int type;
        private String pdf;
        private String firstName;
        private String lastName;
        private String gender;
        private String function;

        private String birthDate;
        private String birthPlace;
        private String birthCountry;
        private String passportNumber;
        private String[] names;

        public Record(String id, String pdf, String firstName, String lastName, String gender, String function, String birthDate, String birthPlace, String birthCountry, String passportNumber) {
            this.type = INDIVIDUAL;
            this.id = id;
            this.pdf = pdf;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.function = function;
            this.birthDate = birthDate;
            this.birthPlace = birthPlace;
            this.birthCountry = birthCountry;
            this.passportNumber = passportNumber;
        }

        public Record(String id, String pdf, String entityNameWithTransalation) {
            this.id = id;
            this.type = ENTITY;
            this.pdf = pdf;
            this.names = entityNameWithTransalation.split(",");
        }
    }
    public Sanctions() {
        records.add(new Record("P1","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Itziar","Alberdi Uranga","F","aktivistka ETA","10/7/1963","v Durangu, Biskajsko","Španělsko","průkaz totožnosti č. 78.865.693"));
        records.add(new Record("P2","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Miguel","Albisu Iriarte","M","aktivista ETA; člen Gestoras pro-amnistía","6/7/1961","San Sebastian, Guipúzcoa","Španělsko","průkaz totožnosti č. 15.954.596"));
        records.add(new Record("P3","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Xavier","Alegría Loinaz","M","aktivista ETA; člen Kas / Ekin","11/26/1958","San Sebastian, Guipúzcoa","Španělsko","průkaz totožnosti č. 15.239.620"));
        records.add(new Record("P4","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Iván","Apaolaza Sancho","M","aktivista ETA; člen K. Madrid","11/10/1971","v Beasainu,Guipúzcoa ","Španělsko","průkaz totožnosti č. 44.129.178"));
        records.add(new Record("P5","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Eusebio","Arzallus Tapia","M","aktivista ETA","11/8/1957","v Regilu, Guipúzcoa","Španělsko","průkaz totožnosti č. 15.927.207"));
        records.add(new Record("P6","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Miguel de Garikoitz","Aspiazu Rubina","M","aktivista ETA","7/6/1973","v Bilbau, Vizcaya","Španělsko","průkaz totožnosti č. 14.257.455"));
        records.add(new Record("P7","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","María Elena","Beloqui Resa","F","aktivistka ETA; členka Xaki","6/12/1961","v Aretě, Álava","Španělsko","průkaz totožnosti č. 14.956.327"));
        records.add(new Record("P8","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Miriam","Campos Alonso","F","aktivistka ETA; členka Xaki","9/2/1971","v Bilbauo, Vizcaya","Španělsko","průkaz totožnosti č. 30.652.316"));
        records.add(new Record("P9","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Mikel","Corta Carrion","M","aktivista ETA; člen Xaki","5/15/1959","v VillafrancadeOrdicia, Guipúzcoa","Španělsko","průkaz totožnosti č. 08.902.967"));
        records.add(new Record("P10","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","ikel","Eguibar Michelena","M","aktivista ETA; člen Xaki","11/14/1963","San Sebastian, Guipúzcoa","Španělsko","průkaz totožnosti č. 44.151.825"));
        records.add(new Record("P11","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Leire","Echeberria Simarro","F","aktivistka ETA","12/20/1977","v Basauri, Biskajsko","Španělsko","průkaz totožnosti č. 45.625.646"));
        records.add(new Record("P12","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Alfonso","Echegaray Achirica","M","aktivista ETA","1/10/1958","v Plencii, Biskajsko","Španělsko","průkaz totožnosti č. 16.027.051"));
        records.add(new Record("P13","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Eneko","Gogeascoechea Arronategui","M","aktivista ETA","4/29/1967","v Guernice, Biskajsko","Španělsko","průkaz totožnosti č. 44.556.097"));
        records.add(new Record("P14","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","María Soledad","Iparraguirre Guenechea","F","aktivistka ETA","4/25/1961","v Escoriaze, Navarra","Španělsko","průkaz totožnosti č. 16.255.819"));
        records.add(new Record("P15","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Aitzol","Iriondo Yarza","M","aktivista ETA","3/8/1977","San Sebastian, Guipúzcoa","Španělsko","průkaz totožnosti č. 72.467.565"));
        records.add(new Record("P16","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Jurdan","Martitegui Lizaso","M","aktivista ETA","5/10/1980","v Durangu, Vizcaya","Španělsko","průkaz totožnosti č. 45.626.584"));
        records.add(new Record("P17","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Gracia","Morcillo Torres","F","aktivistka ETA; členka Kas / Ekin","3/15/1967","San Sebastian, Guipúzcoa","Španělsko","průkaz totožnosti č. 72.439.052"));
        records.add(new Record("P18","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Juan Jesús","Narváez Goñi","M","aktivista ETA","2/23/1961","v Pamploně, Navarra","Španělsko","průkaz totožnosti č. 15.841.101"));
        records.add(new Record("P19","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Juan María","Olano Olano","M","aktivista ETA; člen Gestoras pro-amnistía / Askatasuna","3/25/1955","v Ganze, Guipúzcoa","Španělsko","průkaz totožnosti č. 15.919.168"));
        records.add(new Record("P20","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","José María","Olarra Aguiriano","M","aktivista ETA; člen Xaki","7/27/1957","v Tolose, Guipúzcoa","Španělsko","průkaz totožnosti č. 72.428.996"));
        records.add(new Record("P21","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Zigor","Orbe Sevillano","M","aktivista ETA; člen Jarrai Haika Segi","9/22/1975","v Basauri, Biskajsko","Španělsko","průkaz totožnosti č. 45.622.851"));
        records.add(new Record("P22","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Gorka","Palacios Alday","M","aktivista ETA; člen K. Madrid","10/17/1974","v Baracaldu, Biskajsko","Španělsko","průkaz totožnosti č. 30.654.356"));
        records.add(new Record("P23","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","onIñaki","Perez Aramburu","M","aktivista ETA; člen Jarrai Haika Segi","9/18/1964","San Sebastian, Guipúzcoa","Španělsko","průkaz totožnosti č. 15.976.521"));
        records.add(new Record("P24","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Asier","Quintana Zorrozua","M","aktivista ETA; člen K. Madrid","2/27/1968","v Bilbau, Biskajsko","Španělsko","průkaz totožnosti č. 30.609.430"));
        records.add(new Record("P25","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","José Ignacio","Reta De Frutos","M","aktivista ETA; člen Gestoras pro-amnistía / Askatasuna","7/3/1959","v Elorriu, Vizcaya","Španělsko","průkaz totožnosti č. 72.253.056"));
        records.add(new Record("P26","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Juan Luis","Rubenach Roig","M","aktivista ETA; člen K. Madrid","9/18/1963","v Bilbau, Biskajsko","Španělsko","průkaz totožnosti č. 18.197.545"));
        records.add(new Record("P27","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Nekane","Txapartegi Nieves","F","aktivistka ETA; členka Xaki","1/8/1973","v Asteasu, Guipúzcoa","Španělsko","průkaz totožnosti č. 44.140.578"));
        records.add(new Record("P28","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Kemen","Uranga Artola","M","aktivista ETA; člen HerriBatasuna / EuskalHerritarrok / Batasuna","5/25/1969","v Ondarroi, Biskajsko","Španělsko","průkaz totožnosti č. 30.627.290"));
        records.add(new Record("P29","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","José Antonio","Urruticoechea Bengoechea","M","aktivista ETA","12/24/1950","v Miravalles, Vizcaya","Španělsko","průkaz totožnosti č. 14.884.849"));
        records.add(new Record("P30","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Iñigo","Vallejo Franco","M","aktivista ETA","5/21/1976","v Bilbau, Biskajsko","Španělsko","průkaz totožnosti č. 29.036.694"));
        records.add(new Record("P31","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Fermín","Vila Michelena","M","aktivista ETA; člen Kas / Ekin","3/12/1970","v Irúnu, Guipúzcoa","Španělsko","průkaz totožnosti č. 15.254.214"));

        records.add(new Record("E1","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Brigata XX Luglio, Brigáda 20. července"));
        records.add(new Record("E2","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Brigate Rosse per la Costruzione del Partito Comunista Combattente, Rudé brigády pro budování bojové komunistické strany, Buňka proti kapitálu vězení vězeňským dozorcům a vězeňským celám"));
        records.add(new Record("E3","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Cellula Contro Capitale Carcere i suoi Carcerieri e le sue Celle, CCCCC"));
        records.add(new Record("E4","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Continuity Irish Republican Army, CIRA, Pokračující Irská republikánská armáda"));
        records.add(new Record("E5","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Cooperativa Artigiana Fuoco ed Affini – Occasionalmente Spettacolare"));
        records.add(new Record("E6","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Dekati Evdomi Noemvri, Revoluční organizace 17. listopadu"));
        records.add(new Record("E7","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Epanastatiki Pirines, Revoluční buňky"));
        records.add(new Record("E8","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","EPANASTATIKOS AGONAS, Revoluční boj"));
        records.add(new Record("E9","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Euskadi Ta Askatasuna, Tierra Vasca y Libertad, ETA, Baskicko a jeho svoboda, součásti:Kas, Xaki, Ekin, Jarrai Haika Segi, Gestoras pro-amnistía, Askatasuna, Batasuna, Acción Nacionalista Vasca, Euskal Abertzale Ekintza, ANV/EAE, Partido Comunista de las Tierras Vascas, Euskal Herrialdeetako Alderdi Komunista, PCTV, EHAK, Herri Batasuna, Euskal Herritarrok"));
        records.add(new Record("E10","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Federazione Anarchica Informale, FAI, Neformální anarchistická federace"));
        records.add(new Record("E11","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Grupos de Resistencia Anti-Fascista Primero de Octubre, GRAPO, Antifašistické skupiny odporu prvního října"));
        records.add(new Record("E12","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Loyalist Volunteer Force, LVF, Loajalistický dobrovolnický sbor"));
        records.add(new Record("E13","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Nuclei Armati per il Comunismo, Ozbrojené jednotky pro komunismus"));
        records.add(new Record("E14","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Orange Volunteers, OV, Oranžští dobrovolníci"));
        records.add(new Record("E15","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Real IRA, Skutečná IRA"));
        records.add(new Record("E16","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Red Hand Defenders, RHD, Obránci rudé ruky"));
        records.add(new Record("E17","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Solidarietà Internazionale, Mezinárodní solidarita"));
        records.add(new Record("E18","http://www.mfcr.cz/cps/rde/xbcr/mfcr/NV_88_2009.pdf","Ulster Defence Association/Ulster Freedom Fighters, UDA/UFF, Ulsterské obranné sdružení/Ulsterští bojovníci za svobodu"));
    }
    public Set<Match> search(String entityName) {
        Set<Match> matchedParties = new HashSet<Match>();

        entityName = entityName.trim();
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            if (record.type == ENTITY) {
                String[] names = record.names;
                for (int j = 0; j < names.length; j++) {
                    String name = names[j];
                    if (name.trim().equalsIgnoreCase(entityName)) {
                        matchedParties.add(new Match(record.id,100));
                        break;
                    }
                }
            }
        }
        return matchedParties;
    }

    public Set<Match> search(String firstName, String lastName) {
        if (firstName == null) {
            firstName = "";
        }
        if (lastName == null) {
            lastName = "";
        }
        lastName = lastName.trim();
        firstName = firstName.trim();

        Set<String> candidateParties = new HashSet<String>();
        Set<Match> matchedParties = new HashSet<Match>();


        if (firstName.isEmpty()) {
            //search just against last names

            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                if (record.type == INDIVIDUAL && record.lastName.trim().equalsIgnoreCase(lastName)) {
                    matchedParties.add(new Match(record.id,100));
                }
            }
        }else {
            //search against lastname ans firstname
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                if (record.type == INDIVIDUAL && record.lastName.trim().equalsIgnoreCase(lastName)) {
                    candidateParties.add(record.id);
                }
            }

            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                if (record.type == INDIVIDUAL && record.firstName.trim().equalsIgnoreCase(firstName)) {
                    if (candidateParties.contains(record.id)) {
                        //ok seems like we have a winner
                        matchedParties.add(new Match(record.id,100));
                    }
                }
            }

            if (matchedParties.size() == 0) {
                //both first name and last name didn't match
                //so lets report at least lastname matches with 50% score/confidence
                for (String candidateParty : candidateParties) {
                    matchedParties.add(new Match(candidateParty,50));
                }
            }
        }
        return matchedParties;
    }

    public String getPartyIndexByPartyId(String partyId) {
        return partyId;
    }

}
