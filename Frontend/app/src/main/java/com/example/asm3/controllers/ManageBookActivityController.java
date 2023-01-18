package com.example.asm3.controllers;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm3.ManageBookActivity;
import com.example.asm3.R;
import com.example.asm3.base.adapter.GenericAdapter;
import com.example.asm3.base.adapter.viewHolder.SubCategoryHolder;
import com.example.asm3.base.controller.BaseController;
import com.example.asm3.base.localStorage.LocalFileController;
import com.example.asm3.base.networking.services.AsyncTaskCallBack;
import com.example.asm3.base.networking.services.DeleteAuthenticatedData;
import com.example.asm3.base.networking.services.GetData;
import com.example.asm3.base.networking.services.PostAuthenticatedData;
import com.example.asm3.config.Constant;
import com.example.asm3.config.Helper;
import com.example.asm3.models.ApiData;
import com.example.asm3.models.ApiList;
import com.example.asm3.models.Book;
import com.example.asm3.models.Category;
import com.example.asm3.models.SubCategory;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ManageBookActivityController extends BaseController implements
        AsyncTaskCallBack,
        MaterialButtonToggleGroup.OnButtonCheckedListener,
        SubCategoryHolder.OnSelectListener,
        View.OnClickListener {

    private Book book;
    private PostAuthenticatedData postAuthenticatedData;
    private DeleteAuthenticatedData deleteAuthenticatedData;
    private GetData getData;
    private ArrayList<Category> categories;
    private ArrayList<SubCategory> foreign = new ArrayList<>();
    private ArrayList<SubCategory> domestic = new ArrayList<>();
    private ArrayList<SubCategory> text = new ArrayList<>();
    private ArrayList<SubCategory> displayList = new ArrayList<>();
    private Category selectedCategory;
    private Bitmap productPhoto;

    private MaterialButtonToggleGroup categoriesBtnGrp;
    private TextView cateNotifyTxt;
    private View subCateTopDivider;
    private GenericAdapter<SubCategory> subCateAdapter;
    private RecyclerView subCateRecView;
    private ImageView productView;
    private Button getImageButton;
    private Button uploadProduct;
    private Button updateProduct;
    private Button removeProduct;
    private TextInputEditText productNameEt, authorRegisEt, descriptionEt, priceEt, quantityEt, publishedAtEt;
    private RadioButton newProduct;
    private RadioButton usedProduct;
    private String productId;

    private String token;

    public ManageBookActivityController(Context context, FragmentActivity activity) {
        super(context, activity);
    }

    // render functions
    @Override
    public void onInit(){

        if (!isAuth()) {
            getActivity().finish();
        } else {
            token = getToken();
            getAllCategories();

            categoriesBtnGrp = getActivity().findViewById(R.id.manageProductCategoriesBtnGrp);
            subCateRecView = getActivity().findViewById(R.id.manageProductSubCateRecView);
            cateNotifyTxt = getActivity().findViewById(R.id.manageProductCateNotifyTxt);
            subCateTopDivider = getActivity().findViewById(R.id.manageProductSubCateTopDivider);
            productView = getActivity().findViewById(R.id.productImage);
            getImageButton = getActivity().findViewById(R.id.getImageButton);
            uploadProduct = getActivity().findViewById(R.id.uploadProduct);
            updateProduct = getActivity().findViewById(R.id.updateProduct);
            removeProduct = getActivity().findViewById(R.id.removeProduct);
            productNameEt = getActivity().findViewById(R.id.productNameEt);
            authorRegisEt = getActivity().findViewById(R.id.authorRegisEt);
            descriptionEt = getActivity().findViewById(R.id.descriptionEt);
            priceEt = getActivity().findViewById(R.id.priceEt);
            quantityEt = getActivity().findViewById(R.id.quantityEt);
            publishedAtEt = getActivity().findViewById(R.id.publishedAtEt);
            newProduct = getActivity().findViewById(R.id.newProduct);
            usedProduct = getActivity().findViewById(R.id.usedProduct);
            subCateAdapter = generateSubCateAdapter();

            categoriesBtnGrp.addOnButtonCheckedListener(this);

            getImageButton.setOnClickListener(this);
            uploadProduct.setOnClickListener(this);
            updateProduct.setOnClickListener(this);
            removeProduct.setOnClickListener(this);

            if (isUpload()) {
                uploadProduct.setVisibility(View.VISIBLE);
                subCateRecView.setAdapter(subCateAdapter);
                subCateRecView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                updateProduct.setVisibility(View.VISIBLE);
                removeProduct.setVisibility(View.VISIBLE);
                setProductId();
                getProduct(productId);
            }
        }
    }

    @Override
    public void onSubCateClick(int position, View view, MaterialCheckBox subCateCheckBox) {
        boolean newStatus = false;
        switch (view.getId()) {
            case R.id.subCateBody:
                newStatus = !subCateCheckBox.isChecked();
                subCateCheckBox.setChecked(newStatus);
                break;
            case R.id.subCateCheckBox:
                newStatus = subCateCheckBox.isChecked();
                break;
        }
        displayList.get(position).setChosen(newStatus);
    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        subCateTopDivider.setVisibility(View.VISIBLE);
        cateNotifyTxt.setVisibility(View.GONE);
        subCateRecView.setVisibility(View.VISIBLE);
        switch (group.getCheckedButtonId()) {
            case R.id.manageProductForeignCateBtn:
                displayList.clear();
                displayList.addAll(foreign);
                selectedCategory = categories.get(0);
                subCateAdapter.notifyDataSetChanged();
                break;
            case R.id.manageProductDomesticCateBtn:
                displayList.clear();
                displayList.addAll(domestic);
                selectedCategory = categories.get(1);
                subCateAdapter.notifyDataSetChanged();
                break;
            case R.id.manageProductTextCateBtn:
                displayList.clear();
                displayList.addAll(text);
                selectedCategory = categories.get(2);
                subCateAdapter.notifyDataSetChanged();
                break;
        }
        if (group.getCheckedButtonId() == -1) {
            subCateTopDivider.setVisibility(View.GONE);
            cateNotifyTxt.setVisibility(View.VISIBLE);
            subCateRecView.setVisibility(View.GONE);
        }
    }

    // helper functions
    private GenericAdapter<SubCategory> generateSubCateAdapter() {
        return new GenericAdapter<SubCategory>(displayList) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_item, parent, false);
                return new SubCategoryHolder(view, ManageBookActivityController.this);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, SubCategory item) {
                SubCategoryHolder subCategoryHolder = (SubCategoryHolder) holder;
                subCategoryHolder.getSubCateTxt().setText(item.getName());
                subCategoryHolder.getSubCateCheckBox().setChecked(item.isChosen());
            }
        };
    }

    public boolean isUpload() {
        Intent intent = getActivity().getIntent();
        if (intent.getExtras().get(Constant.isUploadKey) != null) {
            if ((Integer.parseInt(intent.getExtras().get(Constant.isUploadKey).toString()) == Constant.uploadCode)){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setProductId() {
        Intent intent = getActivity().getIntent();
        productId = intent.getExtras().get(Constant.productIdKey).toString();
    }

    public void openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, Constant.cameraPermissionCode);
            } else {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent, Constant.cameraRequest);
            }
        }
    }

    public void getImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.galleryPermissionCode);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, Constant.galleryRequest);
            }
        }
    }

    private void setCategoriesGrp(int cateBtn) {
        categoriesBtnGrp.check(cateBtn);
    }

    public void onUploadProduct() {
        String productName = "";
        String productAuthor = "";
        String productDescription = "";
        float productPrice = 0F;
        int productQuantity = 0;
        String productPublishedAt = "";
        String productCategory = "";
        ArrayList<String> productSubCategory = new ArrayList<String>();
        String productCustomer = "";
        productId = "";
        boolean isProductNew = false;
        String productImage = "";

        productName = productNameEt.getText().toString();
        productAuthor = authorRegisEt.getText().toString();
        productDescription = descriptionEt.getText().toString();
        productPrice = Float.parseFloat(priceEt.getText().toString());
        productQuantity = Integer.parseInt(quantityEt.getText().toString());
        productPublishedAt = publishedAtEt.getText().toString();
        productCategory = selectedCategory.get_id();
        for (int i = 0; i < displayList.size(); ++i) {
            if (displayList.get(i).isChosen()) {
                productSubCategory.add(displayList.get(i).get_id());
            }
        }

        if (newProduct.isChecked()) {
            isProductNew = true;
        }

        productImage = Helper.bitmapToString(productPhoto);

        Book product = new Book(productName, productAuthor, productDescription, productPrice, productQuantity, productPublishedAt, productCategory, productSubCategory, productCustomer, productId, isProductNew, productImage);
        uploadBook(product);
    }

    public void onUpdateProduct() {
        String productName = "";
        String productAuthor = "";
        String productDescription = "";
        float productPrice = 0F;
        int productQuantity = 0;
        String productPublishedAt = "";
        String productCategory = "";
        ArrayList<String> productSubCategory = new ArrayList<String>();
        String productCustomer = "";
        boolean isProductNew = false;
        String productImage = "";

        productName = productNameEt.getText().toString();
        productAuthor = authorRegisEt.getText().toString();
        productDescription = descriptionEt.getText().toString();
        productPrice = Float.parseFloat(priceEt.getText().toString());
        productQuantity = Integer.parseInt(quantityEt.getText().toString());
        productPublishedAt = publishedAtEt.getText().toString();
        productCategory = selectedCategory.get_id();
        for (int i = 0; i < displayList.size(); ++i) {
            if (displayList.get(i).isChosen()) {
                productSubCategory.add(displayList.get(i).get_id());
            }
        }

        if (newProduct.isChecked()) {
            isProductNew = true;
        }

        productImage = Helper.bitmapToString(productPhoto);

        Book product = new Book(productName, productAuthor, productDescription, productPrice, productQuantity, productPublishedAt, productCategory, productSubCategory, productCustomer, productId, isProductNew, productImage);
        updateBook(product);
    }

    public void onRemoveProduct() {
        deleteBook(productId);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.getImageButton:
                getImageFromGallery();
                break;
            case R.id.uploadProduct:
                onUploadProduct();
                break;
            case R.id.updateProduct:
                onUpdateProduct();
                break;
            case R.id.removeProduct:
                onRemoveProduct();
                break;
        }
    }

    // request functions
    public void uploadBook(Book inputBook){
        postAuthenticatedData = new PostAuthenticatedData(getContext(), this);
        postAuthenticatedData.setEndPoint(Constant.uploadBook);
        postAuthenticatedData.setTaskType(Constant.uploadBookTaskType);
        postAuthenticatedData.setToken(token);
        postAuthenticatedData.execute(Book.toJSON(inputBook));
    }

    public void updateBook(Book inputBook) {
        postAuthenticatedData = new PostAuthenticatedData(getContext(), this);
        postAuthenticatedData.setEndPoint(Constant.updateBook);
        postAuthenticatedData.setTaskType(Constant.updateBookTaskType);
        postAuthenticatedData.setToken(token);
        postAuthenticatedData.execute(Book.toJSON(inputBook));
    }

    public void deleteBook(String bookId) {
        deleteAuthenticatedData = new DeleteAuthenticatedData(getContext(), this);
        deleteAuthenticatedData.setEndPoint(Constant.deleteBook + "/" + bookId);
        deleteAuthenticatedData.setTaskType(Constant.deleteBookTaskType);
        deleteAuthenticatedData.setToken(token);
        deleteAuthenticatedData.execute();
    }

    public void getAllCategories() {
        getData = new GetData(getContext(), this);
        getData.setEndPoint(Constant.getAllCategories);
        getData.setTaskType(Constant.getAllCategoriesTaskType);
        getData.execute();
    }

    public void getProduct(String id) {
        getData = new GetData(getContext(), this);
        getData.setEndPoint(Constant.getProduct + "/" + id);
        getData.setTaskType(Constant.getProductTaskType);
        getData.execute();
    }

    // callback functions
    @Override
    public void onFinished(String message,String taskType){
        if (taskType.equals(Constant.uploadBookTaskType)){
            getActivity().finish();
        } else if (taskType.equals(Constant.getAllCategoriesTaskType)) {
            ApiList<Category> apiList = ApiList.fromJSON(ApiList.getData(message),Category.class);
            categories = apiList.getList();
            foreign = categories.get(0).getSubCategories();
            domestic = categories.get(1).getSubCategories();
            text = categories.get(2).getSubCategories();
        } else if (taskType.equals(Constant.getProductTaskType)) {
            ApiData<Book> apiData = ApiData.fromJSON(ApiData.getData(message), Book.class);
            Book product = apiData.getData();
            productNameEt.setText(product.getName());
            authorRegisEt.setText(product.getAuthor());
            descriptionEt.setText(product.getDescription());
            priceEt.setText(String.valueOf(product.getPrice()));
            quantityEt.setText(String.valueOf(product.getQuantity()));
            if (categories.get(0).get_id().equals(product.getCategory())) {
                categoriesBtnGrp.check(R.id.manageProductForeignCateBtn);
            } else if (categories.get(1).get_id().equals(product.getCategory())) {
                categoriesBtnGrp.check(R.id.manageProductDomesticCateBtn);
            } else if (categories.get(2).get_id().equals(product.getCategory())) {
                categoriesBtnGrp.check(R.id.manageProductTextCateBtn);
            }

            ArrayList<String> productSubCategory = product.getSubCategory();
            for (int i = 0; i < displayList.size(); ++i) {
                for (int index = 0; index < productSubCategory.size(); ++index) {
                    if (displayList.get(i).get_id().equals(productSubCategory.get(i))) {
                        displayList.get(i).setChosen(true);
                    }
                }
            }

            subCateRecView.setAdapter(subCateAdapter);
            subCateRecView.setLayoutManager(new LinearLayoutManager(getContext()));

            if (product.isNew()) {
                newProduct.setChecked(true);
                usedProduct.setChecked(false);
            } else {
                newProduct.setChecked(false);
                usedProduct.setChecked(true);
            }

            productPhoto = Helper.stringToBitmap(product.getImage());
            productView.setImageBitmap(productPhoto);
        }
    }

    @Override
    public void onError(String taskType) {

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent;
            switch (requestCode){
                case Constant.cameraPermissionCode:
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(intent, Constant.cameraRequest);
                    break;
                case Constant.galleryPermissionCode:
                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    getActivity().startActivityForResult(intent, Constant.galleryRequest);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.cameraRequest:
                    productPhoto = (Bitmap) data.getExtras().get("data");
                    productView.setImageBitmap(productPhoto);
                    break;
                case Constant.galleryRequest:
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        productPhoto = BitmapFactory.decodeStream(imageStream);
                        productPhoto = Bitmap.createScaledBitmap(productPhoto, (int)(productPhoto.getWidth()*0.3), (int)(productPhoto.getHeight()*0.3), true);
                        productView.setImageBitmap(productPhoto);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
