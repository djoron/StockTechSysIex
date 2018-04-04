/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Objects;

/**
 *
 * @author atlantis
 */
public class Symbol {

 // xxxx a verifier les types. DOuble pour entiers, Big decimal pour float
    protected String symbol;
    protected String name;
    protected String date;
    protected String isEnabled;
    protected String type;
    protected String iexId;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIexId() {
        return iexId;
    }

    public void setIexId(String iexId) {
        this.iexId = iexId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.symbol);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Symbol other = (Symbol) obj;
        if (!Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        return true;
    }
    
}


/* Ajoute Long Id
override equals pour detecter doublons sur symbol
et hashcode --> va retourner symbol.hashcode - cle pour retrouver l<objet dans hashset


SymbolListFromDB
HashSet<Symbol> set=new HashSet<>();  

set.addall(SymbolListFromDb)
set.addall(SymbolUpdated).

List<Symbol> newSymbols = set
    .stream()
    .filter(s -> s.getId() == null)
    .collect(Collectors.toList());

newSymbols
    .stream()
    .forEach(s -> saveToSymbolDB(s));

update company DB
    



  vieille liste . set.addall(Liste de updated "Ravi");  





*/