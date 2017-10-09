package cn.dravvern.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.dravvern.util.AreaInfo;

public class Dao extends BaseDao {
    
    private List<AreaInfo> areaList = new ArrayList<AreaInfo>();
    private boolean areaInfoLoaded = false; 
    
    private Dao() {
    }

    private static class DaoLoader {
        private static final Dao instance = new Dao();
    }

    public static Dao getInstance() {
        return DaoLoader.instance;
    }
    
    public int getNumberType() {
        String sql = "select nvl(length(in_acc_nbr),0), nvl(length(in_user_id),0) "
                + "from jk_input "
                + "where rownum=1 and in_opt_user='" + UserInfo.getInstance().getUserLoginName() + "' " ;
        Object[] result = QueryOnlyNote(sql);
        
        if (result != null) {
            int nbrlength = (Integer) result[0];
            int idlength  = (Integer) result[1];
            System.out.println( nbrlength + "|" + idlength );
            if(nbrlength > 1) {
                return 1;
            } else if (idlength > 1) {
                return 2;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public int getNumberCnt(String sql) {
        Object object = QueryOnlyValue(sql);
        if (object instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) object;
            return bd.intValue();
        } else {
            return (Integer) object;
        }
    }
    
    public void loadAreaInfo() {
        String sql = "select area_id, area_id_name, area_qh, region_id from st.dim_area_id_t where area_id!='498' ";
        List<Object[]> list = QuerySomeNote(sql);
        for (int i = 0; i < list.size(); i++) {
            Object[] objects = list.get(i);
            AreaInfo areaInfo = new AreaInfo();
            areaInfo.setAreaId((String)objects[0]);
            areaInfo.setAreaName((String)objects[1]);
            areaInfo.setAreaqh((String)objects[2]);
            areaInfo.setRegionId((String)objects[3]);
            areaList.add(areaInfo);
        }
        areaInfoLoaded = true;
    }
    
    public AreaInfo getAreaByName(String areaName) {
        if(!areaInfoLoaded) {
            loadAreaInfo();
        }
        for (int i = 0; i < areaList.size(); i++) {
            AreaInfo areaInfo = areaList.get(i);
            if (areaName.equals(areaInfo.getAreaName())) {
                return areaInfo;
            }
        }
        return null;
    }
}
