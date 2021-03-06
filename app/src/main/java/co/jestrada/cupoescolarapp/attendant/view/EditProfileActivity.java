package co.jestrada.cupoescolarapp.attendant.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.jestrada.cupoescolarapp.R;
import co.jestrada.cupoescolarapp.attendant.contract.IEditProfileContract;
import co.jestrada.cupoescolarapp.attendant.model.bo.AttendantBO;
import co.jestrada.cupoescolarapp.common.model.bo.DocIdTypeBO;
import co.jestrada.cupoescolarapp.common.model.enums.GenreEnum;
import co.jestrada.cupoescolarapp.attendant.presenter.EditProfilePresenter;
import co.jestrada.cupoescolarapp.base.view.BaseActivity;

public class EditProfileActivity extends BaseActivity implements
        IEditProfileContract.IEditProfileView{

    private EditProfilePresenter mEditProfilePresenter;

    private static final String ZERO = "0";
    private static final String SEPARATOR = "-";

    public final Calendar mCalendar = Calendar.getInstance();
    final int month = mCalendar.get(Calendar.MONTH);
    final int day = mCalendar.get(Calendar.DAY_OF_MONTH);
    final int year = mCalendar.get(Calendar.YEAR);

    private Toolbar mToolbar;

    private ArrayList<String> docIdTypeArrayList;
    private ArrayList<String> docIdTypeShortNameArrayList;

    @BindView(R.id.fav_edit)
    FloatingActionButton favEdit;
    @BindView(R.id.fav_save)
    FloatingActionButton favSave;
    @BindView(R.id.fav_cancel)
    FloatingActionButton favCancel;

    @BindView(R.id.et_doc_id_type)
    EditText etDocIdType;
    @BindView(R.id.et_doc_id)
    EditText etDocId;
    @BindView(R.id.et_first_name)
    EditText etFirstName;
    @BindView(R.id.et_last_name)
    EditText etLastName;
    @BindView(R.id.rb_male)
    RadioButton rbMale;
    @BindView(R.id.rb_female)
    RadioButton rbFemale;
    @BindView(R.id.et_birthdate)
    EditText etBirthday;

    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_local_phone)
    EditText etLocalPhone;
    @BindView(R.id.et_mobile_phone)
    EditText etMobilPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mEditProfilePresenter = new EditProfilePresenter(EditProfileActivity.this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        initView();

        docIdTypeArrayList = new ArrayList<>();
        docIdTypeShortNameArrayList = new ArrayList<>();

    }

    private void initView() {
        ButterKnife.bind(this);
        enableFields(false);
        setToolbar();
        getData();
    }

    private void setToolbar() {
        if(mToolbar != null){
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(R.string.edit_profile);
            getSupportActionBar().setSubtitle("Actualiza tus datos personales y de contacto");
            mToolbar.setTitleTextAppearance(this, R.style.TextTitle);
            mToolbar.setTitleTextColor(getColor(R.color.mColorPrimaryText));
            mToolbar.setSubtitleTextAppearance(this, R.style.TextSubtitle1);
            mToolbar.setSubtitleTextColor(getColor(R.color.mColorSecondaryText));
            mToolbar.setNavigationIcon(R.drawable.ic_back_bold_blue_48);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private void getData(){
        enableFields(false);
        showProgressBar(true);
        mEditProfilePresenter.getData();

    }

    @OnClick(R.id.fav_edit)
    public void edit(){
        enableFields(true);
/*        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{getColor(R.color.mColorBackgroundSection)});
        favEdit.setBackgroundTintList(csl);
        csl = new ColorStateList(new int[][]{new int[0]}, new int[]{getColor(R.color.mColorSecondaryText)});
        favEdit.setSupportImageTintList(csl);*/
        favEdit.hide();
        favSave.show();
        favCancel.show();
    }

    @OnClick(R.id.fav_save)
    public void save(){
        if(validateInputs()){
            favEdit.show();
            favSave.hide();
            favCancel.hide();
            showProgressBar(true);
            enableFields(false);
            saveAttendant();
        }

    }

    @OnClick(R.id.fav_cancel)
    public void cancel(){
        enableFields(false);
        favEdit.show();
        favSave.hide();
        favCancel.hide();
    }

    private boolean validateInputs() {
        boolean validInputs = true;

        if(!isValidPhone(etMobilPhone.getText().toString().trim())){
            validInputs = false;
            etMobilPhone.setError(getString(R.string.validate_input_mobile_phone));
            etMobilPhone.requestFocus();
        }

        if(!isValidPhone(etLocalPhone.getText().toString().trim())){
            validInputs = false;
            etLocalPhone.setError(getString(R.string.validate_input_local_phone));
            etLocalPhone.requestFocus();
        }

        if(!isValidDocId(etDocId.getText().toString().trim())){
            validInputs = false;
            etDocId.setError(getString(R.string.validate_input_doc_id));
            etDocId.requestFocus();
        } else {
            if(!isValidDocIdType(etDocIdType.getText().toString().trim())){
                validInputs = false;
                etDocIdType.setError(getString(R.string.validate_input_doc_id_type));
                etDocIdType.requestFocus();
            }
        }

        return  validInputs;
    }

    private boolean isValidPhone(String phone){
        boolean err = false;
        if (!TextUtils.isEmpty(phone)){
            if (!Patterns.PHONE.matcher(phone).matches()){
                err = true;
            }
        }
        return !err;
    }

    private boolean isValidDocId(String docId){
        boolean err = false;
        if (!TextUtils.isEmpty(docId)){
            if (!TextUtils.isDigitsOnly(docId)){
                err = true;
            }
        }
        return !err;
    }

    private boolean isValidDocIdType(String docIdType){
        boolean err = false;
        if (TextUtils.isEmpty(docIdType)){
            err = true;
        }
        return !err;
    }

    @OnClick(R.id.et_doc_id_type)
    public void showDocIdTypeList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(R.string.select_doc_id);
        final ArrayAdapter<String> docIdTypeBOArrayAdapter = new ArrayAdapter<>(
                EditProfileActivity.this, android.support.design.R.layout.select_dialog_singlechoice_material);
        docIdTypeBOArrayAdapter.addAll(docIdTypeArrayList);
        builder.setAdapter(docIdTypeBOArrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                etDocIdType.setText(docIdTypeShortNameArrayList.get(item));
                dialog.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.et_birthdate)
    public void getDate(){
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int currentMonth = month + 1;
                String currentDay = (dayOfMonth < 10)? ZERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String buildCurrentMonth = (currentMonth < 10)? ZERO + String.valueOf(currentMonth):String.valueOf(currentMonth);
                etBirthday.setText(currentDay + SEPARATOR + buildCurrentMonth + SEPARATOR + year);
            }
        },year, month, day);
        mDatePickerDialog.show();

    }

    private void saveAttendant() {
        AttendantBO attendantBO = AttendantBO.getInstance();
        attendantBO.setDocIdType(etDocIdType.getText() != null ? etDocIdType.getText().toString() : "");
        attendantBO.setDocId(etDocId.getText() != null ? etDocId.getText().toString() : "");
        attendantBO.setFirstName(etFirstName.getText() != null ? etFirstName.getText().toString() : "");
        attendantBO.setLastName(etLastName.getText() != null ? etLastName.getText().toString() : "");
        if(rbMale.isChecked()){
            attendantBO.setGenre(GenreEnum.HOMBRE);
        } else
        if(rbFemale.isChecked()){
            attendantBO.setGenre(GenreEnum.MUJER);
        }
        attendantBO.setBirthdate(etBirthday.getText() != null ? etBirthday.getText().toString() : "");
        attendantBO.setEmail(etEmail.getText() != null ? etEmail.getText().toString() : "");
        attendantBO.setAddress(etAddress.getText() != null ? etAddress.getText().toString() : "");
        attendantBO.setLocalPhone(etLocalPhone.getText() != null ? etLocalPhone.getText().toString() : "");
        attendantBO.setMobilePhone(etMobilPhone.getText() != null ? etMobilPhone.getText().toString() : "");
        mEditProfilePresenter.saveAttendant();
    }

    void enableFields(boolean enable){
        enableFieldsPersonalInfo(enable);
        enableFieldsContactInfo(enable);
    }

    void enableFieldsPersonalInfo(boolean enable){
        etDocIdType.setEnabled(enable);
        etDocId.setEnabled(enable);
        etFirstName.setEnabled(enable);
        etLastName.setEnabled(enable);
        rbMale.setEnabled(enable);
        rbFemale.setEnabled(enable);
        etBirthday.setEnabled(enable);
    }

    void enableFieldsContactInfo(boolean enable){
        etEmail.setEnabled(enable);
        etAddress.setEnabled(enable);
        etLocalPhone.setEnabled(enable);
        etMobilPhone.setEnabled(enable);
    }

    @Override
    public void goToLogin() {

    }

    @Override
    public void getAttendantTransactionState(boolean successful) {
        if(successful){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_edit_profile), R.string.attendan_profile_updated, Snackbar.LENGTH_LONG)
                    .setActionTextColor(getColor(R.color.mColorPrimaryText))
                    .setAction("Action", null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(getColor(R.color.mColorPrimaryLight));
            TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        }
        showProgressBar(false);
    }

    @Override
    public void setAttendantUI(boolean isChanged) {
        if (isChanged){
            AttendantBO attendantBO = AttendantBO.getInstance();
            etDocIdType.setText((attendantBO.getDocIdType() != null) ? attendantBO.getDocIdType() : "");
            etDocId.setText((attendantBO.getDocId() != null) ? attendantBO.getDocId() : "");
            etFirstName.setText((attendantBO.getFirstName() != null) ? attendantBO.getFirstName() : "");
            etLastName.setText((attendantBO.getLastName() != null) ? attendantBO.getLastName() : "");
            etFirstName.setText((attendantBO.getFirstName() != null) ? attendantBO.getFirstName() : "");
            if(attendantBO.getGenre() != null){
                if(attendantBO.getGenre().equals(GenreEnum.HOMBRE)){
                    rbMale.setChecked(true);
                }
                else{
                    rbFemale.setChecked(true);
                }
            }
            etBirthday.setText((attendantBO.getBirthdate() != null) ? attendantBO.getBirthdate() : "");
            etEmail.setText((attendantBO.getEmail() != null) ? attendantBO.getEmail() : "");
            etAddress.setText((attendantBO.getAddress() != null) ? attendantBO.getAddress() : "");
            etLocalPhone.setText((attendantBO.getLocalPhone() != null) ? attendantBO.getLocalPhone() : "");
            etMobilPhone.setText((attendantBO.getMobilePhone() != null) ? attendantBO.getMobilePhone() : "");
        }
        showProgressBar(false);
    }

    @Override
    public void setDocIdTypesList(ArrayList<DocIdTypeBO> docIdTypeBOS, boolean isChanged) {
        if (isChanged){
            if (!docIdTypeBOS.isEmpty()){
                docIdTypeArrayList.clear();
                docIdTypeShortNameArrayList.clear();
                for (DocIdTypeBO docIdTypeBO : docIdTypeBOS){
                    docIdTypeArrayList.add(docIdTypeBO.getShortName() + " " + docIdTypeBO.getLongName());
                    docIdTypeShortNameArrayList.add(docIdTypeBO.getShortName());
                }
            }
        }
    }

}
