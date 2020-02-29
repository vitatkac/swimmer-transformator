package cz;

import lombok.Data;

import java.util.Map;

/**
 * Created by vtkac on 27.02.2017.
 */
@Data
public class CategoryGroup {

  private String code;

  private Map<String, String> categories;

}
