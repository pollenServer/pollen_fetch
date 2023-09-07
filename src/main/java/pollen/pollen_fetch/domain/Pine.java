package pollen.pollen_fetch.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Pine {

    @Id
    @GeneratedValue
    private Long id;

    private String areaCode;
    
    private int today;

    private int tomorrow;

    private int dayaftertomorrow;

    public Pine(String areaCode, int today, int tomorrow, int dayaftertomorrow) {
        this.areaCode = areaCode;
        this.today = today;
        this.tomorrow = tomorrow;
        this.dayaftertomorrow = dayaftertomorrow;
    }
}