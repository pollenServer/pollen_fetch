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

    private String areaNo;
    
    private int today;

    private int tomorrow;

    private int dayaftertomorrow;

    public Pine(String areaNo, int today, int tomorrow, int dayaftertomorrow) {
        this.areaNo = areaNo;
        this.today = today;
        this.tomorrow = tomorrow;
        this.dayaftertomorrow = dayaftertomorrow;
    }
}