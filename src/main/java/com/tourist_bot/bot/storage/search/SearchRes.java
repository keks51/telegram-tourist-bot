package com.tourist_bot.bot.storage.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class SearchRes {

    public final Map<Integer, ArrayList<FoundTourismAttraction>> groupedAttractions;
    public final int distance;

    private int numberOfAttractions;

    public SearchRes(Map<Integer, ArrayList<FoundTourismAttraction>> groupedAttractions, int distance, int numberOfAttractions) {
        this.groupedAttractions = groupedAttractions;
        this.distance = distance;
        this.numberOfAttractions = numberOfAttractions;
    }

    @Override
    public String toString() {
        String str = groupedAttractions.entrySet().stream()
                .map(e -> "[Tourism: " + e.getKey() + " Attractions(" + e.getValue().stream().map(Object::toString).collect(Collectors.joining(", ")) + ")")
                .collect(Collectors.joining(", "));
        return "SearchRes[MaxDistance=" + distance + " NumberOfAttractions: " + numberOfAttractions + " " + str + "]";
    }


    public Set<Long> getFoundAttractions() {
        return groupedAttractions.values().stream()
                .flatMap(Collection::stream).map(e -> e.id).collect(Collectors.toSet());
    }

    public int getNumberOfAttractions() {
        return numberOfAttractions;
    }

    public void decNumberOfAttractions() {
        numberOfAttractions = numberOfAttractions - 1;
    }

}
