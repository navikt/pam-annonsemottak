package no.nav.pam.annonsemottak.annonsemottak.solr;

import no.nav.pam.annonsemottak.annonsemottak.solr.fetch.StillingSolrBeanFieldNames;
import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Date;

public class StillingSolrBean {

    @Field(StillingSolrBeanFieldNames.ID)
    private Integer id;

    @Field(StillingSolrBeanFieldNames.ANTALLSTILLINGER)
    private Integer antallStillinger;

    @Field(StillingSolrBeanFieldNames.STILLINGSPROSENT)
    private Float stillingsprosent = 0f;

    @Field(StillingSolrBeanFieldNames.KILDETEKST)
    private String kildetekst;

    @Field(StillingSolrBeanFieldNames.ARBEIDSGIVERNAVN)
    private String arbeidsgivernavn;

    @Field(StillingSolrBeanFieldNames.STILLINGSTATUSKODE)
    private String stillingstatuskode;

    @Field(StillingSolrBeanFieldNames.TITTEL)
    private String tittel;

    @Field(StillingSolrBeanFieldNames.STILLINGSBESKRIVELSE)
    private String stillingsbeskrivelse;

    @Field(StillingSolrBeanFieldNames.BEDRIFTSPRESENTASJON)
    private String bedriftspresentasjon;

    @Field(StillingSolrBeanFieldNames.ADRESSEPOSTSTED)
    private String adressepoststed;

    @Field(StillingSolrBeanFieldNames.KOMMUNIKASJON_URL)
    private String kommunikasjonUrl;

    @Field(StillingSolrBeanFieldNames.KOMMUNIKASJON_EPOST)
    private String kommunikasjonEpost;

    @Field(StillingSolrBeanFieldNames.KOMMUNIKASJON_TELEFON)
    private String kommunikasjonTelefon;

    @Field(StillingSolrBeanFieldNames.KONTAKTPERSON)
    private String kontaktperson;

    @Field(StillingSolrBeanFieldNames.STILLINGSTYPE_5)
    private String stillingstype;

    @Field(StillingSolrBeanFieldNames.SOKNADSENDES)
    private String soknadsendes;

    @Field(StillingSolrBeanFieldNames.SOKNADMERKES)
    private String soknadmerkes;

    @Field(StillingSolrBeanFieldNames.PUBLISERES_FRA)
    private Date publiseresFra = new Date();

    @Field(StillingSolrBeanFieldNames.SOKNADSFRIST)
    private Date soknadsfrist = new Date();

    @Field(StillingSolrBeanFieldNames.SISTEPUBLISERINGSDATO)
    private Date sistePubliseringsdato = new Date();

    @Field(StillingSolrBeanFieldNames.ADRESSEPOSTNR)
    private ArrayList<String> adressepostnr = new ArrayList<>();

    @Field(StillingSolrBeanFieldNames.ADRESSELINJE1)
    private ArrayList<String> adresselinje = new ArrayList<>();

    @Field(StillingSolrBeanFieldNames.KOMMUNE)
    private ArrayList<String> kommune = new ArrayList<>();

    @Field(StillingSolrBeanFieldNames.FYLKE)
    private ArrayList<String> fylke = new ArrayList<>();

    @Field(StillingSolrBeanFieldNames.ANSETTELSESFORHOLD)
    private ArrayList<String> ansettelsesforhold = new ArrayList<>();

    @Field(StillingSolrBeanFieldNames.HELTIDDELTID)
    private ArrayList<String> heltiddeltid = new ArrayList<>();

    @Field(StillingSolrBeanFieldNames.ARBEIDSORDNING)
    private ArrayList<String> arbeidsordning = new ArrayList<>();

    @Field(StillingSolrBeanFieldNames.REG_DATO)
    private Date regDato = new Date();

    @Field(StillingSolrBeanFieldNames.LAND)
    private String land;

    @Field(StillingSolrBeanFieldNames.GEOGRAFISKOMRADE)
    private ArrayList<String> geografiskomrade;

    @Field(StillingSolrBeanFieldNames.LONNSINFO)
    private String lonnsinfo;

    @Field(StillingSolrBeanFieldNames.UTDANNING)
    private ArrayList<String> utdanning;

    @Field(StillingSolrBeanFieldNames.SERTIFIKAT)
    private ArrayList<String> sertifikat;

    @Field(StillingSolrBeanFieldNames.KOMPETANSE)
    private ArrayList<String> kompetanse;

    @Field(StillingSolrBeanFieldNames.PRAKSIS)
    private ArrayList<String> praksis;

    public StillingSolrBean() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKildetekst() {
        return kildetekst;
    }

    public void setKildetekst(String kildetekst) {
        this.kildetekst = kildetekst;
    }

    public String getArbeidsgivernavn() {
        return arbeidsgivernavn;
    }

    public void setArbeidsgivernavn(String arbeidsgivernavn) {
        this.arbeidsgivernavn = arbeidsgivernavn;
    }

    public Date getPubliseresFra() {
        return publiseresFra;
    }

    public void setPubliseresFra(Date publiseresFra) {
        this.publiseresFra = publiseresFra;
    }

    public String getStillingstatuskode() {
        return stillingstatuskode;
    }

    public void setStillingstatuskode(String stillingstatuskode) {
        this.stillingstatuskode = stillingstatuskode;
    }

    public Date getSistePubliseringsdato() {
        return sistePubliseringsdato;
    }

