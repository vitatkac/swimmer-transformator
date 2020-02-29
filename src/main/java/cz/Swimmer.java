package cz;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by vtkac on 25.02.2017.
 */
@Data
public class Swimmer {

  private Long id;

  private String name;

  private String surname;

  private String gender;

  private String catYear;

  private Map<String, String> categories;

}
