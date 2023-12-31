package com.example.clothingapp.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class GenerativeCategoryProvider implements IProvider<Category> {

    private List<Category> categories;

    //NOTE(spec): Spec changed here
    public GenerativeCategoryProvider(IProvider<ClothingItem> allItems) {

        // TODO: Make category names non case-sensitive
        Set<String> categoryNames = new HashSet<>();
        for(ClothingItem item : allItems.iter()) {
            categoryNames.add(item.getCategory());
        }

        categories = new ArrayList<>(categoryNames.size());

        for(String category : categoryNames) {
            // Filter all items that have the same category, and assign them under the
            // current category
            categories.add(new Category(category, allItems.filter(clothingItem -> clothingItem.getCategory().equals(category))));
        }
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int index) {
        return categories.get(index);
    }
}
