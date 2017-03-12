package cz;

import java.util.List;

/**
 * Created by vtkac on 25.02.2017.
 */
public class Swimmer {

  private Long id;

  private String name;

  private String surname;

  private String gender;

  private String catYear;

  private List<String> categories;

  public Swimmer() {
  }

  public Long getId() {
    return id;
  }

  public void setId( Long id ) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname( String surname ) {
    this.surname = surname;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories( List<String> categories ) {
    this.categories = categories;
  }

  public String getGender() {
    return gender;
  }

  public void setGender( String gender ) {
    this.gender = gender;
  }

  public String getCatYear() {
    return catYear;
  }

  public void setCatYear( String catYear ) {
    this.catYear = catYear;
  }
}
