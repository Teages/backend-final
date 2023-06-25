package cn.edu.bupt.backendfinal.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

@MappedJdbcTypes(JdbcType.ARRAY)
public class ListArrayTypeHandler extends BaseTypeHandler<List<Object>> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List<Object> parameter, JdbcType jdbcType)
      throws SQLException {
    Connection conn = ps.getConnection();
    Array array = conn.createArrayOf("integer", parameter.toArray());
    ps.setArray(i, array);
  }

  @Override
  public List<Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return arrayToList(rs.getArray(columnName));
  }

  @Override
  public List<Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return arrayToList(rs.getArray(columnIndex));
  }

  @Override
  public List<Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return arrayToList(cs.getArray(columnIndex));
  }

  private List<Object> arrayToList(Array array) {
    if (array == null) {
      return null;
    }
    try {
      Object[] objects = (Object[]) array.getArray();
      return Arrays.asList(objects);
    } catch (Exception e) {
      return null;
    }
  }
}
