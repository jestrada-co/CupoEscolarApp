package co.jestrada.cupoescolarapp.attendant.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import co.jestrada.cupoescolarapp.attendant.contract.IEditProfileContract;
import co.jestrada.cupoescolarapp.attendant.interactor.AttendantInteractor;
import co.jestrada.cupoescolarapp.attendant.interactor.DocIdTypeInteractor;
import co.jestrada.cupoescolarapp.attendant.model.bo.AttendantBO;
import co.jestrada.cupoescolarapp.attendant.model.bo.DocIdTypeBO;
import co.jestrada.cupoescolarapp.attendant.model.bo.RefPositionBO;
import co.jestrada.cupoescolarapp.common.presenter.BasePresenter;
import co.jestrada.cupoescolarapp.login.model.bo.UserBO;

public class EditProfilePresenter extends BasePresenter implements
        IEditProfileContract.IEditProfilePresenter,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_FINE_LOCATION = 123;
    private IEditProfileContract.IEditProfileView mEditProfileView;
    private Context mContext;

    private DocIdTypeInteractor mDocIdTypeInteractor;
    private AttendantInteractor mAttendantInteractor;

    private FirebaseAuth mFirebaseAuth;

    private UserBO userBOApp;

    public EditProfilePresenter(final Context mContext) {
        this.mEditProfileView = (IEditProfileContract.IEditProfileView) mContext;
        this.mDocIdTypeInteractor = new DocIdTypeInteractor(this);
        this.mAttendantInteractor = new AttendantInteractor(
                null,
                null,
                null,
                null,
                this,
                null
        );
        this.mContext = mContext;
        mFirebaseAuth = FirebaseAuth.getInstance();
        userBOApp = UserBO.getInstance();
    }

    private void getData() {
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mDocIdTypeInteractor.getDocIdTypes(false);
            mAttendantInteractor.getAttendant(mFirebaseUser.getUid());
        }
    }

    @Override
    public void getAttendant(AttendantBO attendantBO, boolean isChanged) {
        mEditProfileView.setAttendantUI(attendantBO, isChanged);
    }

    @Override
    public void saveAttendant(AttendantBO attendantBO) {
        userBOApp = UserBO.getInstance();
        if (userBOApp != null){
            attendantBO.setUserUid(userBOApp.getuId());
            mAttendantInteractor.saveAttendant(attendantBO);
        }
    }

    @Override
    public void getDocIdTypes(ArrayList<DocIdTypeBO> docIdTypeBOS, boolean isChanged) {
        if (!docIdTypeBOS.isEmpty()){
            mEditProfileView.setDocIdTypesList(docIdTypeBOS, isChanged);
        }
    }


    @Override
    public void signOut() {
        userBOApp = UserBO.getInstance();
        userBOApp.setOnSession(false);
        mFirebaseAuth.signOut();
        mEditProfileView.goToLogin();
    }

    @Override
    public void onStart() {
        userBOApp = UserBO.getInstance();
        if (!userBOApp.isOnSession()){
            signOut();
        } else {
            getData();
        }

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
