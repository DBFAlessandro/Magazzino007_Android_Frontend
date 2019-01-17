package com.amsoftware.testrestapplication.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

//DEFINE A POJO
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdottiEntity implements Serializable
{

    private int    id_prodotto_p;
    private String descrizione;
    private int    p_quantita;
    private float  p_prezzo;
    private String nome;
  //private Object vendite;

    public ProdottiEntity()
    {

        //this.vendite = null;
    }

    public ProdottiEntity(int id_prodotto, String nome, String descrizione, int quantita, float prezzo) {
        super();
        this.id_prodotto_p = id_prodotto;
        this.descrizione = descrizione;
        this.p_quantita = quantita;
        this.p_prezzo = prezzo;
        this.nome = nome;
      //  this.vendite = null;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getId() {
        return id_prodotto_p;
    }

    public void setId(int id) {
        this.id_prodotto_p = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getQuantita() {
        return p_quantita;
    }

    public void setQuantita(int quantita) {
        this.p_quantita = quantita;
    }

    public float getPrezzo() {
        return p_prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.p_prezzo = prezzo;
    }
//public Object getVendite()
//{
  //  return vendite;
//}
//public void setVendite(Object vendite)
//{

  //  this.vendite = vendite;
//}

    @Override
    public String toString()
    {
        return  "["
                + "prodotto:" + this.id_prodotto_p
                + ",descrizione:" + this.descrizione
                + ",quantita:"
                + this.p_quantita
                + ",prezzo:"
                + this.p_prezzo
                + ",nome:" + this.nome
                + "]";
    }
}
