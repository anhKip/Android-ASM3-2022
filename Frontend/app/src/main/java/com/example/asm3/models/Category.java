package com.example.asm3.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
    private String name;
    private String _id;
    private ArrayList<SubCategory> subCategories;

    public static String nameKey="name";
    public static String idKey="_id";
    public static String subCategoriesKey = "subCategories";

    public Category(String name, String _id, ArrayList<SubCategory> subCategories) {
        this.name = name;
        this._id = _id;
        this.subCategories = subCategories;

    }

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public static Category fromJSON(JSONObject jsonObject) {
        Category category = null;
        String name = "";
        String _id = "";
        ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
        if (jsonObject != null) {
            try {
                name = jsonObject.getString(nameKey);
                _id = jsonObject.getString(idKey);
                JSONArray subCategoryArray = jsonObject.getJSONArray(subCategoriesKey);
                for (int i = 0; i < subCategoryArray.length(); ++i) {
                    SubCategory subCategory = SubCategory.fromJSON(subCategoryArray.getJSONObject(i));
                    subCategories.add(subCategory);
                }
                category = new Category(name, _id, subCategories);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
        return category;
    }

    public static JSONObject toJSON(Category category) {
        JSONObject jsonObject = null;
        if (category != null) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put(nameKey, category.name);
                jsonObject.put(idKey, category._id);
                ArrayList<String> subCategories = new ArrayList<String>();
                for (int i = 0; i < category.subCategories.size(); ++i) {
                    subCategories.add(category.subCategories.get(i).get_id());
                }
                jsonObject.put(subCategoriesKey, subCategories);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
        return jsonObject;
    }
}