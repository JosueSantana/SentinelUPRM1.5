package ListViewHelpers;

/**
 * Created by a136803 on 12/18/15.
 */
public class IncidentsListItem {

    private String incidentRegion;
    private double latitude;
    private double longitude;
    private String createdOn;

    public IncidentsListItem(String region, String subregion, Double latitude, Double longitude, String createdOn){
        this.incidentRegion = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdOn = createdOn;
    }

    public String getItemTitle() {
        return this.incidentRegion;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getIncidentRegion() {
        return incidentRegion;
    }

}
