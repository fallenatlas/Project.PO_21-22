package ggc;

import ggc.notifications.Notification;

public interface Subject {
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObservers(Notification notification);
}
