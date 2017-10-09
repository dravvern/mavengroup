package cn.dravvern.test;

import java.io.File;

import cn.dravvern.thread.FileMergeThread;

public class TestLoadData {

    public static void main(String[] args) {
        String filepath = "C:\\Users\\dravvern\\Documents/111.xlsx";
        filepath = "C:/Users/dravvern/Documents/20170906084912_名单制信息.zip";
        String targetFile = "C:/Users/dravvern/Documents/1.xlsx";

//        String sql = "select b.rn, in_user_id, 用户地市 地市, 用户ID, 用户号码, 套餐ID, 套餐名称, 入网时间, "
//                + "销户时间, 省内客户分群 , 发展渠道ID, 发展渠道业绩归属部门, 用户状态, 停机时间 " + "from st.dw_is_mvuser_di a, "
//                + "(select * from (select xx.*, rownum rn from jk_input xx where rownum<=?) where rn>=?) b "
//                + "where b.in_user_id=a.用户ID(+) and in_opt_user='11' ";
//        String cntsql = "select count(*) from st.dw_is_mvuser_di a, jk_input b "
//                + "where b.in_user_id=a.用户ID(+) and in_opt_user='11' ";
        
        FileMergeThread enit = new FileMergeThread(new File(filepath), new File(targetFile));
        enit.start();
        
//        String string = "3838038200200010263.22";
//        BigDecimal bigDecimal = new BigDecimal(string);
//        BigDecimal longDecimal = new BigDecimal(bigDecimal.longValue());
//        BigDecimal decimal = bigDecimal.subtract(longDecimal);
//        System.out.println("disp0|" + decimal + "|" + longDecimal);
//        if (bigDecimal.subtract(longDecimal).compareTo(BigDecimal.ZERO) != 0) {
//            System.out.println("disp1|" + bigDecimal.toString());
//            if (longDecimal.toString().length() <= 11) {
//                System.out.println("disp4|" + bigDecimal.doubleValue());
//            } else {
//                System.out.println("disp5|" + bigDecimal.toString());
//            }
//        } else if (bigDecimal.toString().length() <= 11) {
//            System.out.println("disp2|" + bigDecimal.longValue());
//        } else {
//            System.out.println("disp3|" + bigDecimal.toString());
//        }
        
    }

}
