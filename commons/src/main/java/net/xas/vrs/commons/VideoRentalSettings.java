package net.xas.vrs.commons;

import org.joda.money.Money;

/**
 * Created by Xavi on 08/02/16.
 */
public class VideoRentalSettings {
    private Money basicPrice = Money.parse("SEK 30");
    private Money premiumPrice = Money.parse("SEK 40");

    public Money getBasicPrice() {
        return basicPrice;
    }

    public Money getPremiumPrice() {
        return premiumPrice;
    }

}
