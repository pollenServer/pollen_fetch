package pollen.pollen_fetch.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Pine {

    @Id
    @GeneratedValue
    private Long id;

    private String areaCode;
    
    private int today;

    private int tomorrow;

    private int dayaftertomorrow;
}