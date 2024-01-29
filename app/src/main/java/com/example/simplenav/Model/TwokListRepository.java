package com.example.simplenav.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TwokListRepository implements Iterable<TwokRepository> {
    public final static String TAG = "TwokListRepository";
    private List<TwokRepository> twokListRepository = new ArrayList<>();

    public TwokListRepository() {}

    public TwokRepository getTwok(int i) {
        return twokListRepository.get(i);
    }

    public int getSize() {
        return twokListRepository.size();
    }

    public void add(TwokRepository t) {
        twokListRepository.add(t);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (TwokRepository twok : twokListRepository) {
            s.append(twok.getTwokText());
        }
        return s.toString();
    }

    @NonNull
    @Override
    public Iterator<TwokRepository> iterator() {
        return twokListRepository.iterator();
    }

    public void sort() {
        twokListRepository.sort(new Comparator<TwokRepository>() {
            @Override
            public int compare(TwokRepository twokRepository, TwokRepository t1) {
                if (twokRepository.tid > t1.tid) {
                    return 1;
                } return -1;
            }
        });
    }

    public boolean addIfNotPresent(TwokRepository twokReceived) {
        for (TwokRepository twokRepository : twokListRepository) {
            if (twokRepository.tid == twokReceived.tid) {
                return false;
            }
        }
        twokListRepository.add(twokReceived);
        return true;
    }

    public void clear() {
        twokListRepository.clear();
    }
}
