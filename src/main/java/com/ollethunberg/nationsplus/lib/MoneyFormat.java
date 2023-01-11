package com.ollethunberg.nationsplus.lib;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormat {
    private static Locale usa = new Locale("en", "US");

    public static NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);
}
