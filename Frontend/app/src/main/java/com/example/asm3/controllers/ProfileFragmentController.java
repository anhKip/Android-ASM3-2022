package com.example.asm3.controllers;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm3.AccountSettingActivity;
import com.example.asm3.AuthenticationActivity;
import com.example.asm3.R;
import com.example.asm3.base.adapter.GenericAdapter;
import com.example.asm3.base.adapter.viewHolder.BookHolder;
import com.example.asm3.base.adapter.viewHolder.OrderHolder;
import com.example.asm3.base.adapter.viewHolder.ReviewHolder;
import com.example.asm3.base.controller.BaseController;
import com.example.asm3.base.networking.services.AsyncTaskCallBack;
import com.example.asm3.base.networking.services.GetAuthenticatedData;
import com.example.asm3.config.Constant;
import com.example.asm3.config.Helper;
import com.example.asm3.fragments.mainActivity.MainViewModel;
import com.example.asm3.models.ApiList;
import com.example.asm3.fragments.mainActivity.ReviewDialogBody;
import com.example.asm3.models.Book;
import com.example.asm3.models.Customer;
import com.example.asm3.models.Order;
import com.example.asm3.models.OrderDetail;
import com.example.asm3.models.Review;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class ProfileFragmentController extends BaseController implements
        MaterialButtonToggleGroup.OnButtonCheckedListener,
        View.OnClickListener,
        BookHolder.OnSelectListener,
        OrderHolder.OnSelectListener,
        ReviewHolder.OnSelectListener,
        AsyncTaskCallBack,
        DialogInterface.OnClickListener {

    private View view;
    private CardView profileAvatarLayout;
    private ImageView profileAvatarImg;
    private TextView profileUsernameTxt;
    private RatingBar ratingBar;
    private MaterialButtonToggleGroup profileDataBtnGrp;
    private Button settingProfileBtn, sellingBtn, purchasedBtn, feedbackBtn;
    private RecyclerView sellingRecView, purchasedRecView, feedbackRecView;
    private MaterialAlertDialogBuilder builder;
    private ReviewDialogBody reviewDialogBody;
    private GenericAdapter<Book> bookAdapter;
    private GenericAdapter<Order> orderAdapter;
    private GenericAdapter<Review> reviewAdapter;

    private MainViewModel mainViewModel;
    private LiveData<Customer> authCustomer;
    private ArrayList<Book> sellingBooks;
    private ArrayList<Order> orders;
    private ArrayList<Review> reviews;
    private String token;
    private GetAuthenticatedData getAuthenticatedData;

    public ProfileFragmentController(Context context, FragmentActivity activity, View view, ViewModel viewModel) {
        super(context, activity);
        this.view = view;
        this.mainViewModel = (MainViewModel) viewModel;

        if(isOnline()) {
            authCustomer = mainViewModel.getAuthCustomer();
            sellingBooks = mainViewModel.getBooks().getValue();
        }
        orders = new ArrayList<>();
        reviews = new ArrayList<>();
    }

    // Render functions
    @Override
    public void onInit() {
        if(!isOnline()) return;
        if (!isAuth()) {
            Helper.goToLogin(getContext(), getActivity());
        } else {
            // for testing
            token = getToken();

            orders.add(new Order("Lord of the ring", "23/06/2000", "dang giao", new Customer() , "New Book",100000, 2, true));
            orders.add(new Order("Lord of the ring", "23/06/2000", "dang giao", new Customer() , "New Book",100000, 2, true));
            orders.add(new Order("Lord of the ring", "23/06/2000", "dang giao", new Customer() , "New Book",100000, 2, true));
            orders.add(new Order("Lord of the ring", "23/06/2000", "dang giao", new Customer() , "New Book",100000, 2, true));
            orders.add(new Order("Lord of the ring", "23/06/2000", "dang giao", new Customer() , "New Book",100000, 2, true));
            orders.add(new Order("Lord of the ring", "23/06/2000", "dang giao", new Customer() , "New Book",100000, 2, true));
            orders.add(new Order("Lord of the ring", "23/06/2000", "dang giao", new Customer() , "New Book",100000, 2, true));

            reviews.add(new Review("0", "hahahah no dai ghe truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha", "22/02/2022", 3, new Customer("Quang"), new Order()));
            reviews.add(new Review("0", "hahahah no dai ghe truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha", "22/02/2022", 3, new Customer("Quang"), new Order()));
            reviews.add(new Review("0", "hahahah no dai ghe truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha", "22/02/2022", 3, new Customer("Quang"), new Order()));
            reviews.add(new Review("0", "ha lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha", "22/02/2022", 3, new Customer("Quang"), new Order()));
            reviews.add(new Review("0", "hh no dai ghe truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha", "22/02/2022", 3, new Customer("Quang"), new Order()));
            reviews.add(new Review("0", "hahahah no dan hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha", "22/02/2022", 3, new Customer("Quang"), new Order()));
            reviews.add(new Review("0", "hahahah no dai ghe truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha truyen hya lam nha", "22/02/2022", 3, new Customer("Quang"), new Order()));
            // end testing

            profileAvatarLayout = view.findViewById(R.id.profileAvatarLayout);
            profileAvatarImg = view.findViewById(R.id.profileAvatarImg);
            profileUsernameTxt = view.findViewById(R.id.profileUsernameTxt);
            ratingBar = view.findViewById(R.id.ratingBar);
            settingProfileBtn = view.findViewById(R.id.settingProfileBtn);
            profileDataBtnGrp = view.findViewById(R.id.profileDataBtnGrp);
            sellingBtn = view.findViewById(R.id.sellingBtn);
            purchasedBtn = view.findViewById(R.id.purchasedBtn);
            feedbackBtn = view.findViewById(R.id.feedbackBtn);
            sellingRecView = view.findViewById(R.id.sellingRecView);
            purchasedRecView = view.findViewById(R.id.purchasedRecView);
            feedbackRecView = view.findViewById(R.id.feedbackRecView);
            bookAdapter = generateBookAdapter();
            orderAdapter = generateOrderAdapter();
            reviewAdapter = generateReviewAdapter();

            profileUsernameTxt.setText(authCustomer.getValue().getUsername());
            profileDataBtnGrp.check(R.id.sellingBtn);
            sellingRecView.setVisibility(View.VISIBLE);

            profileAvatarLayout.setOnClickListener(this);
            settingProfileBtn.setOnClickListener(this);
            profileDataBtnGrp.addOnButtonCheckedListener(this);

            loadSelling();
            loadPurchased();
            loadFeedback();
        }
    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        switch (group.getCheckedButtonId()) {
            case R.id.sellingBtn:
                sellingRecView.setVisibility(View.VISIBLE);
                purchasedRecView.setVisibility(View.GONE);
                feedbackRecView.setVisibility(View.GONE);
                break;
            case R.id.purchasedBtn:
                sellingRecView.setVisibility(View.GONE);
                purchasedRecView.setVisibility(View.VISIBLE);
                feedbackRecView.setVisibility(View.GONE);
                break;
            case R.id.feedbackBtn:
                sellingRecView.setVisibility(View.GONE);
                purchasedRecView.setVisibility(View.GONE);
                feedbackRecView.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onBookClick(int position, View view) {
        if(!isOnline()) {
            showConnectDialog();
            return;
        }
        switch (view.getId()) {
            case R.id.productBody:
                Helper.goToBookDetail(getContext(), getActivity(), sellingBooks.get(position).get_id(), position);
                break;
        }
    }

    @Override
    public void onOrderClick(int position, View view) {
        if(!isOnline()) {
            showConnectDialog();
            return;
        }
        switch (view.getId()) {
            case R.id.orderBody:
                Log.d(TAG, "onOrderClick: test " + position);
                showDialog();
                break;
            case R.id.orderDeleteBtn:
                Log.d(TAG, "onOrderClick: test delete " + position);
                break;
        }
    }

    @Override
    public void onAvatarClick(int position, View view) {
        if(!isOnline()) {
            showConnectDialog();
            return;
        }
        switch (view.getId()) {
            case R.id.reviewUserImgLayout:
                Log.d(TAG, "onAvatarClick: test " + position);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if(!isOnline()) {
            showConnectDialog();
            return;
        }
        switch (view.getId()) {
            case R.id.settingProfileBtn:
                goToSetting();
                break;
            case R.id.profileAvatarLayout:
                break;
        }

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) { // onClick for dialog
        Log.d(TAG, "onClick: dialog click " + i);
        switch (i) {
            case -1: // submit
                float rating = reviewDialogBody.getRatingBar().getRating();
                String userReviewTxt = reviewDialogBody.getReviewTxt().getText().toString();
                Log.d(TAG, "onClick: dialog rating = " + rating);
                Log.d(TAG, "onClick: dialog user review text = " + userReviewTxt);
                break;
            case -2: // cancel
                break;
        }
    }

    // Helpers
    private GenericAdapter<Book> generateBookAdapter() {
        return new GenericAdapter<Book>(sellingBooks) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                return new BookHolder(view, ProfileFragmentController.this);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, Book item) {
                BookHolder bookHolder = (BookHolder) holder;
                bookHolder.getBookNameTxt().setText(item.getName());
                bookHolder.getBookConditionTxt().setText("Condition: New");
                bookHolder.getBookPriceTxt().setText(item.getPrice() + " đ");
            }
        };
    }

    private GenericAdapter<Order> generateOrderAdapter() {
        return new GenericAdapter<Order>(orders) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderHolder(view, ProfileFragmentController.this);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, Order item) {
                OrderHolder orderHolder = (OrderHolder) holder;
                orderHolder.getOrderBookTxt().setText(item.getBookName());
                orderHolder.getOrderQuantityTxt().setText("Quantity: " + item.getQuantity());
                orderHolder.getOrderPriceTxt().setText(item.getBookPrice() + " đ");
                orderHolder.getOrderDeleteBtn().setVisibility(View.GONE);
            }
        };
    }

    private GenericAdapter<Review> generateReviewAdapter() {
        return new GenericAdapter<Review>(reviews) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
                return new ReviewHolder(view, ProfileFragmentController.this);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, Review item) {
                ReviewHolder reviewHolder = (ReviewHolder) holder;
                reviewHolder.getReviewUserNameTxt().setText(item.getCustomer().getUsername());
                reviewHolder.getReviewContentTxt().setText(item.getContent());
                reviewHolder.getReviewRatingBar().setRating(item.getRating());
                reviewHolder.getReviewDateTxt().setText(item.getDate());
            }
        };
    }

    private void loadSelling() {
        sellingRecView.setAdapter(bookAdapter);
        sellingRecView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        sellingRecView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.set(0, 0, 10, 20);
                } else {
                    outRect.set(10, 0, 0, 20);
                }
            }
        });
    }

    private void loadPurchased() {
        purchasedRecView.setAdapter(orderAdapter);
        purchasedRecView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadFeedback() {
        feedbackRecView.setAdapter(reviewAdapter);
        feedbackRecView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showDialog() {
        reviewDialogBody = new ReviewDialogBody(getContext());
        builder = new MaterialAlertDialogBuilder(getContext());
        builder.setView(reviewDialogBody).
                setPositiveButton(R.string.submit, this).
                setNegativeButton(R.string.cancel, this).show();
    }

    // Request functions


    // Navigation functions
    public void goToSetting() {
        Intent intent = new Intent(getContext(), AccountSettingActivity.class);
        intent.putExtra("data", authCustomer.getValue());
        getActivity().startActivityForResult(intent, Constant.accSettingActivityCode);
    }

    // Callback functions
    @Override
    public void onFinished(String message, String taskType) {

    }

    @Override
    public void onError(String taskType) {

    }

    // Getter and Setter
}
