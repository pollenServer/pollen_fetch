package pollen.pollen_fetch.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Pine extends BaseEntity{

    @Id
    private String areaNo;

    @Column
    private int today;

    @Column
    private int tomorrow;

    @Column
    private int dayaftertomorrow;

    @Column
    private int twodaysaftertomorrow;

    public Pine() {}

    public Pine(String areaNo) {
        this.areaNo = areaNo;
    }
}