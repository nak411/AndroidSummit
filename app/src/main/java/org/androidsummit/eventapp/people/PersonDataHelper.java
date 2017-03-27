package org.androidsummit.eventapp.people;

import android.text.TextUtils;

import org.androidsummit.eventapp.model.generic.Person;

/**
 * A helper image_class for mapping/processing person related data.  This class depends on various person classes
 *
 * Created by Naveed on 4/24/15.
 */
public class PersonDataHelper {

    public static String getSubtitle(Person person) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(person.getRole())) {
            sb.append(person.getRole());
        }

        if (!TextUtils.isEmpty(person.getCompany())) {
            if (sb.length() > 0) {
                sb.append(", ").append(person.getCompany());
            } else {
                sb.append(person.getCompany());
            }
        }
        return sb.toString();
    }
}
