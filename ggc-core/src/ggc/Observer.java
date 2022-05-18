package ggc;

import ggc.notifications.Notification;

public interface Observer {
    public void update(Notification notification);
}
