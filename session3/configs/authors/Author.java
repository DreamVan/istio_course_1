package com.course.servicemesh.authors.courseservicemeshauthors.models;

public class Author {
    private int id;
    private String firstName;
    private String lastName;
    private String namePrefix;

    public Author(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(final String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public Author withFirstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public Author withLastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public Author withNamePrefix(String namePrefix) {
        this.setNamePrefix(namePrefix);
        return this;
    }

}
