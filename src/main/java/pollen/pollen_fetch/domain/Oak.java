package pollen.pollen_fetch.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Oak {

    @Id
    @GeneratedValue
    private Long id;
    
    private String areaNo;

    @Nullable
    private int today;

    @Nullable
    private int tomorrow;

    @Nullable
    private int dayaftertomorrow;

    public Oak(String areaNo) {
        this.areaNo = areaNo;
    }

    public Oak(String areaNo, int today, int tomorrow, int dayaftertomorrow) {
        this.areaNo = areaNo;
        this.today = today;
        this.tomorrow = tomorrow;
        this.dayaftertomorrow = dayaftertomorrow;
    }
}