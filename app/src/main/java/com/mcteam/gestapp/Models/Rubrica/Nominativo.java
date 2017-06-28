package com.mcteam.gestapp.Models.Rubrica;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Riccardo Rossi on 13/11/2015.
 */
public class Nominativo implements Parcelable {

    /*************************************************************/

    @SerializedName("ID_NOMINATIVO")
    private int ID;

    @SerializedName("ID_SOCIETA")
    private int IDSocieta;

    @SerializedName("TITOLO")
    private String mTitolo;

    @SerializedName("COGNOME")
    private String mCognome;

    @SerializedName("NOME")
    private String mNome;

    @SerializedName("INDIRIZZO")
    private String mIndirizzo;

    @SerializedName("CAP")
    private String mCap;

    @SerializedName("PROVINCIA")
    private String mProvincia;

    @SerializedName("CITTA")
    private String mCitta;

    @SerializedName("data_nascita")
    private String mDataNascita;

    @SerializedName("luogo_nascita")
    private String mLuogoNascita;

    @SerializedName("p_iva")
    private String mPIVA;

    @SerializedName("FAX")
    private String mFax;

    @SerializedName("cod_fiscale")
    private String mCod_Fiscale;

    @SerializedName("carta_id")
    private String mCartaID;

    @SerializedName("patente")
    private String mPatente;

    @SerializedName("tessera_sanitaria")
    private String mTesseraSanitaria;

    @SerializedName("sito_web")
    private String mSitoWeb;

    @SerializedName("nome_banca")
    private String mNomeBanca;

    @SerializedName("indirizzo_banca")
    private String mIndirizzoBanca;

    @SerializedName("iban")
    private String mIBAN;

    @SerializedName("NAZIONALITA")
    private String mNazionalita;

    @SerializedName("EMAIL")
    private String mEmail;

    @SerializedName("NOTE")
    private String mNote;

    @SerializedName("NOTE_DETT")
    private String mNoteDett;

    @SerializedName("id_stage")
    private String mIDStage;

    private Societa mSocieta;

    @SerializedName("CELLULARE")
    private String mCellulare;

    @SerializedName("TELEFONO")
    private String mTelefono;

    /***************************************************/

    public Nominativo() {
        this.mSocieta = null;
    }

    protected Nominativo(Parcel in) {
        ID = in.readInt();
        IDSocieta = in.readInt();
        mTitolo = in.readString();
        mCognome = in.readString();
        mNome = in.readString();
        mIndirizzo = in.readString();
        mCap = in.readString();
        mProvincia = in.readString();
        mCitta = in.readString();
        mDataNascita = in.readString();
        mLuogoNascita = in.readString();
        mPIVA = in.readString();
        mFax = in.readString();
        mCod_Fiscale = in.readString();
        mCartaID = in.readString();
        mPatente = in.readString();
        mTesseraSanitaria = in.readString();
        mSitoWeb = in.readString();
        mNomeBanca = in.readString();
        mIndirizzoBanca = in.readString();
        mIBAN = in.readString();
        mNazionalita = in.readString();
        mEmail = in.readString();
        mNote = in.readString();
        mNoteDett = in.readString();
        mIDStage = in.readString();
        mSocieta = in.readParcelable(Societa.class.getClassLoader());
        mCellulare = in.readString();
        mTelefono = in.readString();
    }

    public static final Creator<Nominativo> CREATOR = new Creator<Nominativo>() {
        @Override
        public Nominativo createFromParcel(Parcel in) {
            return new Nominativo(in);
        }

        @Override
        public Nominativo[] newArray(int size) {
            return new Nominativo[size];
        }
    };

    public String getTitolo() {
        return mTitolo;
    }

    public void setTitolo(String mTitolo) {
        this.mTitolo = mTitolo;
    }

