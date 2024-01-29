package com.example.simplenav.Model;

import java.util.ArrayList;
import java.util.List;

public class TwokListForProfileRepository {
    private List<TwokRepository> twokListForProfileRepository = new ArrayList<TwokRepository>();

    public TwokListForProfileRepository() {}

    public TwokRepository getContact(int i) {
        return twokListForProfileRepository.get(i);
    }

    public int getSize() {
        return twokListForProfileRepository.size();
    }
    
    public void initWithfakeDataForProfile() {
        for (int i=0; i<5; i++) {
            double rand = (int)(Math.random() * 1000);
            twokListForProfileRepository.add(new TwokRepository());
        }
    }

}
