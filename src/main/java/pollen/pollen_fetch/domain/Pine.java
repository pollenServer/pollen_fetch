package pollen.pollen_fetch.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@Setter
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