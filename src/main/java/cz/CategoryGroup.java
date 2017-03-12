package cz;

import java.util.Map;

/**
 * Created by vtkac on 27.02.2017.
 */
public class CategoryGroup {

  private String code;

  private Map<String, String> categories;

  public String getCode() {
    return code;
  }

  public void setCode( String code ) {
    this.code = code;
  }

  public Map<String, String> getCategories() {
    return categories;
  }

  public void setCategories( Map<String, String> categories ) {
    this.categories = categories;
  }
}
