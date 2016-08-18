package org.androidsummit.eventapp.model.enums;

/**
 * Enum representing category for different events.
 *
 * Created by Naveed on 5/1/15.
 */
public enum EventCategory {

    FOOD(1),
    DEVELOP(2),
    DESIGN(3),
    OTHER(4);

    private int typeCode;

    private static EventCategory[] categories;

    EventCategory(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public static EventCategory getCategoryForTypeCode(int typeCode) {
        if (categories == null) {
            //Cache values since it is relatively expensive to keep calling this method
            categories = EventCategory.values();
        }

        for (int i = 0; i < categories.length; i++) {
            if (categories[i].compare(typeCode)) {
                return categories[i];
            }
        }
        return OTHER;
    }

    public boolean compare(int typeCode) {
        return this.typeCode == typeCode;
    }
}
