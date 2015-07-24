package org.stuartresearch.snapzuisfunner;

import org.stuartresearch.SnapzuAPI.Comment;

import rx.Observable;

/**
 * Created by jake on 7/23/15.
 */
public class SearchComments  {
    public static Observable findInComments(Comment[] hay, String[] needles) {
        return Observable.create(subscriber -> {
            boolean found;
            for (int i = 0; i < hay.length; i++) {
                found = true;
                for (int j = 0; j < needles.length; j++) {
                    if (!hay[i].getParagraph().toLowerCase().contains(needles[j].toLowerCase())) {
                        found = false;
                    }

                }
                if (found) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
            }
            subscriber.onCompleted();
        });
    }
}