    public String getCognome() {
        return mCognome;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFax() {
        return mFax;
    }

    public void setFax(String mFax) {
        this.mFax = mFax;
    }

    public void setCognome(String mCognome) {
        this.mCognome = mCognome;
    }

    public String getNome() {
        return mNome;
    }

    public void setNome(String mNome) {
        this.mNome = mNome;
    }

    public Societa getSocieta() {
        return mSocieta;
    }

    public void setSocieta(Societa mSocieta) {
        this.mSocieta = mSocieta;
    }

    public String getCellulare() {
        return mCellulare;
    }

    public void setCellulare(String mCellulare) {
        this.mCellulare = mCellulare;
    }

    public int getIDSocieta() {
        return IDSocieta;
    }

    public void setIDSocieta(int IDSocieta) {
        this.IDSocieta = IDSocieta;
    }

    public String getIndirizzo() {
        return mIndirizzo;
    }

    public void setIndirizzo(String mIndirizzo) {
        this.mIndirizzo = mIndirizzo;
    }

    public String getCap() {
        return mCap;
    }

    public void setCap(String mCap) {
        this.mCap = mCap;
    }

    public String getProvincia() {
        return mProvincia;
    }

    public void setProvincia(String mProvincia) {
        this.mProvincia = mProvincia;
    }

    public String getNazionalita() {
        return mNazionalita;
    }

    public void setNazionalita(String mNazionalita) {
        this.mNazionalita = mNazionalita;
    }

    public String getDataNascita() {
        return mDataNascita;
    }

    public void setDataNascita(String mDataNascita) {
        this.mDataNascita = mDataNascita;
    }

    public String getLuogoNascita() {
        return mLuogoNascita;
    }

    public void setLuogoNascita(String mLuogoNascita) {
        this.mLuogoNascita = mLuogoNascita;
    }

    public String getPIVA() {
        return mPIVA;
    }

    public void setPIVA(String mPIVA) {
        this.mPIVA = mPIVA;
    }

    public String getCod_Fiscale() {
        return mCod_Fiscale;
    }

    public void setCod_Fiscale(String mCod_Fiscale) {
        this.mCod_Fiscale = mCod_Fiscale;
    }

    public String getCartaID() {
        return mCartaID;
    }

    public void setCartaID(String mCartaID) {
        this.mCartaID = mCartaID;
    }

    public String getPatente() {
        return mPatente;
    }

    public void setPatente(String mPatente) {
        this.mPatente = mPatente;
    }

    public String getNomeBanca() {
        return mNomeBanca;
    }

    public void setNomeBanca(String mNomeBanca) {
        this.mNomeBanca = mNomeBanca;
    }

    public String getTesseraSanitaria() {
        return mTesseraSanitaria;
    }

    public void setTesseraSanitaria(String mTesseraSanitaria) {
        this.mTesseraSanitaria = mTesseraSanitaria;
    }

    public String getIndirizzoBanca() {
        return mIndirizzoBanca;
    }

    public void setIndirizzoBanca(String mIndirizzoBanca) {
        this.mIndirizzoBanca = mIndirizzoBanca;
    }

    public String getIBAN() {
        return mIBAN;
    }

    public void setIBAN(String mIBAN) {
        this.mIBAN = mIBAN;
    }

    public String getCitta() {
        return mCitta;
    }

    public void setCitta(String mCitta) {
        this.mCitta = mCitta;
    }

    public String getSitoWeb() {
        return mSitoWeb;
    }

    public void setSitoWeb(String mSitoWeb) {
        this.mSitoWeb = mSitoWeb;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String mNote) {
        this.mNote = mNote;
    }

    public String getNoteDett() {
        return mNoteDett;
    }

    public void setNoteDett(String mNoteDett) {
        this.mNoteDett = mNoteDett;
    }

    public String getIDStage() {
        return mIDStage;
    }

    public void setIDStage(String mIDStage) {
        this.mIDStage = mIDStage;
    }

    public String getTelefono() {
        return mTelefono;
    }

    public void setTelefono(String mTelfono) {
        this.mTelefono = mTelfono;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(IDSocieta);
        dest.writeString(mTitolo);
        dest.writeString(mCognome);
        dest.writeString(mNome);
        dest.writeString(mIndirizzo);
        dest.writeString(mCap);
        dest.writeString(mProvincia);
        dest.writeString(mCitta);
        dest.writeString(mDataNascita);
        dest.writeString(mLuogoNascita);
        dest.writeString(mPIVA);
        dest.writeString(mFax);
        dest.writeString(mCod_Fiscale);
        dest.writeString(mCartaID);
        dest.writeString(mPatente);
        dest.writeString(mTesseraSanitaria);
        dest.writeString(mSitoWeb);
        dest.writeString(mNomeBanca);
        dest.writeString(mIndirizzoBanca);
        dest.writeString(mIBAN);
        dest.writeString(mNazionalita);
        dest.writeString(mEmail);
        dest.writeString(mNote);
        dest.writeString(mNoteDett);
        dest.writeString(mIDStage);
        dest.writeParcelable(mSocieta, flags);
        dest.writeString(mCellulare);
        dest.writeString(mTelefono);
    }


    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }

        final Nominativo nominativo = (Nominativo) other;
        if (this.getID() == nominativo.getID()) {
            return true;
        }
        return false;
    }
}
