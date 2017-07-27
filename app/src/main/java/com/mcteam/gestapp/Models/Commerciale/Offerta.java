package com.mcteam.gestapp.Models.Commerciale;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Created by Riccardo Rossi on 02/10/2016.
 */

public class Offerta implements Parcelable{
    @SerializedName("id_commessa")
    private int idCommessa;

    @SerializedName("versione")
    private int versione;

    @SerializedName("data_offerta")
    private String dataOfferta;

    @SerializedName("accettata")
    private int accettata;

    @SerializedName("offerta")
    private String offerta;

    @SerializedName("allegato")
    private String allegato;

    public Offerta()
    {

    }

    protected Offerta(Parcel in) {
        idCommessa = in.readInt();
        versione = in.readInt();
        dataOfferta = in.readString();
        accettata = in.readInt();
        offerta = in.readString();
        allegato = in.readString();
    }

    public static final Creator<Offerta> CREATOR = new Creator<Offerta>() {
        @Override
        public Offerta createFromParcel(Parcel in) {
            return new Offerta(in);
        }

        @Override
        public Offerta[] newArray(int size) {
            return new Offerta[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCommessa);
        dest.writeInt(versione);
        dest.writeString(dataOfferta);
        dest.writeInt(accettata);
        dest.writeString(offerta);
        dest.writeString(allegato);
    }

    public int getIdCommessa() {
        return idCommessa;
    }

    public void setIdCommessa(int idCommessa) {
        this.idCommessa = idCommessa;
    }

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
    }

    public String getDataOfferta() {
        return dataOfferta;
    }

    public void setDataOfferta(String dataOfferta) {
        this.dataOfferta = dataOfferta;
    }

    public int getAccettata() {
        return accettata;
    }

    public void setAccettata(int accettata) {
        this.accettata = accettata;
    }

    public String getOfferta() {
        return offerta;
    }

    public void setOfferta(String offerta) {
        this.offerta = offerta;
    }

    public String getAllegato() {
        return allegato;
    }

    public void setAllegato(String allegato) {
        this.allegato = allegato;
    }

    @Override
    public String toString() {
        return "Offerta{" +
                "idCommessa=" + idCommessa +
                ", versione=" + versione +
                ", dataOfferta='" + dataOfferta + '\'' +
                ", accettata=" + accettata +
                ", offerta='" + offerta + '\'' +
                ", allegato='" + allegato + '\'' +
                '}';
    }
}
