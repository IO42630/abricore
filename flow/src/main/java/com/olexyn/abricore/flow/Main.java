package com.olexyn.abricore.flow;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class Main
{
    /**
     *
     * @param args
     */
    public static void main( String[] args ) {

        for (int i = 0; i<args.length; i++) {

            switch (args[i]) {
                case "-m":
                    switch(args[i+1]) {
                        case "scrape":
                            // run scrape mode
                            break;
                        case "trade":
                            // run trade mode
                            break;
                        case "train":
                            // run train mode
                            break;
                    }
            }
        }
    }
}
