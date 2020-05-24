package com.pthhack22.androideatvernew.ui.menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pthhack22.androideatvernew.Callback.ICategoryCallbackListener;
import com.pthhack22.androideatvernew.Common.Common;
import com.pthhack22.androideatvernew.Model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends ViewModel implements ICategoryCallbackListener {

    private MutableLiveData<List<CategoryModel>> categoryListMultable;
    private MutableLiveData<String> messageError = new MutableLiveData<>(  );
    private ICategoryCallbackListener icategoryCallbackListener;

    public MenuViewModel() {
        icategoryCallbackListener = this;
    }

    public MutableLiveData<List<CategoryModel>> getCategoryListMultable() {
        if(categoryListMultable == null)
        {
            categoryListMultable = new MutableLiveData<>(  );
            messageError = new MutableLiveData<>(  );
            loadCategories();
        }
        return categoryListMultable;
    }

    private void loadCategories() {
        List<CategoryModel> tempList = new ArrayList<>(  );
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference( Common.CATEGORY_REF );
        categoryRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    CategoryModel categoryModel = itemSnapShot.getValue(CategoryModel.class);
                    categoryModel.setMenu_id( itemSnapShot.getKey() );
                    tempList.add( categoryModel );
                }
                icategoryCallbackListener.onCategoryLoadSuccess( tempList );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                icategoryCallbackListener.onCategoryLoadFailed( databaseError.getMessage() );
            }
        } );
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onCategoryLoadSuccess(List<CategoryModel> categoryModelList) {
        categoryListMultable.setValue( categoryModelList );
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        messageError.setValue( message );
    }
}