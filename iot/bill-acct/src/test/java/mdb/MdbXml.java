package mdb;

/**
 * Created by geyunfeng on 2017/9/30.
 */
public class MdbXml {

  private String id;          //别名
  private String valueType;   //field value 类型
  private String fieldValue;  //字段的顺序
  private String values;

  public MdbXml() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MdbXml mdbXml = (MdbXml) o;

    return id != null ? id.equals(mdbXml.id) : mdbXml.id == null;

  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "MdbXml{" +
        "id='" + id + '\'' +
        ", valueType='" + valueType + '\'' +
        '}';
  }

  public String getId() {
    return id;
  }

  public String getValueType() {
    return valueType;
  }

  public String getFieldValue() {
    return fieldValue;
  }

  public String getValues() {
    return values;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public void setFieldValue(String fieldValue) {
    this.fieldValue = fieldValue;
  }

  public void setValues(String values) {
    this.values = values;
  }
}
