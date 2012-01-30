package Wizard;



/**
 *
 * @author wolfertdekraker
 */
public class DataSet {

    private String formaat;
    private String datasetnaam;
    private String tabelnaam;
    private String eigenaar;
    private String beschrijving;
    private String copyrights;
    private String bestandsNaam;
    private String trackerURL;
    private String torrentnaam;
    private String bestandsPath;
    private String onlyPath;
    
    private long bestandsGrootte;

    public DataSet() {
    }

    public String getOnlyPath() {
        return onlyPath;
    }

    public void setOnlyPath(String onlyPath) {        
        this.onlyPath = onlyPath;
    }

    public long getBestandsGrootte() {
        return bestandsGrootte;
    }

    public void setBestandsGrootte(long bestandsGrootte) {
        this.bestandsGrootte = bestandsGrootte;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public String getDatasetnaam() {
        return datasetnaam;
    }

    public void setDatasetnaam(String datasetnaam) {
        this.datasetnaam = datasetnaam;
    }

    public String getEigenaar() {
        return eigenaar;
    }

    public void setEigenaar(String eigenaar) {
        this.eigenaar = eigenaar;
    }

    public String getFormaat() {
        return formaat;
    }

    public void setFormaat(String formaat) {
        this.formaat = formaat;
    }

    public String getTabelnaam() {
        return tabelnaam;
    }

    public void setTabelnaam(String tabelnaam) {
        this.tabelnaam = tabelnaam;
    }

    public String getBestandsNaam() {
        return bestandsNaam;
    }

    public void setBestandsNaam(String bestandsNaam) {
        this.bestandsNaam = bestandsNaam;
        this.torrentnaam = bestandsNaam+".torrent";
    }

    public String getTorrentnaam() {
        return torrentnaam;
    }
    

    public String getBestandsPath() {
        return bestandsPath;
    }

    public void setBestandsPath(String bestandsPath) {
        this.bestandsPath = bestandsPath;
    }

    public String getTrackerURL() {
        return trackerURL;
    }

    public void setTrackerURL(String trackerURL) {
        this.trackerURL = trackerURL;
    }


    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<info>");
        sb.append("<formaat>" + formaat + "</formaat>");
        sb.append("<datasetnaam>" + datasetnaam + "</datasetnaam>");
        sb.append("<tabelnaam>" + tabelnaam + "</tabelnaam>");
        sb.append("<eigenaar>" + eigenaar + "</eigenaar>");
        sb.append("<beschrijving>" + beschrijving + "</beschrijving>");
        sb.append("<copyrights>" + copyrights + "</copyrights>");
        sb.append("<bestandsNaam>" + bestandsNaam + "</bestandsNaam>");
        sb.append("</info>");
        return sb.toString();
    }
}
