package ServerSide.dao;

import org.apache.commons.dbutils.BeanProcessor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CamelCaseBeanProcessor extends BeanProcessor {
    @Override
    protected int[] mapColumnsToProperties(java.sql.ResultSetMetaData rsmd,
                                           java.beans.PropertyDescriptor[] props) throws SQLException {
        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Map<String, Integer> propertyMap = new HashMap<>();

        for (int i = 0; i < props.length; i++) {
            propertyMap.put(props[i].getName().toLowerCase(), i);
        }

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || columnName.length() == 0) {
                columnName = rsmd.getColumnName(col);
            }

            // 下划线转驼峰
            String camel = toCamelCase(columnName).toLowerCase();
            Integer propertyIndex = propertyMap.get(camel);
            if (propertyIndex != null) {
                columnToProperty[col] = propertyIndex;
            } else {
                columnToProperty[col] = -1;
            }
        }

        return columnToProperty;
    }

    private String toCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        boolean toUpper = false;
        for (char c : name.toCharArray()) {
            if (c == '_') {
                toUpper = true;
            } else {
                result.append(toUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                toUpper = false;
            }
        }
        return result.toString();
    }
}

