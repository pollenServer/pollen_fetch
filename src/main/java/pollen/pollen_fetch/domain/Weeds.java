package pollen.pollen_fetch.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Weeds {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String areaNo;

    private int today;

    private int tomorrow;

    private int dayaftertomorrow;

    public Weeds() {

    }

    public Weeds(String areaNo) {
        this.areaNo = areaNo;
    }

    public Weeds(String areaNo, int today, int tomorrow, int dayaftertomorrow) {
        this.areaNo = areaNo;
        this.today = today;
        this.tomorrow = tomorrow;
        this.dayaftertomorrow = dayaftertomorrow;
    }
}