package org.stuartresearch.snapzuisfunner;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.orm.SugarRecord;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by jake on 7/9/15.
 */
@Parcel
public class Profile extends SugarRecord<Profile> implements Serializable {
    String name;
    String imageUrl;
    String cookies;

    public Profile() {
    }

    public Profile(String name, String cookies) {
        this.name = name;
        this.cookies = cookies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public ProfileDrawerItem toProfileDrawerItem() {
        ProfileDrawerItem item = new ProfileDrawerItem();
        if (name != null) {
            item = item.withName(name);
        }
        if (imageUrl != null) {
            item = item.withIcon(imageUrl);
        }

        return item;
    }
}