    public void setSistePubliseringsdato(Date sistePubliseringsdato) {
        this.sistePubliseringsdato = sistePubliseringsdato;
    }

    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public Date getSoknadsfrist() {
        return soknadsfrist;
    }

    public void setSoknadsfrist(Date soknadsfrist) {
        this.soknadsfrist = soknadsfrist;
    }

    public String getStillingsbeskrivelse() {
        return stillingsbeskrivelse;
    }

    public void setStillingsbeskrivelse(String stillingsbeskrivelse) {
        this.stillingsbeskrivelse = stillingsbeskrivelse;
    }

    public String getBedriftspresentasjon() {
        return bedriftspresentasjon;
    }

    public void setBedriftspresentasjon(String bedriftspresentasjon) {
        this.bedriftspresentasjon = bedriftspresentasjon;
    }

    public ArrayList<String> getAdressepostnr() {
        return adressepostnr;
    }

    public void setAdressepostnr(ArrayList<String> adressepostnr) {
        this.adressepostnr = adressepostnr;
    }

    public String getAdressepoststed() {
        return adressepoststed;
    }

    public void setAdressepoststed(String adressepoststed) {
        this.adressepoststed = adressepoststed;
    }

    public ArrayList<String> getAdresselinje() {
        return adresselinje;
    }

    public void setAdresselinje(ArrayList<String> adresselinje) {
        this.adresselinje = adresselinje;
    }

    public ArrayList<String> getKommune() {
        return kommune;
    }

    public void setKommune(ArrayList<String> kommune) {
        this.kommune = kommune;
    }

    public ArrayList<String> getFylke() {
        return fylke;
    }

    public void setFylke(ArrayList<String> fylke) {
        this.fylke = fylke;
    }

    public ArrayList<String> getAnsettelsesforhold() {
        return ansettelsesforhold;
    }

    public void setAnsettelsesforhold(ArrayList<String> ansettelsesforhold) {
        this.ansettelsesforhold = ansettelsesforhold;
    }

    public ArrayList<String> getHeltiddeltid() {
        return heltiddeltid;
    }

    public void setHeltiddeltid(ArrayList<String> heltiddeltid) {
        this.heltiddeltid = heltiddeltid;
    }

    public String getKommunikasjonUrl() {
        return kommunikasjonUrl;
    }

    public void setKommunikasjonUrl(String kommunikasjonUrl) {
        this.kommunikasjonUrl = kommunikasjonUrl;
    }

    public String getKommunikasjonEpost() {
        return kommunikasjonEpost;
    }

    public void setKommunikasjonEpost(String kommunikasjonEpost) {
        this.kommunikasjonEpost = kommunikasjonEpost;
    }

    public String getKommunikasjonTelefon() {
        return kommunikasjonTelefon;
    }

    public void setKommunikasjonTelefon(String kommunikasjonTelefon) {
        this.kommunikasjonTelefon = kommunikasjonTelefon;
    }

    public String getKontaktperson() {
        return kontaktperson;
    }

    public void setKontaktperson(String kontaktperson) {
        this.kontaktperson = kontaktperson;
    }

    public Integer getAntallStillinger() {
        return antallStillinger;
    }

    public void setAntallStillinger(Integer antallStillinger) {
        this.antallStillinger = antallStillinger;
    }

    public String getStillingstype() {
        return stillingstype;
    }

    public void setStillingstype(String stillingstype) {
        this.stillingstype = stillingstype;
    }

    public ArrayList<String> getArbeidsordning() {
        return arbeidsordning;
    }

    public void setArbeidsordning(ArrayList<String> arbeidsordning) {
        this.arbeidsordning = arbeidsordning;
    }

    public Float getStillingsprosent() {
        return stillingsprosent;
    }

    public void setStillingsprosent(Float stillingsprosent) {
        this.stillingsprosent = stillingsprosent;
    }

    public String getSoknadsendes() {
        return soknadsendes;
    }

    public void setSoknadsendes(String soknadsendes) {
        this.soknadsendes = soknadsendes;
    }

    public String getSoknadmerkes() {
        return soknadmerkes;
    }

    public void setSoknadmerkes(String soknadmerkes) {
        this.soknadmerkes = soknadmerkes;
    }

    public Date getRegDato() {
        return regDato;
    }

    public void setRegDato(Date regDato) {
        this.regDato = regDato;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public ArrayList<String> getGeografiskomrade() {
        return geografiskomrade;
    }

    public void setGeografiskomrade(ArrayList<String> geografiskomrade) {
        this.geografiskomrade = geografiskomrade;
    }

    public String getLonnsinfo() {
        return lonnsinfo;
    }

    public void setLonnsinfo(String lonnsinfo) {
        this.lonnsinfo = lonnsinfo;
    }

    public ArrayList<String> getUtdanning() {
        return utdanning;
    }

    public void setUtdanning(ArrayList<String> utdanning) {
        this.utdanning = utdanning;
    }

    public ArrayList<String> getSertifikat() {
        return sertifikat;
    }

    public void setSertifikat(ArrayList<String> sertifikat) {
        this.sertifikat = sertifikat;
    }

    public ArrayList<String> getPraksis() {
        return praksis;
    }

    public void setPraksis(ArrayList<String> praksis) {
        this.praksis = praksis;
    }

    public ArrayList<String> getKompetanse() {
        return kompetanse;
    }

    public void setKompetanse(ArrayList<String> kompetanse) {
        this.kompetanse = kompetanse;
    }
}
