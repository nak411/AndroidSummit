package org.androidsummit.eventapp.model.generic;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Model for a generic person
 *
 * Created by Naveed on 3/10/15.
 */
@ParseClassName("Person")
public class Person extends ParseObject{

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String COMPANY = "company";
    public static final String ROLE = "role";
    public static final String PROFILE_PIC = "profilePic";

    private String profilePicPath;


    public Person(){
        // A default constructor is required
    }

    public String getFirstName() {
        return getString(FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        put(FIRST_NAME, firstName);
    }

    public String getLastName() {
        return getString(LAST_NAME);
    }

    public void setLastName(String lastName) {
        put(LAST_NAME, lastName);
    }

    public void setCompany(String company) {
        put(COMPANY, company);
    }

    public String getCompany() {
        return getString(COMPANY);
    }

    public void setRole(String role) {
        put(ROLE, role);
    }

    public String getRole() {
        return getString(ROLE);
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }


    public void setProfilePic(ParseFile file) {
        put(PROFILE_PIC, file);
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public ParseFile getProfilePic() {
        return getParseFile(PROFILE_PIC);
    }
}