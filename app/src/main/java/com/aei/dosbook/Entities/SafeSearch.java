package com.aei.dosbook.Entities;

import static com.aei.dosbook.Entities.SafeSearch.Annonation.VERY_UNLIKELY;

public class SafeSearch {
    private String _id;
    enum Annonation{
        VERY_UNLIKELY,UNLIKELY,POSSIBLE,LIKELY,VERY_LIKELY
    }

    private Annonation spoof = VERY_UNLIKELY,medical = VERY_UNLIKELY,adult = VERY_UNLIKELY,
            violence = VERY_UNLIKELY,man = VERY_UNLIKELY,woman = VERY_UNLIKELY;

    public SafeSearch(){}

    public Annonation getSpoof() {
        return spoof;
    }

    public void setSpoof(Annonation spoof) {
        this.spoof = spoof;
    }

    public Annonation getMedical() {
        return medical;
    }

    public void setMedical(Annonation medical) {
        this.medical = medical;
    }

    public Annonation getAdult() {
        return adult;
    }

    public void setAdult(Annonation adult) {
        this.adult = adult;
    }

    public Annonation getViolence() {
        return violence;
    }

    public void setViolence(Annonation violence) {
        this.violence = violence;
    }

    public Annonation getMan() {
        return man;
    }

    public void setMan(Annonation man) {
        this.man = man;
    }

    public Annonation getWoman() {
        return woman;
    }

    public void setWoman(Annonation woman) {
        this.woman = woman;
    }
}
