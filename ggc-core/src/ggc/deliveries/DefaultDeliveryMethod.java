package ggc.deliveries;

import ggc.notifications.Notification;
import ggc.Partner;

public class DefaultDeliveryMethod extends DeliveryMethod {
    @Override
    public void deliver(Notification notification, Partner partner) {
        partner.addNotification(notification);
    }
}
