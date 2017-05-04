package com.example.bhaum.dditconnect.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhaum on 09-03-2017.
 */

public class DummyContent1 {
    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyContent1.DummyItem1> ITEMS = new ArrayList<DummyContent1.DummyItem1>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyContent1.DummyItem1> ITEM_MAP = new HashMap<String, DummyContent1.DummyItem1>();

    private static final int COUNT = 25;




    public static void addItem(DummyContent1.DummyItem1 item) {
        ITEMS.add(0,item);

        ITEM_MAP.put(item.id, item);
    }

  /*  private static DummyContent1.DummyItem1 createDummyItem(int position) {
        return new DummyContent1.DummyItem1(String.valueOf(position), "Item " + position, makeDetails(position));
    }
    */

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem1 {
        public  String id;
        public  String content;
        public  String ntime;
        public  String name;
        public  String noview;
        public  String noans;
        public String aid;


        public DummyItem1(String aid,String id,String name, String content, String time,String nview,String nans) {
            this.id = id;
            this.content = content;
            this.ntime = time;
            this.name=name;
            this.aid=aid;
            noview=nview;
            noans=nans;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
