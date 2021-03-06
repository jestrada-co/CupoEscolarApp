package co.jestrada.cupoescolarapp.school.interactor;

import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.jestrada.cupoescolarapp.base.contract.IBaseContract;
import co.jestrada.cupoescolarapp.common.contract.IMainContract;
import co.jestrada.cupoescolarapp.school.constant.ConstantsFirebaseSchool;
import co.jestrada.cupoescolarapp.school.contract.SchoolContract;
import co.jestrada.cupoescolarapp.school.model.bo.SchoolBO;
import co.jestrada.cupoescolarapp.school.model.modelDocJson.SchoolDocJson;

public class SchoolInteractor implements IBaseContract.IBaseInteractor{

    private IMainContract.IMainPresenter mMainPresenter;
    private SchoolContract.ISchoolPresenter mSchoolPresenter;

    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference dbRefSchools;

    public SchoolInteractor(@Nullable IMainContract.IMainPresenter mMainPresenter,
                            @Nullable SchoolContract.ISchoolPresenter mSchoolPresenter) {
        this.mMainPresenter = mMainPresenter;
        this.mSchoolPresenter = mSchoolPresenter;

        this.mFirebaseDB = FirebaseDatabase.getInstance();
        this.dbRefSchools = mFirebaseDB.getReference(ConstantsFirebaseSchool.SCHOOL);
    }

    public void getSchools() {
        dbRefSchools.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot schoolDS) {
                    List<SchoolBO> schoolBOS = new ArrayList<>();
                    for (DataSnapshot school : schoolDS.getChildren()){
                        final SchoolDocJson schoolDocJson = school.getValue(SchoolDocJson.class);
                        SchoolBO schoolBO = new SchoolBO();
                        schoolBO.setValues(schoolDocJson);
                        schoolBOS.add(schoolBO);
                    }

                    if(!schoolBOS.isEmpty()){
                        notifySchoolChanges(schoolBOS, true);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    public void getSchoolsByCode(String schoolCode) {
        dbRefSchools.child(schoolCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot schoolDS) {
                    final SchoolDocJson schoolDocJson = schoolDS.getValue(SchoolDocJson.class);
                    SchoolBO schoolBO = new SchoolBO();
                    schoolBO.setValues(schoolDocJson);

                if(schoolBO != null){
                    notifySchoolByCodeChanges(schoolBO, true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void notifySchoolChanges(List<SchoolBO> schoolBOS, boolean isChanged) {
        if(mMainPresenter != null){
            mMainPresenter.getSchools(schoolBOS, isChanged);
        }
    }

    private void notifySchoolByCodeChanges(SchoolBO schoolBO, boolean isChanged) {
        if(mSchoolPresenter != null){
            mSchoolPresenter.getSchool(schoolBO, isChanged);
        }
    }

}