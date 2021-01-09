package com.olexyn.abricore.flow;

import com.olexyn.abricore.model.Asset;

public class Main {

    /**
     * -m mode target
     * -a asset
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        String modeEnumString = null;
        Asset asset = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-m":
                    modeEnumString = (args[i + 1] + "_" + args[i + 2]).toUpperCase();
                case "-a":
                    asset = new Asset(args[i + 1]);
            }
        }



        switch (ModeEnum.valueOf(modeEnumString)) {
            case SCRAPE_TW:
                new ScrapeMode(ModeEnum.SCRAPE_TW, asset).start();
                break;
            case TRADE_SW:
                // run trade mode
                break;
            case TRAIN:
                // run train mode
                break;
        }
    }

}
