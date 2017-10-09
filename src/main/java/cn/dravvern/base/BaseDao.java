package cn.dravvern.base;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dravvern.util.Public;

public class BaseDao extends JdbcUtil {

    public int insertTableBatch(String sql, List<Object[]> oList) {
        Connection conn = getConnection();
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement(sql);
            int i = 0;
            for (; i < oList.size(); i++) {
                Object[] objects = oList.get(i);
                for (int j = 0; j < objects.length; j++) {
                    Object object = objects[j];
                    setPStmtParam(pStatement, j + 1, object);
                }
                pStatement.addBatch();
                if (i % 100000 == 0) {
                    pStatement.executeBatch();
                }
            }
            pStatement.executeBatch();
            conn.commit();
            return i;
        } catch (Exception e) {
            Public.addLog(e.getMessage());
            return -1;
        } finally {
            closePStatement(pStatement);
        }
    }

    public int updateTable(String sql, Object... objects) {
        Connection conn = getConnection();
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                setPStmtParam(pStatement, i + 1, object);
            }
            int cnt = pStatement.executeUpdate();
            conn.commit();
            return cnt;
        } catch (Exception e) {
            Public.addLog(e.getMessage());
            return -1;
        } finally {
            closePStatement(pStatement);
        }
    }

    public List<Object[]> QueryHead(String sql, Object... objects) {
        Map<String, List<Object[]>> map = QueryResult(sql, objects);
        return QueryHead(map);
    }

    public List<Object[]> QueryHead(Map<String, List<Object[]>> map) {
        List<Object[]> list = map.get("HEAD");

        if (list.size() == 2) {
            return list;
        } else {
            return null;
        }
    }

    public Object[] QueryOnlyNote(String sql, Object... objects) {
        Map<String, List<Object[]>> map = QueryResult(sql, objects);
        return QueryOnlyNote(map);
    }

    public Object[] QueryOnlyNote(Map<String, List<Object[]>> map) {
        List<Object[]> list = map.get("BODY");

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<Object[]> QuerySomeNote(String sql, Object... objects) {
        Map<String, List<Object[]>> map = QueryResult(sql, objects);
        return QuerySomeNote(map);
    }

    public List<Object[]> QuerySomeNote(Map<String, List<Object[]>> map) {
        List<Object[]> list = map.get("BODY");
        return list;
    }

    public Object QueryOnlyValue(String sql, Object... objects) {
        Map<String, List<Object[]>> map = QueryResult(sql, objects);
        return QueryOnlyValue(map);
    }

    public Object QueryOnlyValue(Map<String, List<Object[]>> map) {
        List<Object[]> list = map.get("BODY");

        if (list.size() > 0 && list.get(0).length > 0) {
            return list.get(0)[0];
        } else {
            return null;
        }
    }

    public Map<String, List<Object[]>> QueryResult(String sql, Object... objects) {
        Map<String, List<Object[]>> map = new HashMap<String, List<Object[]>>();
        Connection conn = getConnection();
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;
        try {
            pStatement = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                setPStmtParam(pStatement, i + 1, object);
            }
            resultSet = pStatement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            List<Object[]> headlist = new ArrayList<Object[]>();
            Object[] head = new Object[columnCount];
            Object[] type = new Object[columnCount];
            for (int i = 1; i < columnCount + 1; i++) {
                head[i - 1] = resultSet.getMetaData().getColumnName(i);
                type[i - 1] = resultSet.getMetaData().getColumnTypeName(i);
            }
            headlist.add(head);
            headlist.add(type);
            map.put("HEAD", headlist);

            List<Object[]> bodylist = new ArrayList<Object[]>();
            // int curRow = 0;
            while (resultSet.next()) {
                Object[] body = new Object[columnCount];
                for (int j = 1; j < columnCount + 1; j++) {
                    body[j - 1] = resultSet.getObject(j);
                }
                bodylist.add(body);
            }
            map.put("BODY", bodylist);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePStatement(pStatement);
            closeResultSet(resultSet);
        }
        return map;
    }

    private static void setPStmtParam(PreparedStatement pStatement, int i, Object object) throws SQLException {

        if (object == null) {
            pStatement.setNull(i, Types.NULL);
        } else if (object instanceof Integer) {
            pStatement.setInt(i, (Integer) object);
        } else if (object instanceof BigDecimal) {
            pStatement.setBigDecimal(i, (BigDecimal) object);
        } else if (object instanceof Timestamp) {
            pStatement.setTimestamp(i, (Timestamp) object);
        } else if (object instanceof String) {
            pStatement.setString(i, (String) object);
        } else if (object instanceof Double) {
            pStatement.setDouble(i, (Double) object);
        } else if (object instanceof Float) {
            pStatement.setFloat(i, (Float) object);
        } else if (object instanceof java.util.Date) {
            Timestamp timestamp = new Timestamp(((java.util.Date) object).getTime());
            pStatement.setTimestamp(i, timestamp);
        } else {
            System.out.println(object.getClass().getName() + i);
            pStatement.setObject(i, object);
        }
    }

    public static String execProc(String svcnum, String mod, String cycle) {
        Connection conn = getConnection();
        String procresult = null;
        try {
            CallableStatement cstmt = conn.prepareCall("{call chmod_jk_b(?,?,?,?)}");

            cstmt.registerOutParameter(4, Types.VARCHAR);

            cstmt.setString(1, svcnum);
            cstmt.setString(2, mod);
            cstmt.setString(3, cycle);

            cstmt.execute();
            procresult = cstmt.getString(4);
            // System.out.println("第4个参数=\n" + procresult);

            cstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            procresult = "调用失败,请稍后重试";
        }
        return procresult;
    }

}
