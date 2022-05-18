package ggc.deliveries;

import java.io.Serializable;
import ggc.notifications.*;
import ggc.Partner;

public abstract class DeliveryMethod implements Serializable {
    public abstract void deliver(Notification notification, Partner partner);
}
