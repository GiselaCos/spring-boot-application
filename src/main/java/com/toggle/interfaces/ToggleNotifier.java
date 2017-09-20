package com.toggle.interfaces;

import com.toggle.beans.ToggleObserver;
import com.toggle.exceptions.ToggleNotFoundException;

import java.util.Observer;

public interface ToggleNotifier {

    void addObserver(ToggleObserver observer) throws ToggleNotFoundException;

    void removeObserver(ToggleObserver observer);

    void notifyKeyObservers(String key);
}
