//package com.dtafox.finalappro.models;
//
//public class task {
//    private String title;
//    private String description;
//    private String date;
//
//    public task(String title, String description, String date) {
//        this.title = title;
//        this.description = description;
//        this.date = date;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public String getDate() {
//        return date;
//    }
//}
package com.dtafox.finalappro.models;

public class task {
    private String id;          
    private String title;
    private String description;
    private String date;
    private String userId;      // Added userId field for Firebase queries

    public task() {} // Required empty constructor for Firebase

    public task(String id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Constructor without ID (for compatibility)
    public task(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Constructor with userId
    public task(String id, String title, String description, String date, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.userId = userId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    // Setters (required for Firebase)
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
