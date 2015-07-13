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

    public Profile(String name, String cookies, String imageUrl) {
        this.name = name;
        this.cookies = cookies;
        this.imageUrl = imageUrl;
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

    @Override
    public boolean equals(Object o) {
        return name.equals(o);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public ProfileDrawerItem toProfileDrawerItem(int id) {
        ProfileDrawerItem item = new ProfileDrawerItem();
        if (name != null) {
            item = item.withName(name);
        }
        if (imageUrl != null) {
            item = item.withIcon(imageUrl);
        }

        item.setIdentifier(id);

        return item;
    }
}
