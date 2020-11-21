package com.roshik.domains;

import java.util.HashMap;
import java.util.Map;

public enum HotSpots {

    Life("Жизнь"),
    Work("Работа"),
    Personal("Личное");

    private String title;

    HotSpots(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }


    private static final Map<String, HotSpots> lookup = new HashMap<>();

    static
    {
        for(HotSpots hs : HotSpots.values())
        {
            lookup.put(hs.getTitle(), hs);
        }
    }

    public static HotSpots get(String title)
    {
        return lookup.get(title);
    }

}
