package cn.dravvern.util;

public class AreaInfo {
    private String areaId;
    private String areaName;
    private String areaqh;
    private String regionId;
    public String getAreaId() {
        return areaId;
    }
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
    public String getAreaName() {
        return areaName;
    }
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getAreaqh() {
        return areaqh;
    }
    public void setAreaqh(String areaqh) {
        this.areaqh = areaqh;
    }
    public String getRegionId() {
        return regionId;
    }
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
    
    public String toString() {
        return "AreaInfo [areaId=" + areaId + ", areaName=" + areaName + ", areaqh=" + areaqh + ", regionId=" + regionId
                + "]";
    }
}
