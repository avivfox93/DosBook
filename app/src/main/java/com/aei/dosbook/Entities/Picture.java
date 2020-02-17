package com.aei.dosbook.Entities;

public class Picture {

    private String url;
    private String _id;
    private boolean modest;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SafeSearch getSafeSearch() {
        return safeSearch;
    }

    public void setSafeSearch(SafeSearch safeSearch) {
        this.safeSearch = safeSearch;
    }

    private SafeSearch safeSearch;

    public Picture(String url){this.url = url;}

}
