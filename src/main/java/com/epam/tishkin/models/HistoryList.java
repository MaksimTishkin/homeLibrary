package com.epam.tishkin.models;

import java.util.ArrayList;
import java.util.List;

public class HistoryList {
    private List<String> history;

    public HistoryList() {
        history = new ArrayList<>();
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
