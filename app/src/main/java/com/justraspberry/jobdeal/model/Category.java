package com.justraspberry.jobdeal.model;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.justraspberry.jobdeal.App;

import timber.log.Timber;

public class Category implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("parentId")
    @Expose
    private Integer parentId;
    @SerializedName("totalSubCategoryCount")
    @Expose
    private Integer totalSubs;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("subCategory")
    @Expose
    private ArrayList<Category> subCategory = null;

    private Boolean selected = false;

    public final static Parcelable.Creator<Category> CREATOR = new Creator<Category>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return (new Category[size]);
        }

    }
            ;

    protected Category(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.color = ((String) in.readValue((String.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        if (in.readByte() == 0x01) {
            subCategory = new ArrayList<Category>();
            in.readList(subCategory, Category.class.getClassLoader());
        } else {
            subCategory = null;
        }
        this.selected = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.parentId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalSubs = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Category() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Category> getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(ArrayList<Category> subCategory) {
        this.subCategory = subCategory;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getTotalSubs() {
        return totalSubs;
    }

    public void setTotalSubs(Integer totalSubs) {
        this.totalSubs = totalSubs;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(color);
        dest.writeValue(image);
        dest.writeValue(description);
        if (subCategory == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(subCategory);
        }
        dest.writeValue(selected);
        dest.writeValue(parentId);
        dest.writeValue(totalSubs);
    }

    public int describeContents() {
        return 0;
    }

    public Category getParent(){
        if (this.getParentId() == null)
            return null;

        for (Category c : App.getInstance().getCategories()){
            if(c.getId().equals(this.getParentId()))
                return c;
            for (Category cx : c.getSubCategory()){
                if (cx.getId().equals(this.parentId))
                    return cx;
            }
        }
        return null;
    }

    public Category getRoot(){
        if(getParent() != null && getParent().getParent() != null)

        for (Category c : App.getInstance().getCategories()){
            if(c.getId().equals(this.getParent().getParentId()))
                return c;

            for (Category cx : c.getSubCategory()){
                if (cx.getId().equals(this.getParent().getParentId()))
                    return cx;
            }
        }
        return null;
    }

}